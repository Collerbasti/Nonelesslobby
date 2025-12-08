package de.noneless;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

import de.noneless.Main;
import de.noneless.Menues.Settings;
import de.noneless.Menues.Games;
import de.noneless.Menues.Freunde;
import de.noneless.Menues.Warps;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NonelessEventListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger(NonelessEventListener.class.getName());
    private static Main plugin;
    private String KiName = "BUSI";
    public int MainCounter = 0;
    public boolean First = false;
    public boolean Tree = false;
    public boolean GlaDOSListen = false;
    public String GlaDOSFrage = "";
    public String GDOSVersion = "1.1 c";
    public String News = "";
    public String KiNameEditor;
    
    public NonelessEventListener(Main plugin) {
        NonelessEventListener.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        LOGGER.info("NonelessEventListener registriert.");
    }
    
    private void sendMessage(Player player, String message) {
        player.sendMessage(message);
    }
    
    private void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    public void Tick(Time ev) {
        if (ev.getHours() == 0 && ev.getDay() == 1) {
            try {
                MySQL.disconnect();
                MySQL.connect();
                LOGGER.info("MySQL Verbindung wurde für Wochenwechsel neu gestartet.");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Fehler beim MySQL Wochenwechsel", e);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if(Main.Frdb.getBoolean(player.getName()+".VIP.Enable")) {
            if(Calendar.getInstance().getTime().after((Date) Main.Frdb.get(player.getName()+".VIP.Expression"))) {
                Main.Frdb.set(player.getName()+".VIP.Enable", false);
                
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                String command = "/manudelsub "+ player.getName()+" VIP";
                Bukkit.dispatchCommand(console, command);
            }
        }
        
        Main.Frdb.set(player.getName()+".isOnline", true);
        if(Main.Frdb.getBoolean(player.getName()+".webregister")) {
            try {
                PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE PROFILELIST SET isOnline = 'ja' WHERE SPIELERNAME = ?");
                ps.setString(1, player.getName());
                ps.executeUpdate();
            } catch (SQLException e) {
                sendMessage(player, ChatColor.RED + "Sorry, aber irgendetwas ist schiefgelaufen (" + e + ")");
                e.printStackTrace();
            }
        }

        Main.Frdb.set(player.getName()+".Name", player.getName());
        MainCounter = MainCounter+1;
        if(MainCounter == 10) {
            try {
                Main.Frdb.save(Main.Friends);
            } catch (IOException e) {
                e.printStackTrace();
            }
            MainCounter = 0;
        }

        Location alt = player.getLocation();
        Double x = Main.loc.getDouble("spawn.X");
        Double y = Main.loc.getDouble("spawn.Y");
        Double z = Main.loc.getDouble("spawn.Z");
        Float yaw = (float) Main.loc.getDouble("spawn.Yaw");
        Float pitch = (float) Main.loc.getDouble("spawn.Pitch");
        org.bukkit.World w = Bukkit.getWorld(Main.loc.getString("spawn.World"));
        
        sendMessage(player, "Hallo und Herzlich Willkommen in der Noneless Comunity");
        sendMessage(player, "Was gibt es Neues? " + News);
        
        player.teleport(alt);
        player.teleport(new Location(w,x,y,z,yaw,pitch));
        
        if(First == false) {
            if(player.hasPermission("Noneless.lobby.set")) {
                sendMessage(player, "Hey, Wegen dem Neustart wurde Die Lobby Jetzt Automatisch neu gesetzt");
                player.performCommand("setlobby");
                player.performCommand("Spawn");
                First = true;
            }
        }
        
        createWelcomeBook(player);
        
        // Gamemode überschreibend setzen
        boolean gm = Main.Frdb.getBoolean(player.getName() + ".Gamemode");
        Long since = null;
        try { since = Main.Frdb.contains(player.getName() + ".GamemodeCreativeSince") ? Main.Frdb.getLong(player.getName() + ".GamemodeCreativeSince") : null; } catch(Exception ignored) {}
        if (gm && since != null) {
            long now = System.currentTimeMillis();
            if (now - since > 24L * 60 * 60 * 1000) {
                // Mehr als 24h vergangen: zurück auf Adventure
                Main.Frdb.set(player.getName() + ".Gamemode", false);
                Main.Frdb.set(player.getName() + ".GamemodeCreativeSince", null);
                try { Main.Frdb.save(Main.Friends); } catch(Exception ignored) {}
                player.setGameMode(GameMode.ADVENTURE);
            } else {
                player.setGameMode(GameMode.CREATIVE);
            }
        } else if (gm) {
            player.setGameMode(GameMode.CREATIVE);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    private void createWelcomeBook(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        
        BaseComponent[] page1 = new ComponentBuilder("Hallo und herzlich Wilkommen in der Noneunity \n\n")
            .append("Hier kommst du direkt zu unsserer Webseite")
            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://Noneless.de"))
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Noneless.de").create()))
            .create();

        BaseComponent[] page2 = new ComponentBuilder("§4WorldEdit: //wand\n\n")
            .append("§7Mit diesem Befehl aktivierst du das WorldEdit-Werkzeug (Standard: Holzaxt)\n\n")
            .append("§7Linksklick: Setzt Position 1\n")
            .append("§7Rechtsklick: Setzt Position 2\n")
            .create();

        BaseComponent[] page3 = new ComponentBuilder("§4WorldEdit: //set\n\n")
            .append("§7Befehl: §6//set <block>\n\n")
            .append("§7Füllt die gesamte Auswahl mit dem angegebenen Block\n\n")
            .append("§7Beispiel: §6//set stone")
            .create();

        BaseComponent[] page4 = new ComponentBuilder("§4WorldEdit: //replace\n\n")
            .append("§7Befehl: §6//replace <block> <newblock>\n\n")
            .append("§7Ersetzt alle Blöcke des ersten Typs durch den zweiten Typ")
            .create();

        BaseComponent[] page5 = new ComponentBuilder("§4WorldEdit: //copy\n\n")
            .append("§7Kopiert die aktuelle Auswahl in den Zwischenspeicher\n\n")
            .append("§7Nutze §6//paste§7 um die Kopie einzufügen")
            .create();

        BaseComponent[] page6 = new ComponentBuilder("§4WorldEdit: //sphere\n\n")
            .append("§7Befehl: §6//sphere <block> <radius>\n\n")
            .append("§7Erstellt eine massive Kugel\n\n")
            .append("§7Beispiel: §6//sphere stone 5")
            .create();

        BaseComponent[] page7 = new ComponentBuilder("§4WorldEdit: //undo\n\n")
            .append("§7Macht die letzte WorldEdit-Aktion rückgängig\n\n")
            .append("§2Alle WorldEdit Befehle (nicht offiziell):\n")
            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.mmo-core.de/thread/27470-world-edit-kommandos-commands-deutsch/"))
            .append("§9§nKlicke hier für die deutsche Befehlsliste")
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Öffne deutsche WorldEdit Befehlsliste").create()))
            .append("§2Weitere Infos im Wiki:\n")
            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://worldedit.enginehub.org/en/latest/"))
            .append("§9§nKlicke hier für das WorldEdit Wiki")
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Öffne WorldEdit Dokumentation").create()))
            .create();
            
        if (player.hasPermission("worldedit.*")) {
            bookMeta.spigot().setPages(page1, page2, page3, page4, page5, page6, page7);
        } else {
            bookMeta.spigot().setPages(page1);
        }
        
        bookMeta.setTitle("Noneunity Guide");
        bookMeta.setAuthor("Noneless Team");
        
        book.setItemMeta(bookMeta);
        player.getInventory().setItem(1, book);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage().toLowerCase();
        Player player = event.getPlayer();
        if(message.contains("hacke") && message.contains("server") && message.contains("!?")) {
            player.setHealth(0);
            broadcast(ChatColor.RED + KiName + ": " + "Ups, ich glaube das war Absicht");
            event.setCancelled(true);
            Speak_Class.Speak("Ups, ich glaube das war Absicht");
        } else if(message.contains("hilf") && message.contains("!?")) {
            if(!de.noneless.Busi.web.isConnected()) {
                de.noneless.Busi.web.connect();
            }
            
            if(!de.noneless.Busi.web.listallquest(KiName)) {
                broadcast(ChatColor.RED + KiName + ": " + "Ich habe derzeit ein paar Schwierigkeiten mich mit der Datenbank zu verbinden");
            }
            
            broadcast(ChatColor.RED + KiName + ": " + "mconly: ");
            event.setCancelled(true);
            
            ArrayList<String> glados = new ArrayList<>(Main.GDOS.getStringList("GlaDOS.List"));
            for(String line : glados) {
                broadcast(line);
            }
        }
    }

    public static void TeleporttoServer(Player p, String Server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        
        try {
            out.writeUTF("Connect");
            out.writeUTF(Server);
        } catch (IOException ex) {
            // Can never happen
        }
        
        p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        Main.Frdb.set(player.getName()+".isOnline", false);
        
        if(Main.Frdb.getBoolean(player.getName()+".webregister")) {
            try {
                PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE PROFILELIST SET isOnline = 'nein' WHERE SPIELERNAME = ?");
                ps.setString(1, player.getName());
                ps.executeUpdate();
            } catch (SQLException e) {
                sendMessage(player, ChatColor.RED + "Sorry, aber irgendetwas ist schiefgelaufen (" + e + ")");
                e.printStackTrace();
            }
        }
        
        try {
            Main.Frdb.save(Main.Friends);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainCounter = 0;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(player.getWorld().getName().equals(Main.loc.getString("spawn.World"))) {
            event.setCancelled(true);
        } else if(player.getGameMode().equals(GameMode.CREATIVE)) {
            // Allow in creative mode
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity.getType().equals(EntityType.PLAYER)) {
            Player player = Bukkit.getServer().getPlayer(entity.getName());
            if(player.getWorld().getName().equals(Main.loc.getString("spawn.World"))) {
                event.setCancelled(true);
            } else if(player.getGameMode().equals(GameMode.CREATIVE)) {
                // Allow in creative mode
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getItem() == null || event.getItem().getItemMeta() == null) return;
        
        String itemName = event.getItem().getItemMeta().getDisplayName();
        Player player = event.getPlayer();

        if(itemName.equalsIgnoreCase("Warps")) {
            Warps.Spawn(player);
        }

        // EasyMode Zauberstab
        if(event.getItem().getType() == Material.STICK && 
           event.getItem().getItemMeta().getDisplayName().equals("ZauberStab")) {
            
            if(Main.GDOS.getBoolean(player.getName() + ".EasyMode.Enable")) {
                for(Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if(entity.getType() == EntityType.CREEPER || entity.getType() == EntityType.ZOMBIE) {
                        if(Main.GDOS.getInt(player.getName() + ".EasyMode.Magic") > 10) {
                            Location loc = entity.getLocation();
                            entity.teleport(new Location(
                                loc.getWorld(),
                                loc.getX(),
                                loc.getY() + 1000,
                                loc.getZ(),
                                loc.getYaw(),
                                loc.getPitch()
                            ));
                            
                            Main.GDOS.set(player.getName() + ".EasyMode.Magic",
                                Main.GDOS.getInt(player.getName() + ".EasyMode.Magic") - 10);
                                
                            broadcast(ChatColor.RED + KiName + ": " + "Da habe ich einen " + 
                                (entity.getType() == EntityType.CREEPER ? "Creeper" : "ZOMBIE") + 
                                " erwischt");
                        } else {
                            broadcast(ChatColor.RED + KiName + ": " + "Leider habe ich keine Lust den " + 
                                (entity.getType() == EntityType.CREEPER ? "Creeper" : "ZOMBIE") + 
                                " zu töten, Sorry");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        // Cancel inventory interactions in spawn world
        if(player.getWorld().getName().equals(Main.loc.getString("spawn.World"))) {
            if(!player.getGameMode().equals(GameMode.CREATIVE)) {
                event.setCancelled(true);
            }
        }

        // Settings-Menü-Logik
        if(event.getView().getTitle().equals(player.getName() + "§b Settings")) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
            int slot = event.getRawSlot();
            switch(slot) {
                case 2: // Freunde-Teleport umschalten
                    boolean allowFriendsTp = Main.Frdb.getBoolean(player.getName() + ".AllowFriendsTp");
                    Main.Frdb.set(player.getName() + ".AllowFriendsTp", !allowFriendsTp);
                    try { Main.Frdb.save(Main.Friends); } catch(Exception ignored) {}
                    de.noneless.Menues.Settings.Spawn(player);
                    break;
                case 4: // Gamemode umschalten (nur mit Permission)
                    if(player.hasPermission("Noneless.Admin.Gamemode")) {
                        boolean gm = Main.Frdb.getBoolean(player.getName() + ".Gamemode");
                        Main.Frdb.set(player.getName() + ".Gamemode", !gm);
                        if (!gm) {
                            // Creative aktiviert: Zeitstempel setzen
                            Main.Frdb.set(player.getName() + ".GamemodeCreativeSince", System.currentTimeMillis());
                        } else {
                            // Adventure aktiviert: Zeitstempel entfernen
                            Main.Frdb.set(player.getName() + ".GamemodeCreativeSince", null);
                        }
                        try { Main.Frdb.save(Main.Friends); } catch(Exception ignored) {}
                        // Gamemode sofort setzen
                        if (!gm) {
                            player.setGameMode(org.bukkit.GameMode.CREATIVE);
                        } else {
                            player.setGameMode(org.bukkit.GameMode.ADVENTURE);
                        }
                        de.noneless.Menues.Settings.Spawn(player);
                    }
                    break;
                case 26: // Zurück-Button
                    de.noneless.Menues.Warps.Spawn(player);
                    break;
                default:
                    break;
            }
            return;
        }

        // Handle menu clicks
        if(event.getView().getTitle().equals(player.getName() + "§b Noneless Lobby")) {
            event.setCancelled(true);
            
            if(event.getCurrentItem() == null) return;
            
            switch(event.getCurrentItem().getType()) {
                case ENDER_PEARL:
                    // Warps-Menü öffnen (EssentialsX)
                    try {
                        org.bukkit.plugin.Plugin ess = org.bukkit.Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                        if (ess != null) {
                            java.lang.reflect.Method getWarps = ess.getClass().getMethod("getWarps");
                            Object warpsObj = getWarps.invoke(ess);
                            java.lang.reflect.Method getList = warpsObj.getClass().getMethod("getList");
                            java.util.List<String> warps = (java.util.List<String>) getList.invoke(warpsObj);
                            de.noneless.Menues.WarpsMenu.open(player, warps);
                        } else {
                            player.sendMessage("§cEssentialsX nicht gefunden!");
                        }
                    } catch (Exception ex) {
                        player.sendMessage("§cFehler beim Laden der Warps!");
                    }
                    break;
                case RED_BED:
                    // Homes-Menü öffnen (EssentialsX)
                    try {
                        org.bukkit.plugin.Plugin ess = org.bukkit.Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                        if (ess != null) {
                            java.lang.reflect.Method getUser = ess.getClass().getMethod("getUser", org.bukkit.entity.Player.class);
                            Object user = getUser.invoke(ess, player);
                            java.lang.reflect.Method getHomes = user.getClass().getMethod("getHomes");
                            java.util.Collection<String> homes = (java.util.Collection<String>) getHomes.invoke(user);
                            de.noneless.Menues.HomesMenu.open(player, new java.util.ArrayList<>(homes));
                        } else {
                            player.sendMessage("§cEssentialsX nicht gefunden!");
                        }
                    } catch (Exception ex) {
                        player.sendMessage("§cFehler beim Laden der Homes: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    break;
                case APPLE:
                    // Teleport to spawn
                    player.teleport(new Location(
                        Bukkit.getWorld(Main.loc.getString("spawn.World")),
                        Main.loc.getDouble("spawn.X"),
                        Main.loc.getDouble("spawn.Y"),
                        Main.loc.getDouble("spawn.Z"),
                        (float) Main.loc.getDouble("spawn.Yaw"),
                        (float) Main.loc.getDouble("spawn.Pitch")
                    ));
                    break;
                    
                case REDSTONE:
                    TeleporttoServer(player, "creative");
                    break;
                    
                case GLOWSTONE_DUST:
                    // Teleport to admin area
                    player.teleport(new Location(
                        Bukkit.getWorld(Main.loc.getString("Admin.World")),
                        Main.loc.getDouble("Admin.X"),
                        Main.loc.getDouble("Admin.Y"),
                        Main.loc.getDouble("Admin.Z"),
                        (float) Main.loc.getDouble("Admin.Yaw"),
                        (float) Main.loc.getDouble("Admin.Pitch")
                    ));
                    break;
                    
                case BLACK_BANNER:
                    Games.Spawn(player);
                    break;
                    
                case COMPASS:
                    Settings.Spawn(player);
                    break;
                    
                case ACACIA_WOOD:
                    TeleporttoServer(player, "areacity_1");
                    player.teleport(new Location(
                        Bukkit.getWorld(Main.loc.getString("AREA.World")),
                        Main.loc.getDouble("AREA.X"),
                        Main.loc.getDouble("AREA.Y"),
                        Main.loc.getDouble("AREA.Z"),
                        (float) Main.loc.getDouble("AREA.Yaw"),
                        (float) Main.loc.getDouble("AREA.Pitch")
                    ));
                    break;
                    
                case PLAYER_HEAD:
                    Freunde.Spawn(player);
                    break;
                    
                case BAKED_POTATO:
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 10, 1);
                    break;
                    
                case GOLDEN_PICKAXE:
                    String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
                    if(itemName.contains("SkyBlock")) {
                        TeleporttoServer(player, "survival_2");
                    } else if(itemName.contains("Games")) {
                        TeleporttoServer(player, "games");
                    }
                    break;
                case BRICKS:
                    // BauLobby-Warp (EssentialsX)
                    player.closeInventory();
                    player.performCommand("warp BauLobby");
                    break;
                default:
                    // Alle anderen Items werden ignoriert
                    break;
            }
        }
        
        // Warps-Menü-Logik
        if(event.getView().getTitle().equals("§b Warps")) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
            String warpName = event.getCurrentItem().getItemMeta().getDisplayName();
            player.closeInventory();
            player.performCommand("warp " + warpName);
            return;
        }
        // Homes-Menü-Logik
        if(event.getView().getTitle().equals("§b Homes")) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
            String homeName = event.getCurrentItem().getItemMeta().getDisplayName();
            player.closeInventory();
            player.performCommand("home " + homeName);
            return;
        }
        // Freunde-Menü-Logik
        if(event.getView().getTitle().equals(player.getName() + "§b Freunde")) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
            String friendName = event.getCurrentItem().getItemMeta().getDisplayName();
            if(friendName.equals("Zurück")) {
                de.noneless.Menues.Warps.Spawn(player);
                return;
            }
            // Nur echte Spielernamen (keine Offline/Leeren)
            if(friendName.startsWith("§4"))
            { 
                    player.sendMessage( friendName +" ist offline.");
            
                return; // Offline
            }
            // Prüfe, ob Teleport erlaubt ist
            boolean allow = Main.Frdb.getBoolean(friendName + ".AllowFriendsTp");
            boolean isOnline = Main.Frdb.getBoolean(friendName + ".isOnline");
            if(allow && isOnline) {
                Player target = org.bukkit.Bukkit.getPlayer(friendName);
                if(target != null) {
                    player.closeInventory();
                    player.teleport(target.getLocation());
                    player.sendMessage("§aDu wurdest zu " + friendName + " teleportiert!");
                }
            } else {
                player.sendMessage("§cTeleport zu diesem Freund ist nicht erlaubt oder der Spieler ist offline.");
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        String world = location.getWorld().getName();

        // Update player's world in database
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement(
                "UPDATE PROFILELIST SET WORLD=? WHERE SPIELERNAME=?");
            ps.setString(1, world);
            ps.setString(2, player.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Handle spawn world inventory
        if(world.equals(Main.loc.getString("spawn.World"))) {
            player.getInventory().clear();
            
            // Add Warps item
            ItemStack warps = new ItemStack(Material.MINECART);
            ItemMeta warpsMeta = warps.getItemMeta();
            warpsMeta.setDisplayName("Warps");
            warps.setItemMeta(warpsMeta);
            player.getInventory().setItem(8, warps);
            
            // Add welcome book
            createWelcomeBook(player);
            
            player.updateInventory();
        }
        
        // Gamemode überschreibend setzen mit 24h-Timeout
        boolean gm = Main.Frdb.getBoolean(player.getName() + ".Gamemode");
        Long since = null;
        try { since = Main.Frdb.contains(player.getName() + ".GamemodeCreativeSince") ? Main.Frdb.getLong(player.getName() + ".GamemodeCreativeSince") : null; } catch(Exception ignored) {}
        if (gm && since != null) {
            long now = System.currentTimeMillis();
            if (now - since > 24L * 60 * 60 * 1000) {
                // Mehr als 24h vergangen: zurück auf Adventure
                Main.Frdb.set(player.getName() + ".Gamemode", false);
                Main.Frdb.set(player.getName() + ".GamemodeCreativeSince", null);
                try { Main.Frdb.save(Main.Friends); } catch(Exception ignored) {}
                player.setGameMode(GameMode.ADVENTURE);
            } else {
                player.setGameMode(GameMode.CREATIVE);
            }
        } else if (gm) {
            player.setGameMode(GameMode.CREATIVE);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        boolean gm = Main.Frdb.getBoolean(player.getName() + ".Gamemode");
        GameMode forced = gm ? GameMode.CREATIVE : GameMode.ADVENTURE;
        if (event.getNewGameMode() != forced) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> player.setGameMode(forced), 1L);
        }
    }
}