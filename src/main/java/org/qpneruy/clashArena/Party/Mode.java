package org.qpneruy.clashArena.Party;

public enum Mode {
    SOLO(1), DUO(2), TRIO(3), SQUAD(4);
    private final int requiredSize;
    Mode(int requiredSize) {
        this.requiredSize = requiredSize;
    }
    public int getRequiredSize() {
        return requiredSize;
    }
}
