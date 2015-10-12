package net.technicpack.barcraft.impl.playerdata;

public class AccessDiff {
    private DiffType type;
    private String action;

    public AccessDiff(DiffType type, String action) {
        this.type = type;
        this.action = action;
    }

    public DiffType getType() { return type; }
    public String getAction() { return action; }
}
