package de.noneless.lobby.commands;

import de.noneless.lobby.Main;
import de.noneless.lobby.news.NewsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CMDAddNews implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nonelesslobby.news.add")) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für diesen Command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Verwendung: /addnews <text>");
            return true;
        }

        String raw = String.join(" ", args).trim();
        if (raw.startsWith("\"") && raw.endsWith("\"")) {
            raw = raw.substring(1, raw.length()-1).trim();
        }

        de.noneless.lobby.news.NewsManager nm = Main.getInstance().getNewsManager();
        int id = nm.addNews(raw);
        if (id > 0) {
            sender.sendMessage(ChatColor.GREEN + "News hinzugefügt (id: " + id + ").");
        } else {
            sender.sendMessage(ChatColor.RED + "News konnte nicht hinzugefügt werden.");
        }
        return true;
    }
}
