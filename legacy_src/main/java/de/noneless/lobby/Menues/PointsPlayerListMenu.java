package de.noneless.lobby.Menues;

import Mysql.Punkte;
import de.noneless.lobby.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PointsPlayerListMenu {

    public static final String TITLE = ChatColor.DARK_PURPLE + "Punkte: Spieler";
    private static final int PAGE_SIZE = 45;
    private static final NamespacedKey META_KEY = new NamespacedKey(Main.getInstance(), "points_meta");

    public void open(Player viewer, int page) {
        List<OfflinePlayer> players = new ArrayList<>(Arrays.asList(Bukkit.getOfflinePlayers()));
        players.sort(Comparator.comparingLong(OfflinePlayer::getLastPlayed).reversed());
        int totalPages = Math.max(1, (int) Math.ceil(players.size() / (double) PAGE_SIZE));
        page = Math.max(0, Math.min(page, totalPages - 1));
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        int start = page * PAGE_SIZE;
        for (int slot = 0; slot < PAGE_SIZE; slot++) {
            int index = start + slot;
            if (index >= players.size()) break;
            inv.setItem(slot, createPlayerItem(players.get(index), page));
        }

        if (page > 0) {
            inv.setItem(45, createNavItem(ChatColor.YELLOW + "<- Zurück", page - 1, "prev"));
        }
        if (page < totalPages - 1) {
            inv.setItem(53, createNavItem(ChatColor.YELLOW + "Weiter ->", page + 1, "next"));
        }

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "Zurück");
        applyMetadata(backMeta, "back:" + page);
        back.setItemMeta(backMeta);
        inv.setItem(49, back);

        viewer.openInventory(inv);
    }

    private ItemStack createPlayerItem(OfflinePlayer offlinePlayer, int page) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        String name = offlinePlayer.getName() != null ? offlinePlayer.getName() : offlinePlayer.getUniqueId().toString();
        meta.setDisplayName(ChatColor.AQUA + name);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Punkte: " + ChatColor.YELLOW + Punkte.getPoints(offlinePlayer.getUniqueId()));
        if (offlinePlayer.isOnline()) {
            lore.add(ChatColor.GREEN + "Online");
        } else {
            lore.add(ChatColor.DARK_GRAY + "Zuletzt: " + formatLastSeen(offlinePlayer.getLastPlayed()));
        }
        applyMetadata(meta, lore, "player:" + offlinePlayer.getUniqueId() + ";page:" + page);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createNavItem(String name, int targetPage, String direction) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Klicke zum Navigieren");
        applyMetadata(meta, lore, "nav:" + direction + ";page:" + targetPage);
        item.setItemMeta(meta);
        return item;
    }

    private void applyMetadata(ItemMeta meta, String data) {
        applyMetadata(meta, null, data);
    }

    private void applyMetadata(ItemMeta meta, List<String> lore, String data) {
        if (meta == null || data == null) return;
        meta.setLocalizedName(data);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(META_KEY, PersistentDataType.STRING, data);
        List<String> newLore = lore == null
                ? (meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>())
                : new ArrayList<>(lore);
        newLore.add(ChatColor.BLACK + "META:" + data);
        meta.setLore(newLore);
    }

    private String formatLastSeen(long timestamp) {
        if (timestamp <= 0) return "nie";
        long diff = System.currentTimeMillis() - timestamp;
        long days = diff / (1000 * 60 * 60 * 24);
        if (days > 0) {
            return days + "d";
        }
        long hours = diff / (1000 * 60 * 60);
        if (hours > 0) {
            return hours + "h";
        }
        long minutes = diff / (1000 * 60);
        if (minutes > 0) {
            return minutes + "m";
        }
        return "gerade eben";
    }
}
