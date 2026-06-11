public enum BrakeSystem {
    MANDO      (1, "MANDO"),
    CONTINENTAL(2, "CONTINENTAL"),
    BOSCH      (3, "BOSCH");

    private final int value;
    private final String displayName;

    BrakeSystem(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() { return value; }
    public String getDisplayName() { return displayName; }

    public static BrakeSystem fromInt(int value) {
        for (BrakeSystem b : values())
            if (b.value == value) return b;
        throw new IllegalArgumentException("Invalid BrakeSystem: " + value);
    }
}
