package de.noneless.lobby.Menues;

import de.noneless.lobby.Main;
import npc.GameHypeManager;
import npc.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin-Menü zum Konfigurieren des NPC-Spiel-Hypes.
 *
 * <p>Layout (54 Slots):
 * <pre>
 *  Row 0  ( 0- 8):  Glas-Header
 *  Row 1  ( 9-17):  Daily-Toggle | Monthly-Toggle | Duell-Toggle | Min-Intervall | Max-Intervall
 *  Row 2  (18-26):  [GOLD]  Daily-Header  + bis zu 8 Daily-Templates   (Slots 19-26)
 *  Row 3  (27-35):  [AQUA]  Monthly-Header + bis zu 8 Monthly-Templates (Slots 28-35)
 *  Row 4  (36-44):  [ROT]   Duell-Header  + bis zu 8 Duell-Templates   (Slots 37-44)
 *  Row 5  (45-53):  + Daily | + Monthly | + Duell | Reset Daily | Reset Monthly | Reset Duell | Info | Back
 * </pre>
 */
public class NPCGameHypeMenu {

    public static final String TITLE = ChatColor.DARK_PURPLE + "Spiel-Hype Konfiguration";

    // ── Steuerungszeile (Row 1) ───────────────────────────────────────────────
    public static final int SLOT_DAILY_TOGGLE   = 10;
    public static final int SLOT_MONTHLY_TOGGLE = 12;
    public static final int SLOT_DUEL_TOGGLE    = 14;
    public static final int SLOT_INTERVAL_MIN   = 16;
    public static final int SLOT_INTERVAL_MAX   = 17;

    // ── Template-Abschnitte ───────────────────────────────────────────────────
    public static final int SLOT_DAILY_HEADER    = 18;
    public static final int DAILY_TEMPLATE_START = 19; // 19-26 (max 8)

    public static final int SLOT_MONTHLY_HEADER    = 27;
    public static final int MONTHLY_TEMPLATE_START = 28; // 28-35 (max 8)

    public static final int SLOT_DUEL_HEADER    = 36;
    public static final int DUEL_TEMPLATE_START = 37; // 37-44 (max 8)

    public static final int TEMPLATE_ROW_SIZE = 8;

    // ── Aktionsleiste (Row 5) ─────────────────────────────────────────────────
    public static final int SLOT_ADD_DAILY    = 45;
    public static final int SLOT_ADD_MONTHLY  = 46;
    public static final int SLOT_ADD_DUEL     = 47;
    public static final int SLOT_RESET_DAILY  = 48;
    public static final int SLOT_RESET_MONTHLY = 49;
    public static final int SLOT_RESET_DUEL   = 50;
    public static final int SLOT_INFO         = 52;
    public static final int SLOT_BACK         = 53;

    private final NPCManager manager;

    public NPCGameHypeMenu() {
        this.manager = Main.getInstance().getNPCManager();
    }

    // ── Menu builder ──────────────────────────────────────────────────────────

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        GameHypeManager hype = manager.getGameHypeManager();

        // Row 0: Glas-Header
        for (int i = 0; i < 9; i++) inv.setItem(i, glass());

        // ── Row 1: Toggles & Intervalle ───────────────────────────────────────
        inv.setItem(SLOT_DAILY_TOGGLE, createToggle(
                hype.isDailyEnabled(),
                ChatColor.GOLD + "Daily-Hype",
                Material.SUNFLOWER,
                "NPCs schwärmen über die täglichen Booster",
                "{booster} = Booster-Name",
                "{kosten}  = Preis in Punkten"
        ));
        inv.setItem(SLOT_MONTHLY_TOGGLE, createToggle(
                hype.isMonthlyEnabled(),
                ChatColor.AQUA + "Monthly-Hype",
                Material.PRISMARINE_CRYSTALS,
                "NPCs schwärmen über den Monats-Booster",
                "{booster} = Booster-Name",
                "{kosten}  = Preis in Punkten"
        ));
        inv.setItem(SLOT_DUEL_TOGGLE, createToggle(
                hype.isDuelEnabled(),
                ChatColor.RED + "Duell-Hype",
                Material.DIAMOND_SWORD,
                "NPCs schwärmen über aktive Duelle",
                "{spieler1} = Spieler 1",
                "{spieler2} = Spieler 2"
        ));
        inv.setItem(SLOT_INTERVAL_MIN, createItem(Material.CLOCK,
                ChatColor.YELLOW + "Min. Intervall: " + ChatColor.WHITE + hype.getIntervalMinSeconds() + "s",
                ChatColor.GRAY + "Mindestwartezeit zwischen Hype-Nachrichten",
                ChatColor.DARK_GRAY + "Linksklick: +30s   Rechtsklick: -30s"
        ));
        inv.setItem(SLOT_INTERVAL_MAX, createItem(Material.CLOCK,
                ChatColor.YELLOW + "Max. Intervall: " + ChatColor.WHITE + hype.getIntervalMaxSeconds() + "s",
                ChatColor.GRAY + "Maximale Wartezeit zwischen Hype-Nachrichten",
                ChatColor.DARK_GRAY + "Linksklick: +30s   Rechtsklick: -30s"
        ));

        // ── Row 2: Daily-Templates ────────────────────────────────────────────
        inv.setItem(SLOT_DAILY_HEADER, createItem(Material.SUNFLOWER,
                ChatColor.GOLD + "── Daily-Texte ──",
                ChatColor.GRAY + "Platzhalter: {booster}, {kosten}",
                ChatColor.RED  + "Template anklicken zum Entfernen"
        ));
        List<String> daily = hype.getDailyTemplates();
        for (int i = 0; i < Math.min(daily.size(), TEMPLATE_ROW_SIZE); i++) {
            inv.setItem(DAILY_TEMPLATE_START + i, createTemplateItem(daily.get(i), i, ChatColor.GOLD));
        }

