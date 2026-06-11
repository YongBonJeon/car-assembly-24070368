# Phase 4 설계 — UI 분리 (`ConsoleUI`)

## 목적

`Assemble.java`에 혼재된 모든 출력 로직을 `ConsoleUI`로 분리한다.  
Phase 4 완료 후 `Assemble.java`는 루프·입력·단계 전환·상태 관리만 담당하고  
`System.out` 호출을 직접 갖지 않는다.

---

## 현재 문제 (Before)

`Assemble.java` 내 `System.out` 호출 위치:

| 위치 | 출력 내용 | 성격 |
|---|---|---|
| `main()` | 화면 클리어, `INPUT >`, `바이바이`, `ERROR :: 숫자만...`, `Test...` | UI |
| `show*Menu()` 5개 | 각 단계 메뉴 텍스트 | UI |
| `isValidRange()` | 5가지 범위 오류 메시지 | UI |
| `select*()` 4개 | 선택 확인 메시지 | UI |
| `runProducedCar()` | 동작 불가/고장/정상 결과 출력 | UI |
| `testProducedCar()` | PASS 메시지 | UI |
| `fail()` | FAIL + 에러 메시지 | UI |

---

## 신규 파일

```
src/main/java/
└── ConsoleUI.java   (신규)
```

---

## `ConsoleUI` 설계

모든 메서드는 `static`. `Assemble`과 동일하게 인스턴스 불필요.  
`CLEAR_SCREEN` 상수도 `ConsoleUI` 내부로 이동한다.

```java
public class ConsoleUI {

    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    // 화면 제어
    static void clearScreen()          // ANSI 클리어 + flush
    static void printInputPrompt()     // "INPUT > "

    // 메뉴
    static void showCarTypeMenu()
    static void showEngineMenu()
    static void showBrakeMenu()
    static void showSteeringMenu()
    static void showRunTestMenu()

    // 오류
    static void printNumberOnlyError()     // "ERROR :: 숫자만 입력 가능"
    static void printCarTypeRangeError()   // "ERROR :: 차량 타입은 1 ~ 3 ..."
    static void printEngineRangeError()    // "ERROR :: 엔진은 1 ~ 4 ..."
    static void printBrakeRangeError()     // "ERROR :: 제동장치는 1 ~ 3 ..."
    static void printSteeringRangeError()  // "ERROR :: 조향장치는 1 ~ 2 ..."
    static void printRunTestRangeError()   // "ERROR :: Run 또는 Test ..."

    // 선택 확인
    static void printCarTypeSelected(String displayName)
    static void printEngineSelected(String displayName)
    static void printBrakeSelected(String displayName)
    static void printSteeringSelected(String displayName)

    // RUN 결과
    static void printRunInvalid()                    // "자동차가 동작되지 않습니다"
    static void printBrokenEngine()                  // "엔진이 고장나있습니다." + "자동차가 움직이지 않습니다."
    static void printRunSuccess(CarConfiguration config) // 스펙 출력 + "자동차가 동작됩니다."

    // Test 결과
    static void printTestStart()                     // "Test..."
    static void printTestPass()                      // "자동차 부품 조합 테스트 결과 : PASS"
    static void printTestFail(String errorMessage)   // "... : FAIL" + 원인

    // 기타
    static void printExit()  // "바이바이"
}
```

---

## `Assemble.java` 변경 포인트

### `main()` — 출력 호출 교체

```java
// Before
System.out.print(CLEAR_SCREEN);
System.out.flush();
// ...
System.out.print("INPUT > ");
// ...
System.out.println("바이바이");
// ...
System.out.println("ERROR :: 숫자만 입력 가능");
// ...
System.out.println("Test...");

// After
ConsoleUI.clearScreen();
// ...
ConsoleUI.printInputPrompt();
// ...
ConsoleUI.printExit();
// ...
ConsoleUI.printNumberOnlyError();
// ...
ConsoleUI.printTestStart();
```

### `show*Menu()` 5개 — 완전 이동 후 제거

`Assemble.java`의 `show*Menu()` 5개 메서드를 `ConsoleUI`로 그대로 이동한다.  
`Assemble.java`에서는 메서드 정의를 제거하고 `ConsoleUI.show*Menu()` 호출로 교체한다.

