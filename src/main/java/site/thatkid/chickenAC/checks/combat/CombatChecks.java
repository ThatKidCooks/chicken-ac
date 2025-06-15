package site.thatkid.chickenAC.checks.combat;

public enum CombatChecks {
    KILLAURA("Kill Aura", false);

    private final String displayName;
    private boolean enabled;

    CombatChecks(String displayName, boolean enabled) {
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
