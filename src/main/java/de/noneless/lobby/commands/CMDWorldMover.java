package de.noneless.lobby.commands;

import de.noneless.lobby.world.WorldMoverService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class CMDWorldMover implements CommandExecutor, TabCompleter {

    private final WorldMoverService worldMoverService;

    public CMDWorldMover(WorldMoverService worldMoverService) {
        this.worldMoverService = Objects.requireNonNull(worldMoverService, "worldMoverService");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nonelesslobby.worldmover")) {
            sender.sendMessage(ChatColor.RED + "Daf\u00fcr hast du keine Rechte.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }

        String primary = args[0].toLowerCase(Locale.ROOT);
        switch (primary) {
            case "status":
                worldMoverService.describeActiveJob()
                        .ifPresentOrElse(sender::sendMessage,
                                () -> sender.sendMessage(ChatColor.YELLOW + "Es l\u00e4uft aktuell kein WorldMover-Auftrag."));
                return true;
            case "cancel":
                if (worldMoverService.cancelActiveJob("Manuell gestoppt durch " + sender.getName())) {
                    sender.sendMessage(ChatColor.RED + "WorldMover wird gestoppt...");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Kein aktiver Auftrag zum Stoppen gefunden.");
                }
                return true;
            default:
                handleStartCommand(sender, label, args);
                return true;
        }
    }

    private void handleStartCommand(CommandSender sender, String label, String[] args) {
        Direction direction = Direction.fromInput(args[0]);
        if (direction == null) {
            sender.sendMessage(ChatColor.RED + "Unbekannte Richtung '" + args[0] + "'. Erwartet: UP oder DOWN.");
            sendUsage(sender, label);
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Bitte gib die Blockanzahl an.");
            sendUsage(sender, label);
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Die Blockanzahl muss eine Zahl sein.");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Die Blockanzahl muss positiv sein.");
            return;
        }

        World world = resolveWorld(sender, args.length >= 3 ? args[2] : null);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "Konnte keine Welt finden.");
            return;
        }

        int offset = direction == Direction.UP ? amount : -amount;
        try {
            worldMoverService.startMove(sender, world, offset);
            sender.sendMessage(ChatColor.GREEN + "WorldMover wurde f\u00fcr '" + world.getName() + "' gestartet. Der Prozess l\u00e4uft asynchron im Hintergrund.");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        }
    }

    private World resolveWorld(CommandSender sender, String worldName) {
        if (worldName != null && !worldName.isEmpty()) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "Die Welt '" + worldName + "' existiert nicht.");
            }
            return world;
        }

        if (sender instanceof Player player) {
            return player.getWorld();
        }

        return Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.YELLOW + "Verwendung: /" + label + " <UP|DOWN> <bl\u00f6cke> [welt]");
        sender.sendMessage(ChatColor.YELLOW + "Weitere Unterbefehle: /" + label + " status | /" + label + " cancel");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("nonelesslobby.worldmover")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> options = Arrays.asList("up", "down", "status", "cancel");
            return StringUtil.copyPartialMatches(args[0], options, new ArrayList<>());
        }

        if (args.length == 2 && Direction.fromInput(args[0]) != null) {
            List<String> suggestions = Arrays.asList("5", "10", "25", "50", "100");
            return StringUtil.copyPartialMatches(args[1], suggestions, new ArrayList<>());
        }

        if (args.length == 3 && Direction.fromInput(args[0]) != null) {
            List<String> worlds = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args[2], worlds, new ArrayList<>());
        }

        return Collections.emptyList();
    }

    private enum Direction {
        UP,
        DOWN;

        static Direction fromInput(String raw) {
            if (raw == null) {
                return null;
            }
            switch (raw.toLowerCase(Locale.ROOT)) {
                case "up":
                case "oben":
                case "hoch":
                    return UP;
                case "down":
                case "unten":
                case "runter":
                    return DOWN;
                default:
                    return null;
            }
        }
    }
}
