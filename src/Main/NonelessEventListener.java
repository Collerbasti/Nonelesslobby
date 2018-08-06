package Main;



import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;


public class NonelessEventListener implements Listener 
{ 
    private final Main plugin;
    public int MainCounter = 0; 
    public boolean First = false;
    public boolean Tree = false;
    public boolean GlaDOSListen = false;
    public String GlaDOSFrage = "";
    public String GDOSVersion = "1.1 c"; 
    public String KiName = "BUSI";
    public String News = "Abjetzt kann BUSI Sprechen!! Dafür gehe einfach in den ts: Noneless.de auf den Channel : MINECRAFT_BUSI"; 
    public String KiNameEditor;
	public NonelessEventListener(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
    }
	@SuppressWarnings("deprecation")
	public void Tick(Time ev) {
		if (ev.getHours()==0) {
			if(ev.getDay()==1) {
				MySQL.disconnect();
				MySQL.connect();
				
			}
		}
	}
	

	
	
	@EventHandler 
	public void onPlayerJoin(PlayerJoinEvent ev) {	
		Player p = ev.getPlayer();

		
		if(Main.Frdb.getBoolean(p.getName()+".VIP.Enable")) {
			if(Calendar.getInstance().getTime().after((Date) Main.Frdb.get(p.getName()+".VIP.Expression"))){
				Main.Frdb.set(p.getName()+".VIP.Enable", false);
				
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				String command = "/manudelsub "+ p.getName()+" VIP" ;
				Bukkit.dispatchCommand(console, command);
			}
		}
		
		
		Main.Frdb.set(p.getName()+".isOnline", true);
		if(Main.Frdb.getBoolean(p.getName()+".webregister")) {
			
			
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE PROFILELIST SET isOnline = 'ja' WHERE SPIELERNAME = ?");
				ps.setString(1, p.getName());
				ps.executeUpdate();
			} catch (SQLException e) {
				p.sendMessage("sorry aber irgentwas ist schiefgelaufen ( "+ e+" )");
				e.printStackTrace();
			}
			
			
		}
		Main.Frdb.set(p.getName()+".Name", p.getName());
		MainCounter = MainCounter+1;
		if(MainCounter == 10) {
			try {
				Main.Frdb.save(Main.Friends);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MainCounter = 0;
		}
		Location alt = p.getLocation(); 
		Double x = Main.loc.getDouble("spawn.X");
		Double y = Main.loc.getDouble("spawn.Y");
		Double z = Main.loc.getDouble("spawn.Z");
		Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
		Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
		org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
		p.sendMessage("Hallo und Herzlich Willkommen in der Noneless Comunity");
		p.sendMessage("Was gibt es Neues? "+News);
		System.out.println("Spieler ist gejoint");	
		p.teleport(alt);
		p.teleport(new Location(w,x,y,z,yaw,pitch));
		
		
		if(First == false) {
		if(p.hasPermission("Noneless.lobby.set")) {
			p.sendMessage("Hey, Wegem dem Neustart wurde Die Lobby Jetzt Automatisch neu gesetzt");
			p.performCommand("setlobby");
			p.performCommand("Spawn");
			First = true;
		}
		}
}
	// An Timo -- Niemals etwas sagen, dies ist BUSI, Er wird eine kleiner unterstützer in not
	
