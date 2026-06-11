public class ConsoleUI {

    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    // 화면 제어
    static void clearScreen() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }

    static void printInputPrompt() {
        System.out.print("INPUT > ");
    }

    // 메뉴
    static void showCarTypeMenu() {
        System.out.println("        ______________");
        System.out.println("       /|            |");
        System.out.println("  ____/_|_____________|____");
        System.out.println(" |                      O  |");
        System.out.println(" '-(@)----------------(@)--'");
        System.out.println("===============================");
        System.out.println("어떤 차량 타입을 선택할까요?");
        System.out.println("1. Sedan");
        System.out.println("2. SUV");
        System.out.println("3. Truck");
        System.out.println("===============================");
    }

    static void showEngineMenu() {
        System.out.println("어떤 엔진을 탑재할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. GM");
        System.out.println("2. TOYOTA");
        System.out.println("3. WIA");
        System.out.println("4. 고장난 엔진");
        System.out.println("===============================");
    }

    static void showBrakeMenu() {
        System.out.println("어떤 제동장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. MANDO");
        System.out.println("2. CONTINENTAL");
        System.out.println("3. BOSCH");
        System.out.println("===============================");
    }

    static void showSteeringMenu() {
        System.out.println("어떤 조향장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. BOSCH");
        System.out.println("2. MOBIS");
        System.out.println("===============================");
    }

    static void showRunTestMenu() {
        System.out.println("멋진 차량이 완성되었습니다.");
        System.out.println("어떤 동작을 할까요?");
        System.out.println("0. 처음 화면으로 돌아가기");
        System.out.println("1. RUN");
        System.out.println("2. Test");
        System.out.println("===============================");
    }

    // 오류
    static void printNumberOnlyError() {
        System.out.println("ERROR :: 숫자만 입력 가능");
    }

    static void printCarTypeRangeError() {
        System.out.println("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
    }

    static void printEngineRangeError() {
        System.out.println("ERROR :: 엔진은 1 ~ 4 범위만 선택 가능");
    }

    static void printBrakeRangeError() {
        System.out.println("ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능");
    }

    static void printSteeringRangeError() {
        System.out.println("ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능");
    }

    static void printRunTestRangeError() {
        System.out.println("ERROR :: Run 또는 Test 중 하나를 선택 필요");
    }

    // 선택 확인
    static void printCarTypeSelected(String displayName) {
        System.out.printf("차량 타입으로 %s을 선택하셨습니다.\n", displayName);
    }

    static void printEngineSelected(String displayName) {
        System.out.printf("%s 엔진을 선택하셨습니다.\n", displayName);
    }

    static void printBrakeSelected(String displayName) {
        System.out.printf("%s 제동장치를 선택하셨습니다.\n", displayName);
    }

    static void printSteeringSelected(String displayName) {
        System.out.printf("%s 조향장치를 선택하셨습니다.\n", displayName);
    }

    // RUN 결과
    static void printRunInvalid() {
        System.out.println("자동차가 동작되지 않습니다");
    }

    static void printBrokenEngine() {
        System.out.println("엔진이 고장나있습니다.");
        System.out.println("자동차가 움직이지 않습니다.");
    }

    static void printRunSuccess(CarConfiguration config) {
        System.out.printf("Car Type : %s\n", config.getCarType().getDisplayName());
        System.out.printf("Engine   : %s\n", config.getEngine().getDisplayName());
        System.out.printf("Brake    : %s\n", config.getBrakeSystem().getDisplayName());
        System.out.printf("Steering : %s\n", config.getSteeringSystem().getDisplayName());
        System.out.println("자동차가 동작됩니다.");
    }

    // Test 결과
    static void printTestStart() {
        System.out.println("Test...");
    }

    static void printTestPass() {
        System.out.println("자동차 부품 조합 테스트 결과 : PASS");
    }

    static void printTestFail(String errorMessage) {
        System.out.println("자동차 부품 조합 테스트 결과 : FAIL");
        System.out.println(errorMessage);
    }

    // 기타
    static void printExit() {
        System.out.println("바이바이");
    }
}
