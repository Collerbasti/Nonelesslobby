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
 *  Row 0 (0-8):   Header-Leiste
 *  Row 1 (9-17):  Steuerung: Booster-Toggle · Duell-Toggle · Min-Intervall · Max-Intervall
 *  Row 2 (18-26): Booster-Templates (bis zu 8; Linksklick = entfernen)
 *  Row 3 (27-35): Duell-Templates   (bis zu 8; Linksklick = entfernen)
 *  Row 4-5 (36-53): untere Leiste mit Aktions-Buttons
 * </pre>
 *
 * Platzhalter in Templates:
 * <ul>
 *   <li>Booster: {@code {booster}} {@code {kosten}}</li>
 *   <li>Duell:   {@code {spieler1}} {@code {spieler2}}</li>
 * </ul>
 */
public class NPCGameHypeMenu {

    public static final String TITLE = ChatColor.DARK_PURPLE + "Spiel-Hype Konfiguration";

    // Control row (row 1)
    public static final int SLOT_BOOSTER_TOGGLE  = 10;
    public static final int SLOT_DUEL_TOGGLE     = 13;
    public static final int SLOT_INTERVAL_MIN    = 16;
    public static final int SLOT_INTERVAL_MAX    = 17;

    // Template section headers (row 2 / 3 left edge)
    public static final int SLOT_BOOSTER_HEADER  = 18;
    public static final int SLOT_DUEL_HEADER     = 27;

    // Template slots
    public static final int BOOSTER_TEMPLATE_START = 19; // 19-26 (max 8)
    public static final int DUEL_TEMPLATE_START    = 28; // 28-35 (max 8)
    public static final int TEMPLATE_ROW_SIZE      = 8;

    // Bottom action bar (row 5)
    public static final int SLOT_ADD_BOOSTER   = 45;
    public static final int SLOT_ADD_DUEL      = 46;
    public static final int SLOT_RESET_BOOSTER = 48;
    public static final int SLOT_RESET_DUEL    = 50;
    public static final int SLOT_INFO          = 52;
    public static final int SLOT_BACK          = 53;

    private final NPCManager manager;

    public NPCGameHypeMenu() {
        this.manager = Main.getInstance().getNPCManager();
    }

    // ── Menu builder ──────────────────────────────────────────────────────────

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        GameHypeManager hype = manager.getGameHypeManager();

        // ── Row 0: Glass header ───────────────────────────────────────────────
        fillRow(inv, 0, Material.GRAY_STAINED_GLASS_PANE, " ");

        // ── Row 1: Toggles & intervals ────────────────────────────────────────
        boolean bEn = hype.isBoosterEnabled();
        inv.setItem(SLOT_BOOSTER_TOGGLE, createToggle(
                bEn,
                ChatColor.GOLD + "Booster-Hype",
                "NPCs schwärmen über aktuelle Booster",
                "{booster} = Booster-Name",
                "{kosten} = Preis in Punkten"
        ));

        boolean dEn = hype.isDuelEnabled();
        inv.setItem(SLOT_DUEL_TOGGLE, createToggle(
                dEn,
                ChatColor.RED + "Duell-Hype",
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

        // ── Row 2: Booster templates ──────────────────────────────────────────
        inv.setItem(SLOT_BOOSTER_HEADER, createItem(Material.PAPER,
                ChatColor.GOLD + "── Booster-Texte ──",
                ChatColor.GRAY + "Klicke einen Text zum Entfernen"
        ));
        List<String> bt = hype.getBoosterTemplates();
        for (int i = 0; i < Math.min(bt.size(), TEMPLATE_ROW_SIZE); i++) {
            inv.setItem(BOOSTER_TEMPLATE_START + i, createTemplateItem(bt.get(i), i));
        }

        // ── Row 3: Duel templates ─────────────────────────────────────────────
        inv.setItem(SLOT_DUEL_HEADER, createItem(Material.PAPER,
                ChatColor.RED + "── Duell-Texte ──",
                ChatColor.GRAY + "Klicke einen Text zum Entfernen"
        ));
        List<String> dt = hype.getDuelTemplates();
        for (int i = 0; i < Math.min(dt.size(), TEMPLATE_ROW_SIZE); i++) {
            inv.setItem(DUEL_TEMPLATE_START + i, createTemplateItem(dt.get(i), i));
        }

        // ── Bottom action bar ─────────────────────────────────────────────────
        inv.setItem(SLOT_ADD_BOOSTER, createItem(Material.LIME_DYE,
                ChatColor.GREEN + "+ Booster-Text hinzufügen",
                ChatColor.GRAY + "Gibt Platzhalter: {booster}, {kosten}"
        ));
        inv.setItem(SLOT_ADD_DUEL, createItem(Material.LIME_DYE,
                ChatColor.GREEN + "+ Duell-Text hinzufügen",
                ChatColor.GRAY + "Platzhalter: {spieler1}, {spieler2}"
        ));
        inv.setItem(SLOT_RESET_BOOSTER, createItem(Material.ORANGE_DYE,
                ChatColor.GOLD + "Booster-Texte zurücksetzen",
                ChatColor.GRAY + "Setzt Standard-Texte wieder her"
        ));
        inv.setItem(SLOT_RESET_DUEL, createItem(Material.ORANGE_DYE,
                ChatColor.GOLD + "Duell-Texte zurücksetzen",
                ChatColor.GRAY + "Setzt Standard-Texte wieder her"
        ));
        inv.setItem(SLOT_INFO, createItem(Material.BOOK,
                ChatColor.AQUA + "Platzhalter-Info",
                ChatColor.GRAY + "Booster: §e{booster}§7, §e{kosten}",
                ChatColor.GRAY + "Duell:   §c{spieler1}§7, §c{spieler2}",
                ChatColor.DARK_GRAY + "Farben: &e, &c, &f, &a usw.",
                ChatColor.DARK_GRAY + "§-Codes werden ebenfalls unterstützt"
        ));
        inv.setItem(SLOT_BACK, createItem(Material.ARROW,
                ChatColor.RED + "Zurück",
                ChatColor.GRAY + "Zurück zur NPC Verwaltung"
        ));

        // Fill remaining bottom row slots with glass
        for (int s : new int[]{36,37,38,39,40,41,42,43,44,47,49,51}) {
            if (inv.getItem(s) == null) {
                inv.setItem(s, glass(" "));
            }
        }

        player.openInventory(inv);
    }

    // ── Item helpers ──────────────────────────────────────────────────────────

    private ItemStack createToggle(boolean enabled, String name, String... loreLines) {
        Material mat = enabled ? Material.LIME_DYE : Material.RED_DYE;
        String status = enabled
                ? ChatColor.GREEN + "✔ Aktiviert"
                : ChatColor.RED   + "✘ Deaktiviert";
        List<String> lore = new ArrayList<>();
        lore.add(status);
        lore.add(ChatColor.DARK_GRAY + "Klicken zum Umschalten");
        for (String line : loreLines) {
            lore.add(ChatColor.GRAY + line);
        }
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createTemplateItem(String template, int index) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + template);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + "Index: " + index);
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

    private ItemStack glass(String name) {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void fillRow(Inventory inv, int row, Material mat, String name) {
        ItemStack pane = glass(name);
        for (int i = row * 9; i < row * 9 + 9; i++) {
            inv.setItem(i, pane);
        }
    }
}
