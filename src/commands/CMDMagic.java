package commands;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CMDMagic implements CommandExecutor {

	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {
		if(!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player) sender;
		ItemStack Lore = new ItemStack(Material.STICK);
    	ItemMeta Meta = Lore.getItemMeta(); 
    	Meta.setDisplayName("ZauberStab");
    	Lore.setItemMeta(Meta);
    	p.getInventory().addItem(Lore);
		
		
			return true;
			
	
}
}