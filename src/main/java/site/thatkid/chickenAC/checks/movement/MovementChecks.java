package site.thatkid.chickenAC.checks.movement;

public enum MovementChecks {
    FLIGHT("Flight", false),
    SPEED("Speed", false);

    private final String displayName;
    private boolean enabled;

    MovementChecks(String displayName, boolean enabled) {
        this.displayName = displayName;
        this.enabled = enabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }
}
