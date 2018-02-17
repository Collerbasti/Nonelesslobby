package Main;



import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;


public class NonelessEventListener implements Listener {
    private final Main plugin;
	public NonelessEventListener(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
	
	
	
	@EventHandler 
	public void onPlayerJoin(PlayerJoinEvent ev) {
		Player p = ev.getPlayer();
		Main.Frdb.addDefault(p+".CountFriends", 0);
		Main.Frdb.set(p+".isOnline", true);
		Location alt = p.getLocation(); 
		Double x = Main.loc.getDouble("spawn.X");
		Double y = Main.loc.getDouble("spawn.Y");
		Double z = Main.loc.getDouble("spawn.Z");
		Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
		Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
		org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
		p.sendMessage("Hallo");
		System.out.println("Spieler ist gejoint");	
		p.teleport(alt);
		p.teleport(new Location(w,x,y,z,yaw,pitch));
		
    new BukkitRunnable() {
		
		@Override
		public void run() {
			
		Player p = ev.getPlayer();
		Double x = Main.loc.getDouble("spawn.X");
		Double y = Main.loc.getDouble("spawn.Y");
		Double z = Main.loc.getDouble("spawn.Z");
		Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
		Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
		org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
		p.sendMessage("Hallo");
		System.out.println("Spieler ist gejoint");
		p.teleport(alt);
		p.teleport(new Location(w,x,y,z,yaw,pitch));
        }
        
    }.runTaskLater(this.plugin, 20);
}

	@EventHandler
	public void onPlayerClickinLobby(PlayerInteractEvent ev) {
	    Player e = ev.getPlayer();
	    
	    Location loctest = e.getLocation();
	    
	    if(e.getInventory().getItemInMainHand().getType()==Material.MINECART && loctest.getWorld().getName() == Main.loc.getString("spawn.World")) {
	    	
	    	
	    	
	    	Inventory Menue = e.getServer().createInventory(null, 27,e.getName()+"§b Warps");
	    	

	    	
	    	ItemStack Spawn = new ItemStack(Material.APPLE);
	    	ItemMeta Meta = Spawn.getItemMeta(); 
	    	Meta.setDisplayName("Spawn");
	    	Spawn.setItemMeta(Meta);
	    	Menue.setItem(10,Spawn);
	    	
	    	
	    		    	
	    	ItemStack Spawn2 = new ItemStack(Material.BANNER);
	    	ItemMeta Meta2 = Spawn2.getItemMeta(); 
	    	Meta2.setDisplayName("Spiele");
	    	Spawn2.setItemMeta(Meta2);
	    	Menue.setItem(11,Spawn2);
	    	
	    	
	    	ItemStack Spawn3 = new ItemStack(Material.WOOD);
	    	ItemMeta Meta3 = Spawn3.getItemMeta(); 
	    	Meta3.setDisplayName("AREA City");
	    	Spawn3.setItemMeta(Meta3);
	    	Menue.setItem(12,Spawn3);
	    	
	    	
	    	e.openInventory(Menue);
	    	e.sendMessage("Inventory Geöffnet");
	    }
	}
	
	
	
	@EventHandler
	public void InventoryClick(InventoryClickEvent ev) {
		
		Player p = (Player) ev.getWhoClicked();
		
		if(ev.getInventory().getName().equalsIgnoreCase(p.getName()+"§b Warps")){
			ev.setCancelled(true);
			
			
			//Menue Spawns
			if(ev.getCurrentItem().getType() == Material.APPLE) {
				
				Double x = Main.loc.getDouble("spawn.X");
				Double y = Main.loc.getDouble("spawn.Y");
				Double z = Main.loc.getDouble("spawn.Z");
				Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
				Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
				
				
			}else	if(ev.getCurrentItem().getType() == Material.BANNER) {
				
				Double x = Main.loc.getDouble("Spiele.X");
				Double y = Main.loc.getDouble("Spiele.Y");
				Double z = Main.loc.getDouble("Spiele.Z");
				Float yaw = (float) Main.loc.getDouble("Spiele.Yaw");
				Float pitch = (float) Main.loc.getDouble("Spiele.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("Spiele.World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
				
				
			}else	if(ev.getCurrentItem().getType() == Material.WOOD) {
				
				Double x = Main.loc.getDouble("AREA.X");
				Double y = Main.loc.getDouble("AREA.Y");
				Double z = Main.loc.getDouble("AREA.Z");
				Float yaw = (float) Main.loc.getDouble("AREA.Yaw");
				Float pitch = (float) Main.loc.getDouble("AREA.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("AREA.World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
				
				
			}
			
		}
		
		
		
	}
	
	@EventHandler
	public void teleportrec(PlayerTeleportEvent ev) {
	 Player p = ev.getPlayer();
	    Location loctest2 = p.getLocation();
	    
	    if(loctest2.getWorld().getName() == Main.loc.getString("spawn.World")) {
	    	p.getInventory().clear();
	    	ItemStack Lore = new ItemStack(Material.MINECART);
	    	ItemMeta Meta = Lore.getItemMeta(); 
	    	Meta.setDisplayName("Warps");
	    	Lore.setItemMeta(Meta);
	    	p.getInventory().addItem(Lore);
	    }
	    	
	  
	}
	
	@EventHandler 
	public void onPlayerleave(PlayerQuitEvent ev) {
		
		Player p = ev.getPlayer();
		
		Main.Frdb.set(p+".isOnline", false);
		
		
	}
	
	}	

