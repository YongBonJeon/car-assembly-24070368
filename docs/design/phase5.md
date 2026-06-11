# Phase 5 설계 — JUnit 5 테스트 작성

## 목적

Phase 1~4 리팩터링으로 분리된 클래스들에 대한 단위 테스트를 작성한다.  
이후 변경 시 회귀를 자동으로 감지하는 안전망을 구축한다.

---

## 테스트 대상 범위

| 클래스 | 테스트 가능 여부 | 이유 |
|---|---|---|
| `CompatibilityValidator` | ✅ 핵심 대상 | 순수 함수, 입력/출력 명확 |
| Enum 4개 (`CarType` 등) | ✅ 대상 | `fromInt()` 유효/무효, `getDisplayName()` |
| `CarConfiguration` | ✅ 대상 | setter/getter 동작 |
| `ConsoleUI` | ❌ 제외 | 반환값 없는 출력 전용 메서드 |
| `Assemble.main()` | ❌ 제외 | Scanner 의존 인터랙티브 CLI |

---

## 테스트 파일 구조

```
src/test/java/
├── CompatibilityValidatorTest.java
├── EnumFromIntTest.java
└── CarConfigurationTest.java
```

---

## `CompatibilityValidatorTest`

### 헬퍼 메서드

테스트마다 `CarConfiguration`을 생성하는 반복을 줄이기 위한 팩토리 메서드를 정의한다.

```java
private static CarConfiguration config(CarType ct, Engine e, BrakeSystem b, SteeringSystem s) {
    CarConfiguration c = new CarConfiguration();
    c.setCarType(ct);
    c.setEngine(e);
    c.setBrakeSystem(b);
    c.setSteeringSystem(s);
    return c;
}
```

### 규칙별 테스트 케이스

#### 규칙 1 — Sedan + CONTINENTAL 불가

| 테스트 메서드 | 입력 | 기대 결과 |
|---|---|---|
| `sedanWithContinental_returnsFail` | SEDAN, GM, CONTINENTAL, BOSCH | `isPresent() == true`, 메시지 = `"Sedan에는 Continental제동장치 사용 불가"` |
| `sedanWithMando_returnsPass` | SEDAN, GM, MANDO, MOBIS | `isEmpty() == true` |

#### 규칙 2 — SUV + TOYOTA 불가

| 테스트 메서드 | 입력 | 기대 결과 |
|---|---|---|
| `suvWithToyota_returnsFail` | SUV, TOYOTA, MANDO, MOBIS | `isPresent() == true`, 메시지 = `"SUV에는 TOYOTA엔진 사용 불가"` |
| `suvWithGm_returnsPass` | SUV, GM, MANDO, MOBIS | `isEmpty() == true` |

#### 규칙 3 — Truck + WIA 불가

| 테스트 메서드 | 입력 | 기대 결과 |
|---|---|---|
| `truckWithWia_returnsFail` | TRUCK, WIA, CONTINENTAL, MOBIS | `isPresent() == true`, 메시지 = `"Truck에는 WIA엔진 사용 불가"` |
| `truckWithGm_returnsPass` | TRUCK, GM, CONTINENTAL, MOBIS | `isEmpty() == true` |

#### 규칙 4 — Truck + MANDO 불가

| 테스트 메서드 | 입력 | 기대 결과 |
|---|---|---|
| `truckWithMando_returnsFail` | TRUCK, GM, MANDO, MOBIS | `isPresent() == true`, 메시지 = `"Truck에는 Mando제동장치 사용 불가"` |
| `truckWithContinental_returnsPass` | TRUCK, GM, CONTINENTAL, MOBIS | `isEmpty() == true` |

#### 규칙 5 — BOSCH 제동장치 + BOSCH 외 조향장치 불가

| 테스트 메서드 | 입력 | 기대 결과 |
|---|---|---|
| `boschBrakeWithMobis_returnsFail` | SEDAN, GM, BOSCH, MOBIS | `isPresent() == true`, 메시지 = `"Bosch제동장치에는 Bosch조향장치 이외 사용 불가"` |
| `boschBrakeWithBosch_returnsPass` | SEDAN, GM, BOSCH, BOSCH | `isEmpty() == true` |

---

## `EnumFromIntTest`

각 Enum의 `fromInt()`와 `getDisplayName()`을 검증한다.

### `CarType`

| 테스트 메서드 | 검증 내용 |
|---|---|
| `carType_fromInt_validValues` | `fromInt(1)==SEDAN`, `fromInt(2)==SUV`, `fromInt(3)==TRUCK` |
| `carType_fromInt_invalid_throwsException` | `fromInt(0)`, `fromInt(4)` → `IllegalArgumentException` |
| `carType_displayNames` | `SEDAN→"Sedan"`, `SUV→"SUV"`, `TRUCK→"Truck"` |

### `Engine`

| 테스트 메서드 | 검증 내용 |
|---|---|
| `engine_fromInt_validValues` | `fromInt(1)==GM`, `fromInt(2)==TOYOTA`, `fromInt(3)==WIA`, `fromInt(4)==BROKEN` |
| `engine_fromInt_invalid_throwsException` | `fromInt(0)`, `fromInt(5)` → `IllegalArgumentException` |
| `engine_displayNames` | `GM→"GM"`, `BROKEN→"고장난 엔진"` |

### `BrakeSystem`

| 테스트 메서드 | 검증 내용 |
|---|---|
| `brakeSystem_fromInt_validValues` | `fromInt(1)==MANDO`, `fromInt(2)==CONTINENTAL`, `fromInt(3)==BOSCH` |
| `brakeSystem_fromInt_invalid_throwsException` | `fromInt(0)`, `fromInt(4)` → `IllegalArgumentException` |

### `SteeringSystem`

| 테스트 메서드 | 검증 내용 |
|---|---|
| `steeringSystem_fromInt_validValues` | `fromInt(1)==BOSCH`, `fromInt(2)==MOBIS` |
| `steeringSystem_fromInt_invalid_throwsException` | `fromInt(0)`, `fromInt(3)` → `IllegalArgumentException` |

---

## `CarConfigurationTest`

| 테스트 메서드 | 검증 내용 |
|---|---|
| `setAndGetAllFields` | 4개 필드 set 후 get 결과가 일치 |
| `initialFieldsAreNull` | 생성 직후 모든 필드가 `null` |

---

## 완료 기준

- [ ] `CompatibilityValidatorTest`: 5가지 규칙 각각 FAIL/PASS 케이스 (10개 테스트)
- [ ] `EnumFromIntTest`: 4개 Enum의 유효/무효 입력 및 displayName (12개 테스트)
- [ ] `CarConfigurationTest`: 초기 null, setter/getter 동작 (2개 테스트)
- [ ] `gradlew.bat test` 전체 통과
