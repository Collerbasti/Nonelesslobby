package de.noneless.lobby.commands;

import de.noneless.lobby.Main;
import de.noneless.lobby.news.NewsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CMDNews implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        NewsManager nm = Main.getInstance().getNewsManager();
        if (nm == null) {
            sender.sendMessage("§cNewsManager nicht verfügbar.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            // list news
            if (!sender.hasPermission("nonelesslobby.news.view")) {
                sender.sendMessage("§cKeine Berechtigung.");
                return true;
            }

            java.util.LinkedHashMap<Integer, String> entries = nm.getNewsEntries();
            if (entries.isEmpty()) {
                sender.sendMessage("§7Keine News vorhanden.");
                return true;
            }
            sender.sendMessage("§6=== News Liste ===");
            for (java.util.Map.Entry<Integer, String> e : entries.entrySet()) {
                int nid = e.getKey();
                String text = e.getValue();
                if (sender instanceof Player p) {
                    Component line = Component.text("[" + nid + "] ").color(NamedTextColor.GOLD)
                            .append(Component.text(text).color(NamedTextColor.WHITE))
                            .append(Component.text(" "))
                            .append(Component.text("[Löschen]").color(NamedTextColor.RED)
                                    .decorate(TextDecoration.BOLD)
                                    .clickEvent(ClickEvent.runCommand("/news del " + nid)));
                    p.sendMessage(line);
                } else if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("[" + nid + "] " + text + "  (use /news del " + nid + ")");
                } else {
                    sender.sendMessage("[" + nid + "] " + text);
                }
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("nonelesslobby.news.delete")) {
                sender.sendMessage("§cKeine Berechtigung zum Löschen.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("§eVerwendung: /news del <id>");
                return true;
            }
            try {
                int id = Integer.parseInt(args[1]);
                if (!nm.deleteNewsById(id)) {
                    sender.sendMessage("§cNews mit dieser ID nicht gefunden.");
                    return true;
                }
                sender.sendMessage("§aNews gelöscht (id: " + id + ").");
                return true;
            } catch (NumberFormatException ex) {
                sender.sendMessage("§cUngültige ID (muss eine Zahl sein).");
                return true;
            }
        }

        sender.sendMessage("§eVerwendung: /news list | /news del <id>");
        return true;
    }
}
