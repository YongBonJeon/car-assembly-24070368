import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarConfigurationTest {

    @Test
    void initialFieldsAreNull() {
        CarConfiguration c = new CarConfiguration();
        assertNull(c.getCarType());
        assertNull(c.getEngine());
        assertNull(c.getBrakeSystem());
        assertNull(c.getSteeringSystem());
    }

    @Test
    void setAndGetAllFields() {
        CarConfiguration c = new CarConfiguration();
        c.setCarType(CarType.SUV);
        c.setEngine(Engine.GM);
        c.setBrakeSystem(BrakeSystem.BOSCH);
        c.setSteeringSystem(SteeringSystem.BOSCH);

        assertEquals(CarType.SUV,         c.getCarType());
        assertEquals(Engine.GM,           c.getEngine());
        assertEquals(BrakeSystem.BOSCH,   c.getBrakeSystem());
        assertEquals(SteeringSystem.BOSCH, c.getSteeringSystem());
    }
}
