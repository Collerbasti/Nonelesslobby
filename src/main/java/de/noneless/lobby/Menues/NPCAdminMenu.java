package de.noneless.lobby.Menues;

import de.noneless.lobby.Main;
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
import java.util.Map;

public class NPCAdminMenu {

    public static final String MAIN_TITLE = ChatColor.DARK_AQUA + "NPC Verwaltung";
    public static final String NAME_TITLE = ChatColor.DARK_GREEN + "NPC Namen";
    public static final String CHAT_TITLE = ChatColor.DARK_PURPLE + "NPC Chats";
    public static final String PAIRS_TITLE = ChatColor.LIGHT_PURPLE + "NPC Paarverwaltung";
    public static final String NAME_PAIRS_TITLE = ChatColor.LIGHT_PURPLE + "Namenspaare";
    public static final String NAME_PAIR_SELECT_PREFIX = ChatColor.LIGHT_PURPLE + "Partner für: ";
    public static final String PERSONALITY_OVERVIEW_TITLE = ChatColor.BLUE + "NPC Persönlichkeiten";
    public static final String PERSONALITY_DETAIL_PREFIX = ChatColor.BLUE + "Persönlichkeiten: ";
    public static final String PERSONALITY_LIST_TITLE = ChatColor.GOLD + "Persönlichkeiten";
    public static final String PERSONALITY_LINES_PREFIX = ChatColor.GOLD + "Texte: ";
    public static final String CONVERSATION_PERSONALITY_TITLE = ChatColor.DARK_RED + "Gespräche (Personas)";
    public static final String CONVERSATION_LIST_TITLE = ChatColor.DARK_PURPLE + "NPC Gespräche";
    public static final String CONVERSATION_DETAIL_PREFIX = ChatColor.DARK_PURPLE + "Gespräch: ";
    public static final String CONVERSATION_LINES_PREFIX = ChatColor.DARK_PURPLE + "Dialog: ";
    public static final String SETTINGS_TITLE = ChatColor.GOLD + "NPC Manager Einstellungen";

    private final NPCManager manager;

    public NPCAdminMenu() {
        this.manager = Main.getInstance().getNPCManager();
    }

