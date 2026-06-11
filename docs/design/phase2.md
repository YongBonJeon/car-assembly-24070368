# Phase 2 설계 — 도메인 모델 분리 (`CarConfiguration`)

## 목적

`int[] stack` 배열을 의미 있는 도메인 객체로 교체한다.  
Phase 1에서 Enum을 도입했으나 저장소는 여전히 `int[]`라 `getValue()` 호출이 남아있다.  
`CarConfiguration`으로 교체하면 Enum을 직접 저장·비교할 수 있어 `getValue()`가 완전히 사라진다.

---

## 현재 문제 (Before)

### 문제 1 — 인덱스 기반 접근으로 의미 파악이 어렵다

```java
private static int[] stack = new int[5];

stack[0] = a;   // 차량 타입인지 엔진인지 인덱스를 외워야 한다
stack[1] = a;
```

### 문제 2 — 아직 `getValue()` 호출이 남아있다

Phase 1 이후에도 int 저장소 때문에 비교마다 `.getValue()`가 필요하다.

```java
// isValidCheck(), testProducedCar() — 비교 시마다 getValue() 필요
if (stack[CarType_Q] == CarType.SEDAN.getValue() && ...)

// runProducedCar() — 출력 시마다 fromInt() 필요
CarType.fromInt(stack[CarType_Q]).getDisplayName()
```

### 문제 3 — step 상수가 배열 인덱스와 역할이 혼재된다

`CarType_Q = 0`은 "단계 0번"이자 "stack의 0번 인덱스"로 두 역할을 겸한다.  
Phase 2 이후에는 step 식별자 역할만 남는다.

---

## 신규 파일

```
src/main/java/
└── CarConfiguration.java   (신규)
```

---

## `CarConfiguration` 설계

```java
public class CarConfiguration {
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    public CarType getCarType()                      { return carType; }
    public void setCarType(CarType carType)          { this.carType = carType; }

    public Engine getEngine()                        { return engine; }
    public void setEngine(Engine engine)             { this.engine = engine; }

    public BrakeSystem getBrakeSystem()              { return brakeSystem; }
    public void setBrakeSystem(BrakeSystem b)        { this.brakeSystem = b; }

    public SteeringSystem getSteeringSystem()        { return steeringSystem; }
    public void setSteeringSystem(SteeringSystem s)  { this.steeringSystem = s; }
}
```

**설계 결정 사항:**
- 모든 필드는 초기값 `null`. 사용자가 단계별로 선택하면 순서대로 채워진다.
- `null` 상태가 문제가 되지 않는 이유: `isValidCheck()` / `runProducedCar()` / `testProducedCar()`는 4단계를 모두 거친 후에만 도달 가능하다 (워크플로 보장).
- `reset()` 메서드는 추가하지 않는다. 현재 "처음으로" 이동 시 기존 선택값이 유지되는 동작을 그대로 보존한다.

---

## `Assemble.java` 변경 포인트

### 전역 상태 교체

```java
// Before
private static int[] stack = new int[5];

// After
private static CarConfiguration config = new CarConfiguration();
```

`CarType_Q`, `Engine_Q`, `BrakeSystem_Q`, `SteeringSystem_Q`, `Run_Test` 상수는  
step 머신 식별자로 여전히 사용되므로 **그대로 유지**한다.

---

### `select*()` — int 저장 → Enum 저장

```java
// Before
private static void selectCarType(int a) {
    stack[CarType_Q] = a;
    System.out.printf("...", CarType.fromInt(a).getDisplayName());
}

// After
private static void selectCarType(int a) {
    config.setCarType(CarType.fromInt(a));
    System.out.printf("...", config.getCarType().getDisplayName());
}
```

`fromInt()`는 사용자 입력(int)을 Enum으로 변환하는 경계(boundary)에서만 호출된다.  
이후 내부에서는 Enum을 직접 사용한다.

나머지 3개 메서드도 동일 패턴으로 변경한다.

---

### `isValidCheck()` — `getValue()` 제거, 직접 Enum 비교

