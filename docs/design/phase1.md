# Phase 1 설계 — 상수를 Enum으로 전환

## 목적

`Assemble.java`에 흩어진 int 상수와 메서드 내부의 매직 리터럴을 Java Enum으로 대체한다.  
`stack[]` 배열은 이번 Phase에서 건드리지 않는다 (Phase 2 범위).

---

## 현재 문제 (Before)

### 문제 1 — int 상수는 타입 안전성이 없다

```java
private static final int SEDAN = 1, SUV = 2, TRUCK = 3;
private static final int GM = 1, TOYOTA = 2, WIA = 3;       // GM과 SEDAN이 둘 다 1
private static final int MANDO = 1, CONTINENTAL = 2, BOSCH_B = 3;
private static final int BOSCH_S = 1, MOBIS = 2;
```

`SEDAN == GM == MANDO == BOSCH_S == 1`이다. 컴파일러가 잘못된 조합을 잡아줄 수 없다.

### 문제 2 — 매직 리터럴이 메서드 내부에 혼용된다

```java
// runProducedCar() — BROKEN 엔진에 대한 상수 없이 리터럴 4 사용
if (stack[Engine_Q] == 4) { ... }

// runProducedCar() — 이름 배열과 ternary가 혼용
String[] carNames = {"", "Sedan", "SUV", "Truck"};
stack[BrakeSystem_Q]==1? "Mando": stack[BrakeSystem_Q]==2? "Continental":"Bosch"

// select*() — ternary 체인으로 이름 매핑
String name = a == 1 ? "GM" : a == 2 ? "TOYOTA" : a == 3 ? "WIA" : "고장난 엔진";
```

---

## 신규 파일

```
src/main/java/
├── CarType.java          (신규)
├── Engine.java           (신규)
├── BrakeSystem.java      (신규)
└── SteeringSystem.java   (신규)
```

패키지 없이 기존 `Assemble.java`와 동일한 위치에 생성한다.

---

## Enum 상세 설계

모든 Enum은 동일한 구조를 따른다: `value`(사용자 입력 int), `displayName`(출력용 문자열), `fromInt()` 팩토리 메서드.

### `CarType`

```java
public enum CarType {
    SEDAN(1, "Sedan"),
    SUV  (2, "SUV"),
    TRUCK(3, "Truck");

    private final int value;
    private final String displayName;

    CarType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() { return value; }
    public String getDisplayName() { return displayName; }

    public static CarType fromInt(int value) {
        for (CarType t : values())
            if (t.value == value) return t;
        throw new IllegalArgumentException("Invalid CarType: " + value);
    }
}
```

### `Engine`

```java
public enum Engine {
    GM    (1, "GM"),
    TOYOTA(2, "TOYOTA"),
    WIA   (3, "WIA"),
    BROKEN(4, "고장난 엔진");   // 기존 코드에서 상수 없이 리터럴 4로만 쓰이던 값

    private final int value;
    private final String displayName;

    Engine(int value, String displayName) { ... }

    public int getValue() { return value; }
    public String getDisplayName() { return displayName; }

    public static Engine fromInt(int value) { ... }
}
```

### `BrakeSystem`

```java
public enum BrakeSystem {
    MANDO      (1, "MANDO"),
    CONTINENTAL(2, "CONTINENTAL"),
    BOSCH      (3, "BOSCH");   // 기존 상수명 BOSCH_B에서 변경 (_B 접미사 불필요)

    ...
}
```

> `_B` 접미사는 같은 클래스에서 `BOSCH_S`와 구분하기 위한 것이었다.  
> Enum 클래스가 분리되므로 `BrakeSystem.BOSCH`와 `SteeringSystem.BOSCH`는 명확히 구분된다.

### `SteeringSystem`

```java
public enum SteeringSystem {
    BOSCH(1, "BOSCH"),   // 기존 상수명 BOSCH_S에서 변경
    MOBIS(2, "MOBIS");

    ...
}
```

---

## `Assemble.java` 변경 포인트

### 제거

```java
// 아래 4줄 완전 제거
private static final int SEDAN = 1, SUV = 2, TRUCK = 3;
private static final int GM = 1, TOYOTA = 2, WIA = 3;
private static final int MANDO = 1, CONTINENTAL = 2, BOSCH_B = 3;
private static final int BOSCH_S = 1, MOBIS = 2;
```

---

### `selectCarType()` — ternary 체인 → `fromInt()`

```java
// Before
System.out.printf("차량 타입으로 %s을 선택하셨습니다.\n",
    a == 1 ? "Sedan" : a == 2 ? "SUV" : "Truck");

// After
System.out.printf("차량 타입으로 %s을 선택하셨습니다.\n",
    CarType.fromInt(a).getDisplayName());
```

### `selectEngine()` — ternary 체인 → `fromInt()`

