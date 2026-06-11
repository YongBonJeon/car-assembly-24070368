# Refactoring Plan — car-assembly

각 Phase는 독립적으로 완료 가능하며, 완료 후 기존 동작이 유지되어야 한다.  
다음 Phase로 넘어가기 전에 반드시 빌드 성공 및 수동 실행으로 동작을 확인한다.

---

## Phase 1 — 상수를 Enum으로 전환

**목적**: 흩어진 int 상수와 매직 넘버를 타입 안전한 Enum으로 교체한다.

**현재 문제**:
- `SEDAN=1`, `SUV=2` 같은 int 상수는 컴파일러가 잘못된 값을 잡아주지 못한다.
- `runProducedCar()` 내부에서 `stack[BrakeSystem_Q]==1`, `==2` 같은 리터럴이 혼용된다.

**작업 범위**:
- `CarType`, `Engine`, `BrakeSystem`, `SteeringSystem` Enum 클래스 생성
- 각 Enum에 `displayName` 필드 추가 (메뉴 출력 및 결과 출력에 사용)
- `Assemble.java` 내 int 상수 및 리터럴 비교를 Enum으로 교체

**완료 기준**:
- `int` 기반 부품 상수가 코드에서 완전히 제거됨
- 빌드 성공, 기존 동작 변화 없음

---

## Phase 2 — 도메인 모델 분리 (`CarConfiguration`)

**목적**: `int[] stack` 배열을 의미 있는 객체로 대체한다.

**현재 문제**:
- `stack[0]`, `stack[1]` 등 인덱스로만 접근하므로 코드 가독성이 낮다.
- 전역 static 배열이라 테스트 간 상태 오염이 발생할 수 있다.

**작업 범위**:
- `CarConfiguration` 클래스 생성
  - 필드: `CarType carType`, `Engine engine`, `BrakeSystem brakeSystem`, `SteeringSystem steeringSystem`
  - getter/setter 또는 record 스타일
- `Assemble.java`에서 `stack[]` 참조를 `CarConfiguration` 객체로 교체

**완료 기준**:
- `stack[]` 배열이 코드에서 완전히 제거됨
- 빌드 성공, 기존 동작 변화 없음

---

## Phase 3 — 호환성 규칙 통합 (`CompatibilityValidator`)

**목적**: 두 곳에 중복된 호환성 규칙을 단일 소스로 통합한다.

**현재 문제**:
- `isValidCheck()`와 `testProducedCar()` 양쪽에 동일한 5가지 규칙이 하드코딩되어 있다.
- 규칙 추가/수정 시 두 메서드를 동시에 수정해야 하고, 누락 시 RUN과 Test 결과가 달라진다.

**작업 범위**:
- `CompatibilityValidator` 클래스 생성
  - `validate(CarConfiguration config)` 메서드: 위반 규칙을 `Optional<String>` 또는 커스텀 결과 타입으로 반환
- `isValidCheck()`와 `testProducedCar()`를 `CompatibilityValidator`를 호출하도록 교체
- 기존 두 메서드는 단순 위임 후 제거

**완료 기준**:
- 5가지 호환성 규칙이 `CompatibilityValidator` 한 곳에만 존재함
- 빌드 성공, RUN과 Test 모두 기존 동작 유지

---

## Phase 4 — UI 분리 (`ConsoleUI`)

**목적**: 메뉴 출력 로직을 별도 클래스로 분리하여 `Assemble`의 책임을 줄인다.

**현재 문제**:
- `Assemble.java`가 입력 처리, 상태 관리, 비즈니스 로직, 화면 출력을 모두 담당한다.
- 메뉴 출력 메서드(`show*Menu`)와 선택 확인 메서드가 같은 클래스에 섞여 있다.

**작업 범위**:
- `ConsoleUI` 클래스 생성
  - `show*Menu()` 메서드 이동 (5개)
  - 선택 확인 메시지 출력 메서드 이동
  - `runProducedCar()` 결과 출력 부분 이동
- `Assemble.java`는 루프·입력·단계 전환만 담당하도록 정리

**완료 기준**:
- `Assemble.java`에 `System.out.print*` 호출이 없거나 최소화됨
- 빌드 성공, 기존 동작 변화 없음

---

## Phase 5 — 테스트 작성

**목적**: 리팩터링 결과를 검증하고 이후 변경에 대한 회귀 방지망을 구축한다.

**현재 문제**:
- `src/test/java/`가 비어있어 회귀를 자동으로 감지할 수 없다.
- Phase 1~4 완료 후 비즈니스 로직이 분리되어 있어 단위 테스트 작성이 가능한 상태가 된다.

**작업 범위**:
- `CompatibilityValidatorTest`: 5가지 호환성 규칙 각각 PASS/FAIL 케이스
- `CarConfigurationTest`: 객체 생성 및 필드 접근
- 고장난 엔진 선택 시 RUN 동작 검증
- 경계값 테스트 (유효 범위 내/외 입력)

**완료 기준**:
- `gradlew test` 통과
- 5가지 호환성 규칙이 모두 테스트로 커버됨
