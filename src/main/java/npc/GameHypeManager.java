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
 * <p>Daily- und Monthly-Booster werden getrennt verwaltet und mit eigenen
 * Template-Listen angesprochen. Beim Feuern wird je ein konkreter Booster
 * aus der jeweiligen Kategorie ausgewählt.
 *
 * <p>Platzhalter:
 * <ul>
 *   <li>Daily/Monthly:  {@code {booster}} → Name,  {@code {kosten}} → Punkte</li>
 *   <li>Duell:          {@code {spieler1}} {@code {spieler2}}</li>
 * </ul>
 *
 * <p>Slot-Zuordnung (muss mit BoosterRegistry übereinstimmen):
 * <ul>
 *   <li>Daily   → Slots 10, 11</li>
 *   <li>Monthly → Slots 13, 14, 15</li>
 * </ul>
 */
public final class GameHypeManager {

    private static final String CARD_GAME_PLUGIN = "NonelessGame";

    /** Slots der Daily-Booster (muss mit BoosterRegistry.DAILY_SLOTS übereinstimmen). */
    private static final Set<Integer> DAILY_SLOTS   = new HashSet<>(Arrays.asList(10, 11));
    /** Slots der Monthly-Booster (muss mit BoosterRegistry.MONTHLY_SLOTS übereinstimmen). */
    private static final Set<Integer> MONTHLY_SLOTS = new HashSet<>(Arrays.asList(13, 14, 15));

    // ── Config keys ───────────────────────────────────────────────────────────

    private static final String CFG_DAILY_ENABLED    = "gameHype.dailyEnabled";
    private static final String CFG_MONTHLY_ENABLED  = "gameHype.monthlyEnabled";
    private static final String CFG_DUEL_ENABLED     = "gameHype.duelEnabled";
    private static final String CFG_INTERVAL_MIN     = "gameHype.intervalMinSeconds";
    private static final String CFG_INTERVAL_MAX     = "gameHype.intervalMaxSeconds";
    private static final String CFG_DAILY_TEMPLATES  = "gameHype.dailyTemplates";
    private static final String CFG_MONTHLY_TEMPLATES = "gameHype.monthlyTemplates";
    private static final String CFG_DUEL_TEMPLATES   = "gameHype.duelTemplates";
    // Legacy key for migration
    private static final String CFG_LEGACY_BOOSTER_TEMPLATES = "gameHype.boosterTemplates";

    // ── Configurable state ────────────────────────────────────────────────────

    private boolean dailyEnabled   = true;
    private boolean monthlyEnabled = true;
    private boolean duelEnabled    = true;
    private int intervalMinSeconds = 120;
    private int intervalMaxSeconds = 300;

    private final List<String> dailyTemplates   = new ArrayList<>();
    private final List<String> monthlyTemplates = new ArrayList<>();
    private final List<String> duelTemplates    = new ArrayList<>();

    // ── Runtime state ─────────────────────────────────────────────────────────

    private final Main plugin;
    private NPCManager manager;
    private BukkitTask task;

    // ── Constructor ───────────────────────────────────────────────────────────

