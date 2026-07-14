package net.gravijet.clutches.game;

/** Zustand einer Clutch-Session. */
public enum SessionState {

    /** Auf der Insel, wartet auf Start. */
    IDLE,

    /** Countdown läuft. */
    COUNTDOWN,

    /** Läuft: der Spieler fliegt und muss zurück clutchen. */
    ACTIVE
}
