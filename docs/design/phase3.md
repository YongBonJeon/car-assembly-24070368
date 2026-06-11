# Phase 3 설계 — 호환성 규칙 통합 (`CompatibilityValidator`)

## 목적

`isValidCheck()`와 `testProducedCar()`에 각각 하드코딩된 동일한 5가지 호환성 규칙을  
`CompatibilityValidator` 단일 클래스로 통합한다.

---

## 현재 문제 (Before)

동일한 규칙이 두 메서드에 중복되어 있다.

```java
// isValidCheck() — 규칙 위반 여부만 반환
private static boolean isValidCheck() {
    if (config.getCarType() == CarType.SEDAN         && config.getBrakeSystem() == BrakeSystem.CONTINENTAL) return false;
    if (config.getCarType() == CarType.SUV           && config.getEngine() == Engine.TOYOTA)                return false;
    if (config.getCarType() == CarType.TRUCK         && config.getEngine() == Engine.WIA)                   return false;
    if (config.getCarType() == CarType.TRUCK         && config.getBrakeSystem() == BrakeSystem.MANDO)       return false;
    if (config.getBrakeSystem() == BrakeSystem.BOSCH && config.getSteeringSystem() != SteeringSystem.BOSCH) return false;
    return true;
}

// testProducedCar() — 동일한 규칙을 다시 검사하면서 에러 메시지도 생산
private static void testProducedCar() {
    if (config.getCarType() == CarType.SEDAN         && config.getBrakeSystem() == BrakeSystem.CONTINENTAL) {
        fail("Sedan에는 Continental제동장치 사용 불가");
    } else if (config.getCarType() == CarType.SUV    && config.getEngine() == Engine.TOYOTA) {
        fail("SUV에는 TOYOTA엔진 사용 불가");
    } ...
}
```

두 메서드가 요구하는 것이 다르다.

| 호출처 | 필요한 것 |
|---|---|
| `runProducedCar()` → `isValidCheck()` | 유효 여부 (boolean) |
| `testProducedCar()` | 유효 여부 + 위반 시 에러 메시지 |

이 두 가지를 하나의 반환값으로 표현할 수 있는 타입이 필요하다.

---

## 반환 타입 설계 — `Optional<String>`

`Optional<String>`을 반환한다.

| 상태 | 반환값 | 의미 |
|---|---|---|
| 유효 | `Optional.empty()` | 위반 규칙 없음 |
| 위반 | `Optional.of("에러 메시지")` | 위반된 규칙의 에러 메시지 포함 |

- `isPresent()` → 위반 여부 확인 (`runProducedCar()` 용도)
- `get()` → 에러 메시지 추출 (`testProducedCar()` 용도)
- 별도 결과 클래스 없이 표준 라이브러리만 사용

---

## 신규 파일

```
src/main/java/
└── CompatibilityValidator.java   (신규)
```

---

## `CompatibilityValidator` 설계

```java
import java.util.Optional;

public class CompatibilityValidator {

    public static Optional<String> validate(CarConfiguration config) {
        if (config.getCarType() == CarType.SEDAN         && config.getBrakeSystem() == BrakeSystem.CONTINENTAL)
            return Optional.of("Sedan에는 Continental제동장치 사용 불가");
        if (config.getCarType() == CarType.SUV           && config.getEngine() == Engine.TOYOTA)
            return Optional.of("SUV에는 TOYOTA엔진 사용 불가");
        if (config.getCarType() == CarType.TRUCK         && config.getEngine() == Engine.WIA)
            return Optional.of("Truck에는 WIA엔진 사용 불가");
        if (config.getCarType() == CarType.TRUCK         && config.getBrakeSystem() == BrakeSystem.MANDO)
            return Optional.of("Truck에는 Mando제동장치 사용 불가");
        if (config.getBrakeSystem() == BrakeSystem.BOSCH && config.getSteeringSystem() != SteeringSystem.BOSCH)
            return Optional.of("Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
        return Optional.empty();
    }
}
```

- `static` 메서드로 구성. 인스턴스 상태가 없으므로 인스턴스화 불필요
- 5가지 규칙이 이 클래스에만 존재

---

## `Assemble.java` 변경 포인트

### `isValidCheck()` 제거

`isValidCheck()`는 `runProducedCar()` 한 곳에서만 호출된다.  
Phase 3 이후 `CompatibilityValidator.validate()`로 직접 교체하므로 메서드 자체를 제거한다.

### `runProducedCar()` — `isValidCheck()` → `validate()` 인라인 교체

```java
// Before
private static void runProducedCar() {
    if (!isValidCheck()) {
        System.out.println("자동차가 동작되지 않습니다");
        return;
    }
    ...
}

// After
private static void runProducedCar() {
    if (CompatibilityValidator.validate(config).isPresent()) {
        System.out.println("자동차가 동작되지 않습니다");
        return;
    }
    ...
}
```

### `testProducedCar()` — 5가지 규칙 분기 → `validate()` 단일 호출

```java
// Before (규칙 5개 분기)
private static void testProducedCar() {
    if (config.getCarType() == CarType.SEDAN && ...) {
        fail("Sedan에는 ...");
    } else if (...) {
        fail("SUV에는 ...");
    } else if (...) {
        ...
    } else {
        System.out.println("자동차 부품 조합 테스트 결과 : PASS");
    }
}

// After
private static void testProducedCar() {
    Optional<String> result = CompatibilityValidator.validate(config);
    if (result.isPresent()) {
        fail(result.get());
    } else {
        System.out.println("자동차 부품 조합 테스트 결과 : PASS");
    }
}
```

---

## 변경하지 않는 것

| 항목 | 이유 |
|---|---|
| `fail()` 헬퍼 메서드 | Phase 4에서 `ConsoleUI`로 이동 |
| `show*Menu()` 출력 메서드 | Phase 4 범위 |
| `runProducedCar()`의 고장 엔진 처리 | 호환성 규칙과 무관한 별도 로직 |

---

## 완료 기준

- [ ] `CompatibilityValidator.java` 생성, 5가지 규칙이 이 클래스에만 존재함
- [ ] `Assemble.java`에서 `isValidCheck()` 메서드 제거
- [ ] `Assemble.java`에서 호환성 규칙 조건식이 없음
- [ ] `gradlew.bat build` 성공
- [ ] 직접 실행 후 RUN / Test 동작이 기존과 동일함
