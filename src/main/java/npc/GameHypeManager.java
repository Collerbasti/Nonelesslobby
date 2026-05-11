package npc;

import de.noneless.lobby.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import net.citizensnpcs.api.npc.NPC;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Lässt zufällige Lobby-NPCs periodisch über aktive Duelle und Booster
 * im MiniCardGame-Shop schwärmen.
 *
 * <p>Datenzugriff auf das NonelessGame-Plugin erfolgt via Reflection, damit
 * keine Compile-Zeit-Abhängigkeit entsteht.
 *
 * <p>Platzhalter in Templates:
 * <ul>
 *   <li>Booster:  {@code {booster}} → Booster-Name,  {@code {kosten}} → Kosten in Punkten</li>
 *   <li>Duell:    {@code {spieler1}} {@code {spieler2}} → Spieler-Namen</li>
 * </ul>
 *
 * <p>Konfiguration (in {@code npc_config.yml}, Abschnitt {@code gameHype}):
 * <pre>
 *   gameHype:
 *     boosterEnabled: true
 *     duelEnabled: true
 *     intervalMinSeconds: 120
 *     intervalMaxSeconds: 300
 *     boosterTemplates:
 *       - "..."
 *     duelTemplates:
 *       - "..."
 * </pre>
 */
public final class GameHypeManager {

    private static final String CARD_GAME_PLUGIN = "NonelessGame";

    // Config keys (flat, stored in npcConfig under section "gameHype")
    private static final String CFG_BOOSTER_ENABLED  = "gameHype.boosterEnabled";
    private static final String CFG_DUEL_ENABLED     = "gameHype.duelEnabled";
    private static final String CFG_INTERVAL_MIN     = "gameHype.intervalMinSeconds";
    private static final String CFG_INTERVAL_MAX     = "gameHype.intervalMaxSeconds";
    private static final String CFG_BOOSTER_TEMPLATES = "gameHype.boosterTemplates";
    private static final String CFG_DUEL_TEMPLATES   = "gameHype.duelTemplates";

    // ── Configurable state ────────────────────────────────────────────────────

    private boolean boosterEnabled = true;
    private boolean duelEnabled    = true;
    private int intervalMinSeconds = 120;
    private int intervalMaxSeconds = 300;
    private final List<String> boosterTemplates = new ArrayList<>();
    private final List<String> duelTemplates    = new ArrayList<>();

    // ── Runtime state ─────────────────────────────────────────────────────────

    private final Main plugin;
    private NPCManager manager;
    private BukkitTask task;

    // ── Constructor ───────────────────────────────────────────────────────────

