package Main;



import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;


public class NonelessEventListener implements Listener 
{ 
	
	
	
	
		
		
	
	
    private static Main plugin;
    public int MainCounter = 0; 
    public boolean First = false;
    public boolean Tree = false;
    public boolean GlaDOSListen = false;
    public String GlaDOSFrage = "";
    public String GDOSVersion = "1.1 c"; 
    public String KiName = "BUSI";
    public String News = ""; 
    public String KiNameEditor;
   
	public NonelessEventListener(Main plugin) {
        NonelessEventListener.plugin = plugin;
        NonelessEventListener.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
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
	// An Timo -- Niemals etwas sagen, dies ist BUSI, Er wird eine kleiner unterst�tzer in not
	
	@EventHandler 
	public void onChat(AsyncPlayerChatEvent ev) throws IOException {	
		
		String Message = ev.getMessage();
		Message = Message.toLowerCase();
		if(Message.contains("h�cke") && Message.contains("server")&Message.contains("!?")) {
			ev.getPlayer().setHealth(0);
			String Speak = "Ups, ich glaube das war absicht";
			Bukkit.broadcastMessage("�4"+KiName+": �f"+Speak);
			
	ev.setCancelled(true);
					
			
		
				
					Speak_Class.Speak(Speak);
			ev.setCancelled(true);
		}else if(Message.contains("hilf")&Message.contains("!?")) {
			if(!Busi.web.isConnected()) {
				Busi.web.connect();
			}
			if(!Busi.web.listallquest(KiName)) {
				Bukkit.broadcastMessage("�4"+KiName+": �f Ich habe derzeit ein paar schwierigkeiten mich mit der Datenbank zu verbinden");
			}
			Bukkit.broadcastMessage("�4"+KiName+": �f mconly: ");
			ev.setCancelled(true);
			ArrayList<String> Glados = new ArrayList<String>();
			Glados.addAll(Main.GDOS.getStringList("GlaDOS.List"));
			int count = Glados.size();
			int Count2 = 0;
			while(Count2 <= count) {
				
				Bukkit.broadcastMessage("�f"+Glados.get(Count2));
				Count2 = Count2 +1;
			}
			
				
		
			
			ev.setCancelled(true);
			
			
			
		}else if(Message.contains("teleportiere") &Message.contains("!?")) {
			
			
			if(Message.contains("areacity")) {
				Bukkit.broadcastMessage("�4"+KiName+": �fMach ich");
				ev.setCancelled(true);
				Double x = Main.loc.getDouble("AREA.X");
				Double y = Main.loc.getDouble("AREA.Y");
				Double z = Main.loc.getDouble("AREA.Z");
				Float yaw = (float) Main.loc.getDouble("AREA.Yaw");
				Float pitch = (float) Main.loc.getDouble("AREA.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("AREA.World"));
				ev.getPlayer().teleport(new Location(w,x,y,z,yaw,pitch));
				
			}
			
			
			
		
		
		
		
		}else if(Message.contains("version")&Message.contains("!?")) {
			Bukkit.broadcastMessage("�4"+KiName+": �fich bin "+KiName+" in der Version: "+GDOSVersion);
			ev.setCancelled(true);
		}else if(Message.contains("easymode")&Message.contains("!?")) {
		if(Main.GDOS.getBoolean(ev.getPlayer().getName().toString()+".EasyMode.Enable")==false){
			Bukkit.broadcastMessage("�4"+KiName+": �fOkay ich stelle dein Easymode ein "+ev.getPlayer().getDisplayName());
			ev.setCancelled(true);
			Main.GDOS.set(ev.getPlayer().getName().toString()+".EasyMode.Enable",true);
		}else if(Main.GDOS.getBoolean(ev.getPlayer().getName().toString()+".EasyMode.Enable")){
			Bukkit.broadcastMessage("�4"+KiName+": �fOkay ich stelle dein Easymode aus "+ev.getPlayer().getDisplayName());
			ev.setCancelled(true);
			Main.GDOS.set(ev.getPlayer().getName().toString()+".EasyMode.Enable",false);
		}
		}
		else {
		if (Message.equalsIgnoreCase("ist der kuchen eine l�ge?")){
			Bukkit.broadcastMessage("�4"+KiName+": �falsob du das nicht schon w�sstest");
			ev.setCancelled(true);
			
		}else	
		if(GlaDOSListen) {
			if(KiNameEditor==ev.getPlayer().getName()) {
			if(Message.contains("abbrechen!!?")) {
				ev.setCancelled(true);
				Bukkit.broadcastMessage("�4"+KiName+": �f Ich habe den vorgang abgebrochen");
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
			Bukkit.broadcastMessage("�4"+KiName+": �f Absofort werde ich das auf die Frage Antworten Danke Spieler");
			
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
				Bukkit.broadcastMessage("�4"+KiName+": �f Hey, Sorry aber du bist nicht der den ich meine");
			}
		}else {
		if(Main.GDOS.getBoolean(ev.getPlayer().getName()+".Enable")) {
			if(Message.contains("!?")) {
				if(Message == "!?") {
					ev.setCancelled(true);
					Bukkit.broadcastMessage("�4"+KiName+": �f Dazu kann ich nichts sagen");
					
				}else {
					ArrayList<String> Glados = new ArrayList<String>();
					Glados.addAll(Main.GDOS.getStringList("GlaDOS.List"));
					if(Glados.contains(Message)) {
						ev.setCancelled(true);
						Bukkit.broadcastMessage("�4"+KiName+":�f "+ Main.GDOS.getString("GlaDOS."+Message+".answer"));
						Speak_Class.Speak(Main.GDOS.getString("GlaDOS."+Message+".answer"));
					}else {
						ev.setCancelled(true);
						
						
						if(!Busi.web.isConnected()) {
							Busi.web.connect();
						}
						
						if(Busi.web.isConnected()) {
							Bukkit.broadcastMessage("�4"+KiName+":�f "+Busi.web.Answere(Message));
							
							
							
							
							
						}else {
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
	    	Menues.Warps.Spawn(ep);
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
						Bukkit.broadcastMessage("�4"+KiName+":�f Da Habe ich Einen Creeper Erwischt");
						}else {
							Bukkit.broadcastMessage("�4"+KiName+":�f Leider Habe ich keine Lust den Creeper zu t�ten, Sorry");	
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
							Bukkit.broadcastMessage("�4"+KiName+":�f Da Habe ich Einen ZOMBIE Erwischt");
							}else {
								Bukkit.broadcastMessage("�4"+KiName+":�f Leider Habe ich keine Lust den ZOMBIE zu t�ten, Sorry");	
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
			
		}else {
			ev.setCancelled(true);
		}
		}
	}
	
public static void TeleporttoServer(Player p, String Server) {
	ByteArrayOutputStream b = new ByteArrayOutputStream();
	DataOutputStream out = new DataOutputStream(b);
	 
	try {
	    out.writeUTF("Connect");
	    out.writeUTF(Server); // Target Server
	} catch (IOException ex) {
	    // Can never happen
	}

	p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
}
	
	
	@EventHandler
	public void InventoryClick(InventoryClickEvent ev) {

		
		ArrayList<String> Friends = new ArrayList<String>();
		Player p = (Player) ev.getWhoClicked();
		
		if(p.getOpenInventory().getTitle().equalsIgnoreCase(p.getName()+"�b Noneless Lobby")){
			ev.setCancelled(true);
			
			
			//---------------------Warbs Menue 
			if(ev.getCurrentItem().getType() == Material.APPLE) {
				
				Double x = Main.loc.getDouble("spawn.X");
				Double y = Main.loc.getDouble("spawn.Y");
				Double z = Main.loc.getDouble("spawn.Z");
				Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
				Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
				
				
			}else	if(ev.getCurrentItem().getType() == Material.REDSTONE) {
				
				TeleporttoServer(p, "creative");
				
				
			}else	if(ev.getCurrentItem().getType() == Material.GLOWSTONE_DUST) {
				
				
				Double x = Main.loc.getDouble("Admin.X");
				Double y = Main.loc.getDouble("Admin.Y");
				Double z = Main.loc.getDouble("Admin.Z");
				Float yaw = (float) Main.loc.getDouble("Admin.Yaw");
				Float pitch = (float) Main.loc.getDouble("Admin.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("Admin.World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
				
				
				
				
				
			}else	if(ev.getCurrentItem().getType() == Material.BLACK_BANNER) {
				
				Menues.Games.Spawn(p);
				
			}else	if(ev.getCurrentItem().getType() == Material.COMPASS) {
				
				
				
				Menues.Settings.Spawn(p);
				
			
				
				
				
				
				
			}else	if(ev.getCurrentItem().getType() == Material.ACACIA_WOOD) {
				TeleporttoServer(p, "areacity_1");
				Double x = Main.loc.getDouble("AREA.X");
				Double y = Main.loc.getDouble("AREA.Y");
				Double z = Main.loc.getDouble("AREA.Z");
				Float yaw = (float) Main.loc.getDouble("AREA.Yaw");
				Float pitch = (float) Main.loc.getDouble("AREA.Pitch");
				org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("AREA.World"));
				p.teleport(new Location(w,x,y,z,yaw,pitch));
			
				
			}else if(ev.getCurrentItem().getType() == Material.PLAYER_HEAD) {
				Menues.Freunde.Spawn(p);
					
				
			}else	if(ev.getCurrentItem().getType() == Material.BAKED_POTATO) {
				p.setHealth(20);
				p.setFoodLevel(20);
				p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EAT, 10, 1);
			}else	if(ev.getCurrentItem().getType() == Material.GOLDEN_PICKAXE) {
				if(ev.getCurrentItem().getItemMeta().getDisplayName().contains("SkyBlock")) {
					TeleporttoServer(p, "survival_2");
					}else if(ev.getCurrentItem().getItemMeta().getDisplayName().contains("Games")) {
						TeleporttoServer(p, "games");
					}
				
				
			}
			
		}
	}
	
	@EventHandler
	public void ItemClick(PlayerDropItemEvent ev) {
		Player p = ev.getPlayer();
			if(p.getWorld().getName()==Main.loc.getString("spawn.world")) {
				
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
	    	
	    	
	    	
	    	
	    	ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bookMeta = (BookMeta) book.getItemMeta();
					
					
		
		
			BaseComponent[] page1 = new ComponentBuilder("Hallo und herzlich Wilkommen in der Noneunity \n\n").append("Hier kommst du direkt zu unsserer Webseite")
			        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://Noneless.de"))
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Noneless.de").create()))
			        .create();
			BaseComponent[] page2 = new ComponentBuilder("Noneunity Inhalt \n\n").append("Seite 3: Datenschutz hinweise "+ChatColor.RED+"(Bitte Lesen)\n\n")
			        .event(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "3" ))
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Seite 3").create()))
			        .append("Seite 4: Wichtige Befehle \n\n")
			        .event(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "4" ))
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Seite 4").create()))
			        .append("Seite 5: Entwickler und Wichtige Personen \n\n")
			        .event(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "5" ))
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Seite 5").create()))
			        .append("Seite 6: Weiteres \n")
			        .event(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "6" ))
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Seite 6").create()))
			        .create();
			
			
			
			BaseComponent[] page3 = new ComponentBuilder("Noneunity Datenschutz Eure Daten sind uns Wichtig wir werden uns bem�hen eure date zu sichern, f�r den vollen funktions umfang werden euer Spielername auf unserer Webseite ver�ffentlicht, dies passiert sobalt ihr �ber eine Stunde online seit.").append(ChatColor.GREEN+" Webseite")
			        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://Noneless.de"))
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Noneless.de").create()))
			        .create();
			BaseComponent[] page5 = new ComponentBuilder("Wilkommen in der Noneunity \n\n Collerbasti: Admin Entwickler\n\n _Timo__: Vermieter\n\nSheireen25: Builder\n\n").append(" Webseite")
			        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://Noneless.de"))
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Noneless.de").create()))
			        .create();
			BaseComponent[] page4 = new ComponentBuilder("Befehle:\n\n /lobby /spawn").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(" Bringen euch zur�ck zum Spawn").create()))
					.append("\n\n/webregister").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("um euch auf unserere webseite zu registrieren").create()))
					.append(ChatColor.GREEN+"\n\n\nWebseite")
			        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://Noneless.de"))
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Noneless.de").create()))
			        .create();
			BaseComponent[] page6 = new ComponentBuilder("Hier k�nnte deine Werbung Stehen ^^ \n\n").append("Webseite")
			        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://Noneless.de"))
			        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Noneless.de").create()))
			        .create();
			
			bookMeta.spigot().addPage(page1);
			bookMeta.spigot().addPage(page2);
			bookMeta.spigot().addPage(page3);
			bookMeta.spigot().addPage(page4);
			bookMeta.spigot().addPage(page5);
			bookMeta.spigot().addPage(page6);
					bookMeta.setTitle("Interactive Book");
					bookMeta.setAuthor("Noneless");
					book.setItemMeta(bookMeta);
					
					p.getInventory().setItem(1,book);
					
	    	
	    	
	    	
	    	
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
		if(p.getOpenInventory().getTitle().equalsIgnoreCase(p.getName()+"�b Freunde")){
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
			  	//------Men�-----
		    	Menues.Warps.Spawn(e);
	//------Menue

				
			}
	}
	}