```java
// Before
if (stack[CarType_Q] == CarType.SEDAN.getValue()         && stack[BrakeSystem_Q] == BrakeSystem.CONTINENTAL.getValue()) return false;
if (stack[CarType_Q] == CarType.SUV.getValue()           && stack[Engine_Q] == Engine.TOYOTA.getValue())                return false;
if (stack[CarType_Q] == CarType.TRUCK.getValue()         && stack[Engine_Q] == Engine.WIA.getValue())                   return false;
if (stack[CarType_Q] == CarType.TRUCK.getValue()         && stack[BrakeSystem_Q] == BrakeSystem.MANDO.getValue())       return false;
if (stack[BrakeSystem_Q] == BrakeSystem.BOSCH.getValue() && stack[SteeringSystem_Q] != SteeringSystem.BOSCH.getValue()) return false;

// After
if (config.getCarType() == CarType.SEDAN         && config.getBrakeSystem() == BrakeSystem.CONTINENTAL) return false;
if (config.getCarType() == CarType.SUV           && config.getEngine() == Engine.TOYOTA)                return false;
if (config.getCarType() == CarType.TRUCK         && config.getEngine() == Engine.WIA)                   return false;
if (config.getCarType() == CarType.TRUCK         && config.getBrakeSystem() == BrakeSystem.MANDO)       return false;
if (config.getBrakeSystem() == BrakeSystem.BOSCH && config.getSteeringSystem() != SteeringSystem.BOSCH) return false;
```

---

### `testProducedCar()` — `isValidCheck()`와 동일 패턴

```java
// Before
if (stack[CarType_Q] == CarType.SEDAN.getValue() && stack[BrakeSystem_Q] == BrakeSystem.CONTINENTAL.getValue()) { ... }
// ...

// After
if (config.getCarType() == CarType.SEDAN && config.getBrakeSystem() == BrakeSystem.CONTINENTAL) { ... }
// ...
```

---

### `runProducedCar()` — `fromInt()` 제거, 직접 `getDisplayName()`

```java
// Before
if (stack[Engine_Q] == Engine.BROKEN.getValue()) { ... }
System.out.printf("Car Type : %s\n", CarType.fromInt(stack[CarType_Q]).getDisplayName());
System.out.printf("Engine   : %s\n", Engine.fromInt(stack[Engine_Q]).getDisplayName());
System.out.printf("Brake    : %s\n", BrakeSystem.fromInt(stack[BrakeSystem_Q]).getDisplayName());
System.out.printf("Steering : %s\n", SteeringSystem.fromInt(stack[SteeringSystem_Q]).getDisplayName());

// After
if (config.getEngine() == Engine.BROKEN) { ... }
System.out.printf("Car Type : %s\n", config.getCarType().getDisplayName());
System.out.printf("Engine   : %s\n", config.getEngine().getDisplayName());
System.out.printf("Brake    : %s\n", config.getBrakeSystem().getDisplayName());
System.out.printf("Steering : %s\n", config.getSteeringSystem().getDisplayName());
```

---

## Phase 2 완료 후 `fromInt()` 사용처

Phase 2가 끝나면 `fromInt()`는 `select*()` 4개 메서드에서만 호출된다.  
사용자 입력(int) → Enum 변환이 필요한 유일한 지점이기 때문이다.

```
사용자 입력(int)
    → fromInt()        select*() 내부, 경계에서 한 번만 변환
    → Enum 저장        config.set*()
    → Enum 직접 비교   isValidCheck(), testProducedCar()
    → Enum 직접 출력   runProducedCar()
```

---

## 변경하지 않는 것

| 항목 | 이유 |
|---|---|
| `CarType_Q`, `Engine_Q` 등 step 상수 | step 머신 식별자로 계속 사용됨 |
| 비즈니스 규칙 5가지의 중복 | Phase 3에서 `CompatibilityValidator`로 통합 |
| `show*Menu()` 출력 내용 | Phase 4에서 `ConsoleUI`로 분리 |
| `isValidRange()` 범위 하드코딩 | Phase 3 이후 정리 가능하나 현재 범위 아님 |

---

## 완료 기준

- [ ] `CarConfiguration.java` 생성
- [ ] `Assemble.java`에서 `int[] stack` 완전 제거
- [ ] `Assemble.java`에서 `.getValue()` 호출이 없음
- [ ] `fromInt()` 호출이 `select*()` 4개 메서드에만 존재함
- [ ] `gradlew.bat build` 성공
- [ ] 직접 실행 후 모든 메뉴 흐름 및 출력 결과가 기존과 동일함
