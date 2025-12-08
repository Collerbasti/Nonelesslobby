package npc;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.ai.Navigator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import de.noneless.lobby.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Set;
import java.util.IdentityHashMap;
import java.util.Collections;
import java.util.stream.Collectors;

public class NPCManager {

    private static final long CHAT_HOLOGRAM_FOLLOW_INTERVAL = 10L;
    
    private long chatHologramLifetimeTicks = 20L * 5;

    private final Main plugin;
    private final List<NPC> lobbyNPCs;
    private final Random random;
    private final Map<NPC, BukkitTask> movementTasks;
    private final Map<NPC, BukkitTask> lookTasks;
    private final Map<NPC, BukkitTask> chatTasks;
    private final Map<UUID, NPC> entityNpcMap;
    private final Map<String, List<String>> npcNamePersonalities;
    private final Map<String, List<String>> personalityChatMap;
    private final Map<String, String> personalityCaseLookup;
    private final Map<NPC, List<String>> npcAssignedPersonalities;
    private final Set<NPC> conversationLockedNPCs;
    private final List<String> npcNames;
    private final List<String> ambientChatLines;
    private final List<ConversationScript> conversationScripts;
    private final List<BukkitTask> conversationTasks;
    private final Map<NPC, ActiveNpcBubble> npcChatBubbles;
    private final File npcConfigFile;
    private FileConfiguration npcConfig;
    private final File npcDataFile;
    private FileConfiguration npcData;
    private boolean citizensAvailable = false;
    private BukkitTask conversationLoopTask;
    private ConversationContext activeConversation;
    private boolean conversationsEnabled;
    private int conversationMinIntervalSeconds;
    private int conversationMaxIntervalSeconds;
    private int conversationLineDelayTicks;
    private int conversationGatherDelayTicks;
    private double conversationAudienceRadiusSquared;
    private String conversationPrefix;
    private boolean hologramsAvailable;
    
    // ===== GETTER/SETTER FÜR GUI-SETTINGS =====
    private double chatHologramVerticalOffset = 3.0D;
    
    public double getChatHologramVerticalOffset() {
        return chatHologramVerticalOffset;
    }
    
    public void setChatHologramVerticalOffset(double offset) {
        this.chatHologramVerticalOffset = Math.max(0.5D, Math.min(10.0D, offset));
        if (npcConfig != null) {
            npcConfig.set("npcConversations.hologramVerticalOffset", chatHologramVerticalOffset);
            saveNpcConfig();
        }
    }
    
    public long getChatHologramLifetimeTicks() {
        return chatHologramLifetimeTicks;
    }
    
    public void setChatHologramLifetimeTicks(long ticks) {
        this.chatHologramLifetimeTicks = Math.max(20L, ticks);
        if (npcConfig != null) {
            npcConfig.set("npcConversations.hologramLifetimeTicks", chatHologramLifetimeTicks);
            saveNpcConfig();
        }
    }
    
    public long getChatHologramFollowInterval() {
        return CHAT_HOLOGRAM_FOLLOW_INTERVAL;
    }
    
    public int getConversationMinIntervalSeconds() {
        return conversationMinIntervalSeconds;
    }
    
    public void setConversationMinIntervalSeconds(int seconds) {
        this.conversationMinIntervalSeconds = Math.max(30, seconds);
        if (npcConfig != null) {
            npcConfig.set("npcConversations.minIntervalSeconds", conversationMinIntervalSeconds);
            saveNpcConfig();
        }
    }
    
    public int getConversationMaxIntervalSeconds() {
        return conversationMaxIntervalSeconds;
    }
    
    public void setConversationMaxIntervalSeconds(int seconds) {
        this.conversationMaxIntervalSeconds = Math.max(conversationMinIntervalSeconds, seconds);
        if (npcConfig != null) {
            npcConfig.set("npcConversations.maxIntervalSeconds", conversationMaxIntervalSeconds);
            saveNpcConfig();
        }
    }
    
    public int getConversationLineDelayTicks() {
        return conversationLineDelayTicks;
    }
    
    public void setConversationLineDelayTicks(int ticks) {
        this.conversationLineDelayTicks = Math.max(10, ticks);
        if (npcConfig != null) {
            npcConfig.set("npcConversations.lineDelayTicks", conversationLineDelayTicks);
            saveNpcConfig();
        }
    }
    
    public int getConversationGatherDelayTicks() {
        return conversationGatherDelayTicks;
    }
    
    public void setConversationGatherDelayTicks(int ticks) {
        this.conversationGatherDelayTicks = Math.max(0, ticks);
        if (npcConfig != null) {
            npcConfig.set("npcConversations.gatherDelayTicks", conversationGatherDelayTicks);
            saveNpcConfig();
        }
    }
    
    public double getConversationAudienceRadius() {
        return Math.sqrt(conversationAudienceRadiusSquared);
    }
    
    public void setConversationAudienceRadius(double radius) {
        this.conversationAudienceRadiusSquared = Math.pow(Math.max(5D, radius), 2);
        if (npcConfig != null) {
            npcConfig.set("npcConversations.audienceRadius", Math.max(5D, radius));
            saveNpcConfig();
        }
    }
    
    public String getConversationPrefix() {
        return conversationPrefix;
    }
    
    public void setConversationPrefix(String prefix) {
        this.conversationPrefix = prefix != null ? prefix : ChatColor.DARK_PURPLE + "[Privat]";
        if (npcConfig != null) {
            // Speichere mit & statt ChatColor codes für bessere Lesbarkeit in YAML
            String saveValue = conversationPrefix.replace(String.valueOf(ChatColor.COLOR_CHAR), "&");
            npcConfig.set("npcConversations.privatePrefix", saveValue);
            saveNpcConfig();
        }
    }
    
    public boolean isConversationsEnabled() {
        return conversationsEnabled;
    }
    
    public void setConversationsEnabled(boolean enabled) {
        this.conversationsEnabled = enabled;
        if (npcConfig != null) {
            npcConfig.set("npcConversations.enabled", enabled);
            saveNpcConfig();
        }
        if (enabled) {
            restartConversationScheduler();
        } else {
            cancelConversationLoop();
        }
    }
    
    // ===== END GETTER/SETTER =====
    
    public NPCManager(Main plugin) {
        this.plugin = plugin;
        this.lobbyNPCs = new ArrayList<>();
        this.random = new Random();
        this.movementTasks = new HashMap<>();
        this.lookTasks = new HashMap<>();
        this.chatTasks = new HashMap<>();
        this.entityNpcMap = new HashMap<>();
        this.npcNamePersonalities = new HashMap<>();
        this.personalityChatMap = new HashMap<>();
        this.personalityCaseLookup = new HashMap<>();
        this.npcAssignedPersonalities = new HashMap<>();
        this.conversationLockedNPCs = Collections.newSetFromMap(new IdentityHashMap<>());
        this.npcNames = new ArrayList<>();
        this.ambientChatLines = new ArrayList<>();
        this.conversationScripts = new ArrayList<>();
        this.conversationTasks = new ArrayList<>();
        this.npcChatBubbles = new IdentityHashMap<>();
        this.npcConfigFile = new File(plugin.getDataFolder(), "npc_config.yml");
        this.npcDataFile = new File(plugin.getDataFolder(), "lobby_npcs.yml");
        this.conversationPrefix = ChatColor.DARK_PURPLE + "[Privat]";
        this.conversationAudienceRadiusSquared = 2500;
        this.conversationMinIntervalSeconds = 120;
        this.conversationMaxIntervalSeconds = 240;
        this.conversationLineDelayTicks = 40;
        this.conversationGatherDelayTicks = 60;
        this.hologramsAvailable = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
        if (!this.hologramsAvailable) {
            plugin.getLogger().info("DecentHolograms nicht gefunden - NPC-Sprechblasen bleiben deaktiviert.");
        }
        loadNpcConfig();
        loadNpcData();
        this.citizensAvailable = checkCitizensAvailability();
        if (this.citizensAvailable) {
            cleanupExistingLobbyNPCs();
            cleanupPersistedNPCs();
        }
        scheduleInitialLobbyReset();
    }
    
    public void reloadNpcConfig() {
        loadNpcConfig();
    }
    
    private void loadNpcConfig() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            if (!npcConfigFile.exists()) {
                npcConfigFile.createNewFile();
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte npc_config.yml nicht erstellen: " + e.getMessage());
        }
        
        npcConfig = YamlConfiguration.loadConfiguration(npcConfigFile);
        npcConfig.addDefault("npcNames", getDefaultNpcNames());
        npcConfig.addDefault("chatLines", getDefaultChatLines());
        npcConfig.addDefault("npcPersonalities", getDefaultNamePersonalities());
        npcConfig.addDefault("personalityLines", getDefaultPersonalityLines());
        npcConfig.addDefault("npcConversations", getDefaultConversationSettings());
        npcConfig.options().copyDefaults(true);
        saveNpcConfig();
        
        npcNames.clear();
        npcNames.addAll(npcConfig.getStringList("npcNames"));
        if (npcNames.isEmpty()) {
            npcNames.addAll(getDefaultNpcNames());
        }
        
        ambientChatLines.clear();
        ambientChatLines.addAll(npcConfig.getStringList("chatLines"));
        if (ambientChatLines.isEmpty()) {
            ambientChatLines.addAll(getDefaultChatLines());
        }
        
