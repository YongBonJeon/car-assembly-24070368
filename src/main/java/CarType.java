public enum CarType {
    SEDAN(1, "Sedan"),
    SUV  (2, "SUV"),
    TRUCK(3, "Truck");

    private final int value;
    private final String displayName;

    CarType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() { return value; }
    public String getDisplayName() { return displayName; }

    public static CarType fromInt(int value) {
        for (CarType t : values())
            if (t.value == value) return t;
        throw new IllegalArgumentException("Invalid CarType: " + value);
    }
}
