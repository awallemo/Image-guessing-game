package no.uis.imagegame;

public enum MultiplayerStatus {
    HOST_TURN(0),
    GUESSING(1),
    WAITING_HOST(2),
    WONGAME(3);

    private int value;

    private MultiplayerStatus(int v) {
        value = v;
    }
    public int getValue() {
        return value;
    }
}
