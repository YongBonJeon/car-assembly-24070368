import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CompatibilityValidatorTest {

    private static CarConfiguration config(CarType ct, Engine e, BrakeSystem b, SteeringSystem s) {
        CarConfiguration c = new CarConfiguration();
        c.setCarType(ct);
        c.setEngine(e);
        c.setBrakeSystem(b);
        c.setSteeringSystem(s);
        return c;
    }

    // 규칙 1 — Sedan + CONTINENTAL 불가
    @Test
    void sedanWithContinental_returnsFail() {
        Optional<String> result = CompatibilityValidator.validate(
                config(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH));
        assertTrue(result.isPresent());
        assertEquals("Sedan에는 Continental제동장치 사용 불가", result.get());
    }

    @Test
    void sedanWithMando_returnsPass() {
        Optional<String> result = CompatibilityValidator.validate(
                config(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.MOBIS));
        assertTrue(result.isEmpty());
    }

    // 규칙 2 — SUV + TOYOTA 불가
    @Test
    void suvWithToyota_returnsFail() {
        Optional<String> result = CompatibilityValidator.validate(
                config(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.MOBIS));
        assertTrue(result.isPresent());
        assertEquals("SUV에는 TOYOTA엔진 사용 불가", result.get());
    }

    @Test
    void suvWithGm_returnsPass() {
        Optional<String> result = CompatibilityValidator.validate(
                config(CarType.SUV, Engine.GM, BrakeSystem.MANDO, SteeringSystem.MOBIS));
        assertTrue(result.isEmpty());
    }

    // 규칙 3 — Truck + WIA 불가
    @Test
    void truckWithWia_returnsFail() {
        Optional<String> result = CompatibilityValidator.validate(
                config(CarType.TRUCK, Engine.WIA, BrakeSystem.CONTINENTAL, SteeringSystem.MOBIS));
        assertTrue(result.isPresent());
        assertEquals("Truck에는 WIA엔진 사용 불가", result.get());
    }

    @Test
    void truckWithGm_returnsPass() {
        Optional<String> result = CompatibilityValidator.validate(
                config(CarType.TRUCK, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.MOBIS));
        assertTrue(result.isEmpty());
    }

    // 규칙 4 — Truck + MANDO 불가
    @Test
    void truckWithMando_returnsFail() {
        Optional<String> result = CompatibilityValidator.validate(
                config(CarType.TRUCK, Engine.GM, BrakeSystem.MANDO, SteeringSystem.MOBIS));
        assertTrue(result.isPresent());
        assertEquals("Truck에는 Mando제동장치 사용 불가", result.get());
    }

    @Test
    void truckWithContinental_returnsPass() {
        Optional<String> result = CompatibilityValidator.validate(
                config(CarType.TRUCK, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.MOBIS));
        assertTrue(result.isEmpty());
    }

    // 규칙 5 — BOSCH 제동장치 + BOSCH 외 조향장치 불가
    @Test
    void boschBrakeWithMobis_returnsFail() {
        Optional<String> result = CompatibilityValidator.validate(
                config(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.MOBIS));
        assertTrue(result.isPresent());
        assertEquals("Bosch제동장치에는 Bosch조향장치 이외 사용 불가", result.get());
    }

    @Test
    void boschBrakeWithBosch_returnsPass() {
        Optional<String> result = CompatibilityValidator.validate(
                config(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH));
        assertTrue(result.isEmpty());
    }
}
