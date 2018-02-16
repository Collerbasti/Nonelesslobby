package Main;



import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class NonelessEventListener implements Listener {
	public NonelessEventListener(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this , plugin);
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent ev) {
		Player p = ev.getPlayer();
		Double x = Main.loc.getDouble("spawn.X");
		Double y = Main.loc.getDouble("spawn.Y");
		Double z = Main.loc.getDouble("spawn.Z");
		Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
		Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
		org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
		p.sendMessage("Hallo");
		System.out.println("Spieler ist gejoint");
		p.teleport(new Location(w,x,y,z,yaw,pitch));
                }
	@EventHandler
	public void onPlayerClickinLobby(PlayerInteractEvent ev) {
	    Player e = ev.getPlayer();
	    if(e.getInventory().getItemInMainHand().getType()==Material.MINECART) {
	    	
	    	Inventory Menue = e.getServer().createInventory(null, 27,e.getName()+"§b Inventory");
	    	

	    	
	    	ItemStack Spawn = new ItemStack(Material.FIREBALL);
	    	
	    	Menue.setItem(1,Spawn);
	    	e.openInventory(Menue);
	    	e.sendMessage("Hat geklappt");
	    }
	}
}