    public GameHypeManager(Main plugin) {
        this.plugin = plugin;
        boosterTemplates.addAll(getDefaultBoosterTemplates());
        duelTemplates.addAll(getDefaultDuelTemplates());
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Startet den periodischen Hype-Task. Wird vom NPCManager aufgerufen.
     */
    public void start(NPCManager npcManager) {
        this.manager = npcManager;
        scheduleNext();
    }

    /**
     * Stoppt den periodischen Hype-Task. Wird beim Server-Shutdown aufgerufen.
     */
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    // ── Scheduling ────────────────────────────────────────────────────────────

    private void scheduleNext() {
        if (!boosterEnabled && !duelEnabled) return;
        int range = Math.max(1, intervalMaxSeconds - intervalMinSeconds);
        long ticks = 20L * (intervalMinSeconds + ThreadLocalRandom.current().nextInt(range));
        task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                fire();
            } finally {
                scheduleNext();
            }
        }, ticks);
    }

    private void fire() {
        if (manager == null) return;

        // Pick a random spawned NPC
        List<NPC> spawned = new ArrayList<>();
        for (NPC n : manager.getLobbyNPCs()) {
            if (n != null && n.isSpawned()) spawned.add(n);
        }
        if (spawned.isEmpty()) return;
        NPC npc = spawned.get(ThreadLocalRandom.current().nextInt(spawned.size()));

        // Decide type order (random priority)
        List<String> order = new ArrayList<>();
        if (boosterEnabled) order.add("booster");
        if (duelEnabled)    order.add("duel");
        Collections.shuffle(order);

        String message = null;
        for (String type : order) {
            message = "booster".equals(type) ? buildBoosterMessage() : buildDuelMessage();
            if (message != null) break;
        }
        if (message == null) return;

        manager.triggerHypeLine(npc, message);
    }

    // ── Message builders ──────────────────────────────────────────────────────

    private String buildBoosterMessage() {
        if (boosterTemplates.isEmpty()) return null;
        List<BoosterEntry> boosters = fetchBoosters();
        String boosterName;
        String cost;
        if (boosters.isEmpty()) {
            boosterName = "Daily-Booster";
            cost = "?";
        } else {
            BoosterEntry entry = boosters.get(ThreadLocalRandom.current().nextInt(boosters.size()));
            boosterName = entry.name;
            cost = String.valueOf(entry.cost);
        }
        return randomTemplate(boosterTemplates)
                .replace("{booster}", boosterName)
                .replace("{kosten}", cost);
    }

    private String buildDuelMessage() {
        if (duelTemplates.isEmpty()) return null;
        List<DuelEntry> duels = fetchActiveDuels();
        if (duels.isEmpty()) return null;
        DuelEntry duel = duels.get(ThreadLocalRandom.current().nextInt(duels.size()));
        return randomTemplate(duelTemplates)
                .replace("{spieler1}", duel.player1)
                .replace("{spieler2}", duel.player2);
    }

    private String randomTemplate(List<String> templates) {
        return templates.get(ThreadLocalRandom.current().nextInt(templates.size()));
    }

    // ── Reflection: fetch live data from MiniCardGame ─────────────────────────

    /**
     * Holt die aktuellen Booster aus CardGameState.boosters via Reflection.
     */
    private List<BoosterEntry> fetchBoosters() {
        List<BoosterEntry> result = new ArrayList<>();
        try {
            Plugin cardGame = Bukkit.getPluginManager().getPlugin(CARD_GAME_PLUGIN);
            if (cardGame == null || !cardGame.isEnabled()) return result;
            ClassLoader cl = cardGame.getClass().getClassLoader();

            Class<?> stateClass = Class.forName("de.noneless.cardgame.Main.CardGameState", true, cl);
            Field boostersField = stateClass.getField("boosters");
            @SuppressWarnings("unchecked")
            Map<Integer, Object> boosters = (Map<Integer, Object>) boostersField.get(null);

            for (Object display : boosters.values()) {
                try {
                    Method getPack = display.getClass().getMethod("getPack");
                    Object pack = getPack.invoke(display);
                    if (pack == null) continue;
                    Method getName = pack.getClass().getMethod("getName");
                    Method getCost = pack.getClass().getMethod("getCost");
                    String name = ChatColor.stripColor((String) getName.invoke(pack));
                    int cost = (int) getCost.invoke(pack);
                    result.add(new BoosterEntry(name, cost));
                } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }
        return result;
    }

    /**
     * Holt die aktuell aktiven Duelle aus Duell.Duelle via Reflection.
     */
    private List<DuelEntry> fetchActiveDuels() {
        List<DuelEntry> result = new ArrayList<>();
        try {
            Plugin cardGame = Bukkit.getPluginManager().getPlugin(CARD_GAME_PLUGIN);
            if (cardGame == null || !cardGame.isEnabled()) return result;
            ClassLoader cl = cardGame.getClass().getClassLoader();

            Class<?> duellClass = Class.forName("de.noneless.cardgame.Game.Duell", true, cl);
            Field duelleField = duellClass.getField("Duelle");
            @SuppressWarnings("unchecked")
            List<Object> duelle = new ArrayList<>((List<Object>) duelleField.get(null));

            Method isActive  = duellClass.getMethod("isActive");
            Method getPlayer1 = duellClass.getMethod("getPlayer1");
            Method getPlayer2 = duellClass.getMethod("getPlayer2");

            Class<?> duellantClass = Class.forName("de.noneless.cardgame.Game.Duellant", true, cl);
            Method getPlayer = duellantClass.getMethod("getPlayer");

            for (Object duel : duelle) {
                try {
                    if (!(boolean) isActive.invoke(duel)) continue;
                    Object d1 = getPlayer1.invoke(duel);
                    Object d2 = getPlayer2.invoke(duel);
                    String name1 = resolveDuellantName(d1, getPlayer);
                    String name2 = resolveDuellantName(d2, getPlayer);
                    result.add(new DuelEntry(name1, name2));
                } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }
        return result;
    }

    private String resolveDuellantName(Object duellant, Method getPlayer) {
        if (duellant == null) return "?";
        try {
            Player p = (Player) getPlayer.invoke(duellant);
            if (p != null) return p.getName();
        } catch (Exception ignored) { }
        // BotDuellant — try getProfile().getDisplayName()
        try {
            Method getProfile     = duellant.getClass().getMethod("getProfile");
            Object profile        = getProfile.invoke(duellant);
            Method getDisplayName = profile.getClass().getMethod("getDisplayName");
            return ChatColor.stripColor((String) getDisplayName.invoke(profile));
        } catch (Exception ignored) { }
        return "Bot";
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    public void load(FileConfiguration cfg) {
        boosterEnabled     = cfg.getBoolean(CFG_BOOSTER_ENABLED, true);
        duelEnabled        = cfg.getBoolean(CFG_DUEL_ENABLED, true);
        intervalMinSeconds = Math.max(30, cfg.getInt(CFG_INTERVAL_MIN, 120));
        intervalMaxSeconds = Math.max(intervalMinSeconds + 10, cfg.getInt(CFG_INTERVAL_MAX, 300));

        List<String> bt = cfg.getStringList(CFG_BOOSTER_TEMPLATES);
        if (!bt.isEmpty()) {
            boosterTemplates.clear();
            boosterTemplates.addAll(bt);
        }
        List<String> dt = cfg.getStringList(CFG_DUEL_TEMPLATES);
        if (!dt.isEmpty()) {
            duelTemplates.clear();
            duelTemplates.addAll(dt);
        }
    }

    public void save(FileConfiguration cfg) {
        cfg.set(CFG_BOOSTER_ENABLED,   boosterEnabled);
        cfg.set(CFG_DUEL_ENABLED,      duelEnabled);
        cfg.set(CFG_INTERVAL_MIN,      intervalMinSeconds);
        cfg.set(CFG_INTERVAL_MAX,      intervalMaxSeconds);
        cfg.set(CFG_BOOSTER_TEMPLATES, new ArrayList<>(boosterTemplates));
        cfg.set(CFG_DUEL_TEMPLATES,    new ArrayList<>(duelTemplates));
    }

    // ── Public API (for admin menu) ───────────────────────────────────────────

    public boolean isBoosterEnabled()    { return boosterEnabled; }
    public boolean isDuelEnabled()       { return duelEnabled; }
    public int getIntervalMinSeconds()   { return intervalMinSeconds; }
    public int getIntervalMaxSeconds()   { return intervalMaxSeconds; }

    public List<String> getBoosterTemplates() { return Collections.unmodifiableList(boosterTemplates); }
    public List<String> getDuelTemplates()    { return Collections.unmodifiableList(duelTemplates); }

    public void setBoosterEnabled(boolean v) { boosterEnabled = v; }
    public void setDuelEnabled(boolean v)    { duelEnabled = v; }

    public void setIntervalMin(int seconds) {
        intervalMinSeconds = Math.max(30, seconds);
        if (intervalMaxSeconds <= intervalMinSeconds) intervalMaxSeconds = intervalMinSeconds + 30;
    }

    public void setIntervalMax(int seconds) {
        intervalMaxSeconds = Math.max(intervalMinSeconds + 10, seconds);
    }

    public void addBoosterTemplate(String t) {
        if (t != null && !t.isBlank()) boosterTemplates.add(t.trim());
    }

    public boolean removeBoosterTemplate(int index) {
        if (index < 0 || index >= boosterTemplates.size()) return false;
        boosterTemplates.remove(index);
        return true;
    }

    public void addDuelTemplate(String t) {
        if (t != null && !t.isBlank()) duelTemplates.add(t.trim());
    }

    public boolean removeDuelTemplate(int index) {
        if (index < 0 || index >= duelTemplates.size()) return false;
        duelTemplates.remove(index);
        return true;
    }

    // ── Defaults ──────────────────────────────────────────────────────────────

    public static List<String> getDefaultBoosterTemplates() {
        return Arrays.asList(
                "Psst! Der §e{booster}§f-Booster ist gerade für §a{kosten} Punkte§f erhältlich!",
                "Hey, habt ihr schon den §e{booster}§f-Booster gesehen? Nur §a{kosten} Punkte§f!",
                "§e{booster}§f für §a{kosten} Punkte§f - das ist ein echtes Schnäppchen!",
                "Falls ihr neue Karten wollt: §e{booster}§f im Shop für §a{kosten} Punkte§f!",
                "Ich hab gehört, der §e{booster}§f-Booster lohnt sich sehr!"
        );
    }

    public static List<String> getDefaultDuelTemplates() {
        return Arrays.asList(
                "Wahnsinn! §c{spieler1}§f und §c{spieler2}§f kämpfen gerade!",
                "Das Duell zwischen §c{spieler1}§f und §c{spieler2}§f ist episch!",
                "Schaut mal! §c{spieler1}§f kämpft gegen §c{spieler2}§f!",
                "Ich glaube §c{spieler1}§f schlägt §c{spieler2}§f heute... oder doch nicht?",
                "§c{spieler1}§f vs §c{spieler2}§f - wer wird wohl gewinnen?"
        );
    }

    // ── Internal data classes ─────────────────────────────────────────────────

    private static final class BoosterEntry {
        final String name;
        final int cost;
        BoosterEntry(String name, int cost) { this.name = name; this.cost = cost; }
    }

    private static final class DuelEntry {
        final String player1;
        final String player2;
        DuelEntry(String p1, String p2) { this.player1 = p1; this.player2 = p2; }
    }
}
