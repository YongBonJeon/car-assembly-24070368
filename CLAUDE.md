# CLAUDE.md — car-assembly

이 파일은 Claude가 프로젝트를 파악하고 작업할 때 참조하는 문서입니다.

---

## 프로젝트 개요

자동차 부품 조합 유효성을 검증하는 **인터랙티브 CLI 시뮬레이터**.  
사용자가 차량 타입 → 엔진 → 제동장치 → 조향장치를 단계별로 선택하고,  
선택한 조합을 **RUN**(실행) 또는 **Test**(호환성 검사)로 확인한다.

- 언어: Java (외부 런타임 의존성 없음)
- 빌드: Gradle 9.3.0 (Wrapper 포함)
- UI 언어: 한국어

---

## 현재 프로젝트 목표

**이 프로젝트의 목적은 `Assemble.java` 리팩터링이다.**

현재 코드는 단일 클래스에 모든 로직(UI, 상태 관리, 비즈니스 규칙, 입력 처리)이 혼재되어 있다.  
리팩터링 시 아래 문제들을 염두에 두고 작업한다.

- **중복 비즈니스 규칙**: 호환성 규칙이 `isValidCheck()`와 `testProducedCar()`에 각각 하드코딩되어 있음
- **관심사 미분리**: UI 출력, 입력 검증, 비즈니스 로직, 상태 저장이 한 클래스에 섞여 있음
- **매직 넘버 혼용**: 부품 상수가 정의되어 있으나 일부 메서드 내부에서 리터럴로도 사용됨
- **테스트 불가 구조**: 현재 구조로는 JUnit 테스트 작성이 어려움

리팩터링 방향은 사용자와 논의 후 결정한다. 임의로 구조를 바꾸지 않는다.

---

## 빌드 & 실행 커맨드

```bash
# 빌드
gradlew.bat build

# 실행
gradlew.bat run

# 테스트
gradlew.bat test

# 직접 실행 (컴파일 후)
javac src/main/java/Assemble.java -d build/classes/java/main
java -cp build/classes/java/main Assemble
```

---

## 소스 구조

```
src/main/java/Assemble.java   ← 모든 로직이 담긴 단일 클래스
src/test/java/                ← 현재 비어있음 (테스트 미작성)
build.gradle                  ← Java 플러그인 + JUnit 5 의존성
```

---

## 아키텍처

### 상태 머신 (State Machine)

`main()` 루프가 `step` 변수로 현재 단계를 관리한다.

```
step=0  CarType_Q        차량 타입 선택
step=1  Engine_Q         엔진 선택
step=2  BrakeSystem_Q    제동장치 선택
step=3  SteeringSystem_Q 조향장치 선택
step=4  Run_Test         RUN 또는 Test 실행
```

- 각 단계에서 `0` 입력 → `step--` (이전 단계)
- `Run_Test` 단계에서 `0` → `step = CarType_Q` (처음으로)
- `exit` 입력 → 프로그램 종료

### 전역 상태 — `stack[]`

```java
private static int[] stack = new int[5];
```

인덱스가 곧 단계 번호(0~3)이며, 각 단계에서 선택한 부품 번호를 저장한다.

| 인덱스 | 의미 | 저장값 |
|---|---|---|
| 0 | 차량 타입 | 1=Sedan, 2=SUV, 3=Truck |
| 1 | 엔진 | 1=GM, 2=TOYOTA, 3=WIA, 4=고장 |
| 2 | 제동장치 | 1=MANDO, 2=CONTINENTAL, 3=BOSCH |
| 3 | 조향장치 | 1=BOSCH, 2=MOBIS |

---

## 핵심 비즈니스 규칙 (호환성 제약)

`isValidCheck()` 및 `testProducedCar()`에 구현된 5가지 규칙.  
이 규칙을 수정하거나 규칙을 추가할 때 **두 메서드 모두** 반드시 업데이트해야 한다.

| 조건 | 설명 |
|---|---|
| Sedan + CONTINENTAL 제동장치 | 사용 불가 |
| SUV + TOYOTA 엔진 | 사용 불가 |
| Truck + WIA 엔진 | 사용 불가 |
| Truck + MANDO 제동장치 | 사용 불가 |
| BOSCH 제동장치 + BOSCH 이외 조향장치 | 사용 불가 |

### RUN vs Test 동작 차이

| 동작 | 고장난 엔진(4번) | 호환성 규칙 위반 |
|---|---|---|
| **RUN** | "엔진이 고장나있습니다" 출력 후 중단 | "자동차가 동작되지 않습니다" 출력 |
| **Test** | 검사하지 않음 (별도 분기 없음) | FAIL + 위반 규칙 메시지 출력 |

---

## 주요 메서드 목록

| 메서드 | 역할 |
|---|---|
| `main()` | 메인 루프, 입력 처리, 단계 전환 |
| `isValidRange(step, ans)` | 단계별 입력 범위 검사 |
| `isValidCheck()` | 부품 조합 호환성 검사 (boolean 반환) |
| `runProducedCar()` | 조립 차량 실행, 결과 출력 |
| `testProducedCar()` | 부품 조합 테스트, PASS/FAIL 출력 |
| `fail(msg)` | FAIL + 원인 메시지 출력 헬퍼 |
| `delay(ms)` | `Thread.sleep` 래퍼 (UI 타이밍용) |
| `show*Menu()` | 각 단계 메뉴 출력 (5개) |
| `select*(a)` | 부품 선택 후 stack에 저장 (4개) |

---

## 현재 알려진 상태 및 주의사항

- **테스트 없음**: JUnit 5가 설정되어 있으나 `src/test/java/`가 비어있다.
- **isValidCheck vs testProducedCar 중복**: 호환성 규칙이 두 메서드에 각각 하드코딩되어 있어, 규칙 변경 시 양쪽 모두 수정해야 한다.
- **매직 넘버**: 부품 번호가 상수로 정의되어 있으나 일부 메서드 내부(`runProducedCar` 등)에서 리터럴로도 사용 중이다.
- **ANSI 이스케이프**: 화면 클리어(`\033[H\033[2J`)를 사용하므로 ANSI를 지원하지 않는 터미널에서는 이스케이프 코드가 그대로 출력될 수 있다.

# CLAUDE.md

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.