@EventHandler
public void InventorySettingsClick(InventoryClickEvent ev) {
	Player p = (Player) ev.getWhoClicked();
	if( p.getOpenInventory().getTitle().equalsIgnoreCase(p.getName()+"�b Settings")){
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
			
			Menues.Settings.Spawn(p);			
			//----------------
			
			
		}else if(ev.getCurrentItem().getType() == Material.BARRIER) {
			p.closeInventory();
			Player e = p.getPlayer();
		  	//------Men�-----
	    	
	    	Menues.Warps.Spawn(e);
	    	
	    	
	    	
	    	
//------Menue
		} else if(p.getOpenInventory().getTitle().equalsIgnoreCase(p.getName()+"�b Settings")){
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
				Menues.Settings.Spawn(p);
				
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
				
				Menues.Settings.Spawn(p);
				
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
				
				Inventory Settings = p.getServer().createInventory(null, 27,p.getName()+"�b Settings");
				
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
		    	BMeta.setDisplayName("Zur�ck");
		    	Back.setItemMeta(BMeta);
		    	Settings.setItem(26,Back);
				
				p.openInventory(Settings);
				
				//----------------
		
		
	}
}
	}

	
	
	
		


if(ev.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Warps")) {
	Player e = p;
	//------Men�-----
	Menues.Warps.Spawn(e);
//------Menue
}
}



}