```java
// Before
String name = a == 1 ? "GM" : a == 2 ? "TOYOTA" : a == 3 ? "WIA" : "고장난 엔진";

// After
String name = Engine.fromInt(a).getDisplayName();
```

### `selectBrakeSystem()` — ternary 체인 → `fromInt()`

```java
// Before
String name = a == 1 ? "MANDO" : a == 2 ? "CONTINENTAL" : "BOSCH";

// After
String name = BrakeSystem.fromInt(a).getDisplayName();
```

### `selectSteeringSystem()` — ternary 체인 → `fromInt()`

```java
// Before
String name = a == 1 ? "BOSCH" : "MOBIS";

// After
String name = SteeringSystem.fromInt(a).getDisplayName();
```

---

### `isValidCheck()` — int 상수 → Enum 상수

```java
// Before
if (stack[CarType_Q] == SEDAN      && stack[BrakeSystem_Q] == CONTINENTAL) return false;
if (stack[CarType_Q] == SUV        && stack[Engine_Q] == TOYOTA)           return false;
if (stack[CarType_Q] == TRUCK      && stack[Engine_Q] == WIA)              return false;
if (stack[CarType_Q] == TRUCK      && stack[BrakeSystem_Q] == MANDO)       return false;
if (stack[BrakeSystem_Q] == BOSCH_B && stack[SteeringSystem_Q] != BOSCH_S) return false;

// After
if (stack[CarType_Q] == CarType.SEDAN.getValue()           && stack[BrakeSystem_Q] == BrakeSystem.CONTINENTAL.getValue()) return false;
if (stack[CarType_Q] == CarType.SUV.getValue()             && stack[Engine_Q] == Engine.TOYOTA.getValue())                return false;
if (stack[CarType_Q] == CarType.TRUCK.getValue()           && stack[Engine_Q] == Engine.WIA.getValue())                   return false;
if (stack[CarType_Q] == CarType.TRUCK.getValue()           && stack[BrakeSystem_Q] == BrakeSystem.MANDO.getValue())       return false;
if (stack[BrakeSystem_Q] == BrakeSystem.BOSCH.getValue()   && stack[SteeringSystem_Q] != SteeringSystem.BOSCH.getValue()) return false;
```

### `testProducedCar()` — int 상수 → Enum 상수 (isValidCheck와 동일 패턴)

`isValidCheck()`와 동일하게 교체한다.

---

### `runProducedCar()` — 리터럴 4 및 배열/ternary → Enum

```java
// Before
if (stack[Engine_Q] == 4) { ... }

String[] carNames = {"", "Sedan", "SUV", "Truck"};
String[] engNames = {"", "GM", "TOYOTA", "WIA"};
System.out.printf("Car Type : %s\n", carNames[stack[CarType_Q]]);
System.out.printf("Engine   : %s\n", engNames[stack[Engine_Q]]);
System.out.printf("Brake    : %s\n", stack[BrakeSystem_Q]==1? "Mando": stack[BrakeSystem_Q]==2? "Continental":"Bosch");
System.out.printf("Steering : %s\n", stack[SteeringSystem_Q]==1? "Bosch":"Mobis");

// After
if (stack[Engine_Q] == Engine.BROKEN.getValue()) { ... }

System.out.printf("Car Type : %s\n", CarType.fromInt(stack[CarType_Q]).getDisplayName());
System.out.printf("Engine   : %s\n", Engine.fromInt(stack[Engine_Q]).getDisplayName());
System.out.printf("Brake    : %s\n", BrakeSystem.fromInt(stack[BrakeSystem_Q]).getDisplayName());
System.out.printf("Steering : %s\n", SteeringSystem.fromInt(stack[SteeringSystem_Q]).getDisplayName());
```

---

## 변경하지 않는 것

| 항목 | 이유 |
|---|---|
| `int[] stack` | Phase 2에서 `CarConfiguration` 객체로 교체 |
| `CarType_Q`, `Engine_Q` 등 step 상수 | 워크플로 제어용, 도메인 개념 아님 |
| `isValidRange()` 범위 하드코딩 | Phase 2 이후 `values().length` 활용 가능하나 현재 범위 아님 |
| `show*Menu()` 출력 내용 | Phase 4에서 `ConsoleUI`로 분리 |
| 비즈니스 규칙 중복 | Phase 3에서 `CompatibilityValidator`로 통합 |

---

## 완료 기준

- [ ] `CarType`, `Engine`, `BrakeSystem`, `SteeringSystem` Enum 4개 생성
- [ ] `Assemble.java`에서 `private static final int SEDAN ...` 등 4줄 완전 제거
- [ ] `Assemble.java`에 `== 1`, `== 2`, `== 3`, `== 4` 형태의 부품 비교 리터럴이 없음
- [ ] `runProducedCar()`의 `carNames[]`, `engNames[]` 배열 제거
- [ ] `gradlew.bat build` 성공
- [ ] 직접 실행 후 모든 메뉴 흐름 및 출력 결과가 기존과 동일함
