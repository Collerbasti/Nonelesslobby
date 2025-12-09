package de.noneless.lobby.listeners;

import Config.GamemodeSettingsConfig;
import Mysql.Punkte;
import de.noneless.lobby.Main;
import de.noneless.lobby.Menues.GamemodeAdminMenu;
import de.noneless.lobby.Menues.PointsPlayerListMenu;
import de.noneless.lobby.Menues.Settings;
import de.noneless.lobby.Menues.WorldGamemodeEditMenu;
import de.noneless.lobby.Menues.WorldGamemodeListMenu;
import de.noneless.lobby.util.GamemodeEnforcer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class SettingsListener implements Listener {

    private static final NamespacedKey GM_META_KEY = new NamespacedKey(Main.getInstance(), "gm_meta");
    private static final NamespacedKey POINTS_META_KEY = new NamespacedKey(Main.getInstance(), "points_meta");

    private final Settings settingsMenu = new Settings();
    private final GamemodeAdminMenu gamemodeAdminMenu = new GamemodeAdminMenu();
    private final WorldGamemodeListMenu worldGamemodeListMenu = new WorldGamemodeListMenu();
    private final WorldGamemodeEditMenu worldGamemodeEditMenu = new WorldGamemodeEditMenu();
    private final PointsPlayerListMenu pointsPlayerListMenu = new PointsPlayerListMenu();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        String title = event.getView().getTitle();

        if (title.contains(" Settings")) {
            event.setCancelled(true);
            if (event.getClickedInventory() == null) {
                return;
            }
            handleMainSettingsClick(player, event);
        } else if (title.equals(GamemodeAdminMenu.TITLE)) {
            event.setCancelled(true);
            handleGamemodeAdminClick(player, event);
        } else if (title.equals(WorldGamemodeListMenu.TITLE)) {
            event.setCancelled(true);
            handleWorldListClick(player, event);
        } else if (title.startsWith(WorldGamemodeEditMenu.TITLE_PREFIX)) {
            event.setCancelled(true);
            handleWorldEditClick(player, event);
        } else if (title.equals(PointsPlayerListMenu.TITLE)) {
            event.setCancelled(true);
            handlePointsPlayerListClick(player, event);
        }
    }

    private void handleMainSettingsClick(Player player, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }
        switch (event.getSlot()) {
            case 10 -> toggleFriendsTeleport(player);
            case 11 -> toggleNpcChat(player);
            case 12 -> toggleCoinsMode(player);
            case 14 -> {
                if (player.hasPermission("Noneless.Admin.Gamemode")) {
                    openGamemodeAdminMenu(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für Gamemode-Einstellungen!");
                }
            }
            case 16 -> {
                if (player.hasPermission("Noneless.Admin")) {
                    toggleAdminVisibility(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Du hast keine Admin-Berechtigung!");
                }
            }
            case 20 -> {
                if (player.hasPermission("nonelesslobby.npc.admin")) {
                    player.closeInventory();
                    new de.noneless.lobby.Menues.NPCAdminMenu().openMain(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Dir fehlt die Berechtigung für die NPC-Verwaltung.");
                }
            }
            case 22 -> {
                player.closeInventory();
                try {
                    player.performCommand("warps");
                } catch (Exception e) {
                    player.sendMessage(ChatColor.GREEN + "Settings geschlossen.");
                }
            }
            case 24 -> {
                if (player.hasPermission("Noneless.Admin")) {
                    openPointsPlayerList(player, 0);
                } else {
                    player.sendMessage(ChatColor.RED + "Dir fehlt die Berechtigung für die Punkte-Verwaltung.");
                }
            }
            default -> {
            }
        }
    }

    private void handleGamemodeAdminClick(Player player, InventoryClickEvent event) {
        if (!player.hasPermission("Noneless.Admin.Gamemode")) {
            player.sendMessage(ChatColor.RED + "Dir fehlt die Berechtigung für dieses Menü.");
            player.closeInventory();
            return;
        }
        switch (event.getSlot()) {
            case 11 -> openWorldGamemodeList(player, 0);
            case 15 -> toggleCreativeOverride(player);
            case 22 -> openSettingsMenu(player);
            default -> {
            }
        }
    }

    private void handleWorldListClick(Player player, InventoryClickEvent event) {
        if (!player.hasPermission("Noneless.Admin.Gamemode")) {
            player.sendMessage(ChatColor.RED + "Dir fehlt die Berechtigung für dieses Menü.");
            player.closeInventory();
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) {
            if (event.getSlot() == 49) {
                openGamemodeAdminMenu(player);
            }
            return;
        }
        String localized = resolveMetadata(item, GM_META_KEY);
        if (localized == null || localized.isEmpty()) {
            if (event.getSlot() == 49) {
                openGamemodeAdminMenu(player);
            }
            return;
        }
        if (localized.startsWith("world:")) {
            String worldName = getToken(localized, "world");
            int page = parsePage(getToken(localized, "page"));
            if (worldName != null) {
                worldGamemodeEditMenu.open(player, worldName, page);
            }
        } else if (localized.startsWith("nav:")) {
            int targetPage = parsePage(getToken(localized, "page"));
            openWorldGamemodeList(player, targetPage);
        } else if (localized.startsWith("back:")) {
            openGamemodeAdminMenu(player);
        }
    }

    private void handleWorldEditClick(Player player, InventoryClickEvent event) {
        if (!player.hasPermission("Noneless.Admin.Gamemode")) {
            player.sendMessage(ChatColor.RED + "Dir fehlt die Berechtigung für dieses Menü.");
            player.closeInventory();
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) {
            if (event.getSlot() == 22) {
                openWorldGamemodeList(player, 0);
            }
            return;
        }
        String localized = resolveMetadata(item, GM_META_KEY);
        if (localized == null) {
            if (event.getSlot() == 22) {
                openWorldGamemodeList(player, 0);
            }
            return;
        }
        if (localized.startsWith("mode:")) {
            String worldName = getToken(localized, "world");
            String modeName = getToken(localized, "mode");
            int page = parsePage(getToken(localized, "page"));
            if (worldName == null || modeName == null) {
                player.sendMessage(ChatColor.RED + "Konnte Welt oder Gamemode nicht bestimmen.");
                return;
            }
            try {
                GameMode mode = GameMode.valueOf(modeName);
                GamemodeSettingsConfig.setWorldGamemode(worldName, mode);
                player.sendMessage(ChatColor.GREEN + "Standard-Gamemode für " +
                        ChatColor.AQUA + worldName + ChatColor.GREEN + " ist jetzt " +
                        ChatColor.GOLD + mode.name());
                worldGamemodeEditMenu.open(player, worldName, page);
            } catch (IllegalArgumentException ex) {
                player.sendMessage(ChatColor.RED + "Unbekannter Gamemode: " + modeName);
            }
        } else if (localized.startsWith("back:")) {
            int page = parsePage(localized.substring("back:".length()));
            openWorldGamemodeList(player, page);
        }
    }

    private void handlePointsPlayerListClick(Player player, InventoryClickEvent event) {
        if (!player.hasPermission("Noneless.Admin")) {
            player.sendMessage(ChatColor.RED + "Dir fehlt die Berechtigung für dieses Menü.");
            player.closeInventory();
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        String data = resolveMetadata(item, POINTS_META_KEY);
        if (data == null) {
            return;
        }
        if (data.startsWith("player:")) {
            handlePlayerPointsClick(player, event, data);
        } else if (data.startsWith("nav:")) {
            int targetPage = parsePage(getToken(data, "page"));
            openPointsPlayerList(player, targetPage);
        } else if (data.startsWith("back:")) {
            openSettingsMenu(player);
        }
    }

    private void handlePlayerPointsClick(Player player, InventoryClickEvent event, String data) {
        String uuidToken = getToken(data, "player");
        if (uuidToken == null) {
            return;
        }
        if (!event.isLeftClick() && !event.isRightClick()) {
            return;
        }
        int amount = event.isShiftClick() ? 10 : 100;
        try {
            var uuid = java.util.UUID.fromString(uuidToken);
            String targetName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
            if (event.isLeftClick()) {
                Punkte.addPoints(uuid, amount);
                player.sendMessage(ChatColor.GREEN + "+" + amount + " Punkte für " + ChatColor.AQUA + targetName);
            } else {
                Punkte.removePoints(uuid, amount);
                player.sendMessage(ChatColor.RED + "-" + amount + " Punkte für " + ChatColor.AQUA + targetName);
            }
        } catch (IllegalArgumentException ignored) {
            player.sendMessage(ChatColor.RED + "Konnte Spieler nicht bestimmen.");
        }
        int page = parsePage(getToken(data, "page"));
        openPointsPlayerList(player, page);
    }

    private void toggleFriendsTeleport(Player player) {
        try {
            boolean currentSetting = Main.Frdb.getBoolean(player.getName() + ".AllowFriendsTp", false);
            boolean newSetting = !currentSetting;
            Main.Frdb.set(player.getName() + ".AllowFriendsTp", newSetting);
            Main.getInstance().saveSettingsConfigs();
            player.sendMessage(newSetting
                    ? ChatColor.GREEN + "Freunde können sich jetzt zu dir teleportieren!"
                    : ChatColor.RED + "Freunde können sich nicht mehr zu dir teleportieren!");
            openSettingsMenu(player);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Fehler beim Ändern der Freunde-Teleport Einstellung!");
            Bukkit.getLogger().severe("[SettingsListener] Error toggling friend TP: " + e.getMessage());
        }
    }

    private void toggleCoinsMode(Player player) {
        try {
            boolean currentSetting = Main.Frdb.getBoolean(player.getName() + ".GetCoins", false);
            boolean newSetting = !currentSetting;
            Main.Frdb.set(player.getName() + ".GetCoins", newSetting);
            Main.getInstance().saveSettingsConfigs();
            player.sendMessage(newSetting
                    ? ChatColor.GREEN + "Du erhältst jetzt 80 Coins pro Stunde!"
                    : ChatColor.YELLOW + "Du erhältst jetzt Duellpunkte statt Coins!");
            openSettingsMenu(player);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Fehler beim Ändern der Coins-Einstellung!");
            Bukkit.getLogger().severe("[SettingsListener] Error toggling coins: " + e.getMessage());
        }
    }

    private void toggleAdminVisibility(Player player) {
        try {
            boolean currentVisible = Main.AOnline.getBoolean(player.getName() + ".Enable", false);
            boolean newVisible = !currentVisible;
            Main.AOnline.set(player.getName() + ".Enable", newVisible);
            Main.getInstance().saveSettingsConfigs();
            player.sendMessage(newVisible
                    ? ChatColor.GREEN + "Du wirst jetzt im /Hilfe Befehl als Online-Admin angezeigt!"
                    : ChatColor.RED + "Du wirst nicht mehr im /Hilfe Befehl angezeigt!");
            openSettingsMenu(player);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Fehler beim Ändern der Admin-Sichtbarkeit!");
            Bukkit.getLogger().severe("[SettingsListener] Error toggling admin visibility: " + e.getMessage());
        }
    }

    private void toggleCreativeOverride(Player player) {
        long expiresAt = GamemodeSettingsConfig.getCreativeOverrideExpiry(player.getUniqueId());
        if (expiresAt > System.currentTimeMillis()) {
            GamemodeSettingsConfig.clearOverride(player.getUniqueId());
            GamemodeEnforcer.enforce(player);
            player.sendMessage(ChatColor.YELLOW + "Creative Override deaktiviert.");
        } else {
            GamemodeSettingsConfig.enableCreativeOverride(player.getUniqueId(), player.getName());
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(ChatColor.GREEN + "Creative Override für 7 Stunden aktiviert!");
        }
        gamemodeAdminMenu.open(player);
    }

    private void openSettingsMenu(Player player) {
        try {
            settingsMenu.Spawn(player);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Fehler beim Öffnen des Settings-Menüs!");
            Bukkit.getLogger().severe("[SettingsListener] Error opening settings menu: " + e.getMessage());
        }
    }

    private void openGamemodeAdminMenu(Player player) {
        gamemodeAdminMenu.open(player);
    }

    private void openWorldGamemodeList(Player player, int page) {
        worldGamemodeListMenu.open(player, Math.max(0, page));
    }

    private void openPointsPlayerList(Player player, int page) {
        pointsPlayerListMenu.open(player, Math.max(0, page));
    }

    private String getToken(String encoded, String key) {
        if (encoded == null || key == null) {
            return null;
        }
        String prefix = key + ":";
        String[] parts = encoded.split(";");
        for (String part : parts) {
            if (part.startsWith(prefix)) {
                return part.substring(prefix.length());
            }
        }
        return null;
    }

    private int parsePage(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void toggleNpcChat(Player player) {
        try {
            boolean currentSetting = Main.Frdb.getBoolean(player.getName() + ".NpcChatEnabled", true);
            boolean newSetting = !currentSetting;
            Main.Frdb.set(player.getName() + ".NpcChatEnabled", newSetting);
            Main.getInstance().saveSettingsConfigs();
            player.sendMessage(newSetting
                    ? ChatColor.GREEN + "NPC-Chatnachrichten werden wieder angezeigt!"
                    : ChatColor.RED + "NPC-Chatnachrichten wurden deaktiviert.");
            openSettingsMenu(player);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Fehler beim Umschalten der NPC-Chat Einstellung!");
            Bukkit.getLogger().severe("[SettingsListener] Error toggling NPC chat: " + e.getMessage());
        }
    }

    private String resolveMetadata(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        if (item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        }
        String localized = item.getItemMeta().getLocalizedName();
        if (localized != null && !localized.isEmpty()) {
            return localized;
        }
        if (item.getItemMeta().hasLore()) {
            for (String line : item.getItemMeta().getLore()) {
                if (line == null) continue;
                String stripped = ChatColor.stripColor(line);
                if (stripped != null && stripped.startsWith("META:")) {
                    return stripped.substring("META:".length());
                }
            }
        }
        return null;
    }
}
