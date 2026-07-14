package net.gravijet.clutches.command;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.Text;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClutchCommand implements CommandExecutor, TabCompleter {

    private static final String ADMIN = "clutches.admin";
    private static final List<String> USER_SUBS = Arrays.asList("menu", "lobby", "settings", "cosmetics", "stats", "top");
    private static final List<String> ADMIN_SUBS =
            Arrays.asList("setspawn", "setclutch", "givecoins", "reload");

    private final Clutches plugin;

    public ClutchCommand(Clutches plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "menu":
                openMenu(sender);
                break;
            case "lobby":
                lobby(sender);
                break;
            case "settings":
                withProfile(sender, (player, profile) -> plugin.settingsMenu().open(profile, player));
                break;
            case "cosmetics":
                withProfile(sender, (player, profile) -> plugin.cosmeticsMenu().open(profile, player));
                break;
            case "stats":
                withProfile(sender, (player, profile) -> plugin.statsMenu().open(profile, player));
                break;
            case "top":
                withProfile(sender, (player, profile) -> plugin.leaderboardMenu().open(profile, player));
                break;
            case "setspawn":
                admin(sender, () -> setSpawn(sender));
                break;
            case "setclutch":
                admin(sender, () -> setArena(sender, "clutch"));
                break;
            case "givecoins":
                admin(sender, () -> giveCoins(sender, args));
                break;
            case "reload":
                admin(sender, () -> reload(sender));
                break;
            default:
                help(sender);
                break;
        }
        return true;
    }

    private void openMenu(CommandSender sender) {
        withProfile(sender, (player, profile) -> plugin.mainMenu().open(profile, player));
    }

    private void lobby(CommandSender sender) {
        withProfile(sender, (player, profile) -> {
            plugin.engine().returnToLobby(profile, player);
            player.sendMessage(Text.msg("&fZurück in der Lobby."));
        });
    }

    private void setSpawn(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Text.msg("&fNur für Spieler."));
            return;
        }
        plugin.setLobbySpawn(((Player) sender).getLocation());
        sender.sendMessage(Text.msg("&fLobby-Spawn gesetzt."));
    }

    private void setArena(CommandSender sender, String type) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Text.msg("&fNur für Spieler."));
            return;
        }
        plugin.setArena(type, ((Player) sender).getLocation());
        sender.sendMessage(Text.msg("&f" + type + "-Arena an deine Position gesetzt und gebaut."));
    }

    private void giveCoins(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Text.msg("&cNutze: /clutch givecoins <Spieler> <Menge>"));
            return;
        }
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Text.msg("&cSpieler nicht gefunden."));
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(Text.msg("&cUngültige Menge."));
            return;
        }
        Profile profile = plugin.profiles().get(target);
        if (profile == null) {
            return;
        }
        profile.addCoins(amount);
        plugin.engine().applyBoard(profile, target);
        plugin.engine().updateTab(profile, target);
        target.sendMessage(Text.msg("&fDu hast &6" + amount + " Coins &ferhalten!"));
        sender.sendMessage(Text.msg("&f" + amount + " Coins an &c" + target.getName() + " &fgegeben."));
    }

    private void reload(CommandSender sender) {
        plugin.reloadConfig();
        plugin.reloadArenas();
        sender.sendMessage(Text.msg("&fKonfiguration & Arenen neu geladen."));
    }

    private void help(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(Text.color("&c&lCLUTCHES &8» &7Befehle"));
        sender.sendMessage(Text.color("&8» &f/clutch &7– Hauptmenü"));
        sender.sendMessage(Text.color("&8» &f/clutch lobby &7– zurück in die Lobby"));
        sender.sendMessage(Text.color("&8» &f/clutch settings &8| &fcosmetics &8| &fstats &8| &ftop"));
        if (sender.hasPermission(ADMIN)) {
            sender.sendMessage(Text.color("&8» &f/clutch setspawn &8| &fsetclutch"));
            sender.sendMessage(Text.color("&8» &f/clutch givecoins <Spieler> <Menge> &8| &freload"));
        }
        sender.sendMessage("");
    }

    // ------------------------------------------------------------------ helpers

    private interface PlayerAction {
        void run(Player player, Profile profile);
    }

    private void withProfile(CommandSender sender, PlayerAction action) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Text.msg("&fNur für Spieler."));
            return;
        }
        Player player = (Player) sender;
        Profile profile = plugin.profiles().get(player);
        if (profile != null) {
            action.run(player, profile);
        }
    }

    private void admin(CommandSender sender, Runnable action) {
        if (!sender.hasPermission(ADMIN)) {
            sender.sendMessage(Text.msg("&cDazu fehlt dir die Berechtigung."));
            return;
        }
        action.run();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.addAll(USER_SUBS);
            if (sender.hasPermission(ADMIN)) {
                options.addAll(ADMIN_SUBS);
            }
            return filter(options, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("givecoins")) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                options.add(player.getName());
            }
            return filter(options, args[1]);
        }
        return options;
    }

    private List<String> filter(List<String> options, String prefix) {
        List<String> result = new ArrayList<>();
        String lower = prefix.toLowerCase();
        for (String option : options) {
            if (option.toLowerCase().startsWith(lower)) {
                result.add(option);
            }
        }
        return result;
    }
}
