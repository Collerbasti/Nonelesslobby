package de.noneless.commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
		
	public class CMDaddItem implements CommandExecutor  
	{
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
				return true;
			}
			Player p = (Player) sender;
			if (args.length != 1) {
				p.sendMessage("§cVerwendung: /additem <Preis>");
				return true;
			}
			if (!p.hasPermission("Noneless.addItem")) {
				p.sendMessage("§cDu darfst diesen Befehl nicht benutzen");
				return true;
			}
			int count = de.noneless.Main.shp.getInt("Items.Count");
			ArrayList<String> itemList = new ArrayList<>(de.noneless.Main.shp.getStringList("Items.List"));
			String itemType = p.getInventory().getItemInMainHand().getType().toString();
			de.noneless.Main.shp.set(itemType + ".ShopsPreise", args[0]);
			itemList.add(itemType);
			de.noneless.Main.shp.set("Items.List", itemList);
			de.noneless.Main.shp.set("Items.Count", count + 1);
			try {
				de.noneless.Main.shp.save(de.noneless.Main.ShopsPreise);
				p.sendMessage("Item wurde gespeichert");
			} catch (IOException e) {
				p.sendMessage("Fehler beim Speichern: " + e.getMessage());
			}
			return true;
		}
	}
