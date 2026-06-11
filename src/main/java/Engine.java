public enum Engine {
    GM    (1, "GM"),
    TOYOTA(2, "TOYOTA"),
    WIA   (3, "WIA"),
    BROKEN(4, "고장난 엔진");

    private final int value;
    private final String displayName;

    Engine(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() { return value; }
    public String getDisplayName() { return displayName; }

    public static Engine fromInt(int value) {
        for (Engine e : values())
            if (e.value == value) return e;
        throw new IllegalArgumentException("Invalid Engine: " + value);
    }
}
