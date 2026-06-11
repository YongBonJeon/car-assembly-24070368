public enum SteeringSystem {
    BOSCH(1, "BOSCH"),
    MOBIS(2, "MOBIS");

    private final int value;
    private final String displayName;

    SteeringSystem(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() { return value; }
    public String getDisplayName() { return displayName; }

    public static SteeringSystem fromInt(int value) {
        for (SteeringSystem s : values())
            if (s.value == value) return s;
        throw new IllegalArgumentException("Invalid SteeringSystem: " + value);
    }
}