	@EventHandler 
	public void onChat(AsyncPlayerChatEvent ev) throws IOException {	
		
		String Message = ev.getMessage();
		Message = Message.toLowerCase();
		if(Message.contains("häcke") && Message.contains("server")&Message.contains("!?")& Main.GDOS.getBoolean(ev.getPlayer().getName()+".Enable")) {
			ev.getPlayer().setHealth(0);
			String Speak = "Ups, ich glaube das war absicht";
			Bukkit.broadcastMessage("§4"+KiName+": §f"+Speak);
			
	ev.setCancelled(true);
					
			
		
				
					Speak_Class.Speak(Speak);
			ev.setCancelled(true);
		}else if(Message.contains("hilf")&Message.contains("!?")& Main.GDOS.getBoolean(ev.getPlayer().getName()+".Enable")) {
			Bukkit.broadcastMessage("§4"+KiName+": §f");
			ev.setCancelled(true);
			ArrayList<String> Glados = new ArrayList<String>();
			Glados.addAll(Main.GDOS.getStringList("GlaDOS.List"));
			int count = Glados.size();
			int Count2 = 0;
			while(Count2 <= count) {
				
				Bukkit.broadcastMessage("§f"+Glados.get(Count2));
				Count2 = Count2 +1;
			}
			
				
		
			
			ev.setCancelled(true);
			
			
			
		}else if(Message.contains("teleportiere") &Message.contains("!?")& Main.GDOS.getBoolean(ev.getPlayer().getName()+".Enable")) {
			
			
			if(Message.contains("areacity")) {
				Bukkit.broadcastMessage("§4"+KiName+": §fMach ich");
				ev.setCancelled(true);
				Double x = Main.loc.getDouble("AREA.X");
				Double y = Main.loc.getDouble("AREA.Y");
				Double z = Main.loc.getDouble("AREA.Z");
				Float yaw = (float) Main.loc.getDouble("AREA.Yaw");
				Float pitch = (float) Main.loc.getDouble("AREA.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("AREA.World"));
				ev.getPlayer().teleport(new Location(w,x,y,z,yaw,pitch));
				
			}
			
			
			
		
		
		
		
		}else if(Message.contains("version")&Message.contains("!?")& Main.GDOS.getBoolean(ev.getPlayer().getName()+".Enable")) {
			Bukkit.broadcastMessage("§4"+KiName+": §fich bin "+KiName+" in der Version: "+GDOSVersion);
			ev.setCancelled(true);
		}else if(Message.contains("easymode")&Message.contains("!?")& Main.GDOS.getBoolean(ev.getPlayer().getName()+".Enable")) {
		if(Main.GDOS.getBoolean(ev.getPlayer().getName().toString()+".EasyMode.Enable")==false){
			Bukkit.broadcastMessage("§4"+KiName+": §fOkay ich stelle dein Easymode ein "+ev.getPlayer().getDisplayName());
			ev.setCancelled(true);
			Main.GDOS.set(ev.getPlayer().getName().toString()+".EasyMode.Enable",true);
		}else if(Main.GDOS.getBoolean(ev.getPlayer().getName().toString()+".EasyMode.Enable")){
			Bukkit.broadcastMessage("§4"+KiName+": §fOkay ich stelle dein Easymode aus "+ev.getPlayer().getDisplayName());
			ev.setCancelled(true);
			Main.GDOS.set(ev.getPlayer().getName().toString()+".EasyMode.Enable",false);
		}
		}
		else {
		if(Message.equalsIgnoreCase("ist der kuchen eine lüge?")& Main.GDOS.getBoolean(ev.getPlayer().getName()+".Enable")==false) {
			Bukkit.broadcastMessage("§4"+KiName+": §fNein der Kuchen ist keine §4Lüge §f. Niemand hat das behauptet");
			ev.setCancelled(true);
			Main.GDOS.set(ev.getPlayer().getName()+".Enable", true);
		}else if (Message.equalsIgnoreCase("ist der kuchen eine lüge?")& Main.GDOS.getBoolean(ev.getPlayer().getName()+".Enable")==false){
			Bukkit.broadcastMessage("§4"+KiName+": §falsob du das nicht schon wüsstest");
			ev.setCancelled(true);
			
		}else	
		if(GlaDOSListen) {
			if(KiNameEditor==ev.getPlayer().getName()) {
			if(Message.contains("abbrechen!!?")) {
				ev.setCancelled(true);
				Bukkit.broadcastMessage("§4"+KiName+": §f Ich habe den vorgang abgebrochen");
				GlaDOSListen = false;
				GlaDOSFrage = "";
			}else{
			ArrayList<String> Glados = new ArrayList<String>();
			Glados.addAll(Main.GDOS.getStringList("GlaDOS.List"));
			Glados.add(GlaDOSFrage);
			Main.GDOS.set("GlaDOS.List", Glados);
			Main.GDOS.set("GlaDOS."+GlaDOSFrage+".answer", ev.getMessage());
			Main.GDOS.set("GlaDOS."+GlaDOSFrage+".Player", ev.getPlayer());
			ev.setCancelled(true);
			Bukkit.broadcastMessage("§4"+KiName+": §f Absofort werde ich das auf die Frage Antworten Danke Spieler");
			
			GlaDOSListen = false;
			GlaDOSFrage = "";
			try {
				Main.GDOS.save(Main.GlaDOS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
			}}else {
				ev.setCancelled(true);
				Speak_Class.Speak("Hey, Sorry aber du bist nicht der den ich meine");
				Bukkit.broadcastMessage("§4"+KiName+": §f Hey, Sorry aber du bist nicht der den ich meine");
			}
		}else {
		if(Main.GDOS.getBoolean(ev.getPlayer().getName()+".Enable")) {
			if(Message.contains("!?")) {
				if(Message == "!?") {
					ev.setCancelled(true);
					Bukkit.broadcastMessage("§4"+KiName+": §f Dazu kann ich nichts sagen");
					
				}else {
					ArrayList<String> Glados = new ArrayList<String>();
					Glados.addAll(Main.GDOS.getStringList("GlaDOS.List"));
					if(Glados.contains(Message)) {
						ev.setCancelled(true);
						Bukkit.broadcastMessage("§4"+KiName+":§f "+ Main.GDOS.getString("GlaDOS."+Message+".answer"));
						Speak_Class.Speak(Main.GDOS.getString("GlaDOS."+Message+".answer"));
					}else {
						ev.setCancelled(true);
						Bukkit.broadcastMessage("§4"+KiName+":§f Dazu weiß ich leider keine Antwort");
						if(ev.getPlayer().hasPermission("Noneless.GlaDOS")) {
							Bukkit.broadcastMessage("Doch du darfst mich leiten, Schreibe jetzt einfach die Antwort, mit abbrechen!!? kannst du den vorgang abbrechen ");
							GlaDOSListen = true;
							ev.setCancelled(true);
							KiNameEditor=ev.getPlayer().getName();
							GlaDOSFrage = Message;
							
						}
					}
				}
			}
		}}
		}}
	
	

	
	//Glados Ende
	@EventHandler 
	public void onPLayerThrow(PlayerDropItemEvent ev) {	
		Player p = ev.getPlayer();
		if(p.getWorld().getName()== Main.loc.getString("spawn.World")) {
			((Cancellable) ev).setCancelled(true);
		}else if(p.getGameMode().equals(GameMode.CREATIVE)){
			
		}
	}
	
	@EventHandler 
	public void onPLayerPick(EntityPickupItemEvent ev) {	
		 LivingEntity pe = ev.getEntity();
		 if(pe.getType().equals(EntityType.PLAYER)){
			 
			Player p = Bukkit.getServer().getPlayer(pe.getName());
		if(p.getWorld().getName()== Main.loc.getString("spawn.World")) {
			((Cancellable) ev).setCancelled(true);
		}else if(p.getGameMode().equals(GameMode.CREATIVE)){
		}
		 }
		 
	}

	
	@EventHandler
	public void onPlayerClickinLobby(PlayerInteractEvent ev) {
		String Item = ev.getItem().getItemMeta().getDisplayName();
		
	    Player ep = ev.getPlayer();
	    
		
	    if(Item.equalsIgnoreCase("Warps")) {
	    	ep.getPlayer().sendMessage("Du Hast das Menu geöffnet");
	    	//------Menü-----
	    	Inventory Menue = ep.getServer().createInventory(null, 27,ep.getName()+"§b Warps");
	    	

	    	
	    	ItemStack Spawn = new ItemStack(Material.APPLE);
	    	ItemMeta Meta = Spawn.getItemMeta(); 
	    	Meta.setDisplayName("Spawn");
	    	Spawn.setItemMeta(Meta);
	    	Menue.setItem(10,Spawn);
	    	
	    	
	    		    	
	    	ItemStack Spawn2 = new ItemStack(Material.BLACK_BANNER);
	    	ItemMeta Meta2 = Spawn2.getItemMeta(); 
	    	Meta2.setDisplayName("Spiele");
	    	Spawn2.setItemMeta(Meta2);
	    	Menue.setItem(11,Spawn2);
	    	
	    	
	    	ItemStack Spawn3 = new ItemStack(Material.ACACIA_WOOD);
	    	ItemMeta Meta3 = Spawn3.getItemMeta(); 
	    	Meta3.setDisplayName("AREA City");
	    	Spawn3.setItemMeta(Meta3);
	    	Menue.setItem(12,Spawn3);
	    	
	    	ItemStack Skull = new ItemStack(Material.PLAYER_HEAD);
	    	SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
	    	SMeta.setDisplayName("Freunde");
	    	SMeta.setOwningPlayer(ep);
	    	Skull.setItemMeta(SMeta);
	    	Skull.setDurability((short) 3);
	    	Menue.setItem(13,Skull);
	    	
	    	ItemStack Set = new ItemStack(Material.COMPASS);
	    	ItemMeta CMeta =  Set.getItemMeta(); 
	    	CMeta.setDisplayName("Einstellungen");
	    	Set.setItemMeta(CMeta);
	    	Menue.setItem(14,Set);
	    	
	    	ItemStack Meat = new ItemStack(Material.BAKED_POTATO);
	    	ItemMeta MMeta =  Meat.getItemMeta(); 
	    	MMeta.setDisplayName("Essen");
	    	Meat.setItemMeta(MMeta);
	    	Menue.setItem(20,Meat);
	    	if(ep.hasPermission("Noneless.Creative.World")){
	    		ItemStack Creative = new ItemStack(Material.REDSTONE);
		    	ItemMeta CMETA =  Creative.getItemMeta(); 
		    	CMETA.setDisplayName("Kreativ welt");
		    	Creative.setItemMeta(CMETA);
		    	Menue.setItem(21,Creative);
	    		
	    	}
	    	if(ep.hasPermission("Noneless.Admin.World")){
	    		ItemStack Creative = new ItemStack(Material.GLOWSTONE_DUST);
		    	ItemMeta CMETA =  Creative.getItemMeta(); 
		    	CMETA.setDisplayName("Admin AreaCity");
		    	Creative.setItemMeta(CMETA);
		    	Menue.setItem(22,Creative);
	    		
	    	}
	    	
	    	
	    	
	    	
	    	ep.openInventory(Menue);
//------Menue
	    }
		
		
		
		
		
		//easymode
		
		 if(ev.getPlayer().getInventory().getItemInMainHand().getType()==Material.STICK && ev.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()=="ZauberStab") {
			  
			if(Main.GDOS.getBoolean(ev.getPlayer().getName().toString()+".EasyMode.Enable")) {
				
				for(Entity e : ev.getPlayer().getNearbyEntities(10, 10, 10) ) {
					if(e.getType()==EntityType.CREEPER) {
						if(Main.GDOS.getInt(ev.getPlayer().getName().toString()+".EasyMode.Magic")>10) {


							Double x = e.getLocation().getX();
							Double y = e.getLocation().getY();
							Double z = e.getLocation().getZ();
							Float yaw = (float) e.getLocation().getYaw();
							Float pitch = (float) e.getLocation().getPitch();
							org.bukkit.World w = e.getLocation().getWorld();
							e.teleport(new Location(w,x,y+1000,z,yaw,pitch));
							
						Main.GDOS.set(ev.getPlayer().getName().toString()+".EasyMode.Magic",Main.GDOS.getInt(ev.getPlayer().getName().toString()+".EasyMode.Magic")-10);
						Bukkit.broadcastMessage("§4"+KiName+":§f Da Habe ich Einen Creeper Erwischt");
						}else {
							Bukkit.broadcastMessage("§4"+KiName+":§f Leider Habe ich keine Lust den Creeper zu töten, Sorry");	
						}
					}
						
						if(e.getType()==EntityType.ZOMBIE) {
							if(Main.GDOS.getInt(ev.getPlayer().getName().toString()+".EasyMode.Magic")>10) {
								Double x = e.getLocation().getX();
								Double y = e.getLocation().getY();
								Double z = e.getLocation().getZ();
								Float yaw = (float) e.getLocation().getYaw();
								Float pitch = (float) e.getLocation().getPitch();
								org.bukkit.World w = e.getLocation().getWorld();
								e.teleport(new Location(w,x,y+1000,z,yaw,pitch));
								
							Main.GDOS.set(ev.getPlayer().getName().toString()+".EasyMode.Magic",Main.GDOS.getInt(ev.getPlayer().getName().toString()+".EasyMode.Magic")-10);
							Bukkit.broadcastMessage("§4"+KiName+":§f Da Habe ich Einen ZOMBIE Erwischt");
							}else {
								Bukkit.broadcastMessage("§4"+KiName+":§f Leider Habe ich keine Lust den ZOMBIE zu töten, Sorry");	
							}
					}
					
				}
				
			}}
		//easymode end
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

	}
	
	
	
	
	@EventHandler
	public void InventoryClick1(InventoryClickEvent ev) {
		
		Player p = (Player) ev.getWhoClicked();
		if(p.getLocation().getWorld().getName()==Main.loc.getString("spawn.World")) {
		if(p.getGameMode().equals(GameMode.CREATIVE)){
			p.sendMessage("Darfst du dass? Ja klar sonst würdest du das nicht können");
		}else {
			ev.setCancelled(true);
		}
		}
	}
	
	@EventHandler
	public void InventoryClick(InventoryClickEvent ev) {

		
		ArrayList<String> Friends = new ArrayList<String>();
		Player p = (Player) ev.getWhoClicked();
		
		if(ev.getInventory().getName().equalsIgnoreCase(p.getName()+"§b Warps")){
			ev.setCancelled(true);
			
			
			//---------------------Menue Spawns
			if(ev.getCurrentItem().getType() == Material.APPLE) {
				
				Double x = Main.loc.getDouble("spawn.X");
				Double y = Main.loc.getDouble("spawn.Y");
				Double z = Main.loc.getDouble("spawn.Z");
				Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
				Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
				
				
			}else	if(ev.getCurrentItem().getType() == Material.REDSTONE) {
				
				Double x = Main.loc.getDouble("Creative.X");
				Double y = Main.loc.getDouble("Creative.Y");
				Double z = Main.loc.getDouble("Creative.Z");
				Float yaw = (float) Main.loc.getDouble("Creative.Yaw");
				Float pitch = (float) Main.loc.getDouble("Creative.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("Creative.World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
				
				
				
			}else	if(ev.getCurrentItem().getType() == Material.GLOWSTONE_DUST) {
				
				
				Double x = Main.loc.getDouble("Admin.X");
				Double y = Main.loc.getDouble("Admin.Y");
				Double z = Main.loc.getDouble("Admin.Z");
				Float yaw = (float) Main.loc.getDouble("Admin.Yaw");
				Float pitch = (float) Main.loc.getDouble("Admin.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("Admin.World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
				
				
				
				
				
			}else	if(ev.getCurrentItem().getType() == Material.BLACK_BANNER) {
				

				Inventory Games = p.getServer().createInventory(null, 27, p.getName()+"§b Minispiele");
				
				int Counter = Main.MiGm.getInt("Global.Count");
				ArrayList<String> MiniGames = new ArrayList<String>();
				MiniGames.addAll(Main.MiGm.getStringList("Global.Minigames"));
				
					while(Counter > 0 ){
						
						Counter = Counter - 1;
						String Game = MiniGames.get(Counter);
						
						if(Main.MiGm.getInt(MiniGames.get(Counter)+".Mat") == 1) {
							ItemStack FATP = new ItemStack(Material.BLACK_BED,1,(byte)14 );
							ItemMeta FATPM =  FATP.getItemMeta(); 
						    FATPM.setDisplayName(Game);
						    FATP.setItemMeta(FATPM);
						    Games.setItem(Counter, FATP);
						}else if(Main.MiGm.getInt(MiniGames.get(Counter)+".Mat") == 2) {
							ItemStack FATP = new ItemStack(Material.ACACIA_WOOD,1);
							ItemMeta FATPM =  FATP.getItemMeta(); 
						    FATPM.setDisplayName(Game);
						    FATP.setItemMeta(FATPM);
						    Games.setItem(Counter, FATP);
						}else {
							ItemStack FATP = new ItemStack(Material.RED_MUSHROOM,1,(byte)14 );
							ItemMeta FATPM =  FATP.getItemMeta(); 
						    FATPM.setDisplayName(Game);
						    FATP.setItemMeta(FATPM);
						    Games.setItem(Counter, FATP);
						}
						
						
					    
						
					}
					Counter = Main.MiGm.getInt("Global.Count")+1;
					ItemStack FATP = new ItemStack(Material.GLASS_PANE,1);
					ItemMeta FATPM =  FATP.getItemMeta(); 
				    FATPM.setDisplayName("Duell");
				    FATP.setItemMeta(FATPM);
				    Games.setItem(Counter, FATP);
					
					
					
				p.openInventory(Games);
			}else	if(ev.getCurrentItem().getType() == Material.COMPASS) {
				
				
//SettingsMenue-----------------------------
				
				Inventory Settings = p.getServer().createInventory(null, 27,p.getName()+"§b Settings");
				
				if(Main.Frdb.getBoolean(p.getName()+".AllowFriendsTp")){
					
					ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
			    	ItemMeta FATPM =  FATP.getItemMeta(); 
			    	FATPM.setDisplayName("Teleportieren von Freunden zu Einem Erlaubt");
			    	FATP.setItemMeta(FATPM);
			    	Settings.setItem(2, FATP);
				}else {
					ItemStack FATP = new ItemStack(Material.RED_WOOL,1,(byte)14 );
				    ItemMeta FATPM =  FATP.getItemMeta(); 
				    FATPM.setDisplayName("Teleportieren von Freunden zu Einem Verboten");
				    FATP.setItemMeta(FATPM);
				    Settings.setItem(2, FATP);
					}
				if(Main.Frdb.getBoolean(p.getName()+".Gamemode")) {
					ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
			    	ItemMeta FATPM =  FATP.getItemMeta(); 
			    	FATPM.setDisplayName("Gamemode Creative");
			    	FATP.setItemMeta(FATPM);
			    	Settings.setItem(4, FATP);	
				}	
				else {
					if(p.hasPermission("Noneless.Admin.Gamemode")) {
						ItemStack FATP = new ItemStack(Material.BLUE_WOOL,1,(byte)14 );
					    ItemMeta FATPM =  FATP.getItemMeta(); 
					    FATPM.setDisplayName("Gamemode Adventure");
					    FATP.setItemMeta(FATPM);
					    Settings.setItem(4, FATP);	
					}
					else {
						ItemStack FATP = new ItemStack(Material.LIGHT_GRAY_WOOL,7,(byte)14 );
					    ItemMeta FATPM =  FATP.getItemMeta(); 
					    FATPM.setDisplayName("Gamemode Verboten");
					    FATP.setItemMeta(FATPM);
					    Settings.setItem(4, FATP);
					}
				}
				
				ItemStack Back = new ItemStack(Material.BARRIER);
		    	ItemMeta BMeta =  Back.getItemMeta(); 
		    	BMeta.setDisplayName("Zurück");
		    	Back.setItemMeta(BMeta);
		    	Settings.setItem(26,Back);
				
				p.openInventory(Settings);
				
				//----------------
				
				
				
				
				
			}else	if(ev.getCurrentItem().getType() == Material.ACACIA_WOOD) {
				
				Double x = Main.loc.getDouble("AREA.X");
				Double y = Main.loc.getDouble("AREA.Y");
				Double z = Main.loc.getDouble("AREA.Z");
				Float yaw = (float) Main.loc.getDouble("AREA.Yaw");
				Float pitch = (float) Main.loc.getDouble("AREA.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("AREA.World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
			
				
			}else if(ev.getCurrentItem().getType() == Material.PLAYER_HEAD) {
				int Counter = Main.Frdb.getInt(p.getName()+".Count");
				Friends.addAll(Main.Frdb.getStringList(p.getName()+".Friends"));
				
				Inventory FriendsMenue = p.getServer().createInventory(null, 27,p.getName()+"§b Freunde");
				
		    	ItemStack Back = new ItemStack(Material.BARRIER);
		    	ItemMeta BMeta =  Back.getItemMeta(); 
		    	BMeta.setDisplayName("Zurück");
		    	Back.setItemMeta(BMeta);
		    	FriendsMenue.setItem(26,Back);
				
			while(Counter > 0) {	
				Counter = Counter -1;
				if(Main.Frdb.getBoolean(Friends.get(Counter).toString()+".isOnline")){
				
				
		    	
		    	ItemStack Skull = new ItemStack(Material.PLAYER_HEAD);
		    	SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
		    	SMeta.setDisplayName(Friends.get(Counter));
				Player Fp = Bukkit.getPlayer(Friends.get(Counter));
		    	SMeta.setOwningPlayer(Fp);
		    	Skull.setItemMeta(SMeta);
		    	Skull.setDurability((short) 3);
		    	FriendsMenue.setItem(Counter,Skull);
		    	
		    	

		    	
		    	
				
				
				}else {
					
			    	ItemStack Skull = new ItemStack(Material.SKELETON_SKULL , 1);
			    	SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
			    	SMeta.setDisplayName("§4"+Friends.get(Counter));
			    	Skull.setItemMeta(SMeta);
			    	
			    	FriendsMenue.setItem(Counter,Skull);
					
				}
				
				p.openInventory(FriendsMenue);

				
				}
					
				
			}else	if(ev.getCurrentItem().getType() == Material.BAKED_POTATO) {
				p.setHealth(20);
				p.setFoodLevel(20);
				p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EAT, 10, 1);
			}
			
		}
	}
	
	@EventHandler
	public void ItemClick(PlayerDropItemEvent ev) {
		Player p = ev.getPlayer();
			if(p.getWorld().getName()==Main.loc.getString("spawn.world")) {
				p.sendMessage("Das Darfts du nicht");
				ev.setCancelled(true);
			}
			}
	
	
	
	@EventHandler
	public void teleportrec(PlayerTeleportEvent ev) {
	 Player p = ev.getPlayer();
	    Location loctest2 = p.getLocation();
	    
	    
	    String World = loctest2.getWorld().getName();
	    
	    
	    try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE PROFILELIST SET WORLD=\""+World+"\" WHERE SPIELERNAME=\""+p.getName()+"\"");
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    if(loctest2.getWorld().getName() == Main.loc.getString("spawn.World")) {
	    	p.getInventory().clear();
	    	ItemStack Lore = new ItemStack(Material.MINECART);
	    	ItemMeta Meta = Lore.getItemMeta(); 
	    	Meta.setDisplayName("Warps");
	    	Lore.setItemMeta(Meta);
	    	p.getInventory().setItem(8,Lore);
	    	p.getPlayer().updateInventory();
	    	
	    } 
	    	
	  
	}
	
	@EventHandler 
	public void onPlayerleave(PlayerQuitEvent ev) {
		
		Player p = ev.getPlayer();
		
		Main.Frdb.set(p.getName()+".isOnline", false);
		
		if(Main.Frdb.getBoolean(p.getName()+".webregister")) {
			
			
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE PROFILELIST SET isOnline = 'nein' WHERE SPIELERNAME = ?");
				ps.setString(1, p.getName());
				ps.executeUpdate();
			} catch (SQLException e) {
				p.sendMessage("sorry aber irgentwas ist schiefgelaufen ( "+ e+" )");
				e.printStackTrace();
			}
			
			
		}
		
		
		
		try {
			Main.Frdb.save(Main.Friends);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MainCounter = 0;
	}
	
	
	
	@EventHandler
	public void InventoryFriendsClick(InventoryClickEvent ev) {
		Player p = (Player) ev.getWhoClicked();
		if(ev.getInventory().getName().equalsIgnoreCase(p.getName()+"§b Freunde")){
			ev.setCancelled(true);
			
			
			if(ev.getCurrentItem().getType() == Material.PLAYER_HEAD && Main.Frdb.getBoolean(ev.getCurrentItem().getItemMeta().getDisplayName()+".isOnline")) {
				if(Main.Frdb.getBoolean(ev.getCurrentItem().getItemMeta().getDisplayName()+".AllowFriendsTp")){

			Player Friend = Bukkit.getPlayer(ev.getCurrentItem().getItemMeta().getDisplayName());
			String Adminworld = Main.loc.getString("Admin.World");
			if(Friend.getLocation().getWorld().getName() == Adminworld ) {
				if(p.hasPermission("Noneless.Admin.World")) {
					p.teleport(Friend.getLocation());
				}else {
			p.sendMessage("Bitte Frage Einen Admin ob du in die Adminweld Darfst");
				}
			}
				else {
			p.teleport(Friend.getLocation());
			}
				}else {
					p.sendMessage("Sorry aber "+ev.getCurrentItem().getItemMeta().getDisplayName()+" Erlaubt das nicht");
				}
			}
			if(ev.getCurrentItem().getType() == Material.BARRIER) {
				p.closeInventory();
				Player e = p.getPlayer();
			  	//------Menü-----
		    	Inventory Menue = e.getServer().createInventory(null, 27,e.getName()+"§b Warps");
		    	

		    	
		    	ItemStack Spawn = new ItemStack(Material.APPLE);
		    	ItemMeta Meta = Spawn.getItemMeta(); 
		    	Meta.setDisplayName("Spawn");
		    	Spawn.setItemMeta(Meta);
		    	Menue.setItem(10,Spawn);
		    	
		    	
		    		    	
		    	ItemStack Spawn2 = new ItemStack(Material.BLACK_BANNER);
		    	ItemMeta Meta2 = Spawn2.getItemMeta(); 
		    	Meta2.setDisplayName("Spiele");
		    	Spawn2.setItemMeta(Meta2);
		    	Menue.setItem(11,Spawn2);
		    	
		    	
		    	ItemStack Spawn3 = new ItemStack(Material.ACACIA_WOOD);
		    	ItemMeta Meta3 = Spawn3.getItemMeta(); 
		    	Meta3.setDisplayName("AREA City");
		    	Spawn3.setItemMeta(Meta3);
		    	Menue.setItem(12,Spawn3);
		    	
		    	ItemStack Skull = new ItemStack(Material.PLAYER_HEAD);
		    	SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
		    	SMeta.setDisplayName("Freunde");
		    	SMeta.setOwningPlayer(e);
		    	Skull.setItemMeta(SMeta);
		    	Skull.setDurability((short) 3);
		    	Menue.setItem(13,Skull);
		    	
		    	ItemStack Set = new ItemStack(Material.COMPASS);
		    	ItemMeta CMeta =  Set.getItemMeta(); 
		    	CMeta.setDisplayName("Einstellungen");
		    	Set.setItemMeta(CMeta);
		    	Menue.setItem(14,Set);
		    	
		    	ItemStack Meat = new ItemStack(Material.BAKED_POTATO);
		    	ItemMeta MMeta =  Meat.getItemMeta(); 
		    	MMeta.setDisplayName("Essen");
		    	Meat.setItemMeta(MMeta);
		    	Menue.setItem(20,Meat);
		    	if(e.hasPermission("Noneless.Creative.World")){
		    		ItemStack Creative = new ItemStack(Material.REDSTONE);
			    	ItemMeta CMETA =  Creative.getItemMeta(); 
			    	CMETA.setDisplayName("Kreativ welt");
			    	Creative.setItemMeta(CMETA);
			    	Menue.setItem(21,Creative);
		    		
		    	}
		    	if(e.hasPermission("Noneless.Admin.World")){
		    		ItemStack Creative = new ItemStack(Material.GLOWSTONE_DUST);
			    	ItemMeta CMETA =  Creative.getItemMeta(); 
			    	CMETA.setDisplayName("Admin AreaCity");
			    	Creative.setItemMeta(CMETA);
			    	Menue.setItem(22,Creative);
		    		
		    	}
		    	
		    	
		    	
		    	
		    	e.openInventory(Menue);
	//------Menue

				
			}
	}
	}



@EventHandler
public void InventorySettingsClick(InventoryClickEvent ev) {
	Player p = (Player) ev.getWhoClicked();
	if(ev.getInventory().getName().equalsIgnoreCase(p.getName()+"§b Settings")){
		if(ev.getCurrentItem().getType() ==Material.RED_WOOL && ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Teleportieren von Freunden zu Einem Verboten")) {
			Main.Frdb.set(p.getName()+".AllowFriendsTp", true);
			MainCounter = MainCounter+1;
			if(MainCounter == 10) {
				try {
					Main.Frdb.save(Main.Friends);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MainCounter = 0;
			}
			
			
			
			//SettingsMenue-----------------------------
			
			Inventory Settings = p.getServer().createInventory(null, 27,p.getName()+"§b Settings");
			
			if(Main.Frdb.getBoolean(p.getName()+".AllowFriendsTp")){
				
				ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
		    	ItemMeta FATPM =  FATP.getItemMeta(); 
		    	FATPM.setDisplayName("Teleportieren von Freunden zu Einem Erlaubt");
		    	FATP.setItemMeta(FATPM);
		    	Settings.setItem(2, FATP);
			}else {
				ItemStack FATP = new ItemStack(Material.RED_WOOL,1,(byte)14 );
			    ItemMeta FATPM =  FATP.getItemMeta(); 
			    FATPM.setDisplayName("Teleportieren von Freunden zu Einem Verboten");
			    FATP.setItemMeta(FATPM);
			    Settings.setItem(2, FATP);
				}
			if(Main.Frdb.getBoolean(p.getName()+".Gamemode")) {
				ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
		    	ItemMeta FATPM =  FATP.getItemMeta(); 
		    	FATPM.setDisplayName("Gamemode Creative");
		    	FATP.setItemMeta(FATPM);
		    	Settings.setItem(4, FATP);	
			}	
			else {
				if(p.hasPermission("Noneless.Admin.Gamemode")) {
					ItemStack FATP = new ItemStack(Material.BLUE_WOOL,1,(byte)14 );
				    ItemMeta FATPM =  FATP.getItemMeta(); 
				    FATPM.setDisplayName("Gamemode Adventure");
				    FATP.setItemMeta(FATPM);
				    Settings.setItem(4, FATP);	
				}
				else {
					ItemStack FATP = new ItemStack(Material.LIGHT_GRAY_WOOL,7,(byte)14 );
				    ItemMeta FATPM =  FATP.getItemMeta(); 
				    FATPM.setDisplayName("Gamemode Verboten");
				    FATP.setItemMeta(FATPM);
				    Settings.setItem(4, FATP);
				}
			}
			
			ItemStack Back = new ItemStack(Material.BARRIER);
	    	ItemMeta BMeta =  Back.getItemMeta(); 
	    	BMeta.setDisplayName("Zurück");
	    	Back.setItemMeta(BMeta);
	    	Settings.setItem(26,Back);
			
			p.openInventory(Settings);
			
			//----------------
			
			
		}else if(ev.getCurrentItem().getType() == Material.BARRIER) {
			p.closeInventory();
			Player e = p.getPlayer();
		  	//------Menü-----
	    	Inventory Menue = e.getServer().createInventory(null, 27,e.getName()+"§b Warps");
	    	

	    	
	    	ItemStack Spawn = new ItemStack(Material.APPLE);
	    	ItemMeta Meta = Spawn.getItemMeta(); 
	    	Meta.setDisplayName("Spawn");
	    	Spawn.setItemMeta(Meta);
	    	Menue.setItem(10,Spawn);
	    	
	    	
	    		    	
	    	ItemStack Spawn2 = new ItemStack(Material.BLACK_BANNER);
	    	ItemMeta Meta2 = Spawn2.getItemMeta(); 
	    	Meta2.setDisplayName("Spiele");
	    	Spawn2.setItemMeta(Meta2);
	    	Menue.setItem(11,Spawn2);
	    	
	    	
	    	ItemStack Spawn3 = new ItemStack(Material.ACACIA_WOOD);
	    	ItemMeta Meta3 = Spawn3.getItemMeta(); 
	    	Meta3.setDisplayName("AREA City");
	    	Spawn3.setItemMeta(Meta3);
	    	Menue.setItem(12,Spawn3);
	    	
	    	ItemStack Skull = new ItemStack(Material.PLAYER_HEAD);
	    	SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
	    	SMeta.setDisplayName("Freunde");
	    	SMeta.setOwningPlayer(e);
	    	Skull.setItemMeta(SMeta);
	    	Skull.setDurability((short) 3);
	    	Menue.setItem(13,Skull);
	    	
	    	ItemStack Set = new ItemStack(Material.COMPASS);
	    	ItemMeta CMeta =  Set.getItemMeta(); 
	    	CMeta.setDisplayName("Einstellungen");
	    	Set.setItemMeta(CMeta);
	    	Menue.setItem(14,Set);
	    	
	    	ItemStack Meat = new ItemStack(Material.BAKED_POTATO);
	    	ItemMeta MMeta =  Meat.getItemMeta(); 
	    	MMeta.setDisplayName("Essen");
	    	Meat.setItemMeta(MMeta);
	    	Menue.setItem(20,Meat);
	    	if(e.hasPermission("Noneless.Creative.World")){
	    		ItemStack Creative = new ItemStack(Material.REDSTONE);
		    	ItemMeta CMETA =  Creative.getItemMeta(); 
		    	CMETA.setDisplayName("Kreativ welt");
		    	Creative.setItemMeta(CMETA);
		    	Menue.setItem(21,Creative);
	    		
	    	}
	    	if(e.hasPermission("Noneless.Admin.World")){
	    		ItemStack Creative = new ItemStack(Material.GLOWSTONE_DUST);
		    	ItemMeta CMETA =  Creative.getItemMeta(); 
		    	CMETA.setDisplayName("Admin AreaCity");
		    	Creative.setItemMeta(CMETA);
		    	Menue.setItem(22,Creative);
	    		
	    	}
	    	
	    	
	    	
	    	
	    	e.openInventory(Menue);
//------Menue
		} else if(ev.getInventory().getName().equalsIgnoreCase(p.getName()+"§b Settings")){
			if(ev.getCurrentItem().getType() ==Material.GREEN_WOOL && ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Gamemode Creative")) {
				Main.Frdb.set(p.getName()+".Gamemode", false);
				p.setGameMode(GameMode.ADVENTURE);
				MainCounter = MainCounter+1;
				if(MainCounter == 10) {
					try {
						Main.Frdb.save(Main.Friends);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MainCounter = 0;
				}
				
				
				
//SettingsMenue-----------------------------
				
				Inventory Settings = p.getServer().createInventory(null, 27,p.getName()+"§b Settings");
				
				if(Main.Frdb.getBoolean(p.getName()+".AllowFriendsTp")){
					
					ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
			    	ItemMeta FATPM =  FATP.getItemMeta(); 
			    	FATPM.setDisplayName("Teleportieren von Freunden zu Einem Erlaubt");
			    	FATP.setItemMeta(FATPM);
			    	Settings.setItem(2, FATP);
				}else {
					ItemStack FATP = new ItemStack(Material.RED_WOOL,1,(byte)14 );
				    ItemMeta FATPM =  FATP.getItemMeta(); 
				    FATPM.setDisplayName("Teleportieren von Freunden zu Einem Verboten");
				    FATP.setItemMeta(FATPM);
				    Settings.setItem(2, FATP);
					}
				if(Main.Frdb.getBoolean(p.getName()+".Gamemode")) {
					ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
			    	ItemMeta FATPM =  FATP.getItemMeta(); 
			    	FATPM.setDisplayName("Gamemode Creative");
			    	FATP.setItemMeta(FATPM);
			    	Settings.setItem(4, FATP);	
				}	
				else {
					if(p.hasPermission("Noneless.Admin.Gamemode")) {
						ItemStack FATP = new ItemStack(Material.BLUE_WOOL,1,(byte)14 );
					    ItemMeta FATPM =  FATP.getItemMeta(); 
					    FATPM.setDisplayName("Gamemode Adventure");
					    FATP.setItemMeta(FATPM);
					    Settings.setItem(4, FATP);	
					}
					else {
						ItemStack FATP = new ItemStack(Material.LIGHT_GRAY_WOOL,7,(byte)14 );
					    ItemMeta FATPM =  FATP.getItemMeta(); 
					    FATPM.setDisplayName("Gamemode Verboten");
					    FATP.setItemMeta(FATPM);
					    Settings.setItem(4, FATP);
					}
				}
				
				ItemStack Back = new ItemStack(Material.BARRIER);
		    	ItemMeta BMeta =  Back.getItemMeta(); 
		    	BMeta.setDisplayName("Zurück");
		    	Back.setItemMeta(BMeta);
		    	Settings.setItem(26,Back);
				
				p.openInventory(Settings);
				
				//----------------
				
			}else if(ev.getCurrentItem().getType() ==Material.GREEN_WOOL && ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Teleportieren von Freunden zu Einem Erlaubt")) {
				Main.Frdb.set(p.getName()+".AllowFriendsTp", false);
				MainCounter = MainCounter+1;
				if(MainCounter == 10) {
					try {
						Main.Frdb.save(Main.Friends);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MainCounter = 0;
				}
				
				
				
//SettingsMenue-----------------------------
				
				Inventory Settings = p.getServer().createInventory(null, 27,p.getName()+"§b Settings");
				
				if(Main.Frdb.getBoolean(p.getName()+".AllowFriendsTp")){
					
					ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
			    	ItemMeta FATPM =  FATP.getItemMeta(); 
			    	FATPM.setDisplayName("Teleportieren von Freunden zu Einem Erlaubt");
			    	FATP.setItemMeta(FATPM);
			    	Settings.setItem(2, FATP);
				}else {
					ItemStack FATP = new ItemStack(Material.RED_WOOL,1,(byte)14 );
				    ItemMeta FATPM =  FATP.getItemMeta(); 
				    FATPM.setDisplayName("Teleportieren von Freunden zu Einem Verboten");
				    FATP.setItemMeta(FATPM);
				    Settings.setItem(2, FATP);
					}
				if(Main.Frdb.getBoolean(p.getName()+".Gamemode")) {
					ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
			    	ItemMeta FATPM =  FATP.getItemMeta(); 
			    	FATPM.setDisplayName("Gamemode Creative");
			    	FATP.setItemMeta(FATPM);
			    	Settings.setItem(4, FATP);	
				}	
				else {
					if(p.hasPermission("Noneless.Admin.Gamemode")) {
						ItemStack FATP = new ItemStack(Material.BLUE_WOOL,1,(byte)14 );
					    ItemMeta FATPM =  FATP.getItemMeta(); 
					    FATPM.setDisplayName("Gamemode Adventure");
					    FATP.setItemMeta(FATPM);
					    Settings.setItem(4, FATP);	
					}
					else {
						ItemStack FATP = new ItemStack(Material.LIGHT_GRAY_WOOL,7,(byte)14 );
					    ItemMeta FATPM =  FATP.getItemMeta(); 
					    FATPM.setDisplayName("Gamemode Verboten");
					    FATP.setItemMeta(FATPM);
					    Settings.setItem(4, FATP);
					}
				}
				
				ItemStack Back = new ItemStack(Material.BARRIER);
		    	ItemMeta BMeta =  Back.getItemMeta(); 
		    	BMeta.setDisplayName("Zurück");
		    	Back.setItemMeta(BMeta);
		    	Settings.setItem(26,Back);
				
				p.openInventory(Settings);
				
				//----------------
				
			
		
		
		}else if((ev.getCurrentItem().getType() ==Material.BLUE_WOOL && ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Gamemode Adventure"))){
				Main.Frdb.set(p.getName()+".Gamemode", true);
				p.setGameMode(GameMode.CREATIVE);
				
				
				
				
				
				MainCounter = MainCounter+1;
				if(MainCounter == 10) {
					try {
						Main.Frdb.save(Main.Friends);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MainCounter = 0;
				}
				
				
				//SettingsMenue-----------------------------
				
				Inventory Settings = p.getServer().createInventory(null, 27,p.getName()+"§b Settings");
				
				if(Main.Frdb.getBoolean(p.getName()+".AllowFriendsTp")){
					
					ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
			    	ItemMeta FATPM =  FATP.getItemMeta(); 
			    	FATPM.setDisplayName("Teleportieren von Freunden zu Einem Erlaubt");
			    	FATP.setItemMeta(FATPM);
			    	Settings.setItem(2, FATP);
				}else {
					ItemStack FATP = new ItemStack(Material.RED_WOOL,1,(byte)14 );
				    ItemMeta FATPM =  FATP.getItemMeta(); 
				    FATPM.setDisplayName("Teleportieren von Freunden zu Einem Verboten");
				    FATP.setItemMeta(FATPM);
				    Settings.setItem(2, FATP);
					}
				if(Main.Frdb.getBoolean(p.getName()+".Gamemode")) {
					ItemStack FATP = new ItemStack(Material.GREEN_WOOL,1,(byte)5 );
			    	ItemMeta FATPM =  FATP.getItemMeta(); 
			    	FATPM.setDisplayName("Gamemode Creative");
			    	FATP.setItemMeta(FATPM);
			    	Settings.setItem(4, FATP);	
				}	
				else {
					if(p.hasPermission("Noneless.Admin.Gamemode")) {
						ItemStack FATP = new ItemStack(Material.BLUE_WOOL,1,(byte)14 );
					    ItemMeta FATPM =  FATP.getItemMeta(); 
					    FATPM.setDisplayName("Gamemode Adventure");
					    FATP.setItemMeta(FATPM);
					    Settings.setItem(4, FATP);	
					}
					else {
						ItemStack FATP = new ItemStack(Material.LIGHT_GRAY_WOOL,7,(byte)14 );
					    ItemMeta FATPM =  FATP.getItemMeta(); 
					    FATPM.setDisplayName("Gamemode Verboten");
					    FATP.setItemMeta(FATPM);
					    Settings.setItem(4, FATP);
					}
				}
				
				ItemStack Back = new ItemStack(Material.BARRIER);
		    	ItemMeta BMeta =  Back.getItemMeta(); 
		    	BMeta.setDisplayName("Zurück");
		    	Back.setItemMeta(BMeta);
		    	Settings.setItem(26,Back);
				
				p.openInventory(Settings);
				
				//----------------
		
		
	}
}
	}
//-----------Minigames
	
	if(Tree == true) {
		Tree = false;
	ArrayList<String> MiniGames = new ArrayList<String>();
	MiniGames.addAll(Main.MiGm.getStringList("Global.Minigames"));
	int Counter = Main.MiGm.getInt("Global.Count");
		while(Counter > 0 ){
			Counter = Counter - 1;
			
			
			
			
			if(ev.getInventory().getName().contains("Duell Auf:")){
				String Arena=ev.getCurrentItem().getItemMeta().getLore().get(0);
				String Gegner = ev.getCurrentItem().getItemMeta().getDisplayName();
				p.closeInventory();
				p.performCommand("duell "+Gegner+" "+Arena);
				p.sendMessage("duell wird versucht zu erstellen");
			}
				
			
			
			
			
			
			
			if(ev.getInventory().getName().equalsIgnoreCase(p.getName()+"§b "+MiniGames.get(Counter))){
				
				
				
				if(Main.MiGm.getString(MiniGames.get(Counter)).equalsIgnoreCase("Duell")) {
					
					//Duell Gegen
					Inventory GameINV  = p.getServer().createInventory(null, 27,"Duell Auf:"+"§b "+ev.getCurrentItem().getItemMeta().getDisplayName()+"Gegen:");
					int Count = 1;
					for(Player on:Bukkit.getServer().getOnlinePlayers()){
						ItemStack Skull = new ItemStack(Material.PLAYER_HEAD);
						SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
						SMeta.setDisplayName(on.getName());
						ArrayList<String> Lore = new ArrayList<String>();
						Lore.add(ev.getCurrentItem().getItemMeta().getDisplayName());
						SMeta.setLore(Lore);
						SMeta.setOwningPlayer(on);
						Skull.setItemMeta(SMeta);
						GameINV.setItem(Count,Skull);
						Count +=1;
						}
					
					
					
				}else {
				p.performCommand(Main.MiGm.getString(MiniGames.get(Counter)+".StartCommand")+" "+ev.getCurrentItem().getItemMeta().getDisplayName());
				}
			}
		}
	}
	if(ev.getInventory().getName().equalsIgnoreCase(p.getName()+"§b Minispiele")){
		
		Inventory GameINV  = p.getServer().createInventory(null, 27,p.getName()+"§b "+ev.getCurrentItem().getItemMeta().getDisplayName());
		
		if(ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Duell")) {

			ArrayList<String> MiniGames = new ArrayList<String>();
			MiniGames.addAll(Main.MiGm.getStringList(ev.getCurrentItem().getItemMeta().getDisplayName()+".Arenas"));
			int Counter = Main.MiGm.getInt(ev.getCurrentItem().getItemMeta().getDisplayName()+".Count");
			
			while(Counter > 0 ){
				
				Counter = Counter - 1;
				Material mat = Material.getMaterial(Main.MiGm.getString(ev.getCurrentItem().getItemMeta().getDisplayName()+"."+MiniGames.get(Counter)+".Mat"));
					
					
					
					
					ItemStack Skull = new ItemStack(mat , 1);
					ItemMeta SMeta =  Skull.getItemMeta(); 
			    	SMeta.setDisplayName(MiniGames.get(Counter));
			    	Skull.setItemMeta(SMeta);
			    	
			    	GameINV.setItem(Counter,Skull);
				
				
			}
		}else {
			
		
		
		int Counter = Main.MiGm.getInt(ev.getCurrentItem().getItemMeta().getDisplayName()+".Count");
		ArrayList<String> MiniGames = new ArrayList<String>();
		MiniGames.addAll(Main.MiGm.getStringList(ev.getCurrentItem().getItemMeta().getDisplayName()+".Arenas"));
			while(Counter > 0 ){
				
				Counter = Counter - 1;
				Material mat = Material.getMaterial(Main.MiGm.getString(ev.getCurrentItem().getItemMeta().getDisplayName()+"."+MiniGames.get(Counter)+".Mat"));
					
					
					
					
					ItemStack Skull = new ItemStack(mat , 1);
					ItemMeta SMeta =  Skull.getItemMeta(); 
			    	SMeta.setDisplayName(MiniGames.get(Counter));
			    	Skull.setItemMeta(SMeta);
			    	
			    	GameINV.setItem(Counter,Skull);
				
				
			}
			p.openInventory(GameINV);
		Tree = true;
	}
	}
		


if(ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Warps")) {
	Player e = p;
	//------Menü-----
	Inventory Menue = e.getServer().createInventory(null, 27,e.getName()+"§b Warps");
	

	
	ItemStack Spawn = new ItemStack(Material.APPLE);
	ItemMeta Meta = Spawn.getItemMeta(); 
	Meta.setDisplayName("Spawn");
	Spawn.setItemMeta(Meta);
	Menue.setItem(10,Spawn);
	
	
		    	
	ItemStack Spawn2 = new ItemStack(Material.BLACK_BANNER);
	ItemMeta Meta2 = Spawn2.getItemMeta(); 
	Meta2.setDisplayName("Spiele");
	Spawn2.setItemMeta(Meta2);
	Menue.setItem(11,Spawn2);
	
	
	ItemStack Spawn3 = new ItemStack(Material.ACACIA_WOOD);
	ItemMeta Meta3 = Spawn3.getItemMeta(); 
	Meta3.setDisplayName("AREA City");
	Spawn3.setItemMeta(Meta3);
	Menue.setItem(12,Spawn3);
	
	ItemStack Skull = new ItemStack(Material.PLAYER_HEAD);
	SkullMeta SMeta = (SkullMeta) Skull.getItemMeta(); 
	SMeta.setDisplayName("Freunde");
	SMeta.setOwningPlayer(e);
	Skull.setItemMeta(SMeta);
	Menue.setItem(13,Skull);
	
	ItemStack Set = new ItemStack(Material.COMPASS);
	ItemMeta CMeta =  Set.getItemMeta(); 
	CMeta.setDisplayName("Einstellungen");
	Set.setItemMeta(CMeta);
	Menue.setItem(14,Set);
	
	ItemStack Meat = new ItemStack(Material.BAKED_POTATO);
	ItemMeta MMeta =  Meat.getItemMeta(); 
	MMeta.setDisplayName("Essen");
	Meat.setItemMeta(MMeta);
	Menue.setItem(20,Meat);
	if(e.hasPermission("Noneless.Creative.World")){
		ItemStack Creative = new ItemStack(Material.REDSTONE);
    	ItemMeta CMETA =  Creative.getItemMeta(); 
    	CMETA.setDisplayName("Kreativ welt");
    	Creative.setItemMeta(CMETA);
    	Menue.setItem(21,Creative);
		
	}
	if(e.hasPermission("Noneless.Admin.World")){
		ItemStack Creative = new ItemStack(Material.GLOWSTONE_DUST);
    	ItemMeta CMETA =  Creative.getItemMeta(); 
    	CMETA.setDisplayName("Admin AreaCity");
    	Creative.setItemMeta(CMETA);
    	Menue.setItem(22,Creative);
		
	}
	
	
	
	
	e.openInventory(Menue);
//------Menue
}
}




}











