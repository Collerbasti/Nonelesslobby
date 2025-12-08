package de.noneless.lobby.api;

import de.noneless.lobby.scoreboard.LobbyScoreboard;

import java.util.List;

public class ScoreboardAPI {

    public static void setCustomLine(int index, String text) {
        LobbyScoreboard.setCustomLine(index, text);
    }

    public static List<String> getCustomLines() {
        return LobbyScoreboard.getCustomLines();
    }
}
