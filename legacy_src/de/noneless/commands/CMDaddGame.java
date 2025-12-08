package de.noneless.commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.noneless.Main;

public class CMDaddGame implements CommandExecutor


{
	//addGame MinispielName ArenaName Material
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("Noneless.Admin.AddGame")) {
            p.sendMessage("Du hast nicht das Recht dazu");
            return true;
        }
        if (args.length != 3) {
            p.sendMessage("/addGame MinispielName ArenaName Material");
            return true;
        }
        ArrayList<String> miniGames = new ArrayList<>(Main.MiGm.getStringList("Global.Minigames"));
        ArrayList<String> miniGameArenas = new ArrayList<>();
        if (miniGames.contains(args[0])) {
            miniGameArenas.addAll(Main.MiGm.getStringList(args[0] + ".Arenas"));
            if (miniGameArenas.contains(args[1])) {
                p.sendMessage("Arena ist bereits gelistet");
            } else {
                miniGameArenas.add(args[1]);
                Main.MiGm.set(args[0] + ".Arenas", miniGameArenas);
                int aCounter = Main.MiGm.getInt(args[0] + ".Count");
                Main.MiGm.set(args[0] + ".Count", aCounter + 1);
                Main.MiGm.set(args[0] + "." + args[1] + ".Mat", args[2]);
            }
        } else {
            p.sendMessage("Das MiniSpiel gibt es leider nicht. Das angegebene Material wird als Game Material genutzt");
            miniGames.add(args[0]);
            Main.MiGm.set("Global.Minigames", miniGames);
            Main.MiGm.set(args[0] + ".StartCommand", "Bitte hier den Befehl eingeben");
            Main.MiGm.set("Global.Count", Main.MiGm.getInt("Global.Count") + 1);
            int matValue = 3;
            if ("BED".equals(args[2])) {
                matValue = 1;
            } else if ("WOOD".equals(args[2])) {
                matValue = 2;
            }
            Main.MiGm.set(args[0] + ".Mat", matValue);
            miniGameArenas.add(args[1]);
            Main.MiGm.set(args[0] + ".Arenas", miniGameArenas);
            int aCounter = Main.MiGm.getInt(args[0] + ".Count");
            Main.MiGm.set(args[0] + ".Count", aCounter + 1);
            Main.MiGm.set(args[0] + "." + args[1] + ".Mat", args[2]);
        }
        try {
            Main.MiGm.save(Main.Minigames);
        } catch (IOException e) {
            p.sendMessage("Fehler beim Speichern: " + e.getMessage());
        }
        return true;
    }

	}
