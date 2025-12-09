package de.noneless.lobby;

import Config.ConfigManager;
import Config.GamemodeSettingsConfig;
import friends.FriendManager;
import npc.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;
import Mysql.Punkte;
import de.noneless.lobby.scoreboard.LobbyScoreboard;
import de.noneless.lobby.util.GamemodeEnforcer;
import de.noneless.lobby.world.WorldMoverService;

public class Main extends JavaPlugin {
    
    private static Main instance;
    private NPCManager npcManager;
    private de.noneless.lobby.news.NewsManager newsManager;
    private Location lobbyLocation;
    private WorldMoverService worldMoverService;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Lade Konfiguration zuerst
        loadConfig();
        
        Punkte.initializeDatabase();
        
        // Initialisiere Settings-Konfigurationen
        initializeSettingsConfigs();
        GamemodeSettingsConfig.initialize(this);
        LobbyScoreboard.init(this);
        FriendManager.initialize(this);
        
        // Initialisiere NPC Manager
        npcManager = new NPCManager(this);
        newsManager = new de.noneless.lobby.news.NewsManager(this);
        worldMoverService = new WorldMoverService(this);
        worldMoverService.resumePendingJob();
        GamemodeEnforcer.start(this);
        
        // Registriere Commands
        registerCommands();
        
        // Registriere Event Listener
        registerEventListeners();
        // register news join listener
        getServer().getPluginManager().registerEvents(new de.noneless.lobby.news.PlayerJoinNewsListener(this, newsManager), this);
        
        // Spawne NPCs
        if (npcManager.isCitizensAvailable()) {
            npcManager.spawnLobbyNPCs();
        }

        // expose NewsManager to other classes (already set)

        Bukkit.getScheduler().runTaskLater(this, LobbyScoreboard::updateAll, 1L);
        
