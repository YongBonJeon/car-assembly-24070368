import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumFromIntTest {

    // CarType
    @Test
    void carType_fromInt_validValues() {
        assertEquals(CarType.SEDAN, CarType.fromInt(1));
        assertEquals(CarType.SUV,   CarType.fromInt(2));
        assertEquals(CarType.TRUCK, CarType.fromInt(3));
    }

    @Test
    void carType_fromInt_invalid_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> CarType.fromInt(0));
        assertThrows(IllegalArgumentException.class, () -> CarType.fromInt(4));
    }

    @Test
    void carType_displayNames() {
        assertEquals("Sedan", CarType.SEDAN.getDisplayName());
        assertEquals("SUV",   CarType.SUV.getDisplayName());
        assertEquals("Truck", CarType.TRUCK.getDisplayName());
    }

    // Engine
    @Test
    void engine_fromInt_validValues() {
        assertEquals(Engine.GM,     Engine.fromInt(1));
        assertEquals(Engine.TOYOTA, Engine.fromInt(2));
        assertEquals(Engine.WIA,    Engine.fromInt(3));
        assertEquals(Engine.BROKEN, Engine.fromInt(4));
    }

    @Test
    void engine_fromInt_invalid_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Engine.fromInt(0));
        assertThrows(IllegalArgumentException.class, () -> Engine.fromInt(5));
    }

    @Test
    void engine_displayNames() {
        assertEquals("GM",       Engine.GM.getDisplayName());
        assertEquals("고장난 엔진", Engine.BROKEN.getDisplayName());
    }

    // BrakeSystem
    @Test
    void brakeSystem_fromInt_validValues() {
        assertEquals(BrakeSystem.MANDO,       BrakeSystem.fromInt(1));
        assertEquals(BrakeSystem.CONTINENTAL, BrakeSystem.fromInt(2));
        assertEquals(BrakeSystem.BOSCH,       BrakeSystem.fromInt(3));
    }

    @Test
    void brakeSystem_fromInt_invalid_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> BrakeSystem.fromInt(0));
        assertThrows(IllegalArgumentException.class, () -> BrakeSystem.fromInt(4));
    }

    // SteeringSystem
    @Test
    void steeringSystem_fromInt_validValues() {
        assertEquals(SteeringSystem.BOSCH, SteeringSystem.fromInt(1));
        assertEquals(SteeringSystem.MOBIS, SteeringSystem.fromInt(2));
    }

    @Test
    void steeringSystem_fromInt_invalid_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> SteeringSystem.fromInt(0));
        assertThrows(IllegalArgumentException.class, () -> SteeringSystem.fromInt(3));
    }
}