    public GameHypeManager(Main plugin) {
        this.plugin = plugin;
        dailyTemplates.addAll(getDefaultDailyTemplates());
        monthlyTemplates.addAll(getDefaultMonthlyTemplates());
        duelTemplates.addAll(getDefaultDuelTemplates());
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    public void start(NPCManager npcManager) {
        this.manager = npcManager;
        scheduleNext();
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    // ── Scheduling ────────────────────────────────────────────────────────────

    private void scheduleNext() {
        if (!dailyEnabled && !monthlyEnabled && !duelEnabled) return;
        int range = Math.max(1, intervalMaxSeconds - intervalMinSeconds);
        long ticks = 20L * (intervalMinSeconds + ThreadLocalRandom.current().nextInt(range));
        task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try { fire(); } finally { scheduleNext(); }
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

        // Build candidate types in random order
        List<String> types = new ArrayList<>();
        if (dailyEnabled)   types.add("daily");
        if (monthlyEnabled) types.add("monthly");
        if (duelEnabled)    types.add("duel");
        Collections.shuffle(types);

        for (String type : types) {
            String msg = switch (type) {
                case "daily"   -> buildBoosterMessage(dailyTemplates,   DAILY_SLOTS);
                case "monthly" -> buildBoosterMessage(monthlyTemplates, MONTHLY_SLOTS);
                case "duel"    -> buildDuelMessage();
                default        -> null;
            };
            if (msg != null) {
                manager.triggerHypeLine(npc, msg);
                return;
            }
        }
    }

    // ── Message builders ──────────────────────────────────────────────────────

    /**
     * Baut eine Booster-Nachricht für die angegebenen Slots.
     * Wählt einen konkreten Booster zufällig aus den verfügbaren aus.
     */
    private String buildBoosterMessage(List<String> templates, Set<Integer> slots) {
        if (templates.isEmpty()) return null;
        List<BoosterEntry> boosters = fetchBoosters(slots);
        if (boosters.isEmpty()) return null; // Keine Booster in dieser Kategorie aktiv
        BoosterEntry entry = boosters.get(ThreadLocalRandom.current().nextInt(boosters.size()));
        return randomTemplate(templates)
                .replace("{booster}", entry.name)
                .replace("{kosten}", String.valueOf(entry.cost));
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
     * Holt Booster aus CardGameState.boosters, gefiltert nach Slot-Nummern.
     * Jeder Slot entspricht genau einem Booster → bei mehreren Daily-Slots
     * kommt ein Eintrag pro aktivem Slot zurück.
     */
    private List<BoosterEntry> fetchBoosters(Set<Integer> allowedSlots) {
        List<BoosterEntry> result = new ArrayList<>();
        try {
            Plugin cardGame = Bukkit.getPluginManager().getPlugin(CARD_GAME_PLUGIN);
            if (cardGame == null || !cardGame.isEnabled()) return result;
            ClassLoader cl = cardGame.getClass().getClassLoader();

            Class<?> stateClass = Class.forName("de.noneless.cardgame.Main.CardGameState", true, cl);
            Field boostersField = stateClass.getField("boosters");
            @SuppressWarnings("unchecked")
            Map<Integer, Object> boosters = (Map<Integer, Object>) boostersField.get(null);

            for (Map.Entry<Integer, Object> e : boosters.entrySet()) {
                int slot = e.getKey();
                if (!allowedSlots.contains(slot)) continue; // Nur passende Kategorie
                try {
                    Object display = e.getValue();
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

            Method isActive   = duellClass.getMethod("isActive");
            Method getPlayer1 = duellClass.getMethod("getPlayer1");
            Method getPlayer2 = duellClass.getMethod("getPlayer2");

            Class<?> duellantClass = Class.forName("de.noneless.cardgame.Game.Duellant", true, cl);
            Method getPlayer = duellantClass.getMethod("getPlayer");

            for (Object duel : duelle) {
                try {
                    if (!(boolean) isActive.invoke(duel)) continue;
                    Object d1 = getPlayer1.invoke(duel);
                    Object d2 = getPlayer2.invoke(duel);
                    result.add(new DuelEntry(
                            resolveDuellantName(d1, getPlayer),
                            resolveDuellantName(d2, getPlayer)));
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
        dailyEnabled   = cfg.getBoolean(CFG_DAILY_ENABLED,
                cfg.getBoolean("gameHype.boosterEnabled", true)); // legacy fallback
        monthlyEnabled = cfg.getBoolean(CFG_MONTHLY_ENABLED, true);
        duelEnabled    = cfg.getBoolean(CFG_DUEL_ENABLED, true);
        intervalMinSeconds = Math.max(30, cfg.getInt(CFG_INTERVAL_MIN, 120));
        intervalMaxSeconds = Math.max(intervalMinSeconds + 10, cfg.getInt(CFG_INTERVAL_MAX, 300));

        List<String> dt_raw = cfg.getStringList(CFG_DAILY_TEMPLATES);
        if (!dt_raw.isEmpty()) {
            dailyTemplates.clear();
            dailyTemplates.addAll(dt_raw);
        } else {
            // Migration: falls alte boosterTemplates vorhanden, übernehmen
            List<String> legacy = cfg.getStringList(CFG_LEGACY_BOOSTER_TEMPLATES);
            if (!legacy.isEmpty()) {
                dailyTemplates.clear();
                dailyTemplates.addAll(legacy);
            }
        }

        List<String> mt_raw = cfg.getStringList(CFG_MONTHLY_TEMPLATES);
        if (!mt_raw.isEmpty()) {
            monthlyTemplates.clear();
            monthlyTemplates.addAll(mt_raw);
        }

        List<String> duel_raw = cfg.getStringList(CFG_DUEL_TEMPLATES);
        if (!duel_raw.isEmpty()) {
            duelTemplates.clear();
            duelTemplates.addAll(duel_raw);
        }
    }

    public void save(FileConfiguration cfg) {
        cfg.set(CFG_DAILY_ENABLED,    dailyEnabled);
        cfg.set(CFG_MONTHLY_ENABLED,  monthlyEnabled);
        cfg.set(CFG_DUEL_ENABLED,     duelEnabled);
        cfg.set(CFG_INTERVAL_MIN,     intervalMinSeconds);
        cfg.set(CFG_INTERVAL_MAX,     intervalMaxSeconds);
        cfg.set(CFG_DAILY_TEMPLATES,  new ArrayList<>(dailyTemplates));
        cfg.set(CFG_MONTHLY_TEMPLATES, new ArrayList<>(monthlyTemplates));
        cfg.set(CFG_DUEL_TEMPLATES,   new ArrayList<>(duelTemplates));
        // Alten Key entfernen, damit keine veralteten Daten übrig bleiben
        cfg.set("gameHype.boosterEnabled",  null);
        cfg.set("gameHype.boosterTemplates", null);
    }

    // ── Public API (for admin menu) ───────────────────────────────────────────

    public boolean isDailyEnabled()    { return dailyEnabled; }
    public boolean isMonthlyEnabled()  { return monthlyEnabled; }
    public boolean isDuelEnabled()     { return duelEnabled; }
    public int getIntervalMinSeconds() { return intervalMinSeconds; }
    public int getIntervalMaxSeconds() { return intervalMaxSeconds; }

    public List<String> getDailyTemplates()   { return Collections.unmodifiableList(dailyTemplates); }
    public List<String> getMonthlyTemplates() { return Collections.unmodifiableList(monthlyTemplates); }
    public List<String> getDuelTemplates()    { return Collections.unmodifiableList(duelTemplates); }

    public void setDailyEnabled(boolean v)   { dailyEnabled = v; }
    public void setMonthlyEnabled(boolean v) { monthlyEnabled = v; }
    public void setDuelEnabled(boolean v)    { duelEnabled = v; }

    public void setIntervalMin(int seconds) {
        intervalMinSeconds = Math.max(30, seconds);
        if (intervalMaxSeconds <= intervalMinSeconds) intervalMaxSeconds = intervalMinSeconds + 30;
    }

    public void setIntervalMax(int seconds) {
        intervalMaxSeconds = Math.max(intervalMinSeconds + 10, seconds);
    }

    // Daily templates
    public void addDailyTemplate(String t) {
        if (t != null && !t.isBlank()) dailyTemplates.add(t.trim());
    }
    public boolean removeDailyTemplate(int index) {
        if (index < 0 || index >= dailyTemplates.size()) return false;
        dailyTemplates.remove(index);
        return true;
    }

    // Monthly templates
    public void addMonthlyTemplate(String t) {
        if (t != null && !t.isBlank()) monthlyTemplates.add(t.trim());
    }
    public boolean removeMonthlyTemplate(int index) {
        if (index < 0 || index >= monthlyTemplates.size()) return false;
        monthlyTemplates.remove(index);
        return true;
    }

    // Duel templates
    public void addDuelTemplate(String t) {
        if (t != null && !t.isBlank()) duelTemplates.add(t.trim());
    }
    public boolean removeDuelTemplate(int index) {
        if (index < 0 || index >= duelTemplates.size()) return false;
        duelTemplates.remove(index);
        return true;
    }

    // ── Defaults ──────────────────────────────────────────────────────────────

    public static List<String> getDefaultDailyTemplates() {
        return Arrays.asList(
                "Psst! Der §e{booster}§f-Booster ist heute für §a{kosten} Punkte§f erhältlich!",
                "Der tägliche §e{booster}§f-Booster wartet auf euch - nur §a{kosten} Punkte§f!",
                "Heute im Shop: §e{booster}§f für §a{kosten} Punkte§f. Lohnt sich!",
                "Habt ihr schon den heutigen §e{booster}§f-Booster geholt? §a{kosten} Punkte§f!",
                "§e{booster}§f ist der Daily-Booster des Tages - §a{kosten} Punkte§f!"
        );
    }

    public static List<String> getDefaultMonthlyTemplates() {
        return Arrays.asList(
                "Dieser Monat gibt es den §b{booster}§f-Booster für §a{kosten} Punkte§f!",
                "Der Monats-Booster §b{booster}§f ist noch für §a{kosten} Punkte§f erhältlich!",
                "Habt ihr schon den §b{booster}§f-Monats-Booster? Nur §a{kosten} Punkte§f!",
                "§b{booster}§f ist der Booster des Monats - §a{kosten} Punkte§f. Nicht verpassen!",
                "Tipp des Monats: §b{booster}§f für §a{kosten} Punkte§f im Shop!"
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
