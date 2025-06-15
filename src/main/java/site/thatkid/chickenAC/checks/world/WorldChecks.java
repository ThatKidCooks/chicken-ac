package site.thatkid.chickenAC.checks.world;

public enum WorldChecks {
    SCAFFOLD("Scaffold", false);

    private final String displayName;
    private boolean enabled;

    WorldChecks(String displayName, boolean enabled) {
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
