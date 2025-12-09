package npc;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
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

    private static final long CHAT_HOLOGRAM_LIFETIME_TICKS = 20L * 5;
    private static final long CHAT_HOLOGRAM_FOLLOW_INTERVAL = 10L;
    private static final double CHAT_HOLOGRAM_VERTICAL_OFFSET = 2.25D;

    private final Main plugin;
    private final List<NPC> lobbyNPCs; // NPCs von der Citizens API
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

}
