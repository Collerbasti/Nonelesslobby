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
    private final Map<NPC, BukkitTask> pairFollowTasks;
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
    private final Map<String, SpawnEntry> savedSpawns = new LinkedHashMap<>();
    // pairing: npcId -> PairEntry (internal)
    private final Map<Integer, PairEntry> pairs = new LinkedHashMap<>();

    private static final class PairEntry {
        final int partnerId;
        final String prefix;

        PairEntry(int partnerId, String prefix) {
            this.partnerId = partnerId;
            this.prefix = prefix;
        }
    }

    public static final class PairInfo {
        public final int partnerId;
        public final String prefix;

        public PairInfo(int partnerId, String prefix) {
            this.partnerId = partnerId;
            this.prefix = prefix;
        }
    }
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
        this.conversationLineDelayTicks = Math.max(20, ticks);
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
        this.pairFollowTasks = new IdentityHashMap<>();
        this.npcConfigFile = new File(plugin.getDataFolder(), "npc_config.yml");
        this.npcDataFile = new File(plugin.getDataFolder(), "lobby_npcs.yml");
        this.conversationPrefix = ChatColor.DARK_PURPLE + "[Privat]";
        this.conversationAudienceRadiusSquared = 2500;
        this.conversationMinIntervalSeconds = 120;
        this.conversationMaxIntervalSeconds = 240;
        this.conversationLineDelayTicks = 100;
        this.conversationGatherDelayTicks = 60;
        this.hologramsAvailable = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
        if (!this.hologramsAvailable) {
            plugin.getLogger().info("DecentHolograms nicht gefunden - NPC-Sprechblasen bleiben deaktiviert.");
        }
        loadNpcConfig();
        loadNpcData();
        loadPairs();
        this.citizensAvailable = checkCitizensAvailability();
        if (this.citizensAvailable) {
            cleanupExistingLobbyNPCs();
            cleanupPersistedNPCs();
            try {
                for (SpawnEntry entry : new ArrayList<>(savedSpawns.values())) {
                    if (entry != null) {
                        spawnNpcForEntry(entry);
                    }
                }
            } catch (Exception ignored) { }
        }
        scheduleInitialLobbyReset();
    }
    
    public void reloadNpcConfig() {
        loadNpcConfig();
    }

    // ... backup contains full original content (trimmed here for brevity) ...