```java
// Before (Assemble.java 내 switch)
case CarType_Q: showCarTypeMenu(); break;

// After
case CarType_Q: ConsoleUI.showCarTypeMenu(); break;
```

### `isValidRange()` — 오류 출력 교체

로직(범위 검사)은 유지하고 출력만 `ConsoleUI`로 위임한다.

```java
// Before
if (ans < 1 || ans > 3) {
    System.out.println("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
    return false;
}

// After
if (ans < 1 || ans > 3) {
    ConsoleUI.printCarTypeRangeError();
    return false;
}
```

### `select*()` — 출력 교체

```java
// Before
private static void selectCarType(int a) {
    config.setCarType(CarType.fromInt(a));
    System.out.printf("차량 타입으로 %s을 선택하셨습니다.\n", config.getCarType().getDisplayName());
}

// After
private static void selectCarType(int a) {
    config.setCarType(CarType.fromInt(a));
    ConsoleUI.printCarTypeSelected(config.getCarType().getDisplayName());
}
```

나머지 3개도 동일 패턴으로 교체한다.

### `runProducedCar()` — 출력 교체

```java
// Before
if (CompatibilityValidator.validate(config).isPresent()) {
    System.out.println("자동차가 동작되지 않습니다");
    return;
}
if (config.getEngine() == Engine.BROKEN) {
    System.out.println("엔진이 고장나있습니다.");
    System.out.println("자동차가 움직이지 않습니다.");
    return;
}
System.out.printf("Car Type : %s\n", config.getCarType().getDisplayName());
// ...
System.out.println("자동차가 동작됩니다.");

// After
if (CompatibilityValidator.validate(config).isPresent()) {
    ConsoleUI.printRunInvalid();
    return;
}
if (config.getEngine() == Engine.BROKEN) {
    ConsoleUI.printBrokenEngine();
    return;
}
ConsoleUI.printRunSuccess(config);
```

### `testProducedCar()` — 출력 교체

```java
// Before
Optional<String> result = CompatibilityValidator.validate(config);
if (result.isPresent()) {
    fail(result.get());
} else {
    System.out.println("자동차 부품 조합 테스트 결과 : PASS");
}

// After
Optional<String> result = CompatibilityValidator.validate(config);
if (result.isPresent()) {
    ConsoleUI.printTestFail(result.get());
} else {
    ConsoleUI.printTestPass();
}
```

### `fail()` — 완전 제거

```java
// 제거 대상
private static void fail(String msg) {
    System.out.println("자동차 부품 조합 테스트 결과 : FAIL");
    System.out.println(msg);
}
```

순수 출력 메서드였으므로 `ConsoleUI.printTestFail()`로 역할이 완전히 이전된다.

### `CLEAR_SCREEN` 상수 — 이동

```java
// Assemble.java에서 제거
private static final String CLEAR_SCREEN = "\033[H\033[2J";

// ConsoleUI.java로 이동
private static final String CLEAR_SCREEN = "\033[H\033[2J";
```

---

## 변경하지 않는 것

| 항목 | 이유 |
|---|---|
| step 상수 및 step 머신 흐름 | 워크플로 제어 로직, UI 아님 |
| `select*()` 내 `config.set*()` 호출 | 상태 관리 로직 |
| `runProducedCar()` 내 유효성·고장 분기 | 비즈니스 로직 |
| `testProducedCar()` 내 `CompatibilityValidator` 호출 | 비즈니스 로직 |
| `isValidRange()` 범위 검사 조건 | 입력 검증 로직 |
| `delay()` | 타이밍 유틸리티 |

---

## 완료 기준

- [ ] `ConsoleUI.java` 생성
- [ ] `Assemble.java`에 `System.out` 호출이 없음
- [ ] `Assemble.java`에서 `show*Menu()` 5개 메서드 정의 제거
- [ ] `Assemble.java`에서 `fail()` 메서드 제거
- [ ] `CLEAR_SCREEN` 상수가 `ConsoleUI`로 이동
- [ ] `gradlew.bat build` 성공
- [ ] 직접 실행 후 모든 출력이 기존과 동일함