        getLogger().info("§aNonelessLobby erfolgreich gestartet!");
    }
    
    @Override
    public void onDisable() {
        // Entferne NPCs
        if (npcManager != null) {
            npcManager.removeAllLobbyNPCs();
        }
        FriendManager.shutdown();
        LobbyScoreboard.shutdown();
        if (worldMoverService != null) {
            worldMoverService.shutdown();
        }
        
        Punkte.saveAllPoints();
        GamemodeSettingsConfig.save();
        saveSettingsConfigs(); // Speichere Player-Einstellungen
        
        getLogger().info("§cNonelessLobby gestoppt!");
    }
    
    private void loadConfig() {
        // Standard-Lobby-Position
        ConfigManager.initialize(this);
        lobbyLocation = ConfigManager.getLobbyLocation();
        if (lobbyLocation == null) {
            World world = getServer().getWorld("world");
            if (world != null) {
                lobbyLocation = world.getSpawnLocation();
            }
        }
    }
    
    private void registerCommands() {
        // Registriere alle Commands mit Executors
        safeRegisterCommand("lobby", new de.noneless.lobby.commands.CMDLobby());
        safeRegisterCommand("setlobby", new de.noneless.lobby.commands.CMDSetLobby());
        safeRegisterCommand("profile", new de.noneless.lobby.commands.CMDProfile());
        safeRegisterCommand("punkte", new de.noneless.lobby.commands.CMDPunkteleaderboard());
        safeRegisterCommand("warps", new de.noneless.lobby.commands.CMDWarps());
        safeRegisterCommand("punkteadmin", new de.noneless.lobby.commands.CMDPunkteAdmin());
        safeRegisterCommand("punktegeben", new de.noneless.lobby.commands.CMDPunkteGeben());
        safeRegisterCommand("friend", new de.noneless.lobby.commands.CMDFriend());
        safeRegisterCommand("annehmen", new de.noneless.lobby.commands.CMDAcceptFriend());
        safeRegisterCommand("ablehnen", new de.noneless.lobby.commands.CMDDenyFriend());
        safeRegisterCommand("testfriendrequest", new de.noneless.lobby.commands.CMDTestFriendRequest());
        safeRegisterCommand("serverinfo", new de.noneless.lobby.commands.CMDServerInfo());
        safeRegisterCommand("lobbynpc", new de.noneless.lobby.commands.CMDNPCManager());
        safeRegisterCommand("addnews", new de.noneless.lobby.commands.CMDAddNews());
        safeRegisterCommand("news", new de.noneless.lobby.commands.CMDNews());
        safeRegisterCommand("settings", new de.noneless.lobby.commands.CMDSettings());
        de.noneless.lobby.commands.CMDWorldMover worldMoverCommandExecutor = new de.noneless.lobby.commands.CMDWorldMover(worldMoverService);
        safeRegisterCommand("worldmover", worldMoverCommandExecutor);
        safeSetTabCompleter("worldmover", worldMoverCommandExecutor);
        
        // Registriere TabCompleter
        de.noneless.lobby.tabcompleters.LobbyTabCompleter lobbyTabCompleter = new de.noneless.lobby.tabcompleters.LobbyTabCompleter();
        de.noneless.lobby.tabcompleters.FriendTabCompleter friendTabCompleter = new de.noneless.lobby.tabcompleters.FriendTabCompleter();
        de.noneless.lobby.tabcompleters.NPCTabCompleter npcTabCompleter = new de.noneless.lobby.tabcompleters.NPCTabCompleter();
        de.noneless.lobby.tabcompleters.AdminTabCompleter adminTabCompleter = new de.noneless.lobby.tabcompleters.AdminTabCompleter();
        
        // Setze TabCompleter für Commands
        safeSetTabCompleter("lobby", lobbyTabCompleter);
        safeSetTabCompleter("setlobby", lobbyTabCompleter);
        safeSetTabCompleter("profile", lobbyTabCompleter);
        safeSetTabCompleter("punkte", lobbyTabCompleter);
        safeSetTabCompleter("warps", lobbyTabCompleter);
        safeSetTabCompleter("punkteadmin", adminTabCompleter);
        safeSetTabCompleter("punktegeben", adminTabCompleter);
        safeSetTabCompleter("friend", friendTabCompleter);
        safeSetTabCompleter("serverinfo", lobbyTabCompleter);
        safeSetTabCompleter("lobbynpc", npcTabCompleter);
        safeSetTabCompleter("settings", lobbyTabCompleter);
        
        getLogger().info("§aAlle Commands und TabCompleter erfolgreich registriert!");
    }
    
    private void registerEventListeners() {
        getServer().getPluginManager().registerEvents(new de.noneless.lobby.listeners.PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new de.noneless.lobby.listeners.SettingsListener(), this);
        getServer().getPluginManager().registerEvents(new de.noneless.lobby.listeners.WarpsListener(), this);
        getServer().getPluginManager().registerEvents(new de.noneless.lobby.listeners.FriendsListener(), this);
        getServer().getPluginManager().registerEvents(new de.noneless.lobby.listeners.NPCMenuListener(), this);
        getServer().getPluginManager().registerEvents(new de.noneless.lobby.listeners.NPCInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new de.noneless.lobby.listeners.WorldMoveProtectionListener(worldMoverService), this);
        getServer().getPluginManager().registerEvents(new de.noneless.lobby.listeners.LobbyWorldListener(), this);
        getServer().getPluginManager().registerEvents(new de.noneless.lobby.listeners.EssentialsWarpMenuListener(), this);
        getLogger().info("§aEvent Listener erfolgreich registriert.");
    }
    
    private void saveData() {
        // TODO: Speichere Spielerdaten
        getLogger().info("§7Daten werden gespeichert...");
    }

    private void safeRegisterCommand(String name, org.bukkit.command.CommandExecutor executor) {
        try {
            org.bukkit.command.PluginCommand cmd = getCommand(name);
            if (cmd == null) {
                getLogger().warning("Command '" + name + "' ist nicht in plugin.yml deklariert; Registrierung übersprungen.");
                return;
            }
            cmd.setExecutor(executor);
        } catch (Exception e) {
            getLogger().warning("Fehler beim Registrieren des Commands '" + name + "': " + e.getMessage());
        }
    }

    private void safeSetTabCompleter(String name, org.bukkit.command.TabCompleter completer) {
        try {
            org.bukkit.command.PluginCommand cmd = getCommand(name);
            if (cmd == null) {
                getLogger().warning("TabCompleter: Command '" + name + "' ist nicht in plugin.yml deklariert; setzen übersprungen.");
                return;
            }
            cmd.setTabCompleter(completer);
        } catch (Exception e) {
            getLogger().warning("Fehler beim Setzen des TabCompleters für '" + name + "': " + e.getMessage());
        }
    }
    
    // Getter und Setter
    public static Main getInstance() {
        return instance;
    }
    
    public NPCManager getNpcManager() {
        return npcManager;
    }
    
    public NPCManager getNPCManager() {
        return npcManager;
    }

    public de.noneless.lobby.news.NewsManager getNewsManager() {
        return newsManager;
    }
    
    // Settings-Konfigurationen
    public static org.bukkit.configuration.file.FileConfiguration Frdb;
    public static org.bukkit.configuration.file.FileConfiguration AOnline;
    private static java.io.File frdbFile;
    private static java.io.File aOnlineFile;
    
    public void saveSettingsConfigs() {
        try {
            if (Frdb != null && frdbFile != null) {
                Frdb.save(frdbFile);
                getLogger().fine("§7Friends-Konfiguration gespeichert.");
            }
            if (AOnline != null && aOnlineFile != null) {
                AOnline.save(aOnlineFile);
                getLogger().fine("§7Admin-Online-Konfiguration gespeichert.");
            }
        } catch (java.io.IOException e) {
            getLogger().warning("§cFehler beim Speichern der Konfigurationen: " + e.getMessage());
            getLogger().warning("Stack trace: " + java.util.Arrays.toString(e.getStackTrace()));
        }
    }
    
    // Initialisiere Settings-Konfigurationen
    private void initializeSettingsConfigs() {
        // Erstelle Data-Ordner, falls nicht vorhanden
        if (!getInstance().getDataFolder().exists()) {
            getInstance().getDataFolder().mkdirs();
        }
        
        // Lade oder erstelle Friends-Konfiguration
        frdbFile = new java.io.File(getInstance().getDataFolder(), "FriendsDB.yml");
        if (!frdbFile.exists()) {
            try {
                frdbFile.createNewFile();
            } catch (java.io.IOException e) {
                getLogger().warning("Konnte FriendsDB.yml nicht erstellen: " + e.getMessage());
            }
        }
        Frdb = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(frdbFile);
        
        // Lade oder erstelle Admin-Online-Konfiguration
        aOnlineFile = new java.io.File(getInstance().getDataFolder(), "AdminOnline.yml");
        if (!aOnlineFile.exists()) {
            try {
                aOnlineFile.createNewFile();
            } catch (java.io.IOException e) {
                getLogger().warning("Konnte AdminOnline.yml nicht erstellen: " + e.getMessage());
            }
        }
        AOnline = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(aOnlineFile);
    }
    
    public Location getLobbyLocation() {
        return lobbyLocation;
    }
    
    public void setLobbyLocation(Location location) {
        this.lobbyLocation = location;
        ConfigManager.setLobbyLocation(location);
        if (npcManager != null) {
            npcManager.setLobbyLocation(location);
        }
    }
    
    // Punkte-System
    public void addPoints(UUID playerUUID, int points) {
        Punkte.addPoints(playerUUID, points);
    }
    
    public void removePoints(UUID playerUUID, int points) {
        Punkte.removePoints(playerUUID, points);
    }
    
    public int getPoints(UUID playerUUID) {
        return Punkte.getPoints(playerUUID);
    }
    
    public void setPoints(UUID playerUUID, int points) {
        Punkte.setPoints(playerUUID, Math.max(0, points));
    }

    public WorldMoverService getWorldMoverService() {
        return worldMoverService;
    }
}


