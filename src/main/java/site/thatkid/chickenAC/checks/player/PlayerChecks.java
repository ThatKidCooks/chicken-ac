package site.thatkid.chickenAC.checks.player;

public enum PlayerChecks {
    TIMER("Timer", false);

    private final String displayName;
    private boolean enabled;

    PlayerChecks(String displayName, boolean enabled) {
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
