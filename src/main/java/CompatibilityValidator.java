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