    public void openMain(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, MAIN_TITLE);
        inv.setItem(10, createItem(Material.TOTEM_OF_UNDYING, ChatColor.GREEN + "NPCs neu spawnen", ChatColor.GRAY + "Entfernt alle NPCs und spawnt sie neu"));
        inv.setItem(12, createItem(Material.SPAWNER, ChatColor.AQUA + "Weitere NPCs spawnen", ChatColor.GRAY + "Fügt sofort neue NPCs hinzu"));
        inv.setItem(14, createItem(Material.NAME_TAG, ChatColor.YELLOW + "NPC-Namen bearbeiten", ChatColor.GRAY + "Füge hinzu, benenne um oder lösche"));
        inv.setItem(16, createItem(Material.BOOK, ChatColor.LIGHT_PURPLE + "NPC-Chat bearbeiten", ChatColor.GRAY + "Verwalte Chat-Nachrichten"));
        inv.setItem(18, createItem(Material.PLAYER_HEAD, ChatColor.BLUE + "Persönlichkeiten je Name", ChatColor.GRAY + "Ordne Namen Persönlichkeiten zu"));
        inv.setItem(20, createItem(Material.LECTERN, ChatColor.DARK_PURPLE + "NPC Gespräche", ChatColor.GRAY + "Dialoge ansehen & bearbeiten"));
        inv.setItem(24, createItem(Material.WRITABLE_BOOK, ChatColor.GOLD + "Persönlichkeits-Texte", ChatColor.GRAY + "Verwalte Persönlichkeitstypen"));
        inv.setItem(26, createItem(Material.BOOKSHELF, ChatColor.DARK_RED + "Gespräch-Fokus", ChatColor.GRAY + "Filter pro Gespräch setzen"));
        inv.setItem(11, createItem(Material.REDSTONE, ChatColor.GOLD + "Manager Einstellungen", ChatColor.GRAY + "Hologramme, Gespräche etc."));
        inv.setItem(22, createItem(Material.BARRIER, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zu den Settings"));
        inv.setItem(21, createItem(Material.HEART_OF_THE_SEA, ChatColor.LIGHT_PURPLE + "Namenspaare", ChatColor.GRAY + "Paarungen über Namen verwalten"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openSettings(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, SETTINGS_TITLE);
        
        // Hologramm Höhe
        inv.setItem(10, createItem(Material.ARMOR_STAND, ChatColor.AQUA + "Hologramm Höhe",
                ChatColor.GRAY + "Aktuell: " + ChatColor.YELLOW + String.format("%.2f", manager.getChatHologramVerticalOffset()),
                ChatColor.GRAY + "Linksklick: +0.1 | Rechtsklick: -0.1"));
        
        // Hologramm Lebensdauer
        inv.setItem(11, createItem(Material.REDSTONE, ChatColor.LIGHT_PURPLE + "Hologramm Lebensdauer",
                ChatColor.GRAY + "Aktuell: " + ChatColor.YELLOW + manager.getChatHologramLifetimeTicks() + " Ticks",
                ChatColor.GRAY + "Linksklick: +10 | Rechtsklick: -10"));
        
        // Hologramm Follow Intervall
        inv.setItem(12, createItem(Material.REPEATER, ChatColor.LIGHT_PURPLE + "Hologramm Follow Intervall",
                ChatColor.GRAY + "Aktuell: " + ChatColor.YELLOW + manager.getChatHologramFollowInterval() + " Ticks",
                ChatColor.GRAY + "Nur Anzeige (konstant)"));
        
        // Gespräche aktiviert
        String conversationStatus = manager.isConversationsEnabled() ? ChatColor.GREEN + "AN" : ChatColor.RED + "AUS";
        inv.setItem(14, createItem(Material.LEVER, ChatColor.GOLD + "Gespräche aktiviert",
                ChatColor.GRAY + "Status: " + conversationStatus,
                ChatColor.GRAY + "Klick: Toggle"));
        
        // Min Interval
        inv.setItem(15, createItem(Material.CLOCK, ChatColor.YELLOW + "Min. Gesprächs-Interval",
                ChatColor.GRAY + "Aktuell: " + ChatColor.YELLOW + manager.getConversationMinIntervalSeconds() + "s",
                ChatColor.GRAY + "Linksklick: +10s | Rechtsklick: -10s"));
        
        // Max Interval
        inv.setItem(16, createItem(Material.CLOCK, ChatColor.YELLOW + "Max. Gesprächs-Interval",
                ChatColor.GRAY + "Aktuell: " + ChatColor.YELLOW + manager.getConversationMaxIntervalSeconds() + "s",
                ChatColor.GRAY + "Linksklick: +10s | Rechtsklick: -10s"));
        
        // Line Delay
        inv.setItem(18, createItem(Material.REPEATER, ChatColor.AQUA + "Dialog-Zeilen Verzögerung",
                ChatColor.GRAY + "Aktuell: " + ChatColor.YELLOW + manager.getConversationLineDelayTicks() + " Ticks",
                ChatColor.GRAY + "Linksklick: +5 | Rechtsklick: -5"));
        
        // Gather Delay
        inv.setItem(19, createItem(Material.REPEATER, ChatColor.AQUA + "Gather Verzögerung",
                ChatColor.GRAY + "Aktuell: " + ChatColor.YELLOW + manager.getConversationGatherDelayTicks() + " Ticks",
                ChatColor.GRAY + "Linksklick: +5 | Rechtsklick: -5"));
        
        // Audience Radius
        inv.setItem(20, createItem(Material.SPYGLASS, ChatColor.DARK_AQUA + "Publikums Reichweite",
                ChatColor.GRAY + "Aktuell: " + ChatColor.YELLOW + String.format("%.1f", manager.getConversationAudienceRadius()) + " Blöcke",
                ChatColor.GRAY + "Linksklick: +5 | Rechtsklick: -5"));
        
        // Conversation Prefix
        inv.setItem(24, createItem(Material.BOOK, ChatColor.LIGHT_PURPLE + "Gespräch Präfix",
                ChatColor.GRAY + "Rechtsklick: Bearbeiten"));
        
        // Zurück
        inv.setItem(26, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur NPC Verwaltung"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openNameEditor(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, NAME_TITLE);
        List<String> names = manager.getNpcNamesSnapshot();
        int slot = 0;
        for (String name : names) {
            if (slot >= 45) break;
            inv.setItem(slot++, createItem(Material.PAPER, ChatColor.GOLD + name,
                    ChatColor.GRAY + "Linksklick: Löschen", ChatColor.GRAY + "Rechtsklick: Umbenennen"));
        }
        inv.setItem(48, createItem(Material.EMERALD, ChatColor.GREEN + "Name hinzufügen", ChatColor.GRAY + "Klicke für neuen Namen"));
        inv.setItem(50, createItem(Material.SLIME_BALL, ChatColor.AQUA + "Namen neu laden", ChatColor.GRAY + "Lädt aus Config"));
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur NPC Verwaltung"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openPersonalityOverview(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, PERSONALITY_OVERVIEW_TITLE);
        List<String> names = manager.getNpcNamesSnapshot();
        int slot = 0;
        for (String name : names) {
            if (slot >= 45) break;
            List<String> personalities = manager.getPersonalitiesForNameSnapshot(name);
            inv.setItem(slot++, createItem(Material.PAPER, ChatColor.AQUA + name,
                    ChatColor.GRAY + "Persönlichkeiten: " + (personalities.isEmpty() ? ChatColor.DARK_GRAY + "keine" :
                            ChatColor.YELLOW + String.join(", ", personalities)),
                    ChatColor.GRAY + "Linksklick: verwalten"));
        }
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur NPC Verwaltung"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openPersonalityDetail(Player player, String name) {
        Inventory inv = Bukkit.createInventory(null, 54, PERSONALITY_DETAIL_PREFIX + name);
        List<String> personalities = manager.getPersonalitiesForNameSnapshot(name);
        int slot = 0;
        for (String personality : personalities) {
            if (slot >= 45) break;
            inv.setItem(slot++, createItem(Material.MAGENTA_DYE, ChatColor.GOLD + personality,
                    ChatColor.GRAY + "Linksklick: entfernen"));
        }
        inv.setItem(48, createItem(Material.EMERALD, ChatColor.GREEN + "Persönlichkeit hinzufügen", ChatColor.GRAY + "Name eingeben"));
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur Übersicht"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openPersonalityList(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, PERSONALITY_LIST_TITLE);
        List<String> personalities = manager.getAllPersonalityNamesSnapshot();
        int slot = 0;
        for (String personality : personalities) {
            if (slot >= 45) break;
            List<String> lines = manager.getPersonalityLinesSnapshot(personality);
            inv.setItem(slot++, createItem(Material.BOOK, ChatColor.GOLD + personality,
                    ChatColor.GRAY + String.valueOf(lines.size()) + " Nachrichten",
                    ChatColor.GRAY + "Linksklick: Texte bearbeiten",
                    ChatColor.GRAY + "Rechtsklick: Persönlichkeit löschen"));
        }
        inv.setItem(48, createItem(Material.EMERALD, ChatColor.GREEN + "Persönlichkeit erstellen", ChatColor.GRAY + "Namen eingeben"));
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur NPC Verwaltung"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openPersonalityLines(Player player, String personality) {
        Inventory inv = Bukkit.createInventory(null, 54, PERSONALITY_LINES_PREFIX + personality);
        List<String> lines = manager.getPersonalityLinesSnapshot(personality);
        int slot = 0;
        for (String line : lines) {
            if (slot >= 45) break;
            inv.setItem(slot++, createItem(Material.PAPER, ChatColor.GOLD + cut(line, 30),
                    ChatColor.GRAY + line,
                    ChatColor.GRAY + "Linksklick: löschen",
                    ChatColor.GRAY + "Rechtsklick: bearbeiten"));
        }
        inv.setItem(48, createItem(Material.EMERALD, ChatColor.GREEN + "Nachricht hinzufügen", ChatColor.GRAY + "Text eingeben"));
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur Übersicht"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openPairsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, PAIRS_TITLE);
        Map<Integer, String> active = manager.getActiveNpcIdNameMap();
        int slot = 0;
        for (Map.Entry<Integer, String> e : active.entrySet()) {
            if (slot >= 45) break;
            int id = e.getKey();
            String name = e.getValue();
            NPCManager.PairInfo pe = manager.getPairFor(id);
            String partnerInfo = pe == null ? ChatColor.DARK_GRAY + "(kein Partner)" : ChatColor.YELLOW + "Partner: " + ChatColor.AQUA + pe.partnerId;
            inv.setItem(slot++, createItem(Material.PAPER, ChatColor.GOLD + "#" + id + " " + ChatColor.WHITE + name,
                    ChatColor.GRAY + "Linksklick: Cycle Partner", ChatColor.GRAY + partnerInfo));
        }
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur NPC Verwaltung"));
        fill(inv);
        player.openInventory(inv);
    }

    /**
     * Öffnet das Menü für namensbasierte Paarungen.
     * Zeigt alle NPC-Namen mit ihrem aktuellen Partner.
     */
    public void openNamePairsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, NAME_PAIRS_TITLE);
        List<String> names = manager.getNpcNamesSnapshot();
        int slot = 0;
        for (String name : names) {
            if (slot >= 45) break;
            NPCManager.NamePairInfo pe = manager.getNamePairFor(name);
            String partnerInfo = pe == null ? ChatColor.DARK_GRAY + "(kein Partner)" : 
                    ChatColor.GREEN + "Partner: " + ChatColor.YELLOW + pe.partnerName;
            String prefixInfo = pe != null && pe.prefix != null && !pe.prefix.isBlank() ? 
                    ChatColor.GRAY + "Präfix: " + ChatColor.LIGHT_PURPLE + pe.prefix : "";
            if (prefixInfo.isEmpty()) {
                inv.setItem(slot++, createItem(Material.PLAYER_HEAD, ChatColor.GOLD + name,
                        partnerInfo,
                        ChatColor.GRAY + "Linksklick: Partner wählen",
                        ChatColor.GRAY + "Rechtsklick: Paarung entfernen"));
            } else {
                inv.setItem(slot++, createItem(Material.PLAYER_HEAD, ChatColor.GOLD + name,
                        partnerInfo, prefixInfo,
                        ChatColor.GRAY + "Linksklick: Partner wählen",
                        ChatColor.GRAY + "Rechtsklick: Paarung entfernen"));
            }
        }
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur NPC Verwaltung"));
        fill(inv);
        player.openInventory(inv);
    }

    /**
     * Öffnet die Partner-Auswahl für einen bestimmten Namen.
     */
    public void openNamePairSelect(Player player, String selectedName) {
        Inventory inv = Bukkit.createInventory(null, 54, NAME_PAIR_SELECT_PREFIX + selectedName);
        List<String> names = manager.getNpcNamesSnapshot();
        NPCManager.NamePairInfo currentPair = manager.getNamePairFor(selectedName);
        int slot = 0;
        
        // "Kein Partner" Option
        inv.setItem(slot++, createItem(Material.BARRIER, ChatColor.RED + "Kein Partner",
                ChatColor.GRAY + "Entfernt die aktuelle Paarung"));
        
        for (String name : names) {
            if (slot >= 45) break;
            if (name.equalsIgnoreCase(selectedName)) continue; // Nicht sich selbst anzeigen
            
            boolean isCurrentPartner = currentPair != null && currentPair.partnerName.equalsIgnoreCase(name);
            Material mat = isCurrentPartner ? Material.EMERALD : Material.PAPER;
            String status = isCurrentPartner ? ChatColor.GREEN + "✓ Aktueller Partner" : ChatColor.GRAY + "Klicke zum Auswählen";
            
            inv.setItem(slot++, createItem(mat, ChatColor.YELLOW + name, status));
        }
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur Übersicht"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openConversationPersonalityMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, CONVERSATION_PERSONALITY_TITLE);
        Map<String, List<String>> filters = manager.getConversationPersonalityFiltersSnapshot();
        int slot = 0;
        for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
            if (slot >= 45) break;
            inv.setItem(slot++, createItem(Material.BOOK, ChatColor.DARK_RED + entry.getKey(),
                    ChatColor.GRAY + "Personas: " + (entry.getValue().isEmpty() ? ChatColor.DARK_GRAY + "keine" :
                            ChatColor.YELLOW + String.join(", ", entry.getValue())),
                    ChatColor.GRAY + "Linksklick: hinzufügen", ChatColor.GRAY + "Rechtsklick: entfernen"));
        }
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur NPC Verwaltung"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openChatEditor(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, CHAT_TITLE);
        List<String> chats = manager.getChatLinesSnapshot();
        int slot = 0;
        for (String line : chats) {
            if (slot >= 45) break;
            inv.setItem(slot++, createItem(Material.BOOK, ChatColor.GOLD + cut(line, 30),
                    ChatColor.GRAY + line,
                    ChatColor.GRAY + "Linksklick: Löschen",
                    ChatColor.GRAY + "Rechtsklick: Bearbeiten"));
        }
        inv.setItem(48, createItem(Material.EMERALD, ChatColor.GREEN + "Nachricht hinzufügen", ChatColor.GRAY + "Klicke für neue Nachricht"));
        inv.setItem(50, createItem(Material.SLIME_BALL, ChatColor.AQUA + "Chats neu laden", ChatColor.GRAY + "Lädt aus Config"));
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur NPC Verwaltung"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openConversationList(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, CONVERSATION_LIST_TITLE);
        List<NPCManager.ConversationSnapshot> scripts = manager.getConversationSnapshots();
        int slot = 0;
        for (NPCManager.ConversationSnapshot snapshot : scripts) {
            if (slot >= 45) break;
            inv.setItem(slot++, createItem(Material.WRITTEN_BOOK,
                    ChatColor.DARK_PURPLE + snapshot.getId(),
                    ChatColor.GRAY + "Titel: " + ChatColor.LIGHT_PURPLE + safeText(snapshot.getTitle()),
                    ChatColor.GRAY + "Erster: " + formatList(snapshot.getFirstPersonalities()),
                    ChatColor.GRAY + "Zweiter: " + formatList(snapshot.getSecondPersonalities()),
                    ChatColor.GRAY + "Gemeinsam: " + formatList(snapshot.getSharedPersonalities()),
                    ChatColor.YELLOW + "Linksklick: Details",
                    ChatColor.RED + "Rechtsklick: Entfernen"));
        }
        inv.setItem(48, createItem(Material.EMERALD, ChatColor.GREEN + "Gespräch hinzufügen", ChatColor.GRAY + "Nutze id|Titel im Chat"));
        inv.setItem(50, createItem(Material.SLIME_BALL, ChatColor.AQUA + "Gespräche neu laden", ChatColor.GRAY + "Aus Config lesen"));
        inv.setItem(51, createItem(Material.BOOKSHELF, ChatColor.GOLD + "Filter (Personas)", ChatColor.GRAY + "Gemeinsame Filter verwalten"));
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur NPC Verwaltung"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openConversationDetail(Player player, String scriptId) {
        NPCManager.ConversationSnapshot snapshot = manager.getConversationSnapshot(scriptId);
        if (snapshot == null) {
            player.sendMessage(ChatColor.RED + "Gespräch nicht gefunden.");
            openConversationList(player);
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 27, CONVERSATION_DETAIL_PREFIX + snapshot.getId());
        inv.setItem(10, createItem(Material.NAME_TAG, ChatColor.GOLD + "Titel bearbeiten",
                ChatColor.GRAY + "Aktuell: " + ChatColor.LIGHT_PURPLE + safeText(snapshot.getTitle()),
                ChatColor.YELLOW + "Linksklick: Titel im Chat eingeben"));
        inv.setItem(12, createItem(Material.PLAYER_HEAD, ChatColor.AQUA + "Erste Personas",
                ChatColor.GRAY + "Aktuell: " + formatList(snapshot.getFirstPersonalities()),
                ChatColor.YELLOW + "Kommaseparierte Liste im Chat"));
        inv.setItem(13, createItem(Material.ZOMBIE_HEAD, ChatColor.AQUA + "Zweite Personas",
                ChatColor.GRAY + "Aktuell: " + formatList(snapshot.getSecondPersonalities()),
                ChatColor.YELLOW + "Kommaseparierte Liste im Chat"));
        inv.setItem(14, createItem(Material.HEART_OF_THE_SEA, ChatColor.AQUA + "Gemeinsame Personas",
                ChatColor.GRAY + "Aktuell: " + formatList(snapshot.getSharedPersonalities()),
                ChatColor.YELLOW + "Kommaseparierte Liste im Chat"));
        inv.setItem(16, createItem(Material.WRITABLE_BOOK, ChatColor.LIGHT_PURPLE + "Dialogzeilen",
                ChatColor.GRAY + "Linksklick: Linien verwalten"));
        inv.setItem(24, createItem(Material.SLIME_BALL, ChatColor.AQUA + "Gespräch neu laden", ChatColor.GRAY + "Config neu einlesen"));
        inv.setItem(26, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zur Liste"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openConversationLines(Player player, String scriptId) {
        Inventory inv = Bukkit.createInventory(null, 54, CONVERSATION_LINES_PREFIX + scriptId);
        List<NPCManager.ConversationLineSnapshot> lines = manager.getConversationLinesSnapshot(scriptId);
        int slot = 0;
        for (NPCManager.ConversationLineSnapshot line : lines) {
            if (slot >= 45) break;
            String name = ChatColor.GOLD + "#" + (line.getIndex() + 1) + " " + ChatColor.LIGHT_PURPLE + line.getSpeaker();
            inv.setItem(slot++, createItem(Material.PAPER, name,
                    ChatColor.DARK_GRAY + "Index: " + line.getIndex(),
                    ChatColor.GRAY + "Pause: " + line.getPauseTicks() + " Ticks",
                    ChatColor.WHITE + safeText(line.getText()),
                    ChatColor.YELLOW + "Linksklick: entfernen",
                    ChatColor.AQUA + "Rechtsklick: bearbeiten"));
        }
        inv.setItem(48, createItem(Material.EMERALD, ChatColor.GREEN + "Zeile hinzufügen",
                ChatColor.GRAY + "Format: Sprecher|Text|Pause"));
        inv.setItem(50, createItem(Material.SLIME_BALL, ChatColor.AQUA + "Neu laden", ChatColor.GRAY + "Liest Gespräch neu"));
        inv.setItem(53, createItem(Material.ARROW, ChatColor.RED + "Zurück", ChatColor.GRAY + "Zurück zum Gespräch"));
        fill(inv);
        player.openInventory(inv);
    }

    private String safeText(String value) {
        if (value == null || value.isBlank()) {
            return ChatColor.DARK_GRAY + "(leer)";
        }
        return value;
    }

    private String formatList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return ChatColor.DARK_GRAY + "keine";
        }
        return ChatColor.YELLOW + String.join(ChatColor.GRAY + ", ", values);
    }

    private ItemStack createItem(Material material, String name, String... loreLines) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        if (loreLines != null && loreLines.length > 0) {
            List<String> lore = new ArrayList<>();
            for (String line : loreLines) {
                lore.add(line);
            }
            meta.setLore(lore);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    private void fill(Inventory inventory) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + " ");
        filler.setItemMeta(meta);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    private String cut(String input, int max) {
        if (input.length() <= max) return input;
        return input.substring(0, Math.max(0, max - 3)) + "...";
    }
}
