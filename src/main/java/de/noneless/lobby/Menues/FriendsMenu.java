package de.noneless.lobby.Menues;

import friends.FriendManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class FriendsMenu {

    public static final String MAIN_TITLE = ChatColor.AQUA + "Deine Freunde";
    public static final String ADD_TITLE = ChatColor.GREEN + "Freund hinzufügen";
    public static final String REQUEST_TITLE = ChatColor.YELLOW + "Freundes-Anfragen";

    public void openMain(Player player) {
        player.openInventory(buildMainInventory(player));
    }

    public void openAdd(Player player) {
        player.openInventory(buildAddInventory(player));
    }

    public void openRequests(Player player) {
        player.openInventory(buildRequestInventory(player));
    }

    private Inventory buildMainInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, MAIN_TITLE);

        List<String> friends = FriendManager.getFriends(player.getName());
        int index = 0;
        for (String friendName : friends) {
            if (index >= 45) break;
            ItemStack head = createFriendHead(friendName, player);
            inv.setItem(index, head);
            index++;
        }

        inv.setItem(45, createSimpleItem(Material.EMERALD, ChatColor.GREEN + "Freund hinzufügen", ChatColor.GRAY + "Klicke, um einen Freund auszuwählen"));
        inv.setItem(46, createSimpleItem(Material.BOOK, ChatColor.YELLOW + "Anfragen ansehen", ChatColor.GRAY + "Akzeptiere oder lehne Anfragen ab"));
        inv.setItem(49, createSimpleItem(Material.BLAZE_POWDER, ChatColor.AQUA + "Freunde aktualisieren", ChatColor.GRAY + "Aktualisiere die Liste"));
        inv.setItem(53, createSimpleItem(Material.BARRIER, ChatColor.RED + "Schließen", ChatColor.GRAY + "Schließe das Menü"));

        fillEmpty(inv);
        return inv;
    }

    private Inventory buildAddInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ADD_TITLE);
        List<String> friends = FriendManager.getFriends(player.getName());

        int slot = 0;
        for (Player possible : Bukkit.getOnlinePlayers()) {
            if (possible.equals(player)) continue;
            if (friends.stream().anyMatch(existing -> existing.equalsIgnoreCase(possible.getName()))) continue;
            if (slot >= 45) break;
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(possible);
            meta.setDisplayName(ChatColor.YELLOW + possible.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Klicke, um diesen Spieler hinzuzufügen.");
            meta.setLore(lore);
            head.setItemMeta(meta);
            inv.setItem(slot++, head);
        }

        inv.setItem(49, createSimpleItem(Material.ARROW, ChatColor.GREEN + "Zurück", ChatColor.GRAY + "Zurück zur Übersicht"));
        inv.setItem(53, createSimpleItem(Material.BARRIER, ChatColor.RED + "Schließen", ChatColor.GRAY + "Schließe das Menü"));
        fillEmpty(inv);
        return inv;
    }

    private Inventory buildRequestInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, REQUEST_TITLE);
        List<String> requests = FriendManager.getPendingRequests(player.getName());

        int slot = 0;
        for (String requester : requests) {
            if (slot >= 18) break;
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + requester);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Linksklick: Anfrage akzeptieren");
            lore.add(ChatColor.RED + "Rechtsklick: Anfrage ablehnen");
            meta.setLore(lore);
            head.setItemMeta(meta);
            inv.setItem(slot++, head);
        }

        inv.setItem(22, createSimpleItem(Material.ARROW, ChatColor.GREEN + "Zurück", ChatColor.GRAY + "Zurück zur Übersicht"));
        inv.setItem(26, createSimpleItem(Material.BARRIER, ChatColor.RED + "Schließen", ChatColor.GRAY + "Schließe das Menü"));
        fillEmpty(inv);
        return inv;
    }

    private ItemStack createFriendHead(String friendName, Player viewer) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + friendName);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Linksklick: Teleportieren (wenn erlaubt)");
        lore.add(ChatColor.GRAY + "Rechtsklick: Freund entfernen");
        meta.setLore(lore);
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createSimpleItem(Material material, String name, String loreLine) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (loreLine != null && !loreLine.isEmpty()) {
            List<String> lore = new ArrayList<>();
            lore.add(loreLine);
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    private void fillEmpty(Inventory inventory) {
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
}
