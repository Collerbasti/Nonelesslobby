package de.noneless.lobby.listeners;

import de.noneless.lobby.Main;
import de.noneless.lobby.Menues.NPCAdminMenu;
import npc.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NPCMenuListener implements Listener {

    private final NPCAdminMenu menu = new NPCAdminMenu();
    private final NPCManager manager = Main.getInstance().getNPCManager();

    private final Map<UUID, EditContext> editSessions = new ConcurrentHashMap<>();

    private enum EditType {
        ADD_NAME,
        RENAME_NAME,
        ADD_CHAT,
        EDIT_CHAT,
        ADD_NAME_PERSONALITY,
        ADD_PERSONALITY,
        ADD_PERSONALITY_LINE,
        EDIT_PERSONALITY_LINE,
        ADD_CONVERSATION_PERSONALITY,
        REMOVE_CONVERSATION_PERSONALITY,
        ADD_CONVERSATION_SCRIPT,
        EDIT_CONVERSATION_TITLE,
        EDIT_CONVERSATION_PERSONALITIES_FIRST,
        EDIT_CONVERSATION_PERSONALITIES_SECOND,
        EDIT_CONVERSATION_PERSONALITIES_SHARED,
        ADD_CONVERSATION_LINE,
        EDIT_CONVERSATION_LINE,
        SETTINGS_HOLOGRAM_HEIGHT,
        SETTINGS_HOLOGRAM_LIFETIME,
        SETTINGS_CONVERSATION_MIN_INTERVAL,
        SETTINGS_CONVERSATION_MAX_INTERVAL,
        SETTINGS_CONVERSATION_LINE_DELAY,
        SETTINGS_CONVERSATION_GATHER_DELAY,
        SETTINGS_CONVERSATION_AUDIENCE_RADIUS,
        SETTINGS_CONVERSATION_PREFIX,
        SETTINGS_CONVERSATIONS_TOGGLE
    }

    private static class EditContext {
        private final EditType type;
        private final String reference;
        private final String secondary;

        private EditContext(EditType type, String reference, String secondary) {
            this.type = type;
            this.reference = reference;
            this.secondary = secondary;
        }
    }

    private static class LineInput {
        private final String speaker;
        private final String text;
        private final Integer pause;

        private LineInput(String speaker, String text, Integer pause) {
            this.speaker = speaker;
            this.text = text;
            this.pause = pause;
        }
    }

    public void openMain(Player player) {
        menu.openMain(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();

        if (title.equals(NPCAdminMenu.MAIN_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            if (type == Material.TOTEM_OF_UNDYING) {
                manager.reloadNPCs();
                player.sendMessage(ChatColor.GREEN + "NPCs werden neu gespawnt.");
            } else if (type == Material.SPAWNER) {
                manager.spawnLobbyNPCs();
                player.sendMessage(ChatColor.AQUA + "Weitere NPCs wurden gespawnt.");
            } else if (type == Material.NAME_TAG) {
                menu.openNameEditor(player);
            } else if (type == Material.BOOK) {
                menu.openChatEditor(player);
            } else if (type == Material.PLAYER_HEAD) {
                menu.openPersonalityOverview(player);
            } else if (type == Material.LECTERN) {
                menu.openConversationList(player);
            } else if (type == Material.WRITABLE_BOOK) {
                menu.openPersonalityList(player);
            } else if (type == Material.REDSTONE) {
                menu.openSettings(player);
            } else if (type == Material.BARRIER || type == Material.ARROW) {
                player.closeInventory();
                new de.noneless.lobby.Menues.Settings().Spawn(player);
            }
        } else if (title.equals(NPCAdminMenu.NAME_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            if (type == Material.PAPER) {
                String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                if (event.isLeftClick()) {
                    boolean removed = manager.removeNpcName(name);
                    player.sendMessage(removed ? ChatColor.GREEN + "Name '" + name + "' entfernt." :
                            ChatColor.RED + "Name konnte nicht entfernt werden.");
                    menu.openNameEditor(player);
                } else if (event.isRightClick()) {
                    beginEdit(player, EditType.RENAME_NAME, name, null, "Neuer Name für " + name + ":");
                }
            } else if (type == Material.EMERALD) {
                beginEdit(player, EditType.ADD_NAME, null, null, "Gib den neuen NPC-Namen im Chat ein:");
            } else if (type == Material.SLIME_BALL) {
                manager.reloadNpcConfig();
                player.sendMessage(ChatColor.GREEN + "NPC-Namen neu geladen.");
                menu.openNameEditor(player);
            } else if (type == Material.ARROW) {
                menu.openMain(player);
            }
        } else if (title.equals(NPCAdminMenu.CHAT_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            if (type == Material.BOOK) {
                List<String> lore = clicked.getItemMeta().getLore();
                if (lore == null || lore.isEmpty()) return;
                String line = ChatColor.stripColor(lore.get(0));
                if (event.isLeftClick()) {
                    boolean removed = manager.removeChatLine(line);
                    player.sendMessage(removed ? ChatColor.GREEN + "Nachricht entfernt." :
                            ChatColor.RED + "Nachricht konnte nicht entfernt werden.");
                    menu.openChatEditor(player);
                } else if (event.isRightClick()) {
                    beginEdit(player, EditType.EDIT_CHAT, line, null, "Neue Nachricht für Auswahl:");
                }
            } else if (type == Material.EMERALD) {
                beginEdit(player, EditType.ADD_CHAT, null, null, "Gib die neue NPC-Nachricht im Chat ein:");
            } else if (type == Material.SLIME_BALL) {
                manager.reloadNpcConfig();
                player.sendMessage(ChatColor.GREEN + "NPC-Chats neu geladen.");
                menu.openChatEditor(player);
            } else if (type == Material.ARROW) {
                menu.openMain(player);
            }
        } else if (title.equals(NPCAdminMenu.PERSONALITY_OVERVIEW_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            if (type == Material.PAPER) {
                String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                menu.openPersonalityDetail(player, name);
            } else if (type == Material.ARROW) {
                menu.openMain(player);
            }
        } else if (title.startsWith(NPCAdminMenu.PERSONALITY_DETAIL_PREFIX)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            String name = ChatColor.stripColor(title.substring(NPCAdminMenu.PERSONALITY_DETAIL_PREFIX.length()));
            Material type = clicked.getType();
            if (type == Material.MAGENTA_DYE) {
                String personality = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                boolean removed = manager.removePersonalityFromName(name, personality);
                player.sendMessage(removed ? ChatColor.GREEN + "Persönlichkeit entfernt." :
                        ChatColor.RED + "Konnte Persönlichkeits-Zuordnung nicht entfernen.");
                menu.openPersonalityDetail(player, name);
            } else if (type == Material.EMERALD) {
                beginEdit(player, EditType.ADD_NAME_PERSONALITY, name, null,
                        "Neue Persönlichkeit für " + name + " eingeben:");
            } else if (type == Material.ARROW) {
                menu.openPersonalityOverview(player);
            }
        } else if (title.equals(NPCAdminMenu.PERSONALITY_LIST_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            if (type == Material.BOOK) {
                String personality = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                if (event.isLeftClick()) {
                    menu.openPersonalityLines(player, personality);
                } else if (event.isRightClick()) {
                    boolean removed = manager.removePersonalityDefinition(personality);
                    player.sendMessage(removed ? ChatColor.GREEN + "Persönlichkeit gelöscht." :
                            ChatColor.RED + "Konnte Persönlichkeit nicht löschen.");
                    menu.openPersonalityList(player);
                }
            } else if (type == Material.EMERALD) {
                beginEdit(player, EditType.ADD_PERSONALITY, null, null,
                        "Name der neuen Persönlichkeit:");
            } else if (type == Material.ARROW) {
                menu.openMain(player);
            }
        } else if (title.startsWith(NPCAdminMenu.PERSONALITY_LINES_PREFIX)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            String personality = ChatColor.stripColor(title.substring(NPCAdminMenu.PERSONALITY_LINES_PREFIX.length()));
            Material type = clicked.getType();
            if (type == Material.PAPER) {
                List<String> lore = clicked.getItemMeta().getLore();
                if (lore == null || lore.isEmpty()) return;
                String line = ChatColor.stripColor(lore.get(0));
                if (event.isLeftClick()) {
                    boolean removed = manager.removePersonalityLine(personality, line);
                    player.sendMessage(removed ? ChatColor.GREEN + "Nachricht entfernt." :
                            ChatColor.RED + "Nachricht konnte nicht entfernt werden.");
                    menu.openPersonalityLines(player, personality);
                } else if (event.isRightClick()) {
                    beginEdit(player, EditType.EDIT_PERSONALITY_LINE, personality, line,
                            "Neue Nachricht für " + personality + ":");
                }
            } else if (type == Material.EMERALD) {
                beginEdit(player, EditType.ADD_PERSONALITY_LINE, personality, null,
                        "Gib die neue Nachricht für " + personality + " ein:");
            } else if (type == Material.ARROW) {
                menu.openPersonalityList(player);
            }
        } else if (title.equals(NPCAdminMenu.CONVERSATION_LIST_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            if (type == Material.WRITTEN_BOOK) {
                String scriptId = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                if (event.isLeftClick()) {
                    menu.openConversationDetail(player, scriptId);
                } else if (event.isRightClick()) {
                    boolean removed = manager.deleteConversationScript(scriptId);
                    player.sendMessage(removed ? ChatColor.GREEN + "Gespräch gelöscht." :
                            ChatColor.RED + "Gespräch konnte nicht entfernt werden.");
                    menu.openConversationList(player);
                }
            } else if (type == Material.EMERALD) {
                beginEdit(player, EditType.ADD_CONVERSATION_SCRIPT, null, null,
                        "Neues Gespräch als id|Titel eingeben:");
            } else if (type == Material.SLIME_BALL) {
                manager.reloadNpcConfig();
                player.sendMessage(ChatColor.GREEN + "Gespräche neu geladen.");
                menu.openConversationList(player);
            } else if (type == Material.BOOKSHELF) {
                menu.openConversationPersonalityMenu(player);
            } else if (type == Material.ARROW) {
                menu.openMain(player);
            }
        } else if (title.startsWith(NPCAdminMenu.CONVERSATION_DETAIL_PREFIX)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            String scriptId = ChatColor.stripColor(title.substring(NPCAdminMenu.CONVERSATION_DETAIL_PREFIX.length()));
            Material type = clicked.getType();
            if (type == Material.NAME_TAG) {
                beginEdit(player, EditType.EDIT_CONVERSATION_TITLE, scriptId, null,
                        "Neuer Titel für " + scriptId + ":");
            } else if (type == Material.PLAYER_HEAD) {
                beginEdit(player, EditType.EDIT_CONVERSATION_PERSONALITIES_FIRST, scriptId, null,
                        "Personas für die erste Rolle (Komma getrennt):");
            } else if (type == Material.ZOMBIE_HEAD) {
                beginEdit(player, EditType.EDIT_CONVERSATION_PERSONALITIES_SECOND, scriptId, null,
                        "Personas für die zweite Rolle (Komma getrennt):");
            } else if (type == Material.HEART_OF_THE_SEA) {
                beginEdit(player, EditType.EDIT_CONVERSATION_PERSONALITIES_SHARED, scriptId, null,
                        "Gemeinsame Personas (Komma getrennt):");
            } else if (type == Material.WRITABLE_BOOK) {
                menu.openConversationLines(player, scriptId);
            } else if (type == Material.SLIME_BALL) {
                manager.reloadNpcConfig();
                player.sendMessage(ChatColor.GREEN + "Gespräch neu geladen.");
                menu.openConversationDetail(player, scriptId);
            } else if (type == Material.ARROW) {
                menu.openConversationList(player);
            }
        } else if (title.startsWith(NPCAdminMenu.CONVERSATION_LINES_PREFIX)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            String scriptId = ChatColor.stripColor(title.substring(NPCAdminMenu.CONVERSATION_LINES_PREFIX.length()));
            Material type = clicked.getType();
            if (type == Material.PAPER) {
                Integer index = extractLineIndex(clicked);
                if (index == null) return;
                if (event.isLeftClick()) {
                    boolean removed = manager.removeConversationLine(scriptId, index);
                    player.sendMessage(removed ? ChatColor.GREEN + "Zeile entfernt." :
                            ChatColor.RED + "Zeile konnte nicht entfernt werden.");
                    menu.openConversationLines(player, scriptId);
                } else if (event.isRightClick()) {
                    beginEdit(player, EditType.EDIT_CONVERSATION_LINE, scriptId, String.valueOf(index),
                            "Neuer Wert als Sprecher|Text|Pause:");
                }
            } else if (type == Material.EMERALD) {
                beginEdit(player, EditType.ADD_CONVERSATION_LINE, scriptId, null,
                        "Neue Zeile als Sprecher|Text|Pause:");
            } else if (type == Material.SLIME_BALL) {
                manager.reloadNpcConfig();
                player.sendMessage(ChatColor.GREEN + "Gespräch neu geladen.");
                menu.openConversationLines(player, scriptId);
            } else if (type == Material.ARROW) {
                menu.openConversationDetail(player, scriptId);
            }
        } else if (title.equals(NPCAdminMenu.CONVERSATION_PERSONALITY_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            if (clicked.getType() == Material.BOOK) {
                String scriptId = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                if (event.isLeftClick()) {
                    beginEdit(player, EditType.ADD_CONVERSATION_PERSONALITY, scriptId, null,
                            "Welche Persona soll hinzugefügt werden?");
                } else if (event.isRightClick()) {
                    beginEdit(player, EditType.REMOVE_CONVERSATION_PERSONALITY, scriptId, null,
                            "Welche Persona soll entfernt werden?");
                }
            } else if (clicked.getType() == Material.ARROW) {
                menu.openConversationList(player);
            }
        } else if (title.equals(NPCAdminMenu.SETTINGS_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            if (type == Material.ARMOR_STAND) {
                // Hologramm Höhe: +0.1 / -0.1
                double current = manager.getChatHologramVerticalOffset();
                double newValue = event.isLeftClick() ? current + 0.1 : current - 0.1;
                manager.setChatHologramVerticalOffset(newValue);
                player.sendMessage(ChatColor.GREEN + "Hologramm-Höhe: " + String.format("%.2f", newValue));
                menu.openSettings(player);
            } else if (type == Material.REDSTONE) {
                // Hologram Lebensdauer: +10 / -10 Ticks
                long current = manager.getChatHologramLifetimeTicks();
                long newValue = event.isLeftClick() ? current + 10 : current - 10;
                manager.setChatHologramLifetimeTicks(newValue);
                player.sendMessage(ChatColor.GREEN + "Hologramm-Lebensdauer: " + newValue + " Ticks");
                menu.openSettings(player);
            } else if (type == Material.REPEATER) {
                // Überprüfe welcher REPEATER geklickt wurde via Slot
                if (event.getSlot() == 12) {
                    // Hologram Follow Interval - Read-Only, ignorieren
                    return;
                } else if (event.getSlot() == 18) {
                    // Dialog-Zeilen Verzögerung: +5 / -5 Ticks
                    int current = manager.getConversationLineDelayTicks();
                    int newValue = event.isLeftClick() ? current + 5 : current - 5;
                    manager.setConversationLineDelayTicks(newValue);
                    player.sendMessage(ChatColor.GREEN + "Dialog-Zeilen Verzögerung: " + newValue + " Ticks");
                    menu.openSettings(player);
                } else if (event.getSlot() == 19) {
                    // Gather Verzögerung: +5 / -5 Ticks
                    int current = manager.getConversationGatherDelayTicks();
                    int newValue = event.isLeftClick() ? current + 5 : current - 5;
                    manager.setConversationGatherDelayTicks(newValue);
                    player.sendMessage(ChatColor.GREEN + "Gather Verzögerung: " + newValue + " Ticks");
                    menu.openSettings(player);
                }
            } else if (type == Material.LEVER) {
                // Gespräche aktiviert/deaktiviert - Toggle
                boolean currentState = manager.isConversationsEnabled();
                manager.setConversationsEnabled(!currentState);
                player.sendMessage(ChatColor.GREEN + "Gespräche sind jetzt " + 
                        (manager.isConversationsEnabled() ? "aktiviert" : "deaktiviert") + ".");
                menu.openSettings(player);
            } else if (type == Material.CLOCK) {
                // Überprüfe welche CLOCK geklickt wurde via Slot
                if (event.getSlot() == 15) {
                    // Min. Gesprächs-Interval: +10 / -10 Sekunden
                    int current = manager.getConversationMinIntervalSeconds();
                    int newValue = event.isLeftClick() ? current + 10 : current - 10;
                    manager.setConversationMinIntervalSeconds(newValue);
                    player.sendMessage(ChatColor.GREEN + "Min. Gesprächs-Intervall: " + newValue + "s");
                    menu.openSettings(player);
                } else if (event.getSlot() == 16) {
                    // Max. Gesprächs-Interval: +10 / -10 Sekunden
                    int current = manager.getConversationMaxIntervalSeconds();
                    int newValue = event.isLeftClick() ? current + 10 : current - 10;
                    manager.setConversationMaxIntervalSeconds(newValue);
                    player.sendMessage(ChatColor.GREEN + "Max. Gesprächs-Intervall: " + newValue + "s");
                    menu.openSettings(player);
                }
            } else if (type == Material.SPYGLASS) {
                // Publikums Reichweite: +5 / -5 Blöcke
                double current = manager.getConversationAudienceRadius();
                double newValue = event.isLeftClick() ? current + 5 : current - 5;
                manager.setConversationAudienceRadius(newValue);
                player.sendMessage(ChatColor.GREEN + "Publikums Reichweite: " + String.format("%.1f", newValue) + " Blöcke");
                menu.openSettings(player);
            } else if (type == Material.BOOK) {
                // Gespräch Präfix - nur mit Chat editierbar
                beginEdit(player, EditType.SETTINGS_CONVERSATION_PREFIX, null, null,
                        "Neuer Gespräch-Präfix eingeben (z.B. &5[NPC-Privat]):");
            } else if (type == Material.ARROW) {
                menu.openMain(player);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        EditContext context = editSessions.remove(event.getPlayer().getUniqueId());
        if (context == null) return;

        event.setCancelled(true);
        String message = event.getMessage().trim();
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> handleChatInput(player, context, message));
    }

    private Integer extractLineIndex(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getLore() == null || meta.getLore().isEmpty()) {
            return null;
        }
        String raw = ChatColor.stripColor(meta.getLore().get(0));
        if (raw == null || !raw.startsWith("Index:")) {
            return null;
        }
        String number = raw.substring("Index:".length()).trim();
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private List<String> parsePersonalityList(String message) {
        List<String> values = new ArrayList<>();
        if (message == null || message.isBlank()) {
            return values;
        }
        String[] parts = message.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                values.add(trimmed);
            }
        }
        return values;
    }

    private LineInput parseConversationLine(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        String[] parts = message.split("\\|", 3);
        if (parts.length < 2) {
            return null;
        }
        String speaker = parts[0].trim();
        String text = parts[1].trim();
        if (text.isEmpty()) {
            return null;
        }
        Integer pause = null;
        if (parts.length >= 3) {
            String pauseRaw = parts[2].trim();
            if (!pauseRaw.isEmpty()) {
                try {
                    pause = Integer.parseInt(pauseRaw);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }
        return new LineInput(speaker, text, pause);
    }

    private void beginEdit(Player player, EditType type, String reference, String secondary, String prompt) {
        editSessions.put(player.getUniqueId(), new EditContext(type, reference, secondary));
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + prompt);
        
        // Zeige Platzhalter-Hilfe für Chat und Konversations-Linien
        if (type == EditType.ADD_CHAT || type == EditType.EDIT_CHAT || 
            type == EditType.ADD_PERSONALITY_LINE || type == EditType.EDIT_PERSONALITY_LINE ||
            type == EditType.ADD_CONVERSATION_LINE || type == EditType.EDIT_CONVERSATION_LINE) {
            showPlaceholderHelp(player, type);
        }
        
        player.sendMessage(ChatColor.GRAY + "(Tippe 'abbrechen' um zu stoppen)");
    }
    
    private void showPlaceholderHelp(Player player, EditType type) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "=== Verfügbare Platzhalter ===");
        player.sendMessage(ChatColor.AQUA + "Spieler-Platzhalter:");
        player.sendMessage(ChatColor.WHITE + "  {SPIELERNAME}, {PLAYER}, {PLAYERNAME} → Name des Spielers");
        player.sendMessage(ChatColor.WHITE + "  {SELF} → Name des sprechenden NPCs");
        player.sendMessage("");
        
        if (type == EditType.ADD_CHAT || type == EditType.EDIT_CHAT ||
            type == EditType.ADD_PERSONALITY_LINE || type == EditType.EDIT_PERSONALITY_LINE) {
            player.sendMessage(ChatColor.AQUA + "NPC-Platzhalter:");
            player.sendMessage(ChatColor.WHITE + "  {NPC}, {NPC2} → Name eines anderen NPCs");
            player.sendMessage(ChatColor.WHITE + "  {NPCXX} → Name eines anderen NPCs (XX = beliebige Zahl)");
        }
        
        if (type == EditType.ADD_CONVERSATION_LINE || type == EditType.EDIT_CONVERSATION_LINE) {
            player.sendMessage(ChatColor.AQUA + "Konversations-Platzhalter:");
            player.sendMessage(ChatColor.WHITE + "  {A}, {a}, {FIRST} → Name des ersten Gesprächs-Partners");
            player.sendMessage(ChatColor.WHITE + "  {B}, {b}, {SECOND} → Name des zweiten Gesprächs-Partners");
        }
        
        player.sendMessage("");
    }

    private void handleChatInput(Player player, EditContext context, String message) {
        if (message.equalsIgnoreCase("abbrechen")) {
            player.sendMessage(ChatColor.RED + "Aktion abgebrochen.");
            return;
        }
        boolean success = false;
        switch (context.type) {
            case ADD_NAME:
                success = manager.addNpcName(message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Name hinzugefügt.");
                }
                menu.openNameEditor(player);
                break;
            case RENAME_NAME:
                success = manager.renameNpcName(context.reference, message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Name geändert.");
                }
                menu.openNameEditor(player);
                break;
            case ADD_CHAT:
                success = manager.addChatLine(message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Nachricht hinzugefügt.");
                }
                menu.openChatEditor(player);
                break;
            case EDIT_CHAT:
                success = manager.replaceChatLine(context.reference, message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Nachricht aktualisiert.");
                }
                menu.openChatEditor(player);
                break;
            case ADD_NAME_PERSONALITY:
                success = manager.assignPersonalityToName(context.reference, message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Persönlichkeit zugewiesen.");
                }
                menu.openPersonalityDetail(player, context.reference);
                break;
            case ADD_PERSONALITY:
                success = manager.addPersonalityDefinition(message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Persönlichkeit erstellt.");
                }
                menu.openPersonalityList(player);
                break;
            case ADD_PERSONALITY_LINE:
                success = manager.addPersonalityLine(context.reference, message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Nachricht hinzugefügt.");
                }
                menu.openPersonalityLines(player, context.reference);
                break;
            case EDIT_PERSONALITY_LINE:
                success = manager.replacePersonalityLine(context.reference, context.secondary, message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Nachricht aktualisiert.");
                }
                menu.openPersonalityLines(player, context.reference);
                break;
            case ADD_CONVERSATION_SCRIPT:
                String[] parts = message.split("\\|", 2);
                String id = parts[0].trim();
                String title = parts.length > 1 ? parts[1].trim() : id;
                if (!id.isEmpty()) {
                    success = manager.createConversationScript(id, title);
                }
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Gespräch erstellt.");
                }
                menu.openConversationList(player);
                break;
            case EDIT_CONVERSATION_TITLE:
                success = manager.updateConversationTitle(context.reference, message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Titel aktualisiert.");
                }
                menu.openConversationDetail(player, context.reference);
                break;
            case EDIT_CONVERSATION_PERSONALITIES_FIRST:
                success = manager.setConversationPersonalities(context.reference,
                        NPCManager.ConversationPersonaRole.FIRST, parsePersonalityList(message));
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Personas aktualisiert.");
                }
                menu.openConversationDetail(player, context.reference);
                break;
            case EDIT_CONVERSATION_PERSONALITIES_SECOND:
                success = manager.setConversationPersonalities(context.reference,
                        NPCManager.ConversationPersonaRole.SECOND, parsePersonalityList(message));
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Personas aktualisiert.");
                }
                menu.openConversationDetail(player, context.reference);
                break;
            case EDIT_CONVERSATION_PERSONALITIES_SHARED:
                success = manager.setConversationPersonalities(context.reference,
                        NPCManager.ConversationPersonaRole.SHARED, parsePersonalityList(message));
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Personas aktualisiert.");
                }
                menu.openConversationDetail(player, context.reference);
                break;
            case ADD_CONVERSATION_LINE:
                LineInput addLine = parseConversationLine(message);
                if (addLine != null) {
                    success = manager.addConversationLine(context.reference, addLine.speaker, addLine.text, addLine.pause);
                }
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Zeile hinzugefügt.");
                }
                menu.openConversationLines(player, context.reference);
                break;
            case EDIT_CONVERSATION_LINE:
                LineInput editLine = parseConversationLine(message);
                if (editLine != null && context.secondary != null) {
                    try {
                        int index = Integer.parseInt(context.secondary);
                        success = manager.replaceConversationLine(context.reference, index, editLine.speaker, editLine.text, editLine.pause);
                    } catch (NumberFormatException ignored) {
                        success = false;
                    }
                }
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Zeile aktualisiert.");
                }
                menu.openConversationLines(player, context.reference);
                break;
            case ADD_CONVERSATION_PERSONALITY:
                success = manager.addConversationPersonality(context.reference,
                        NPCManager.ConversationPersonaRole.SHARED, message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Persona hinzugefügt.");
                }
                menu.openConversationPersonalityMenu(player);
                break;
            case REMOVE_CONVERSATION_PERSONALITY:
                success = manager.removeConversationPersonality(context.reference,
                        NPCManager.ConversationPersonaRole.SHARED, message);
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Persona entfernt.");
                }
                menu.openConversationPersonalityMenu(player);
                break;
            case SETTINGS_HOLOGRAM_HEIGHT:
                try {
                    double height = Double.parseDouble(message);
                    if (height < 0.5 || height > 10.0) {
                        player.sendMessage(ChatColor.RED + "Höhe muss zwischen 0.5 und 10.0 liegen.");
                        break;
                    }
                    manager.setChatHologramVerticalOffset(height);
                    success = true;
                    player.sendMessage(ChatColor.GREEN + "Hologramm-Höhe auf " + height + " gesetzt.");
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Ungültige Zahl eingegeben.");
                }
                menu.openSettings(player);
                break;
            case SETTINGS_HOLOGRAM_LIFETIME:
                try {
                    long ticks = Long.parseLong(message);
                    if (ticks < 20) {
                        player.sendMessage(ChatColor.RED + "Lebensdauer muss mindestens 20 Ticks sein (1 Sekunde).");
                        break;
                    }
                    manager.setChatHologramLifetimeTicks(ticks);
                    success = true;
                    player.sendMessage(ChatColor.GREEN + "Hologramm-Lebensdauer auf " + ticks + " Ticks gesetzt.");
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Ungültige Zahl eingegeben.");
                }
                menu.openSettings(player);
                break;
            case SETTINGS_CONVERSATION_MIN_INTERVAL:
                try {
                    int seconds = Integer.parseInt(message);
                    if (seconds < 30) {
                        player.sendMessage(ChatColor.RED + "Mindestintervall muss mindestens 30 Sekunden sein.");
                        break;
                    }
                    manager.setConversationMinIntervalSeconds(seconds);
                    success = true;
                    player.sendMessage(ChatColor.GREEN + "Min. Gesprächs-Intervall auf " + seconds + " Sekunden gesetzt.");
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Ungültige Zahl eingegeben.");
                }
                menu.openSettings(player);
                break;
            case SETTINGS_CONVERSATION_MAX_INTERVAL:
                try {
                    int seconds = Integer.parseInt(message);
                    int minSeconds = manager.getConversationMinIntervalSeconds();
                    if (seconds < minSeconds) {
                        player.sendMessage(ChatColor.RED + "Max. Intervall muss größer als Min. Intervall (" + minSeconds + "s) sein.");
                        break;
                    }
                    manager.setConversationMaxIntervalSeconds(seconds);
                    success = true;
                    player.sendMessage(ChatColor.GREEN + "Max. Gesprächs-Intervall auf " + seconds + " Sekunden gesetzt.");
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Ungültige Zahl eingegeben.");
                }
                menu.openSettings(player);
                break;
            case SETTINGS_CONVERSATION_LINE_DELAY:
                try {
                    int ticks = Integer.parseInt(message);
                    if (ticks < 10) {
                        player.sendMessage(ChatColor.RED + "Verzögerung muss mindestens 10 Ticks sein.");
                        break;
                    }
                    manager.setConversationLineDelayTicks(ticks);
                    success = true;
                    player.sendMessage(ChatColor.GREEN + "Dialog-Zeilen-Verzögerung auf " + ticks + " Ticks gesetzt.");
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Ungültige Zahl eingegeben.");
                }
                menu.openSettings(player);
                break;
            case SETTINGS_CONVERSATION_GATHER_DELAY:
                try {
                    int ticks = Integer.parseInt(message);
                    if (ticks < 0) {
                        player.sendMessage(ChatColor.RED + "Verzögerung kann nicht negativ sein.");
                        break;
                    }
                    manager.setConversationGatherDelayTicks(ticks);
                    success = true;
                    player.sendMessage(ChatColor.GREEN + "Gather-Verzögerung auf " + ticks + " Ticks gesetzt.");
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Ungültige Zahl eingegeben.");
                }
                menu.openSettings(player);
                break;
            case SETTINGS_CONVERSATION_AUDIENCE_RADIUS:
                try {
                    double radius = Double.parseDouble(message);
                    if (radius < 5.0) {
                        player.sendMessage(ChatColor.RED + "Reichweite muss mindestens 5.0 Blöcke sein.");
                        break;
                    }
                    manager.setConversationAudienceRadius(radius);
                    success = true;
                    player.sendMessage(ChatColor.GREEN + "Publikums-Reichweite auf " + radius + " Blöcke gesetzt.");
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Ungültige Zahl eingegeben.");
                }
                menu.openSettings(player);
                break;
            case SETTINGS_CONVERSATION_PREFIX:
                String coloredPrefix = ChatColor.translateAlternateColorCodes('&', message);
                manager.setConversationPrefix(message);
                success = true;
                player.sendMessage(ChatColor.GREEN + "Gespräch-Präfix auf " + coloredPrefix + ChatColor.GREEN + " gesetzt.");
                menu.openSettings(player);
                break;
            case SETTINGS_CONVERSATIONS_TOGGLE:
                // This should not be called directly, toggle happens via button
                menu.openSettings(player);
                break;
        }
        if (!success) {
            player.sendMessage(ChatColor.RED + "Aktion fehlgeschlagen. Bitte Eingabe prüfen.");
        }
    }
}
