package de.noneless.commands;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CMDMagic implements CommandExecutor {

	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
			return true;
		}
		Player p = (Player) sender;
		ItemStack lore = new ItemStack(Material.STICK);
		ItemMeta meta = lore.getItemMeta();
		meta.setDisplayName("ZauberStab");
		lore.setItemMeta(meta);
		p.getInventory().addItem(lore);
		p.sendMessage("Du hast einen ZauberStab erhalten!");
		return true;
	}
}
