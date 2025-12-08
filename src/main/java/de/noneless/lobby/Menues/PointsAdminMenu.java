package de.noneless.lobby.Menues;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PointsAdminMenu {

    public static final String TITLE = ChatColor.DARK_PURPLE + "Punkte Verwaltung";

    public void open(Player player, String targetName, int currentPoints) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        inv.setItem(11, createPlayerSelector(targetName, currentPoints));
        inv.setItem(15, createControlItem(targetName != null));
        inv.setItem(22, createBackItem());
        player.openInventory(inv);
    }

    private ItemStack createPlayerSelector(String targetName, int points) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Spieler auswählen");
        List<String> lore = new ArrayList<>();
        if (targetName != null) {
            lore.add(ChatColor.GRAY + "Aktuell: " + ChatColor.AQUA + targetName);
            lore.add(ChatColor.GRAY + "Punkte: " + ChatColor.YELLOW + points);
        } else {
            lore.add(ChatColor.GRAY + "Kein Spieler ausgewählt");
        }
        lore.add(ChatColor.YELLOW + "Linksklick: Spieler-Liste öffnen");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createControlItem(boolean enabled) {
        ItemStack item = new ItemStack(enabled ? Material.GOLD_BLOCK : Material.GRAY_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(enabled ? ChatColor.GREEN + "Punkte anpassen" : ChatColor.DARK_GRAY + "Punkte anpassen");
        List<String> lore = new ArrayList<>();
        if (!enabled) {
            lore.add(ChatColor.RED + "Wähle zuerst einen Spieler aus.");
        } else {
            lore.add(ChatColor.GRAY + "Linksklick: +100 Punkte");
            lore.add(ChatColor.GRAY + "Shift + Linksklick: +10 Punkte");
            lore.add(ChatColor.GRAY + "Rechtsklick: -100 Punkte");
            lore.add(ChatColor.GRAY + "Shift + Rechtsklick: -10 Punkte");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Zurück");
        item.setItemMeta(meta);
        return item;
    }
}
