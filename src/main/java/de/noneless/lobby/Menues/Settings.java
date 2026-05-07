package de.noneless.lobby.Menues;

import de.noneless.lobby.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    public static final int SLOT_FRIENDS_TP = 10;
    public static final int SLOT_NPC_CHAT = 13;
    public static final int SLOT_REWARDS = 16;
    public static final int SLOT_ADMIN_VISIBILITY = 22;
    public static final int SLOT_GAMEMODE_ADMIN = 28;
    public static final int SLOT_NPC_ADMIN = 31;
    public static final int SLOT_POINTS_ADMIN = 34;
    public static final int SLOT_BACK = 49;

    public void Spawn(Player player) {
        Inventory settings = player.getServer().createInventory(null, 54,
                ChatColor.AQUA + player.getName() + ChatColor.GOLD + " Settings");

        settings.setItem(4, createInfoItem(
                Material.COMPARATOR,
                ChatColor.GOLD + "Noneless Lobby Einstellungen",
                List.of(
                        ChatColor.GRAY + "Persoenliche Lobby-Optionen",
                        ChatColor.GRAY + "und Admin-Werkzeuge"
                )
        ));

        settings.setItem(SLOT_FRIENDS_TP, createFriendsTeleportItem(player));
        settings.setItem(SLOT_NPC_CHAT, createNpcChatItem(player));
        settings.setItem(SLOT_REWARDS, createRewardsItem(player));

        settings.setItem(SLOT_ADMIN_VISIBILITY, createAdminVisibilityItem(player));
        settings.setItem(SLOT_GAMEMODE_ADMIN, createGamemodeAdminItem(player));
        settings.setItem(SLOT_NPC_ADMIN, createNpcAdminItem(player));
        settings.setItem(SLOT_POINTS_ADMIN, createPointsAdminItem(player));

        settings.setItem(SLOT_BACK, createInfoItem(
                Material.ARROW,
                ChatColor.RED + "Zurueck",
                List.of(ChatColor.GRAY + "Zum Lobby-Menue")
        ));

        fillWithGlass(settings);
        player.openInventory(settings);
    }

    private ItemStack createFriendsTeleportItem(Player player) {
        boolean enabled = Main.Frdb.getBoolean(player.getName() + ".AllowFriendsTp", false);
        return createToggleItem(
                enabled,
                "Freunde-Teleport",
                enabled ? "Freunde duerfen sich zu dir teleportieren" : "Freunde duerfen sich nicht zu dir teleportieren",
                List.of(ChatColor.GRAY + "Klick: Erlaubnis umschalten")
        );
    }

    private ItemStack createNpcChatItem(Player player) {
        boolean enabled = Main.Frdb.getBoolean(player.getName() + ".NpcChatEnabled", true);
        return createToggleItem(
                enabled,
                "NPC-Chat",
                enabled ? "NPC-Chatnachrichten sichtbar" : "NPC-Chatnachrichten versteckt",
                List.of(ChatColor.GRAY + "Klick: NPC-Chat umschalten")
        );
    }

    private ItemStack createRewardsItem(Player player) {
        boolean coins = Main.Frdb.getBoolean(player.getName() + ".GetCoins", false);
        ItemStack item = new ItemStack(coins ? Material.GOLD_INGOT : Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName((coins ? ChatColor.GOLD : ChatColor.GREEN) + "Stunden-Belohnung");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Aktuell: " + (coins ? ChatColor.GOLD + "80 Coins" : ChatColor.GREEN + "Duellpunkt"));
            lore.add(ChatColor.GRAY + "Klick: Belohnung wechseln");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createAdminVisibilityItem(Player player) {
        if (!player.hasPermission("Noneless.Admin")) {
            return createLockedItem("Admin-Anzeige", "Nur fuer Admins verfuegbar");
        }
        boolean visible = Main.AOnline.getBoolean(player.getName() + ".Enable", false);
        return createToggleItem(
                visible,
                "Admin-Anzeige",
                visible ? "Du wirst in /Hilfe angezeigt" : "Du wirst in /Hilfe nicht angezeigt",
                List.of(ChatColor.GRAY + "Klick: Sichtbarkeit umschalten")
        );
    }

    private ItemStack createGamemodeAdminItem(Player player) {
        if (!player.hasPermission("Noneless.Admin.Gamemode")) {
            return createLockedItem("Gamemode Verwaltung", "Nur fuer Gamemode-Admins");
        }
        return createInfoItem(
                Material.NETHER_STAR,
                ChatColor.GOLD + "Gamemode Verwaltung",
                List.of(
                        ChatColor.GRAY + "Standard-Gamemodes pro Welt",
                        ChatColor.GRAY + "und Creative-Override"
                )
        );
    }

    private ItemStack createNpcAdminItem(Player player) {
        if (!player.hasPermission("nonelesslobby.npc.admin")) {
            return createLockedItem("NPC Verwaltung", "Nur fuer NPC-Admins");
        }
        return createInfoItem(
                Material.VILLAGER_SPAWN_EGG,
                ChatColor.DARK_AQUA + "NPC Verwaltung",
                List.of(
                        ChatColor.GRAY + "NPCs, Namen, Chats, POIs",
                        ChatColor.GRAY + "und Gespraeche verwalten"
                )
        );
    }

    private ItemStack createPointsAdminItem(Player player) {
        if (!player.hasPermission("Noneless.Admin")) {
            return createLockedItem("Punkte Verwaltung", "Nur fuer Admins verfuegbar");
        }
        return createInfoItem(
                Material.GOLD_BLOCK,
                ChatColor.GOLD + "Punkte Verwaltung",
                List.of(
                        ChatColor.GRAY + "Spieler-Liste oeffnen",
                        ChatColor.GRAY + "Linksklick: +100 | Shift: +10",
                        ChatColor.GRAY + "Rechtsklick: -100 | Shift: -10"
                )
        );
    }

    private ItemStack createToggleItem(boolean enabled, String title, String stateLine, List<String> extraLore) {
        ItemStack item = new ItemStack(enabled ? Material.GREEN_WOOL : Material.RED_WOOL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName((enabled ? ChatColor.GREEN : ChatColor.RED) + title);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + stateLine);
            if (extraLore != null) {
                lore.addAll(extraLore);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createLockedItem(String title, String reason) {
        return createInfoItem(
                Material.GRAY_DYE,
                ChatColor.DARK_GRAY + title,
                List.of(ChatColor.GRAY + reason)
        );
    }

    private ItemStack createInfoItem(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private void fillWithGlass(Inventory inventory) {
        ItemStack filler = createInfoItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + " ", null);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler.clone());
            }
        }
    }
}