        // ── Row 3: Monthly-Templates ──────────────────────────────────────────
        inv.setItem(SLOT_MONTHLY_HEADER, createItem(Material.PRISMARINE_CRYSTALS,
                ChatColor.AQUA + "── Monthly-Texte ──",
                ChatColor.GRAY + "Platzhalter: {booster}, {kosten}",
                ChatColor.RED  + "Template anklicken zum Entfernen"
        ));
        List<String> monthly = hype.getMonthlyTemplates();
        for (int i = 0; i < Math.min(monthly.size(), TEMPLATE_ROW_SIZE); i++) {
            inv.setItem(MONTHLY_TEMPLATE_START + i, createTemplateItem(monthly.get(i), i, ChatColor.AQUA));
        }

        // ── Row 4: Duell-Templates ────────────────────────────────────────────
        inv.setItem(SLOT_DUEL_HEADER, createItem(Material.DIAMOND_SWORD,
                ChatColor.RED + "── Duell-Texte ──",
                ChatColor.GRAY + "Platzhalter: {spieler1}, {spieler2}",
                ChatColor.RED  + "Template anklicken zum Entfernen"
        ));
        List<String> duel = hype.getDuelTemplates();
        for (int i = 0; i < Math.min(duel.size(), TEMPLATE_ROW_SIZE); i++) {
            inv.setItem(DUEL_TEMPLATE_START + i, createTemplateItem(duel.get(i), i, ChatColor.RED));
        }

        // ── Row 5: Aktionsleiste ──────────────────────────────────────────────
        inv.setItem(SLOT_ADD_DAILY, createItem(Material.LIME_DYE,
                ChatColor.GREEN + "+ Daily-Text",
                ChatColor.GRAY + "Neuen Daily-Hype-Text hinzufügen",
                ChatColor.DARK_GRAY + "Platzhalter: {booster}, {kosten}"
        ));
        inv.setItem(SLOT_ADD_MONTHLY, createItem(Material.CYAN_DYE,
                ChatColor.AQUA + "+ Monthly-Text",
                ChatColor.GRAY + "Neuen Monthly-Hype-Text hinzufügen",
                ChatColor.DARK_GRAY + "Platzhalter: {booster}, {kosten}"
        ));
        inv.setItem(SLOT_ADD_DUEL, createItem(Material.PINK_DYE,
                ChatColor.LIGHT_PURPLE + "+ Duell-Text",
                ChatColor.GRAY + "Neuen Duell-Hype-Text hinzufügen",
                ChatColor.DARK_GRAY + "Platzhalter: {spieler1}, {spieler2}"
        ));
        inv.setItem(SLOT_RESET_DAILY, createItem(Material.ORANGE_DYE,
                ChatColor.GOLD + "Daily zurücksetzen",
                ChatColor.GRAY + "Standard-Daily-Texte wiederherstellen"
        ));
        inv.setItem(SLOT_RESET_MONTHLY, createItem(Material.ORANGE_DYE,
                ChatColor.GOLD + "Monthly zurücksetzen",
                ChatColor.GRAY + "Standard-Monthly-Texte wiederherstellen"
        ));
        inv.setItem(SLOT_RESET_DUEL, createItem(Material.ORANGE_DYE,
                ChatColor.GOLD + "Duell zurücksetzen",
                ChatColor.GRAY + "Standard-Duell-Texte wiederherstellen"
        ));
        inv.setItem(SLOT_INFO, createItem(Material.BOOK,
                ChatColor.AQUA + "Platzhalter-Info",
                ChatColor.GOLD  + "Daily/Monthly: {booster}, {kosten}",
                ChatColor.RED   + "Duell: {spieler1}, {spieler2}",
                ChatColor.DARK_GRAY + "Farben mit &e, &c, &a, &f usw.",
                ChatColor.DARK_GRAY + "§-Codes werden auch unterstützt"
        ));
        inv.setItem(SLOT_BACK, createItem(Material.ARROW,
                ChatColor.RED + "Zurück",
                ChatColor.GRAY + "Zurück zur NPC Verwaltung"
        ));

        // Restliche Slots in Row 5 mit Glas füllen
        inv.setItem(51, glass());

        player.openInventory(inv);
    }

    // ── Item helpers ──────────────────────────────────────────────────────────

    private ItemStack createToggle(boolean enabled, String name, Material icon, String... loreLines) {
        Material mat = enabled ? Material.LIME_DYE : Material.RED_DYE;
        String status = enabled
                ? ChatColor.GREEN + "✔ Aktiviert"
                : ChatColor.RED   + "✘ Deaktiviert";
        List<String> lore = new ArrayList<>();
        lore.add(status);
        lore.add(ChatColor.DARK_GRAY + "Klicken zum Umschalten");
        for (String line : loreLines) lore.add(ChatColor.GRAY + line);
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createTemplateItem(String template, int index, ChatColor accent) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Farbcodes im Template für Anzeige auflösen
            String display = ChatColor.translateAlternateColorCodes('&', template);
            meta.setDisplayName(ChatColor.WHITE + display);
            List<String> lore = new ArrayList<>();
            lore.add(accent + "" + ChatColor.BOLD + "Index " + index);
            lore.add(ChatColor.RED + "Linksklick: Entfernen");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    static ItemStack createItem(Material mat, String name, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            for (String l : loreLines) lore.add(l);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack glass() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }
}
