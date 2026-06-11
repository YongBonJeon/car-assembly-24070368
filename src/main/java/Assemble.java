import java.util.Optional;
import java.util.Scanner;

public class Assemble {

    private static final int CarType_Q        = 0;
    private static final int Engine_Q         = 1;
    private static final int BrakeSystem_Q    = 2;
    private static final int SteeringSystem_Q = 3;
    private static final int Run_Test         = 4;

    private static CarConfiguration config = new CarConfiguration();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int step = CarType_Q;

        while (true) {
            ConsoleUI.clearScreen();

            switch (step) {
                case CarType_Q:        ConsoleUI.showCarTypeMenu();   break;
                case Engine_Q:         ConsoleUI.showEngineMenu();    break;
                case BrakeSystem_Q:    ConsoleUI.showBrakeMenu();     break;
                case SteeringSystem_Q: ConsoleUI.showSteeringMenu();  break;
                case Run_Test:         ConsoleUI.showRunTestMenu();   break;
            }

            ConsoleUI.printInputPrompt();
            String buf = sc.nextLine().trim();

            if (buf.equalsIgnoreCase("exit")) {
                ConsoleUI.printExit();
                break;
            }

            int answer;
            try {
                answer = Integer.parseInt(buf);
            } catch (NumberFormatException e) {
                ConsoleUI.printNumberOnlyError();
                delay(800);
                continue;
            }

            if (!isValidRange(step, answer)) {
                delay(800);
                continue;
            }

            if (answer == 0) {
                if (step == Run_Test) {
                    step = CarType_Q;
                } else if (step > CarType_Q) {
                    step--;
                }
                continue;
            }

            switch (step) {
                case CarType_Q:
                    selectCarType(answer);
                    delay(800);
                    step = Engine_Q;
                    break;
                case Engine_Q:
                    selectEngine(answer);
                    delay(800);
                    step = BrakeSystem_Q;
                    break;
                case BrakeSystem_Q:
                    selectBrakeSystem(answer);
                    delay(800);
                    step = SteeringSystem_Q;
                    break;
                case SteeringSystem_Q:
                    selectSteeringSystem(answer);
                    delay(800);
                    step = Run_Test;
                    break;
                case Run_Test:
                    if (answer == 1) {
                        runProducedCar();
                        delay(2000);
                    } else if (answer == 2) {
                        ConsoleUI.printTestStart();
                        delay(1500);
                        testProducedCar();
                        delay(2000);
                    }
                    break;
            }
        }

        sc.close();
    }

    private static boolean isValidRange(int step, int ans) {
        switch (step) {
            case CarType_Q:
                if (ans < 1 || ans > 3) { ConsoleUI.printCarTypeRangeError();   return false; }
                break;
            case Engine_Q:
                if (ans < 0 || ans > 4) { ConsoleUI.printEngineRangeError();    return false; }
                break;
            case BrakeSystem_Q:
                if (ans < 0 || ans > 3) { ConsoleUI.printBrakeRangeError();     return false; }
                break;
            case SteeringSystem_Q:
                if (ans < 0 || ans > 2) { ConsoleUI.printSteeringRangeError();  return false; }
                break;
            case Run_Test:
                if (ans < 0 || ans > 2) { ConsoleUI.printRunTestRangeError();   return false; }
                break;
        }
        return true;
    }

    private static void selectCarType(int a) {
        config.setCarType(CarType.fromInt(a));
        ConsoleUI.printCarTypeSelected(config.getCarType().getDisplayName());
    }

    private static void selectEngine(int a) {
        config.setEngine(Engine.fromInt(a));
        ConsoleUI.printEngineSelected(config.getEngine().getDisplayName());
    }

    private static void selectBrakeSystem(int a) {
        config.setBrakeSystem(BrakeSystem.fromInt(a));
        ConsoleUI.printBrakeSelected(config.getBrakeSystem().getDisplayName());
    }

    private static void selectSteeringSystem(int a) {
        config.setSteeringSystem(SteeringSystem.fromInt(a));
        ConsoleUI.printSteeringSelected(config.getSteeringSystem().getDisplayName());
    }

    private static void runProducedCar() {
        if (CompatibilityValidator.validate(config).isPresent()) {
            ConsoleUI.printRunInvalid();
            return;
        }
        if (config.getEngine() == Engine.BROKEN) {
            ConsoleUI.printBrokenEngine();
            return;
        }
        ConsoleUI.printRunSuccess(config);
    }

    private static void testProducedCar() {
        Optional<String> result = CompatibilityValidator.validate(config);
        if (result.isPresent()) {
            ConsoleUI.printTestFail(result.get());
        } else {
            ConsoleUI.printTestPass();
        }
    }

    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