        loadPersonalityMaps();
        refreshAllNpcPersonalities();
        loadConversationSettings();
        restartConversationScheduler();
    }
    
    private void loadNpcData() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            if (!npcDataFile.exists()) {
                npcDataFile.createNewFile();
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte lobby_npcs.yml nicht erstellen: " + e.getMessage());
        }
        npcData = YamlConfiguration.loadConfiguration(npcDataFile);
        if (!npcData.isList("npcIds")) {
            npcData.set("npcIds", new ArrayList<>());
            saveNpcData();
        }
    }

    private void scheduleInitialLobbyReset() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                removeAllLobbyNPCs();
            } catch (Exception ignored) { }
            cleanupCitizensInLobbyWorld();
            spawnLobbyNPCs();
        }, 20L * 60L);
    }
    
    private void saveNpcConfig() {
        if (npcConfig == null) {
            return;
        }
        try {
            npcConfig.save(npcConfigFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte npc_config.yml nicht speichern: " + e.getMessage());
        }
    }
    
    private void saveNpcData() {
        if (npcData == null) return;
        try {
            npcData.save(npcDataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte lobby_npcs.yml nicht speichern: " + e.getMessage());
        }
    }
    
    /**
     * Prüft ob Citizens2 verfügbar ist
     */
    private boolean checkCitizensAvailability() {
        try {
            return Bukkit.getPluginManager().isPluginEnabled("Citizens") &&
                   Class.forName("net.citizensnpcs.api.CitizensAPI") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Spawnt 5-6 zufällige NPCs in der Nähe der Lobby
     */
    public void spawnLobbyNPCs() {
        if (!isCitizensAvailable()) {
            plugin.getLogger().warning("Citizens2 Plugin ist nicht verfügbar! NPCs können nicht gespawnt werden.");
            return;
        }
        
        try {
            // Versuche die echte Lobby-Position zu bekommen
            Location lobbySpawn = null;
            try {
                Class<?> configManagerClass = Class.forName("Config.ConfigManager");
                lobbySpawn = (Location) configManagerClass.getMethod("getLobbyLocation").invoke(null);
            } catch (Exception e) {
                // ConfigManager nicht verfügbar oder keine Lobby-Position gesetzt
            }
            
            // Fallback zur Standard-Position
            if (lobbySpawn == null || lobbySpawn.getWorld() == null) {
                World world = Bukkit.getWorld("world");
                if (world == null) {
                    plugin.getLogger().warning("Welt 'world' wurde nicht gefunden! NPCs können nicht gespawnt werden.");
                    return;
                }
                lobbySpawn = new Location(world, 0.5, 65, 0.5);
                plugin.getLogger().info("§7Verwende Standard-Lobby-Position für NPCs (0.5, 65, 0.5)");
            } else {
                plugin.getLogger().info("§7Verwende gesetzte Lobby-Position für NPCs: " + 
                                       String.format("%.1f, %.1f, %.1f", 
                                                   lobbySpawn.getX(), 
                                                   lobbySpawn.getY(), 
                                                   lobbySpawn.getZ()));
            }
            
            // Spawne 5-6 zufällige NPCs
            int npcCount = 5 + random.nextInt(2);
            
            for (int i = 0; i < npcCount; i++) {
                spawnNPCWithReflection(lobbySpawn, i);
            }
            
            plugin.getLogger().info("§aEs wurden " + npcCount + " NPCs in der Lobby gespawnt!");
            restartConversationScheduler();
            
        } catch (Exception e) {
            plugin.getLogger().severe("Fehler beim Spawnen der NPCs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Spawnt einen NPC mit Reflection API
     */
    private void spawnNPCWithReflection(Location lobbySpawn, int index) {
        try {
            // Zufaellige Position um die Lobby herum
            double offsetX = (random.nextDouble() - 0.5) * 20;
            double offsetZ = (random.nextDouble() - 0.5) * 20;
            
            Location spawnLocation = lobbySpawn.clone().add(offsetX, 0, offsetZ);
            spawnLocation.setY(lobbySpawn.getY());
            
            // Stelle sicher, dass die Position auf festem Boden ist
            spawnLocation = spawnLocation.getWorld().getHighestBlockAt(spawnLocation).getLocation().add(0, 1, 0);
            
            // Erstelle NPC mit Citizens API (direkt, ohne Reflection)
            NPCRegistry registry = CitizensAPI.getNPCRegistry();
            
            String npcName = npcNames.isEmpty() ? "NPC" : npcNames.get(index % npcNames.size());
            
            NPC npc = registry.createNPC(org.bukkit.entity.EntityType.PLAYER, npcName);
            
            // Spawne den NPC
            boolean spawned = npc.spawn(spawnLocation);
            
            if (spawned) {
                // Konfiguriere NPC fuer bessere Sichtbarkeit
                configureNPC(npc, npcName);
                
                lobbyNPCs.add(npc);
                registerNPCEntity(npc);
                addPersistentNpcEntry(npc);
                
                plugin.getLogger().info("NPC '" + npcName + "' wurde gespawnt bei: " + 
                                       String.format("%.1f, %.1f, %.1f", 
                                                   spawnLocation.getX(), 
                                                   spawnLocation.getY(), 
                                                   spawnLocation.getZ()));
            } else {
                plugin.getLogger().warning("NPC '" + npcName + "' konnte nicht gespawnt werden!");
            }
                                               
        } catch (Exception e) {
            plugin.getLogger().warning("Fehler beim Spawnen von NPC " + index + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Konfiguriert einen NPC fuer bessere Funktionalitaet
     */
    private void configureNPC(NPC npc, String npcName) {
        try {
            // Markiere NPC als Lobby-NPC und verhindere das Speichern
            npc.data().set("lobby-npc", true);
            npc.data().setPersistent("lobby-npc", true);
            
            // Stelle sicher, dass der NPC-Name sichtbar ist (wird nur waehrend Holodisplay versteckt)
            Entity entity = npc.getEntity();
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                living.setCustomName(npcName);
                living.setCustomNameVisible(true);
            }
            
            // Aktiviere AI/Movement
            Navigator navigator = npc.getNavigator();
            if (navigator != null) {
                navigator.getDefaultParameters().range(10.0f);
            }
            
            // Fuege zufaellige Bewegung hinzu
            scheduleRandomMovement(npc);
            schedulePlayerTracking(npc);
            scheduleAmbientChat(npc);
            assignPersonalities(npc);
            
        } catch (Exception e) {
            plugin.getLogger().info("NPC-Konfiguration teilweise fehlgeschlagen: " + e.getMessage());
        }
    }
    
    /**
     * Plant zufaellige Bewegungen fuer den NPC
     */
    private void scheduleRandomMovement(NPC npc) {
        cancelMovementTask(npc);
        
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                if (conversationLockedNPCs.contains(npc)) return;
                // Pruefe ob NPC noch gespawnt ist
                if (!npc.isSpawned()) return;
                
                // Hole aktuelle Position
                Entity entity = npc.getEntity();
                Location currentLoc = entity.getLocation();
                
                // Berechne zufällige Zielposition (5 Blöcke Radius)
                double newX = currentLoc.getX() + (random.nextDouble() - 0.5) * 10;
                double newZ = currentLoc.getZ() + (random.nextDouble() - 0.5) * 10;
                Location targetLoc = new Location(currentLoc.getWorld(), newX, currentLoc.getY(), newZ);
                
                // Stelle sicher, dass Ziel auf festem Boden ist
                targetLoc = targetLoc.getWorld().getHighestBlockAt(targetLoc).getLocation().add(0, 1, 0);
                
                // Bewege NPC zur Zielposition
                Navigator navigator = npc.getNavigator();
                if (navigator != null) {
                    if (navigator.isNavigating()) {
                        return; // NPC bewegt sich bereits
                    }
                    navigator.setTarget(targetLoc);
                }
                
            } catch (Exception e) {
                // Bewegung fehlgeschlagen - ignorieren
            }
        }, 100L, 120L + random.nextInt(120)); // Alle ~11-15 Sekunden
        
        movementTasks.put(npc, task);
    }
    
    /**
     * Lässt NPCs in Richtung des nächstgelegenen Spielers schauen
     */
    private void schedulePlayerTracking(NPC npc) {
        cancelLookTask(npc);
        
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                if (conversationLockedNPCs.contains(npc)) {
                    faceConversationPartner(npc);
                    return;
                }
                if (!npc.isSpawned()) {
                    return;
                }
                
                Entity entity = npc.getEntity();
                Location npcLocation = entity.getLocation();
                if (npcLocation == null) {
                    return;
                }
                
                Player nearest = findNearestPlayer(npcLocation, 25);
                if (nearest != null) {
                    Location targetLoc = nearest.getLocation();
                    faceTarget(npc, entity, npcLocation, targetLoc);
                } else {
                    randomHeadMovement(npc, entity, npcLocation);
                }
            } catch (Exception ignored) {
                // Blickverfolgung fehlgeschlagen - ueberspringen
            }
        }, 60L, 30L);
        
        lookTasks.put(npc, task);
    }
    
    private void scheduleAmbientChat(NPC npc) {
        scheduleAmbientChat(npc, true);
    }
    
    private void scheduleAmbientChat(NPC npc, boolean cancelExisting) {
        if (cancelExisting) {
            cancelAmbientChat(npc);
        }
        
        long delay = 20L * (90 + random.nextInt(61)); // 90-150 Sekunden
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                if (!npc.isSpawned()) {
                    return;
                }
                
                Entity entity = npc.getEntity();
                if (entity == null) {
                    return;
                }
                Location npcLocation = entity.getLocation();
                if (npcLocation == null) {
                    return;
                }
                
                List<Player> nearbyPlayers = getNearbyPlayers(npcLocation, 400); // 20 Blöcke Radius
                if (nearbyPlayers.isEmpty()) {
                    return;
                }
                List<Player> recipients = filterChatReceivers(nearbyPlayers);
                if (recipients.isEmpty()) {
                    // sende keine Chatnachricht, aber Hologramm trotzdem anzeigen
                }
                
                String message = resolveAmbientTemplate(npc, nearbyPlayers);
                if (message == null) {
                    return;
                }
                
                String npcName = getNPCDisplayName(npc);
                String formatted = ChatColor.AQUA + npcName + ChatColor.GRAY + ": "
                                 + ChatColor.WHITE + message;
                
                for (Player player : recipients) {
                    player.sendMessage(formatted);
                }
                showNpcChatBubble(npc, message);
            } catch (Exception ignored) {
                // Chat fehlgeschlagen - überspringen
            } finally {
                // plane nächste Nachricht
                scheduleAmbientChat(npc, false);
            }
        }, delay);
        
        chatTasks.put(npc, task);
    }
    
    private void assignPersonalities(NPC npc) {
        if (npc == null) return;
        String rawName = getRawNPCName(npc);
        if (rawName == null) {
            npcAssignedPersonalities.remove(npc);
            return;
        }
        List<String> configured = npcNamePersonalities.get(rawName.toLowerCase());
        if (configured == null || configured.isEmpty()) {
            npcAssignedPersonalities.remove(npc);
        } else {
            npcAssignedPersonalities.put(npc, new ArrayList<>(configured));
        }
    }
    
    /**
     * Versteckt den Namen des NPCs durch Nameplate-Manipulation
     * Dies macht den NPC-Namen in der Spielerwelt unsichtbar
     */
    private void hideNPCNameplate(NPC npc) {
        try {
            // Nutze die Citizens API direkt
            Entity entity = npc.getEntity();
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                living.setCustomNameVisible(false);
                living.setCustomName("");
            }
        } catch (Exception e) {
            plugin.getLogger().fine("Konnte NPC-Namen nicht vollständig verstecken: " + e.getMessage());
        }
    }
    
    /**
     * Stoppt geplante Bewegung für einen NPC
     */
    private void cancelMovementTask(NPC npc) {
        BukkitTask task = movementTasks.remove(npc);
        if (task != null) {
            task.cancel();
        }
    }
    
    /**
     * Stoppt Blickverfolgung für einen NPC
     */
    private void cancelLookTask(NPC npc) {
        BukkitTask task = lookTasks.remove(npc);
        if (task != null) {
            task.cancel();
        }
    }
    
    private void cancelAmbientChat(NPC npc) {
        BukkitTask task = chatTasks.remove(npc);
        if (task != null) {
            task.cancel();
        }
    }
    
    private void registerNPCEntity(NPC npc) {
        try {
            Entity entityObj = npc.getEntity();
            if (entityObj != null) {
                entityNpcMap.put(entityObj.getUniqueId(), npc);
            }
        } catch (Exception ignored) { }
        
        assignPersonalities(npc);
    }
    
    private void unregisterNPCEntity(NPC npc) {
        try {
            Entity entity = npc.getEntity();
            if (entity != null) {
                entityNpcMap.remove(entity.getUniqueId());
            }
        } catch (Exception ignored) { }
    }
    
    /**
     * Entfernt alle Lobby NPCs
     */
    public void removeAllLobbyNPCs() {
        cancelConversationLoop();
        cancelConversationTasks();
        destroyAllNpcBubbles();
        try {
            for (NPC npc : lobbyNPCs) {
                if (npc != null) {
                    cancelMovementTask(npc);
                    cancelLookTask(npc);
                    cancelAmbientChat(npc);
                    unregisterNPCEntity(npc);
                    npcAssignedPersonalities.remove(npc);
                    removePersistentNpcEntry(npc);
                    // Pruefe ob NPC gespawnt ist
                    if (npc.isSpawned()) {
                        npc.despawn();
                    }
                    npc.destroy();
                }
            }
            lobbyNPCs.clear();
            movementTasks.clear();
            lookTasks.clear();
            chatTasks.clear();
            entityNpcMap.clear();
            npcAssignedPersonalities.clear();
            conversationLockedNPCs.clear();
            npcChatBubbles.clear();
            if (npcData != null) {
                npcData.set("npcIds", new ArrayList<>());
                saveNpcData();
            }
            plugin.getLogger().info("§cAlle Lobby NPCs wurden entfernt.");
            
        } catch (Exception e) {
            plugin.getLogger().warning("Fehler beim Entfernen der NPCs: " + e.getMessage());
            lobbyNPCs.clear(); // Sicherheit
            movementTasks.clear();
            lookTasks.clear();
            chatTasks.clear();
            entityNpcMap.clear();
            npcAssignedPersonalities.clear();
            conversationLockedNPCs.clear();
            npcChatBubbles.clear();
            if (npcData != null) {
                npcData.set("npcIds", new ArrayList<>());
                saveNpcData();
            }
        }
    }
    
    /**
     * Lädt alle NPCs neu
     */
    public void reloadNPCs() {
        removeAllLobbyNPCs();
        
        // Verzögerung vor dem neu spawnen
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            spawnLobbyNPCs();
        }, 20L); // 1 Sekunde Verzögerung
    }
    
    /**
     * Setzt eine neue Lobby-Position und spawnt NPCs dort
     */
    public void setLobbyLocation(Location newLobbyLocation) {
        removeAllLobbyNPCs();
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (newLobbyLocation.getWorld() != null) {
                spawnLobbyNPCs(); // Spawnt an der neuen Position
            }
        }, 20L);
    }
    
    /**
     * Gibt die Anzahl der aktiven Lobby NPCs zurück
     */
    public int getActiveLobbyNPCCount() {
        try {
            return (int) lobbyNPCs.stream()
                    .filter(npc -> npc != null && npc.isSpawned())
                    .count();
        } catch (Exception e) {
            return lobbyNPCs.size(); // Fallback
        }
    }
    
    /**
     * Prüft ob Citizens verfügbar ist
     */
    public boolean isCitizensAvailable() {
        return citizensAvailable && Bukkit.getPluginManager().isPluginEnabled("Citizens");
    }
    
    /**
     * Gibt Status-Informationen zurück
     */
    public String getStatusInfo() {
        return String.format("Citizens2: %s, Aktive NPCs: %d", 
                           isCitizensAvailable() ? "Verfügbar" : "Nicht verfügbar",
                           getActiveLobbyNPCCount());
    }
    
    /**
     * Gibt eine Liste der aktuellen NPC-Positionen zurück (für Debug)
     */
    public List<String> getCurrentNPCPositions() {
        List<String> positions = new ArrayList<>();
        
        try {
            for (NPC npc : lobbyNPCs) {
                if (npc == null) continue;
                
                if (!npc.isSpawned()) continue;
                
                Entity entity = npc.getEntity();
                if (entity == null) continue;
                Location loc = entity.getLocation();
                String name = npc.getName();
                
                positions.add(String.format("%s: %.1f, %.1f, %.1f", 
                            name, loc.getX(), loc.getY(), loc.getZ()));
            }
        } catch (Exception e) {
            positions.add("Fehler beim Abrufen der NPC-Positionen: " + e.getMessage());
        }
        
        return positions;
    }
    
    public boolean triggerNPCInteraction(Player player, UUID entityId) {
        if (player == null || entityId == null) return false;
        NPC npc = entityNpcMap.get(entityId);
        if (npc == null) return false;
        List<Player> listeners = new ArrayList<>();
        listeners.add(player);
        String message = resolveAmbientTemplate(npc, listeners);
        if (message == null) return false;
        String npcName = getNPCDisplayName(npc);
        if (isNpcChatEnabled(player)) {
            player.sendMessage(ChatColor.AQUA + npcName + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
        }
        showNpcChatBubble(npc, message);
        return true;
    }

    private void showNpcChatBubble(NPC npc, String rawMessage) {
        if (!hologramsAvailable || npc == null) {
            return;
        }
        if (rawMessage == null) {
            return;
        }
        String trimmed = formatBubbleText(rawMessage);
        if (trimmed.isEmpty()) {
            return;
        }
        Location baseLocation = getNpcLocation(npc);
        if (baseLocation == null) {
            return;
        }
        Location displayLocation = baseLocation.clone().add(0, chatHologramVerticalOffset, 0);
        String sanitized = ChatColor.translateAlternateColorCodes('&', trimmed);
        if (!sanitized.startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
            sanitized = ChatColor.WHITE + sanitized;
        }
        destroyNpcBubble(npc);
        Boolean previousNameplateState = null;
        try {
            previousNameplateState = !npc.requiresNameHologram();
            npc.setAlwaysUseNameHologram(true);
        } catch (Exception ignored) { }
        
        String hologramName = "nlobby_chat_" + UUID.randomUUID().toString().replace("-", "");
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.AQUA + getNPCDisplayName(npc));
        String[] messageLines = sanitized.split("\\n");
        for (String part : messageLines) {
            if (part == null || part.isEmpty()) {
                lines.add(" ");
            } else {
                lines.add(part);
            }
        }
        
        Hologram hologram;
        try {
            hologram = DHAPI.createHologram(hologramName, displayLocation, false, lines);
        } catch (Exception ex) {
            plugin.getLogger().fine("Konnte NPC-Hologramm nicht erstellen: " + ex.getMessage());
            return;
        }
        BukkitTask followTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Location current = getNpcLocation(npc);
            if (current == null) {
                return;
            }
            Location updated = current.clone().add(0, chatHologramVerticalOffset, 0);
            try {
                DHAPI.moveHologram(hologram, updated);
            } catch (Exception ignored) { }
        }, CHAT_HOLOGRAM_FOLLOW_INTERVAL, CHAT_HOLOGRAM_FOLLOW_INTERVAL);
        BukkitTask cleanupTask = Bukkit.getScheduler().runTaskLater(plugin, () -> destroyNpcBubble(npc), chatHologramLifetimeTicks);
        npcChatBubbles.put(npc, new ActiveNpcBubble(hologramName, followTask, cleanupTask, previousNameplateState));
    }

    private void destroyNpcBubble(NPC npc) {
        if (!hologramsAvailable || npc == null) {
            return;
        }
        ActiveNpcBubble bubble = npcChatBubbles.remove(npc);
        if (bubble == null) {
            return;
        }
        if (bubble.followTask != null) {
            bubble.followTask.cancel();
        }
        if (bubble.cleanupTask != null) {
            bubble.cleanupTask.cancel();
        }
        try {
            DHAPI.removeHologram(bubble.hologramName);
        } catch (Exception ignored) { }
        if (bubble.previousNameplateState != null) {
            try {
                npc.setAlwaysUseNameHologram(!bubble.previousNameplateState);
            } catch (Exception ignored) { }
        }
    }

    private void destroyAllNpcBubbles() {
        if (!hologramsAvailable || npcChatBubbles.isEmpty()) {
            npcChatBubbles.clear();
            return;
        }
        List<NPC> npcs = new ArrayList<>(npcChatBubbles.keySet());
        for (NPC npc : npcs) {
            destroyNpcBubble(npc);
        }
        npcChatBubbles.clear();
    }

    private String formatBubbleText(String message) {
        if (message == null) {
            return "";
        }
        String cleaned = message.replace('\n', ' ').trim();
        if (cleaned.isEmpty()) {
            return "";
        }
        String[] tokens = cleaned.split("\\s+");
        StringBuilder builder = new StringBuilder();
        int wordsInLine = 0;
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }
            if (wordsInLine == 4) {
                builder.append('\n');
                wordsInLine = 0;
            } else if (builder.length() > 0 && builder.charAt(builder.length() - 1) != '\n') {
                builder.append(' ');
            }
            builder.append(token);
            wordsInLine++;
        }
        return builder.toString();
    }

    private void showConversationBubble(ConversationContext context, ConversationLine line, String text) {
        if (context == null || line == null || text == null) {
            return;
        }
        switch (line.getSpeaker()) {
            case BOTH:
                showNpcChatBubble(context.getFirstNpc(), text);
                showNpcChatBubble(context.getSecondNpc(), text);
                break;
            case SECOND:
                showNpcChatBubble(context.getSecondNpc(), text);
                break;
            case FIRST:
            default:
                showNpcChatBubble(context.getFirstNpc(), text);
                break;
        }
    }

    private void loadConversationSettings() {
        conversationScripts.clear();
        conversationsEnabled = false;
        if (npcConfig == null) {
            return;
        }

        ConfigurationSection section = npcConfig.getConfigurationSection("npcConversations");
        if (section == null) {
            section = npcConfig.createSection("npcConversations", getDefaultConversationSettings());
        }

        // Lade Hologram-Höhe und Lebensdauer
        double loadedHeight = section.getDouble("hologramVerticalOffset", 3.0D);
        this.chatHologramVerticalOffset = Math.max(0.5D, Math.min(10.0D, loadedHeight));
        
        long loadedLifetime = section.getLong("hologramLifetimeTicks", 100L);
        this.chatHologramLifetimeTicks = Math.max(20L, loadedLifetime);

        conversationsEnabled = section.getBoolean("enabled", true);
        conversationMinIntervalSeconds = Math.max(30, section.getInt("minIntervalSeconds", 180));
        conversationMaxIntervalSeconds = Math.max(conversationMinIntervalSeconds, section.getInt("maxIntervalSeconds", 300));
        conversationGatherDelayTicks = Math.max(0, section.getInt("gatherDelayTicks", 60));
        conversationLineDelayTicks = Math.max(10, section.getInt("lineDelayTicks", 50));
        double radius = Math.max(5D, section.getDouble("audienceRadius", 60D));
        conversationAudienceRadiusSquared = radius * radius;
        String configuredPrefix = section.getString("privatePrefix", "&5[NPC-Privat]");
        conversationPrefix = ChatColor.translateAlternateColorCodes('&',
            configuredPrefix == null ? "&5[NPC-Privat]" : configuredPrefix);

        ConfigurationSection scriptsSection = section.getConfigurationSection("scripts");
        if (scriptsSection != null) {
            for (String key : scriptsSection.getKeys(false)) {
                ConfigurationSection scriptSection = scriptsSection.getConfigurationSection(key);
                if (scriptSection == null) {
                    continue;
                }
                List<ConversationLine> lines = parseConversationLines(scriptSection);
                if (lines.isEmpty()) {
                    continue;
                }
                String title = scriptSection.getString("title", key);
                List<String> firstPersonality = normalizePersonalityList(scriptSection.getStringList("firstPersonalities"));
                List<String> secondPersonality = normalizePersonalityList(scriptSection.getStringList("secondPersonalities"));
                List<String> sharedPersonality = normalizePersonalityList(scriptSection.getStringList("sharedPersonalities"));
                conversationScripts.add(new ConversationScript(key, title, lines, firstPersonality, secondPersonality, sharedPersonality));
            }
        }

        if (conversationScripts.isEmpty()) {
            conversationsEnabled = false;
        }
    }
    
    private List<String> normalizePersonalityList(List<String> rawList) {
        if (rawList == null || rawList.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> normalized = new ArrayList<>();
        for (String entry : rawList) {
            if (entry == null || entry.trim().isEmpty()) continue;
            String key = resolvePersonalityKey(entry);
            if (key == null) {
                key = ensurePersonalityExists(entry.trim());
            }
            if (key != null) {
                normalized.add(key);
            }
        }
        return normalized;
    }

    private List<ConversationLine> parseConversationLines(ConfigurationSection scriptSection) {
        List<ConversationLine> lines = new ArrayList<>();
        if (scriptSection == null) {
            return lines;
        }
        List<?> rawLines = scriptSection.getList("lines");
        if (rawLines == null) {
            return lines;
        }

        for (Object raw : rawLines) {
            if (raw instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) raw;
                Object textObj = map.get("text");
                if (textObj == null) {
                    continue;
                }
                String text = String.valueOf(textObj);
                if (text.trim().isEmpty()) {
                    continue;
                }
                ConversationSpeaker speaker = resolveSpeaker(map.get("speaker"));
                int pauseTicks = parsePauseTicks(map.get("pauseTicks"));
                lines.add(new ConversationLine(speaker, text, pauseTicks));
            } else if (raw instanceof String) {
                String text = ((String) raw).trim();
                if (!text.isEmpty()) {
                    lines.add(new ConversationLine(ConversationSpeaker.FIRST, text, -1));
                }
            }
        }
        return lines;
    }

    private ConversationSpeaker resolveSpeaker(Object rawSpeaker) {
        if (rawSpeaker == null) {
            return ConversationSpeaker.FIRST;
        }
        String key = rawSpeaker.toString().trim().toUpperCase(Locale.ROOT);
        switch (key) {
            case "B":
            case "SECOND":
            case "NPC2":
            case "TWO":
            case "PLAYER2":
            case "ZWEI":
                return ConversationSpeaker.SECOND;
            case "BOTH":
            case "TOGETHER":
            case "SHARED":
            case "NARRATOR":
            case "BEIDE":
                return ConversationSpeaker.BOTH;
            default:
                return ConversationSpeaker.FIRST;
        }
    }

    private int parsePauseTicks(Object rawValue) {
        if (rawValue == null) {
            return -1;
        }
        if (rawValue instanceof Number) {
            return ((Number) rawValue).intValue();
        }
        try {
            return Integer.parseInt(rawValue.toString());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private void restartConversationScheduler() {
        cancelConversationLoop();
        cancelConversationTasks();
        if (!conversationsEnabled || conversationScripts.isEmpty()) {
            return;
        }
        scheduleNextConversation();
    }

    public boolean triggerConversationNow() {
        if (conversationScripts.isEmpty()) {
            return false;
        }
        cancelConversationLoop();
        boolean started = attemptConversationStart(true);
        if (!started) {
            scheduleNextConversation();
        }
        return started;
    }

    public boolean triggerConversationById(String scriptId) {
        if (scriptId == null || conversationScripts.isEmpty()) {
            return false;
        }
        ConversationScript match = conversationScripts.stream()
                .filter(script -> script.getId().equalsIgnoreCase(scriptId))
                .findFirst()
                .orElse(null);
        if (match == null) {
            return false;
        }
        cancelConversationLoop();
        boolean started = attemptConversationStart(true, match);
        if (!started) {
            scheduleNextConversation();
        }
        return started;
    }

    private void scheduleNextConversation() {
        if (!conversationsEnabled || conversationScripts.isEmpty()) {
            return;
        }
        int spread = conversationMaxIntervalSeconds - conversationMinIntervalSeconds;
        int delaySeconds = conversationMinIntervalSeconds + (spread > 0 ? random.nextInt(spread + 1) : 0);
        long delayTicks = Math.max(20L, delaySeconds * 20L);
        conversationLoopTask = Bukkit.getScheduler().runTaskLater(plugin, () -> attemptConversationStart(false), delayTicks);
    }

    private void cancelConversationLoop() {
        if (conversationLoopTask != null) {
            conversationLoopTask.cancel();
            conversationLoopTask = null;
        }
    }

    private void cancelConversationTasks() {
        if (!conversationTasks.isEmpty()) {
            for (BukkitTask task : conversationTasks) {
                if (task != null) {
                    task.cancel();
                }
            }
            conversationTasks.clear();
        }
        if (activeConversation != null) {
            releaseConversationParticipants(activeConversation);
            activeConversation = null;
        }
    }

    private void attemptConversationStart() {
        attemptConversationStart(false);
    }

    private boolean attemptConversationStart(boolean manualTrigger) {
        return attemptConversationStart(manualTrigger, null);
    }

    private boolean attemptConversationStart(boolean manualTrigger, ConversationScript forcedScript) {
        conversationLoopTask = null;
        if ((!manualTrigger && (!conversationsEnabled || conversationScripts.isEmpty())) || conversationScripts.isEmpty()) {
            return false;
        }
        ConversationScript script = forcedScript != null ? forcedScript : pickRandomConversationScript();
        if (script == null) {
            if (!manualTrigger) {
                scheduleNextConversation();
            }
            return false;
        }
        ConversationContext context = prepareConversationContext(script);
        if (context == null) {
            if (!manualTrigger) {
                scheduleNextConversation();
            }
            return false;
        }
        beginConversation(context);
        return true;
    }

    private ConversationScript pickRandomConversationScript() {
        if (conversationScripts.isEmpty()) {
            return null;
        }
        return conversationScripts.get(random.nextInt(conversationScripts.size()));
    }

    private ConversationContext prepareConversationContext(ConversationScript script) {
        if (script == null) {
            return null;
        }
        List<NPC> ready = new ArrayList<>();
        for (NPC npc : lobbyNPCs) {
            if (isNpcReady(npc) && matchesPersonalityRequirements(npc, script.getSharedPersonalities())) {
                ready.add(npc);
            }
        }
        if (ready.size() < 2) {
            return null;
        }
        List<NPC> firstCandidates = new ArrayList<>();
        List<NPC> secondCandidates = new ArrayList<>();
        for (NPC npc : ready) {
            if (matchesPersonalityRequirements(npc, script.getFirstPersonalities())) {
                firstCandidates.add(npc);
            }
            if (matchesPersonalityRequirements(npc, script.getSecondPersonalities())) {
                secondCandidates.add(npc);
            }
        }
        if (firstCandidates.isEmpty()) {
            firstCandidates.addAll(ready);
        }
        if (secondCandidates.isEmpty()) {
            secondCandidates.addAll(ready);
        }
        Collections.shuffle(firstCandidates, random);
        Collections.shuffle(secondCandidates, random);
        for (NPC firstNpc : firstCandidates) {
            for (NPC secondNpc : secondCandidates) {
                if (firstNpc == secondNpc) continue;
                String firstName = getNPCDisplayName(firstNpc);
                String secondName = getNPCDisplayName(secondNpc);
                Location meetingPoint = resolveMeetingPoint(firstNpc, secondNpc);
                return new ConversationContext(script, firstNpc, secondNpc, firstName, secondName, meetingPoint);
            }
        }
        return null;
    }

    private void beginConversation(ConversationContext context) {
        if (context == null) {
            scheduleNextConversation();
            return;
        }
        cancelConversationTasks();
        activeConversation = context;
        lockNpcForConversation(context.getFirstNpc());
        lockNpcForConversation(context.getSecondNpc());
        guideNpcsToMeetingPoint(context);
        long delay = Math.max(0, conversationGatherDelayTicks);
        for (ConversationLine line : context.getScript().getLines()) {
            long runDelay = delay;
            BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> deliverConversationLine(context, line), runDelay);
            conversationTasks.add(task);
            int pause = line.getPauseTicks() > -1 ? line.getPauseTicks() : conversationLineDelayTicks;
            delay += Math.max(10, pause);
        }
        BukkitTask followUp = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (activeConversation == context) {
                releaseConversationParticipants(context);
                activeConversation = null;
            }
            scheduleNextConversation();
        }, delay + 20L);
        conversationTasks.add(followUp);
    }

    private void deliverConversationLine(ConversationContext context, ConversationLine line) {
        if (context == null || line == null) {
            return;
        }
        if (context != activeConversation) {
            return;
        }
        if (!isNpcReady(context.getFirstNpc()) || !isNpcReady(context.getSecondNpc())) {
            cancelConversationTasks();
            scheduleNextConversation();
            return;
        }

        List<Player> audience = collectConversationAudience(context);
        if (audience.isEmpty()) {
            return;
        }

        String resolvedText = applyConversationPlaceholders(line.getText(), context, line, audience);
        if (resolvedText == null || resolvedText.isEmpty()) {
            return;
        }

        String formatted = buildConversationMessage(line, context, resolvedText);
        List<Player> recipients = filterChatReceivers(audience);
        for (Player player : recipients) {
            player.sendMessage(formatted);
        }
        showConversationBubble(context, line, resolvedText);
    }

    private List<Player> collectConversationAudience(ConversationContext context) {
        List<Player> audience = new ArrayList<>();
        if (context == null) {
            return audience;
        }
        Location focus = context.getMeetingPoint();
        if (focus == null) {
            focus = resolveMeetingPoint(context.getFirstNpc(), context.getSecondNpc());
        }
        if (focus == null || focus.getWorld() == null) {
            return audience;
        }
        for (Player player : focus.getWorld().getPlayers()) {
            if (player == null || !player.isOnline() || player.isDead()) {
                continue;
            }
            if (player.getLocation().distanceSquared(focus) <= conversationAudienceRadiusSquared) {
                audience.add(player);
            }
        }
        return audience;
    }

    private boolean isNpcReady(NPC npc) {
        if (npc == null) {
            return false;
        }
        try {
            return npc.isSpawned();
        } catch (Exception e) {
            return false;
        }
    }

    private void guideNpcsToMeetingPoint(ConversationContext context) {
        Location base = context.getMeetingPoint();
        if (base == null) {
            base = resolveMeetingPoint(context.getFirstNpc(), context.getSecondNpc());
        }
        if (base == null) {
            return;
        }
        double angle = random.nextDouble() * Math.PI * 2;
        double radius = 1.25D;
        Location firstTarget = adjustToGround(base.clone().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius));
        Location secondTarget = adjustToGround(base.clone().add(Math.cos(angle + Math.PI) * radius, 0, Math.sin(angle + Math.PI) * radius));
        directNpcToLocation(context.getFirstNpc(), firstTarget);
        directNpcToLocation(context.getSecondNpc(), secondTarget);
        Bukkit.getScheduler().runTaskLater(plugin, () -> faceEachOther(context.getFirstNpc(), context.getSecondNpc()), 40L);
    }

    private Location resolveMeetingPoint(NPC firstNpc, NPC secondNpc) {
        Location first = getNpcLocation(firstNpc);
        Location second = getNpcLocation(secondNpc);
        if (first == null && second == null) {
            return null;
        }
        if (first == null) {
            return adjustToGround(second);
        }
        if (second == null) {
            return adjustToGround(first);
        }
        if (first.getWorld() == null || !first.getWorld().equals(second.getWorld())) {
            return adjustToGround(first);
        }
        double x = (first.getX() + second.getX()) / 2.0D;
        double y = Math.max(first.getY(), second.getY());
        double z = (first.getZ() + second.getZ()) / 2.0D;
        Location center = new Location(first.getWorld(), x, y, z);
        return adjustToGround(center);
    }

    private Location adjustToGround(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }
        Location grounded = location.clone();
        try {
            grounded = grounded.getWorld().getHighestBlockAt(grounded).getLocation().add(0.5, 1, 0.5);
        } catch (Exception ignored) { }
        return grounded;
    }

    private void directNpcToLocation(NPC npc, Location target) {
        if (npc == null || target == null) {
            return;
        }
        try {
            Navigator navigator = npc.getNavigator();
            if (navigator != null) {
                navigator.setTarget(target);
            }
        } catch (Exception ignored) { }
    }

    private void faceEachOther(NPC firstNpc, NPC secondNpc) {
        try {
            Entity firstEntity = firstNpc != null ? firstNpc.getEntity() : null;
            Entity secondEntity = secondNpc != null ? secondNpc.getEntity() : null;
            Location firstLocation = firstEntity != null ? firstEntity.getLocation() : null;
            Location secondLocation = secondEntity != null ? secondEntity.getLocation() : null;
            if (firstNpc != null && firstEntity != null && firstLocation != null && secondLocation != null) {
                faceTarget(firstNpc, firstEntity, firstLocation, secondLocation);
            }
            if (secondNpc != null && secondEntity != null && firstLocation != null && secondLocation != null) {
                faceTarget(secondNpc, secondEntity, secondLocation, firstLocation);
            }
        } catch (Exception ignored) { }
    }
    
    private void faceConversationPartner(NPC npc) {
        ConversationContext context = activeConversation;
        if (context == null || npc == null) return;
        NPC other = null;
        if (npc == context.getFirstNpc()) {
            other = context.getSecondNpc();
        } else if (npc == context.getSecondNpc()) {
            other = context.getFirstNpc();
        }
        if (other == null) return;
        try {
            Entity entity = npc.getEntity();
            Entity otherEntity = other.getEntity();
            if (entity == null || otherEntity == null) {
                return;
            }
            Location npcLocation = entity.getLocation();
            Location otherLocation = otherEntity.getLocation();
            faceTarget(npc, entity, npcLocation, otherLocation);
        } catch (Exception ignored) { }
    }

    private Location getNpcLocation(NPC npc) {
        if (npc == null) {
            return null;
        }
        try {
            Entity entity = npc.getEntity();
            if (entity != null) {
                return entity.getLocation().clone();
            }
        } catch (Exception ignored) { }
        return null;
    }

    private String applyConversationPlaceholders(String template,
                                                 ConversationContext context,
                                                 ConversationLine line,
                                                 List<Player> listeners) {
        if (template == null || context == null) {
            return null;
        }
        String result = template;
        // Ersetze Conversation-Namen mit farbigem Text (LIGHT_PURPLE)
        String coloredFirstName = ChatColor.LIGHT_PURPLE + context.getFirstName() + ChatColor.WHITE;
        String coloredSecondName = ChatColor.LIGHT_PURPLE + context.getSecondName() + ChatColor.WHITE;
        result = result
            .replace("{A}", coloredFirstName)
            .replace("{a}", coloredFirstName)
            .replace("{FIRST}", coloredFirstName)
            .replace("{B}", coloredSecondName)
            .replace("{b}", coloredSecondName)
            .replace("{SECOND}", coloredSecondName);
        String selfName = null;
        if (line != null) {
            switch (line.getSpeaker()) {
                case SECOND:
                    selfName = context.getSecondName();
                    break;
                case BOTH:
                    selfName = context.getFirstName() + " & " + context.getSecondName();
                    break;
                case FIRST:
                default:
                    selfName = context.getFirstName();
                    break;
            }
        }
        result = applySharedPlaceholderReplacements(result, selfName, listeners);
        if (result == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    private String buildConversationMessage(ConversationLine line, ConversationContext context, String text) {
        String prefix = (conversationPrefix == null ? ChatColor.DARK_PURPLE + "[Privat]" : conversationPrefix) + ChatColor.RESET + " ";
        String firstName = context.getFirstName();
        String secondName = context.getSecondName();
        if (line.getSpeaker() == ConversationSpeaker.BOTH) {
            return prefix + ChatColor.LIGHT_PURPLE + firstName
                 + ChatColor.DARK_GRAY + " & "
                 + ChatColor.LIGHT_PURPLE + secondName
                 + ChatColor.GRAY + ": "
                 + ChatColor.WHITE + text;
        }
        boolean firstSpeaking = line.getSpeaker() == ConversationSpeaker.FIRST;
        String speakerName = firstSpeaking ? firstName : secondName;
        String listenerName = firstSpeaking ? secondName : firstName;
        return prefix + ChatColor.LIGHT_PURPLE + speakerName
             + ChatColor.DARK_GRAY + " -> "
             + ChatColor.LIGHT_PURPLE + listenerName
             + ChatColor.GRAY + ": "
             + ChatColor.WHITE + text;
    }
    
    private String generateUniqueNPCName() {
        List<String> available = new ArrayList<>();
        for (String name : npcNames) {
            if (!isNPCNameInUse(name)) {
                available.add(name);
            }
        }
        
        if (!available.isEmpty()) {
            return available.get(random.nextInt(available.size()));
        }
        
        // Alle Basisnamen belegt -> erweitere mit Index ohne Unterstrich
        int suffix = lobbyNPCs.size() + 1;
        return getRandomBaseName() + suffix;
    }
    
    private boolean isNPCNameInUse(String name) {
        for (NPC npc : lobbyNPCs) {
            String existing = getRawNPCName(npc);
            if (existing != null && existing.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    private String resolveAmbientTemplate(NPC npc, List<Player> listeners) {
        List<String> templates = getChatTemplatesForNPC(npc);
        if (templates.isEmpty()) {
            return null;
        }
        String template = templates.get(random.nextInt(templates.size()));
        return populateAmbientPlaceholders(template, npc, listeners);
    }
    
    private String populateAmbientPlaceholders(String template, NPC npc, List<Player> listeners) {
        if (template == null || template.isEmpty()) {
            return null;
        }
        String selfName = getNPCDisplayName(npc);
        return applySharedPlaceholderReplacements(template, selfName, listeners);
    }

    private String applySharedPlaceholderReplacements(String template, String selfName, List<Player> listeners) {
        if (template == null || template.isEmpty()) {
            return null;
        }

        String result = template;
        if (requiresPlayerPlaceholder(result)) {
            String playerName = selectRandomPlayerName(listeners);
            if (playerName == null || playerName.isEmpty()) {
                return null;
            }
            // Ersetze Spieler-Platzhalter mit farbigem Text (LIGHT_PURPLE)
            String coloredPlayerName = ChatColor.LIGHT_PURPLE + playerName + ChatColor.WHITE;
            result = result.replace("{SPIELERNAME}", coloredPlayerName)
                           .replace("{PLAYER}", coloredPlayerName)
                           .replace("{PLAYERNAME}", coloredPlayerName);
        }

        if (selfName != null && !selfName.isEmpty()) {
            // Ersetze SELF-Platzhalter mit farbigem Text (LIGHT_PURPLE)
            String coloredSelfName = ChatColor.LIGHT_PURPLE + selfName + ChatColor.WHITE;
            result = result.replace("{SELF}", coloredSelfName);
        }

        if (result.contains("{NPC}") || result.contains("{NPC2}")) {
            String primary = null;
            if (result.contains("{NPC}")) {
                primary = getRandomOtherNPCName(selfName, null);
                if (primary == null || primary.isEmpty()) {
                    primary = "NPC";
                }
                // Ersetze {NPC} mit farbigem Text (DARK_PURPLE)
                String coloredNPC = ChatColor.DARK_PURPLE + primary + ChatColor.WHITE;
                result = result.replace("{NPC}", coloredNPC);
            }

            if (result.contains("{NPC2}")) {
                String secondary = getRandomOtherNPCName(selfName, primary);
                if (secondary == null || secondary.isEmpty()) {
                    secondary = "NPC";
                }
                // Ersetze {NPC2} mit farbigem Text (DARK_PURPLE)
                String coloredNPC2 = ChatColor.DARK_PURPLE + secondary + ChatColor.WHITE;
                result = result.replace("{NPC2}", coloredNPC2);
            }
        }

        return result;
    }

    private String selectRandomPlayerName(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return null;
        }
        List<Player> eligible = new ArrayList<>();
        for (Player player : players) {
            if (player != null && player.isOnline() && !player.isDead()) {
                eligible.add(player);
            }
        }
        if (eligible.isEmpty()) {
            return null;
        }
        Player target = eligible.get(random.nextInt(eligible.size()));
        return target != null ? target.getName() : null;
    }
    
    private boolean requiresPlayerPlaceholder(String template) {
        return template.contains("{SPIELERNAME}") ||
               template.contains("{PLAYER}") ||
               template.contains("{PLAYERNAME}");
    }
    
    private String getNPCDisplayName(NPC npc) {
        String raw = getRawNPCName(npc);
        if (raw == null || raw.isEmpty()) {
            return "NPC";
        }
        return raw;
    }
    
    private String getRawNPCName(NPC npc) {
        try {
            return npc.getName();
        } catch (Exception e) {
            return null;
        }
    }
    
    private String getRandomOtherNPCName(String excludeA, String excludeB) {
        List<String> available = new ArrayList<>();
        for (NPC npcObj : lobbyNPCs) {
            if (npcObj == null) continue;
            String name = getRawNPCName(npcObj);
            if (name == null || name.isEmpty()) continue;
            if (equalsIgnoreCase(name, excludeA) || equalsIgnoreCase(name, excludeB)) continue;
            available.add(name);
        }
        if (available.isEmpty()) {
            return getFallbackNPCName(excludeA, excludeB);
        }
        return available.get(random.nextInt(available.size()));
    }

    private boolean isNpcChatEnabled(Player player) {
        if (player == null) {
            return true;
        }
        try {
            if (Main.Frdb == null) {
                return true;
            }
            return Main.Frdb.getBoolean(player.getName() + ".NpcChatEnabled", true);
        } catch (Exception e) {
            return true;
        }
    }
    
    private LivingEntity resolveNpcLivingEntity(NPC npc) {
        if (npc == null) {
            return null;
        }
        try {
            Entity entity = npc.getEntity();
            if (entity instanceof LivingEntity) {
                return (LivingEntity) entity;
            }
        } catch (Exception ignored) { }
        return null;
    }
    
    private List<Player> filterChatReceivers(List<Player> players) {
        List<Player> allowed = new ArrayList<>();
        if (players == null) {
            return allowed;
        }
        for (Player player : players) {
            if (isNpcChatEnabled(player)) {
                allowed.add(player);
            }
        }
        return allowed;
    }
    
    private String getFallbackNPCName(String excludeA, String excludeB) {
        List<String> pool = new ArrayList<>();
        for (String candidate : npcNames) {
            if (equalsIgnoreCase(candidate, excludeA) || equalsIgnoreCase(candidate, excludeB)) {
                continue;
            }
            pool.add(candidate);
        }
        if (pool.isEmpty()) {
            return "NPC";
        }
        return pool.get(random.nextInt(pool.size()));
    }
    
    private Map<String, Object> getDefaultConversationSettings() {
        Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("enabled", true);
        defaults.put("hologramVerticalOffset", 3.0D);
        defaults.put("hologramLifetimeTicks", 100L);
        defaults.put("minIntervalSeconds", 180);
        defaults.put("maxIntervalSeconds", 300);
        defaults.put("gatherDelayTicks", 60);
        defaults.put("lineDelayTicks", 50);
        defaults.put("audienceRadius", 60);
        defaults.put("privatePrefix", "&5[NPC-Privat]");

        Map<String, Object> scripts = new LinkedHashMap<>();
        scripts.put("geruechte", createConversationScriptDefaults("Geruechtekueche", new String[][]{
            {"first", "Pssst {B}, tu so als wuerdest du nur rumstehen."},
            {"second", "Schon klar {A}, aber alle glauben sowieso, dass wir was planen."},
            {"first", "Ich habe gehoert, dass direkt unter dem Spawn ein Event vorbereitet wird."},
            {"second", "Dann treffen wir uns spaeter dort, bevor die Spieler es merken."},
            {"both", "&o*die beiden nicken sich verschworen zu*"}
        }, new String[]{"witzig"}, new String[]{"witzig"}, new String[]{"witzig"}));
        scripts.put("eventplanung", createConversationScriptDefaults("Eventplanung", new String[][]{
            {"first", "{B}, hast du die Liste fuer das Lobby-Festival dabei?"},
            {"second", "Klar {A}, sogar das geheime Musikportal ist markiert."},
            {"first", "Wir muessen nur verhindern, dass jemand die VIP-Terrasse entdeckt."},
            {"second", "Also treffen wir die Baucrew spaeter beim Baumhaus."},
            {"both", "&o*ein leises Klirren von Schluesseln ist zu hoeren*"}
        }, new String[]{"freundlich"}, new String[]{"freundlich"}, new String[]{"freundlich"}));
        scripts.put("handel", createConversationScriptDefaults("VerdeckterHandel", new String[][]{
            {"first", "Hast du die Emerald-Lieferung versteckt, {B}?"},
            {"second", "Ja {A}, im Fass hinter dem alten Portal findet sie niemand."},
            {"first", "Gut, sonst wuerde wieder jemand Preise fuer Lobby-Tickets erfinden."},
            {"second", "Vergiss nicht die Beweiszettel zu verbrennen."},
            {"both", "&o*beide schauen nervoes ueber die Schultern*"}
        }, null, null, new String[]{"listig"}));
        scripts.put("technik", createConversationScriptDefaults("TechnikTalk", new String[][]{
            {"first", "{B}, das Admin-Panel hat heute Morgen gegrummelt."},
            {"second", "Ich weiss {A}, das Update hat die Hologramme verdoppelt."},
            {"first", "Wir verteilen lieber einen Hotfix, bevor ein Spieler es merkt."},
            {"second", "Abgemacht, stell aber den Chat-Filter auf Fluesterton."},
            {"both", "&o*kurzes Tippen auf unsichtbare Schalter*"}
        }, new String[]{"technik"}, new String[]{"technik"}, new String[]{"technik"}));
        scripts.put("geheimgang", createConversationScriptDefaults("Geheimgang", new String[][]{
            {"first", "Hast du die Karte vom geheimen Gang noch, {B}?"},
            {"second", "Klar {A}, sie fuehrt direkt hinter die Wasserfaelle."},
            {"first", "Perfekt, dort koennen wir unbeobachtet weiterreden."},
            {"second", "Dann markiere ich den Einstieg mit violetten Partikeln."},
            {"both", "&o*sprueht ein paar Partikel in die Luft*"}
        }, null, null, new String[]{"abenteuerlustig"}));
        defaults.put("scripts", scripts);
        return defaults;
    }

    private Map<String, Object> createConversationScriptDefaults(String title, String[][] entries, String[] firstPersonalities, String[] secondPersonalities, String[] sharedPersonalities) {
        Map<String, Object> script = new LinkedHashMap<>();
        script.put("title", title);
        List<Map<String, Object>> lines = new ArrayList<>();
        if (entries != null) {
            for (String[] entry : entries) {
                if (entry == null || entry.length < 2) {
                    continue;
                }
                lines.add(createConversationLineDefault(entry[0], entry[1]));
            }
        }
        script.put("lines", lines);
        if (firstPersonalities != null && firstPersonalities.length > 0) {
            script.put("firstPersonalities", Arrays.asList(firstPersonalities));
        }
        if (secondPersonalities != null && secondPersonalities.length > 0) {
            script.put("secondPersonalities", Arrays.asList(secondPersonalities));
        }
        if (sharedPersonalities != null && sharedPersonalities.length > 0) {
            script.put("sharedPersonalities", Arrays.asList(sharedPersonalities));
        }
        return script;
    }

    private Map<String, Object> createConversationLineDefault(String speaker, String text) {
        Map<String, Object> line = new LinkedHashMap<>();
        line.put("speaker", speaker);
        line.put("text", text);
        return line;
    }

    private List<String> getDefaultNpcNames() {
        return Arrays.asList(
            "Alex", "Steve", "Emma", "Liam", "Sophie", "Noah",
            "Mia", "Ben", "Luna", "Max", "Zoe", "Felix",
            "Aria", "Leo", "Cleo", "Sam", "Nova", "Kai"
        );
    }
    
    private List<String> getDefaultChatLines() {
        return Arrays.asList(
            "Was für ein schöner Tag in der Lobby!",
            "Ich frage mich, welche Abenteuer heute anstehen.",
            "Hast du schon das neue Minigame ausprobiert?",
            "Diese Aussicht hier oben ist unglaublich.",
            "Ich hab gehört, dass es geheime Räume gibt!",
            "Schon wieder wartest du hier? Wir sollten mal was erleben.",
            "Manchmal bleibe ich einfach stehen und schaue den Wolken zu.",
            "Sag Bescheid, wenn du Tipps für coole Orte brauchst.",
            "Ich liebe es, neue Leute in der Lobby kennenzulernen.",
            "Brauchst du Hilfe? Ich kenne mich hier ganz gut aus.",
            "Hey {SPIELERNAME}! Alles fresh?",
            "Jo {SPIELERNAME}, bleibst du hier oder gehst du gleich los?",
            "Ja LOL, schon gehört {NPC} hat was mit {NPC2}!",
            "{SPIELERNAME}, wenn du Tipps brauchst sag Bescheid!",
            "Ich glaub {NPC} plant wieder einen Streich mit {NPC2}.",
            "Hast du {NPC} heute schon gesehen? Der war mega aufgedreht.",
            "Moin {SPIELERNAME}! Schicke Rüstung übrigens.",
            "{SPIELERNAME}, komm doch mal mit zum Spawn-Festival!",
            "{NPC} meinte, dass {NPC2} eine geheime Basis gefunden hat.",
            "Ganz ehrlich, ohne {SPIELERNAME} wäre es hier viel langweiliger."
        );
    }
    
    private List<String> getChatTemplatesForNPC(NPC npc) {
        List<String> templates = new ArrayList<>(ambientChatLines);
        List<String> personalities = npcAssignedPersonalities.get(npc);
        if (personalities != null) {
            for (String personality : personalities) {
                if (personality == null) continue;
                List<String> lines = personalityChatMap.get(personality.toLowerCase());
                if (lines != null && !lines.isEmpty()) {
                    templates.addAll(lines);
                }
            }
        }
        return templates;
    }

    public List<String> getConversationScriptIds() {
        return conversationScripts.stream()
                .map(ConversationScript::getId)
                .collect(Collectors.toList());
    }

    public ConversationSnapshot getConversationSnapshot(String scriptId) {
        ConversationScript script = findConversationScript(scriptId);
        if (script == null) {
            return null;
        }
        return buildConversationSnapshot(script);
    }

    public List<ConversationSnapshot> getConversationSnapshots() {
        List<ConversationSnapshot> snapshots = new ArrayList<>();
        for (ConversationScript script : conversationScripts) {
            if (script == null) continue;
            ConversationSnapshot snapshot = buildConversationSnapshot(script);
            if (snapshot != null) {
                snapshots.add(snapshot);
            }
        }
        snapshots.sort(Comparator.comparing(ConversationSnapshot::getId, String.CASE_INSENSITIVE_ORDER));
        return snapshots;
    }

    private ConversationSnapshot buildConversationSnapshot(ConversationScript script) {
        if (script == null) {
            return null;
        }
        return new ConversationSnapshot(
                script.getId(),
                script.getTitle(),
                new ArrayList<>(script.getFirstPersonalities()),
                new ArrayList<>(script.getSecondPersonalities()),
                new ArrayList<>(script.getSharedPersonalities())
        );
    }

    public List<ConversationLineSnapshot> getConversationLinesSnapshot(String scriptId) {
        ConversationScript script = findConversationScript(scriptId);
        if (script == null) {
            return Collections.emptyList();
        }
        List<ConversationLineSnapshot> snapshots = new ArrayList<>();
        List<ConversationLine> lines = script.getLines();
        for (int i = 0; i < lines.size(); i++) {
            ConversationLine line = lines.get(i);
            if (line == null) continue;
            snapshots.add(new ConversationLineSnapshot(
                    i,
                    line.getSpeaker() == null ? "FIRST" : line.getSpeaker().name(),
                    line.getText(),
                    line.getPauseTicks()
            ));
        }
        return snapshots;
    }

    public boolean createConversationScript(String scriptId, String title) {
        if (npcConfig == null) return false;
        String key = normalizeConversationId(scriptId);
        if (key == null) return false;
        String path = "npcConversations." + key;
        if (npcConfig.isConfigurationSection(path)) {
            return false;
        }
        ConfigurationSection section = npcConfig.createSection(path);
        section.set("title", (title == null || title.isBlank()) ? key : title.trim());
        section.set("lines", new ArrayList<>());
        saveNpcConfig();
        refreshConversationRuntime();
        return true;
    }

    public boolean deleteConversationScript(String scriptId) {
        if (npcConfig == null) return false;
        String key = normalizeConversationId(scriptId);
        if (key == null) return false;
        String path = "npcConversations." + key;
        if (!npcConfig.isConfigurationSection(path)) {
            return false;
        }
        npcConfig.set(path, null);
        saveNpcConfig();
        refreshConversationRuntime();
        return true;
    }

    public boolean updateConversationTitle(String scriptId, String newTitle) {
        ConfigurationSection section = getConversationSection(scriptId, false);
        if (section == null) return false;
        String value = (newTitle == null || newTitle.isBlank())
                ? section.getName()
                : ChatColor.translateAlternateColorCodes('&', newTitle.trim());
        section.set("title", value);
        saveNpcConfig();
        refreshConversationRuntime();
        return true;
    }

    public List<String> getConversationPersonalityList(String scriptId, ConversationPersonaRole role) {
        ConversationScript script = findConversationScript(scriptId);
        if (script == null) return Collections.emptyList();
        switch (role) {
            case SECOND:
                return new ArrayList<>(script.getSecondPersonalities());
            case SHARED:
                return new ArrayList<>(script.getSharedPersonalities());
            case FIRST:
            default:
                return new ArrayList<>(script.getFirstPersonalities());
        }
    }

    public boolean setConversationPersonalities(String scriptId, ConversationPersonaRole role, List<String> values) {
        ConfigurationSection section = getConversationSection(scriptId, false);
        if (section == null) return false;
        List<String> sanitized = new ArrayList<>();
        if (values != null) {
            for (String value : values) {
                if (value == null) continue;
                String trimmed = value.trim();
                if (!trimmed.isEmpty()) {
                    sanitized.add(trimmed);
                }
            }
        }
        section.set(resolveConversationPersonaPath(role), sanitized);
        saveNpcConfig();
        refreshConversationRuntime();
        return true;
    }

    public boolean addConversationPersonality(String scriptId, ConversationPersonaRole role, String personality) {
        if (personality == null || personality.isBlank()) return false;
        List<String> current = getConversationPersonalityList(scriptId, role);
        for (String existing : current) {
            if (existing.equalsIgnoreCase(personality.trim())) {
                return false;
            }
        }
        current.add(personality.trim());
        return setConversationPersonalities(scriptId, role, current);
    }

    public boolean removeConversationPersonality(String scriptId, ConversationPersonaRole role, String personality) {
        if (personality == null || personality.isBlank()) return false;
        List<String> current = getConversationPersonalityList(scriptId, role);
        boolean removed = current.removeIf(entry -> entry.equalsIgnoreCase(personality.trim()));
        if (!removed) return false;
        return setConversationPersonalities(scriptId, role, current);
    }

    public boolean addConversationLine(String scriptId, String speakerKey, String text, Integer pauseTicks) {
        List<Map<String, Object>> lines = loadConversationLineMaps(scriptId);
        if (lines == null) return false;
        Map<String, Object> map = createConversationLineMap(speakerKey, text, pauseTicks);
        if (map == null) return false;
        lines.add(map);
        return saveConversationLineMaps(scriptId, lines);
    }

    public boolean replaceConversationLine(String scriptId, int index, String speakerKey, String text, Integer pauseTicks) {
        List<Map<String, Object>> lines = loadConversationLineMaps(scriptId);
        if (lines == null) return false;
        if (index < 0 || index >= lines.size()) {
            return false;
        }
        Map<String, Object> map = createConversationLineMap(speakerKey, text, pauseTicks);
        if (map == null) return false;
        lines.set(index, map);
        return saveConversationLineMaps(scriptId, lines);
    }

    public boolean removeConversationLine(String scriptId, int index) {
        List<Map<String, Object>> lines = loadConversationLineMaps(scriptId);
        if (lines == null) return false;
        if (index < 0 || index >= lines.size()) {
            return false;
        }
        lines.remove(index);
        return saveConversationLineMaps(scriptId, lines);
    }
    private Map<String, Object> getDefaultNamePersonalities() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("Alex", Arrays.asList("freundlich"));
        defaults.put("Steve", Arrays.asList("witzig"));
        return defaults;
    }
    
    private Map<String, Object> getDefaultPersonalityLines() {
        Map<String, Object> defaults = new HashMap<>();
        Map<String, Object> freundlichLines = new HashMap<>();
        freundlichLines.put("messages", Arrays.asList(
            "Ich freue mich dich zu sehen!",
            "Braucht jemand Hilfe?"
        ));
        defaults.put("freundlich", freundlichLines);
        
        Map<String, Object> witzigLines = new HashMap<>();
        witzigLines.put("messages", Arrays.asList(
            "Hast du schon den Creeper-Witz gehört?",
            "Ich wollte was sagen, aber der Enderman hat's geklaut."
        ));
        defaults.put("witzig", witzigLines);
        return defaults;
    }
    
    private String getRandomBaseName() {
        if (npcNames.isEmpty()) {
            return "NPC";
        }
        return npcNames.get(random.nextInt(npcNames.size()));
    }
    
    public List<String> getNpcNamesSnapshot() {
        return new ArrayList<>(npcNames);
    }
    
    public List<String> getChatLinesSnapshot() {
        return new ArrayList<>(ambientChatLines);
    }
    
    public Map<String, List<String>> getConversationPersonalityFiltersSnapshot() {
        Map<String, List<String>> snapshot = new LinkedHashMap<>();
        for (ConversationScript script : conversationScripts) {
            if (script.getSharedPersonalities() != null) {
                snapshot.put(script.getId(), new ArrayList<>(script.getSharedPersonalities()));
            }
        }
        return snapshot;
    }
    
    public List<String> getPersonalitiesForNameSnapshot(String name) {
        if (npcConfig == null || name == null) return Collections.emptyList();
        return new ArrayList<>(npcConfig.getStringList("npcPersonalities." + name));
    }
    
    public List<String> getAllPersonalityNamesSnapshot() {
        return new ArrayList<>(new LinkedHashSet<>(personalityCaseLookup.values()));
    }
    
    public List<String> getPersonalityLinesSnapshot(String personality) {
        if (personality == null) return Collections.emptyList();
        String key = resolvePersonalityKey(personality);
        if (key == null) return Collections.emptyList();
        return new ArrayList<>(personalityChatMap.getOrDefault(key.toLowerCase(Locale.ROOT), Collections.emptyList()));
    }
    
    public boolean addNpcName(String name) {
        if (name == null || name.isBlank()) return false;
        npcNames.add(name.trim());
        updateConfigList("npcNames", npcNames);
        return true;
    }
    
    public boolean removeNpcName(String name) {
        if (name == null || name.isBlank()) return false;
        boolean removed = npcNames.removeIf(entry -> entry.equalsIgnoreCase(name.trim()));
        if (removed) {
            updateConfigList("npcNames", npcNames);
        }
        return removed;
    }
    
    public boolean renameNpcName(String oldName, String newName) {
        if (oldName == null || newName == null) return false;
        String trimmed = newName.trim();
        if (trimmed.isEmpty()) return false;
        for (int i = 0; i < npcNames.size(); i++) {
            if (npcNames.get(i).equalsIgnoreCase(oldName)) {
                npcNames.set(i, trimmed);
                updateConfigList("npcNames", npcNames);
                return true;
            }
        }
        return false;
    }
    
    public boolean addChatLine(String line) {
        if (line == null || line.isBlank()) return false;
        ambientChatLines.add(line);
        updateConfigList("chatLines", ambientChatLines);
        return true;
    }
    
    public boolean removeChatLine(String line) {
        if (line == null || line.isBlank()) return false;
        boolean removed = ambientChatLines.removeIf(entry -> entry.equalsIgnoreCase(line.trim()));
        if (removed) {
            updateConfigList("chatLines", ambientChatLines);
        }
        return removed;
    }
    
    public boolean replaceChatLine(String oldLine, String newLine) {
        if (oldLine == null || newLine == null) return false;
        String trimmed = newLine.trim();
        if (trimmed.isEmpty()) return false;
        for (int i = 0; i < ambientChatLines.size(); i++) {
            if (ambientChatLines.get(i).equalsIgnoreCase(oldLine)) {
                ambientChatLines.set(i, trimmed);
                updateConfigList("chatLines", ambientChatLines);
                return true;
            }
        }
        return false;
    }
    
    public boolean assignPersonalityToName(String name, String personality) {
        if (npcConfig == null || name == null || personality == null) return false;
        String trimmedName = name.trim();
        String key = ensurePersonalityExists(personality.trim());
        if (key == null) return false;
        List<String> current = new ArrayList<>(npcConfig.getStringList("npcPersonalities." + trimmedName));
        for (String existing : current) {
            if (existing.equalsIgnoreCase(key)) {
                return false;
            }
        }
        current.add(key);
        npcConfig.set("npcPersonalities." + trimmedName, current);
        addNameToPersonality(key, trimmedName);
        saveNpcConfig();
        loadPersonalityMaps();
        refreshAllNpcPersonalities();
        return true;
    }
    
    public boolean removePersonalityFromName(String name, String personality) {
        if (npcConfig == null || name == null || personality == null) return false;
        String trimmedName = name.trim();
        String key = resolvePersonalityKey(personality);
        if (key == null) return false;
        List<String> current = new ArrayList<>(npcConfig.getStringList("npcPersonalities." + trimmedName));
        boolean removed = current.removeIf(entry -> entry.equalsIgnoreCase(key));
        if (!removed) return false;
        npcConfig.set("npcPersonalities." + trimmedName, current);
        removeNameFromPersonality(key, trimmedName);
        saveNpcConfig();
        loadPersonalityMaps();
        refreshAllNpcPersonalities();
        return true;
    }
    
    public boolean addPersonalityDefinition(String personality) {
        if (npcConfig == null || personality == null) return false;
        String key = ensurePersonalityExists(personality.trim());
        if (key == null) return false;
        saveNpcConfig();
        loadPersonalityMaps();
        return true;
    }
    
    public boolean removePersonalityDefinition(String personality) {
        if (npcConfig == null || personality == null) return false;
        String key = resolvePersonalityKey(personality);
        if (key == null) return false;
        ConfigurationSection section = npcConfig.getConfigurationSection("personalityLines");
        if (section != null) {
            section.set(key, null);
        }
        // remove from all names
        if (npcConfig.isConfigurationSection("npcPersonalities")) {
            ConfigurationSection nameSection = npcConfig.getConfigurationSection("npcPersonalities");
            for (String npcName : nameSection.getKeys(false)) {
                List<String> entries = new ArrayList<>(nameSection.getStringList(npcName));
                if (entries.removeIf(entry -> entry.equalsIgnoreCase(key))) {
                    nameSection.set(npcName, entries);
                }
            }
        }
        saveNpcConfig();
        loadPersonalityMaps();
        refreshAllNpcPersonalities();
        return true;
    }
    
    public boolean addPersonalityLine(String personality, String line) {
        if (npcConfig == null || personality == null || line == null) return false;
        String key = ensurePersonalityExists(personality.trim());
        if (key == null) return false;
        ConfigurationSection section = getPersonalitySection(key);
        List<String> messages = new ArrayList<>(section.getStringList("messages"));
        messages.add(line);
        section.set("messages", messages);
        saveNpcConfig();
        loadPersonalityMaps();
        return true;
    }
    
    public boolean replacePersonalityLine(String personality, String oldLine, String newLine) {
        if (npcConfig == null || personality == null || oldLine == null || newLine == null) return false;
        String key = resolvePersonalityKey(personality);
        if (key == null) return false;
        ConfigurationSection section = getPersonalitySection(key);
        List<String> messages = new ArrayList<>(section.getStringList("messages"));
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).equalsIgnoreCase(oldLine)) {
                messages.set(i, newLine);
                section.set("messages", messages);
                saveNpcConfig();
                loadPersonalityMaps();
                return true;
            }
        }
        return false;
    }
    
    public boolean removePersonalityLine(String personality, String line) {
        if (npcConfig == null || personality == null || line == null) return false;
        String key = resolvePersonalityKey(personality);
        if (key == null) return false;
        ConfigurationSection section = getPersonalitySection(key);
        List<String> messages = new ArrayList<>(section.getStringList("messages"));
        boolean removed = messages.removeIf(entry -> entry.equalsIgnoreCase(line));
        if (!removed) return false;
        section.set("messages", messages);
        saveNpcConfig();
        loadPersonalityMaps();
        return true;
    }
    
    private void updateConfigList(String path, List<String> values) {
        if (npcConfig == null) return;
        npcConfig.set(path, new ArrayList<>(values));
        saveNpcConfig();
    }
    
    private void loadPersonalityMaps() {
        npcNamePersonalities.clear();
        personalityChatMap.clear();
        personalityCaseLookup.clear();
        
        if (npcConfig == null) return;
        
        if (npcConfig.isConfigurationSection("npcPersonalities")) {
            ConfigurationSection section = npcConfig.getConfigurationSection("npcPersonalities");
            for (String key : section.getKeys(false)) {
                List<String> values = section.getStringList(key);
                if (!values.isEmpty()) {
                    npcNamePersonalities.put(key.toLowerCase(Locale.ROOT), new ArrayList<>(values));
                    for (String personality : values) {
                        if (personality == null) continue;
                        String lower = personality.toLowerCase(Locale.ROOT);
                        personalityCaseLookup.putIfAbsent(lower, personality);
                    }
                }
            }
        }
        
        if (npcConfig.isConfigurationSection("personalityLines")) {
            ConfigurationSection section = npcConfig.getConfigurationSection("personalityLines");
            for (String key : section.getKeys(false)) {
                ConfigurationSection personality = section.getConfigurationSection(key);
                if (personality == null) continue;
                String lower = key.toLowerCase(Locale.ROOT);
                personalityCaseLookup.put(lower, key);
                List<String> messages = personality.getStringList("messages");
                List<String> names = personality.getStringList("names");
                if (messages != null && !messages.isEmpty()) {
                    personalityChatMap.put(lower, new ArrayList<>(messages));
                }
                if (names != null && !names.isEmpty()) {
                    for (String name : names) {
                        if (name == null) continue;
                        npcNamePersonalities
                            .computeIfAbsent(name.toLowerCase(Locale.ROOT), unused -> new ArrayList<>())
                            .add(key);
                    }
                }
            }
        }
    }
    
    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.equalsIgnoreCase(b);
    }
    
    /**
     * Sucht den nächstgelegenen Spieler in Reichweite
     */
    private Player findNearestPlayer(Location npcLocation, double maxDistanceSquared) {
        if (npcLocation == null || npcLocation.getWorld() == null) {
            return null;
        }
        
        Player closest = null;
        double closestDistance = maxDistanceSquared;
        
        for (Player player : npcLocation.getWorld().getPlayers()) {
            if (player == null || !player.isOnline() || player.isDead()) {
                continue;
            }
            double distance = player.getLocation().distanceSquared(npcLocation);
            if (distance <= closestDistance) {
                closestDistance = distance;
                closest = player;
            }
        }
        return closest;
    }
    
    private List<Player> getNearbyPlayers(Location origin, double maxDistanceSquared) {
        List<Player> nearby = new ArrayList<>();
        if (origin == null || origin.getWorld() == null) {
            return nearby;
        }
        for (Player player : origin.getWorld().getPlayers()) {
            if (player == null || !player.isOnline() || player.isDead()) {
                continue;
            }
            if (player.getLocation().distanceSquared(origin) <= maxDistanceSquared) {
                nearby.add(player);
            }
        }
        return nearby;
    }

    private ConfigurationSection getConversationSection(String scriptId, boolean create) {
        if (npcConfig == null) return null;
        String key = normalizeConversationId(scriptId);
        if (key == null) return null;
        String path = "npcConversations." + key;
        ConfigurationSection section = npcConfig.getConfigurationSection(path);
        if (section == null && create) {
            section = npcConfig.createSection(path);
        }
        return section;
    }

    private String normalizeConversationId(String scriptId) {
        if (scriptId == null) return null;
        String normalized = scriptId.trim().replace(' ', '_');
        return normalized.isEmpty() ? null : normalized;
    }

    private String resolveConversationPersonaPath(ConversationPersonaRole role) {
        if (role == null) {
            return "sharedPersonalities";
        }
        return switch (role) {
            case FIRST -> "firstPersonalities";
            case SECOND -> "secondPersonalities";
            case SHARED -> "sharedPersonalities";
        };
    }

    private List<Map<String, Object>> loadConversationLineMaps(String scriptId) {
        ConfigurationSection section = getConversationSection(scriptId, false);
        if (section == null) return null;
        List<?> raw = section.getList("lines");
        List<Map<String, Object>> lines = new ArrayList<>();
        if (raw != null) {
            for (Object entry : raw) {
                if (entry instanceof Map) {
                    lines.add(new LinkedHashMap<>((Map<String, Object>) entry));
                } else if (entry != null) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("text", entry.toString());
                    lines.add(map);
                }
            }
        }
        return lines;
    }

    private boolean saveConversationLineMaps(String scriptId, List<Map<String, Object>> lines) {
        ConfigurationSection section = getConversationSection(scriptId, false);
        if (section == null) return false;
        section.set("lines", lines);
        saveNpcConfig();
        refreshConversationRuntime();
        return true;
    }

    private Map<String, Object> createConversationLineMap(String speakerKey, String text, Integer pauseTicks) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        String normalizedSpeaker = normalizeSpeakerKey(speakerKey);
        map.put("speaker", normalizedSpeaker);
        map.put("text", text.trim());
        if (pauseTicks != null) {
            map.put("pauseTicks", pauseTicks);
        }
        return map;
    }

    private String normalizeSpeakerKey(String speakerKey) {
        if (speakerKey == null || speakerKey.isBlank()) {
            return "FIRST";
        }
        String normalized = speakerKey.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "A", "FIRST", "NPC1", "PLAYER1", "1" -> "FIRST";
            case "B", "SECOND", "NPC2", "PLAYER2", "2" -> "SECOND";
            case "BOTH", "ALL", "BEIDE", "ZUSAMMEN" -> "BOTH";
            default -> normalized;
        };
    }

    private void refreshConversationRuntime() {
        loadConversationSettings();
        restartConversationScheduler();
    }

    private ConversationScript findConversationScript(String scriptId) {
        if (scriptId == null) {
            return null;
        }
        for (ConversationScript script : conversationScripts) {
            if (script == null) continue;
            if (equalsIgnoreCase(script.getId(), scriptId)) {
                return script;
            }
        }
        return null;
    }
    
    /**
     * Lässt NPC-Gesichter auf ein Ziel zeigen
     */
    private void faceTarget(NPC npc, Entity entity, Location npcLocation, Location targetLocation) {
        if (targetLocation == null || npcLocation == null) {
            return;
        }
        
        try {
            // Nutze Citizens faceLocation API direkt
            npc.faceLocation(targetLocation);
        } catch (Exception ignored) {
            // Fallback: Rotiere Entity zur Location
            try {
                if (entity instanceof LivingEntity) {
                    Location look = npcLocation.clone();
                    look.setDirection(targetLocation.toVector().subtract(npcLocation.toVector()));
                    entity.teleport(look);
                }
            } catch (Exception ignored2) {
                // Keine weitere Option verfügbar
            }
        }
    }
    
    private void randomHeadMovement(NPC npc, Entity entity, Location npcLocation) {
        if (npcLocation == null || entity == null) return;
        double offsetX = (random.nextDouble() - 0.5) * 8;
        double offsetZ = (random.nextDouble() - 0.5) * 8;
        double offsetY = (random.nextDouble() - 0.5) * 1.0; // leichtes Rauf/Runter
        Location lookTarget = npcLocation.clone().add(offsetX, offsetY, offsetZ);
        faceTarget(npc, entity, npcLocation, lookTarget);
    }
    
    private void refreshAllNpcPersonalities() {
        for (NPC npc : lobbyNPCs) {
            assignPersonalities(npc);
        }
    }
    
    private void cleanupExistingLobbyNPCs() {
        try {
            NPCRegistry registry = CitizensAPI.getNPCRegistry();
            Iterator<NPC> iterator = registry.iterator();
            int removed = 0;
            while (iterator.hasNext()) {
                NPC npc = iterator.next();
                if (npc == null) continue;
                Object shouldRemoveObj = npc.data().get("lobby-npc");
                boolean shouldRemove = false;
                if (shouldRemoveObj instanceof Boolean) {
                    shouldRemove = (Boolean) shouldRemoveObj;
                } else if (shouldRemoveObj instanceof String) {
                    shouldRemove = Boolean.parseBoolean((String) shouldRemoveObj);
                }
                if (shouldRemove) {
                    npc.destroy();
                    removed++;
                }
            }
            if (removed > 0) {
                plugin.getLogger().info("§e" + removed + " bestehende Lobby-NPCs wurden entfernt.");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Konnte alte Lobby-NPCs nicht bereinigen: " + e.getMessage());
        }
    }
    
    private void cleanupCitizensInLobbyWorld() {
        if (!isCitizensAvailable()) return;
        Location lobby = plugin.getLobbyLocation();
        World lobbyWorld = lobby != null ? lobby.getWorld() : Bukkit.getWorld("world");
        if (lobbyWorld == null) return;
        try {
            NPCRegistry registry = CitizensAPI.getNPCRegistry();
            Iterator<NPC> iterator = registry.iterator();
            int removed = 0;
            while (iterator.hasNext()) {
                NPC npc = iterator.next();
                if (npc == null) continue;
                Entity entity = npc.getEntity();
                if (entity != null && entity.getWorld() != null && entity.getWorld().equals(lobbyWorld)) {
                    npc.destroy();
                    removed++;
                }
            }
            if (removed > 0) {
                plugin.getLogger().info("§e" + removed + " Citizens-NPCs aus der Lobby-Welt entfernt.");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Fehler beim Entfernen der Lobby-NPCs: " + e.getMessage());
        }
    }
    
    private void cleanupPersistedNPCs() {
        if (npcData == null) return;
        try {
            List<Integer> ids = npcData.getIntegerList("npcIds");
            if (ids == null || ids.isEmpty()) return;
            NPCRegistry registry = CitizensAPI.getNPCRegistry();
            int removed = 0;
            for (Integer id : ids) {
                if (id == null) continue;
                try {
                    NPC npc = registry.getById(id);
                    if (npc != null) {
                        npc.destroy();
                        removed++;
                    }
                } catch (Exception ignored) { }
            }
            if (removed > 0) {
                plugin.getLogger().info("§e" + removed + " NPCs aus lobby_npcs.yml wurden entfernt.");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Fehler beim Bereinigen der NPC-Daten: " + e.getMessage());
        } finally {
            npcData.set("npcIds", new ArrayList<>());
            saveNpcData();
        }
    }
    
    private void addPersistentNpcEntry(NPC npc) {
        if (npcData == null || npc == null) return;
        try {
            int id = npc.getId();
            List<Integer> ids = new ArrayList<>(npcData.getIntegerList("npcIds"));
            if (!ids.contains(id)) {
                ids.add(id);
                npcData.set("npcIds", ids);
                saveNpcData();
            }
        } catch (Exception ignored) { }
    }
    
    private void removePersistentNpcEntry(NPC npc) {
        if (npcData == null || npc == null) return;
        try {
            int id = npc.getId();
            List<Integer> ids = new ArrayList<>(npcData.getIntegerList("npcIds"));
            if (ids.removeIf(existing -> existing != null && existing.equals(id))) {
                npcData.set("npcIds", ids);
                saveNpcData();
            }
        } catch (Exception ignored) { }
    }

    private void releaseConversationParticipants(ConversationContext context) {
        if (context == null) return;
        unlockNpcForConversation(context.getFirstNpc());
        unlockNpcForConversation(context.getSecondNpc());
    }

    private String resolvePersonalityKey(String personality) {
        if (personality == null) return null;
        String trimmed = personality.trim();
        if (trimmed.isEmpty()) return null;
        String lower = trimmed.toLowerCase(Locale.ROOT);
        return personalityCaseLookup.get(lower);
    }

    private String ensurePersonalityExists(String personality) {
        if (npcConfig == null || personality == null) return null;
        String trimmed = personality.trim();
        if (trimmed.isEmpty()) return null;
        String lower = trimmed.toLowerCase(Locale.ROOT);
        if (personalityCaseLookup.containsKey(lower)) {
            return personalityCaseLookup.get(lower);
        }
        ConfigurationSection base = npcConfig.getConfigurationSection("personalityLines");
        if (base == null) {
            base = npcConfig.createSection("personalityLines");
        }
        ConfigurationSection section = base.createSection(trimmed);
        section.set("messages", new ArrayList<>());
        section.set("names", new ArrayList<>());
        personalityCaseLookup.put(lower, trimmed);
        return trimmed;
    }

    private ConfigurationSection getPersonalitySection(String key) {
        ConfigurationSection base = npcConfig.getConfigurationSection("personalityLines");
        if (base == null) {
            base = npcConfig.createSection("personalityLines");
        }
        ConfigurationSection section = base.getConfigurationSection(key);
        if (section == null) {
            section = base.createSection(key);
        }
        if (!section.isList("messages")) {
            section.set("messages", new ArrayList<>());
        }
        if (!section.isList("names")) {
            section.set("names", new ArrayList<>());
        }
        return section;
    }
    
    private void addNameToPersonality(String personality, String name) {
        ConfigurationSection section = getPersonalitySection(personality);
        List<String> names = new ArrayList<>(section.getStringList("names"));
        boolean exists = names.stream().anyMatch(entry -> entry.equalsIgnoreCase(name));
        if (!exists) {
            names.add(name);
            section.set("names", names);
        }
    }

    private void removeNameFromPersonality(String personality, String name) {
        ConfigurationSection section = getPersonalitySection(personality);
        List<String> names = new ArrayList<>(section.getStringList("names"));
        if (names.removeIf(entry -> entry.equalsIgnoreCase(name))) {
            section.set("names", names);
        }
    }
    
    private boolean matchesPersonalityRequirements(NPC npc, List<String> requirements) {
        if (requirements == null || requirements.isEmpty()) {
            return true;
        }
        List<String> assigned = npcAssignedPersonalities.get(npc);
        if (assigned == null || assigned.isEmpty()) {
            return false;
        }
        for (String required : requirements) {
            for (String have : assigned) {
                if (have.equalsIgnoreCase(required)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void lockNpcForConversation(NPC npc) {
        if (npc != null) {
            conversationLockedNPCs.add(npc);
        }
    }
    
    private void unlockNpcForConversation(NPC npc) {
        if (npc != null) {
            conversationLockedNPCs.remove(npc);
        }
    }

    private static final class ActiveNpcBubble {
        private final String hologramName;
        private final BukkitTask followTask;
        private final BukkitTask cleanupTask;
        private final Boolean previousNameplateState;

        private ActiveNpcBubble(String hologramName, BukkitTask followTask, BukkitTask cleanupTask, Boolean previousNameplateState) {
            this.hologramName = hologramName;
            this.followTask = followTask;
            this.cleanupTask = cleanupTask;
            this.previousNameplateState = previousNameplateState;
        }
    }

    public static final class ConversationSnapshot {
        private final String id;
        private final String title;
        private final List<String> firstPersonalities;
        private final List<String> secondPersonalities;
        private final List<String> sharedPersonalities;

        private ConversationSnapshot(String id,
                                     String title,
                                     List<String> firstPersonalities,
                                     List<String> secondPersonalities,
                                     List<String> sharedPersonalities) {
            this.id = id;
            this.title = title;
            this.firstPersonalities = firstPersonalities;
            this.secondPersonalities = secondPersonalities;
            this.sharedPersonalities = sharedPersonalities;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getFirstPersonalities() {
            return firstPersonalities;
        }

        public List<String> getSecondPersonalities() {
            return secondPersonalities;
        }

        public List<String> getSharedPersonalities() {
            return sharedPersonalities;
        }
    }

    public static final class ConversationLineSnapshot {
        private final int index;
        private final String speaker;
        private final String text;
        private final int pauseTicks;

        private ConversationLineSnapshot(int index, String speaker, String text, int pauseTicks) {
            this.index = index;
            this.speaker = speaker;
            this.text = text;
            this.pauseTicks = pauseTicks;
        }

        public int getIndex() {
            return index;
        }

        public String getSpeaker() {
            return speaker;
        }

        public String getText() {
            return text;
        }

        public int getPauseTicks() {
            return pauseTicks;
        }
    }

    public enum ConversationPersonaRole {
        FIRST,
        SECOND,
        SHARED
    }

    private static final class ConversationScript {
        private final String id;
        private final String title;
        private final List<ConversationLine> lines;
        private final List<String> firstPersonalities;
        private final List<String> secondPersonalities;
        private final List<String> sharedPersonalities;

        private ConversationScript(String id, String title, List<ConversationLine> lines,
                                   List<String> firstPersonalities,
                                   List<String> secondPersonalities,
                                   List<String> sharedPersonalities) {
            this.id = id;
            this.title = title;
            this.lines = lines;
            this.firstPersonalities = firstPersonalities == null ? Collections.emptyList() : firstPersonalities;
            this.secondPersonalities = secondPersonalities == null ? Collections.emptyList() : secondPersonalities;
            this.sharedPersonalities = sharedPersonalities == null ? Collections.emptyList() : sharedPersonalities;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public List<ConversationLine> getLines() {
            return lines;
        }

        public List<String> getFirstPersonalities() {
            return firstPersonalities;
        }

        public List<String> getSecondPersonalities() {
            return secondPersonalities;
        }

        public List<String> getSharedPersonalities() {
            return sharedPersonalities;
        }
    }

    private static final class ConversationLine {
        private final ConversationSpeaker speaker;
        private final String text;
        private final int pauseTicks;

        private ConversationLine(ConversationSpeaker speaker, String text, int pauseTicks) {
            this.speaker = speaker;
            this.text = text;
            this.pauseTicks = pauseTicks;
        }

        public ConversationSpeaker getSpeaker() {
            return speaker;
        }

        public String getText() {
            return text;
        }

        public int getPauseTicks() {
            return pauseTicks;
        }
    }

    private enum ConversationSpeaker {
        FIRST,
        SECOND,
        BOTH
    }

    private static final class ConversationContext {
        private final ConversationScript script;
        private final NPC firstNpc;
        private final NPC secondNpc;
        private final String firstName;
        private final String secondName;
        private final Location meetingPoint;

        private ConversationContext(ConversationScript script,
                                    NPC firstNpc,
                                    NPC secondNpc,
                                    String firstName,
                                    String secondName,
                                    Location meetingPoint) {
            this.script = script;
            this.firstNpc = firstNpc;
            this.secondNpc = secondNpc;
            this.firstName = firstName == null ? "NPC" : firstName;
            this.secondName = secondName == null ? "NPC" : secondName;
            this.meetingPoint = meetingPoint != null ? meetingPoint.clone() : null;
        }

        public ConversationScript getScript() {
            return script;
        }

        public NPC getFirstNpc() {
            return firstNpc;
        }

        public NPC getSecondNpc() {
            return secondNpc;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public Location getMeetingPoint() {
            return meetingPoint;
        }
    }
}
