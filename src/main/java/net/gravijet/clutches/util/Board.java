package net.gravijet.clutches.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

/**
 * Flackerfreies Sidebar-Scoreboard für 1.8.
 * Jede Zeile bekommt ein eindeutiges (unsichtbares) Entry aus Farbcodes,
 * der sichtbare Text steckt in Team-Prefix/Suffix und wird nur aktualisiert.
 */
public class Board {

    private static final int MAX_LINES = 15;

    private final Scoreboard scoreboard;
    private final Objective objective;
    private final String[] entries = new String[MAX_LINES];

    public Board(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("clutches", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(Text.color(title));

        ChatColor[] colors = ChatColor.values();
        for (int i = 0; i < MAX_LINES; i++) {
            entries[i] = colors[i].toString() + ChatColor.RESET;
        }
    }

    public Scoreboard scoreboard() {
        return scoreboard;
    }

    public void title(String title) {
        objective.setDisplayName(Text.color(title));
    }

    /** Setzt alle Zeilen (oben nach unten). Die Zeilenzahl muss konstant bleiben. */
    public void lines(List<String> lines) {
        int size = Math.min(lines.size(), MAX_LINES);
        for (int i = 0; i < size; i++) {
            String teamName = "line" + i;
            Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
                team.addEntry(entries[i]);
                objective.getScore(entries[i]).setScore(size - i);
            }
            apply(team, Text.color(lines.get(i)));
        }
    }

    private void apply(Team team, String line) {
        String prefix;
        String suffix;
        if (line.length() <= 16) {
            prefix = line;
            suffix = "";
        } else {
            prefix = line.substring(0, 16);
            String carried = ChatColor.getLastColors(prefix);
            suffix = carried + line.substring(16);
            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }
        }
        team.setPrefix(prefix);
        team.setSuffix(suffix);
    }
}
