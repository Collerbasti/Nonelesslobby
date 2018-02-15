package Main;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
		Double x = Main.loc.getDouble("spawn.X");
		Double y = Main.loc.getDouble("spawn.Y");
		Double z = Main.loc.getDouble("spawn.Z");
		Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
		Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
		org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
		p.sendMessage("Hallo");
		System.out.println("Spieler ist gejoint");
		p.teleport(new Location(w,x,y,z,yaw,pitch));
		
        new BukkitRunnable() {
            
            @Override
            public void run() {
                // What you want to schedule goes here
                plugin.getServer().broadcastMessage(
                    "Welcome to Bukkit! Remember to read the documentation!");
            }
            
        }.runTaskLater(this.plugin, 5000);
    }
 
}
