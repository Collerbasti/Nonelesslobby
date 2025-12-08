package de.noneless.lobby.Menues;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

import de.noneless.lobby.Main;

public class Settings {
    
    public void Spawn(Player player) {
        // Erstelle Settings-MenÃ¼ basierend auf dem originalen Design
        Inventory settings = player.getServer().createInventory(null, 27, 
                           player.getName() + " Settings");
        
        // Friend Teleport Setting (Slot 10)
        boolean allowFriendsTp = Main.Frdb.getBoolean(player.getName() + ".AllowFriendsTp", false);
        ItemStack friendsTpItem;
        ItemMeta friendsMeta;
        
        if (allowFriendsTp) {
            friendsTpItem = new ItemStack(Material.GREEN_WOOL, 1, (short) 1);
            friendsMeta = friendsTpItem.getItemMeta();
            friendsMeta.setDisplayName(ChatColor.GREEN + "Teleportieren von Freunden zu Einem Erlaubt");
        } else {
            friendsTpItem = new ItemStack(Material.RED_WOOL, 1, (short) 1);
            friendsMeta = friendsTpItem.getItemMeta();
            friendsMeta.setDisplayName(ChatColor.RED + "Teleportieren von Freunden zu Einem Verboten");
        }
        friendsTpItem.setItemMeta(friendsMeta);
        settings.setItem(10, friendsTpItem);

        // NPC-Chat Setting (Slot 11)
        boolean npcChatEnabled = Main.Frdb.getBoolean(player.getName() + ".NpcChatEnabled", true);
        ItemStack npcChatItem = new ItemStack(npcChatEnabled ? Material.GREEN_WOOL : Material.RED_WOOL);
        ItemMeta npcChatMeta = npcChatItem.getItemMeta();
        npcChatMeta.setDisplayName(npcChatEnabled
                ? ChatColor.GREEN + "NPC-Chatnachrichten sichtbar"
                : ChatColor.RED + "NPC-Chatnachrichten versteckt");
        List<String> npcLore = new ArrayList<>();
        npcLore.add(ChatColor.GRAY + "Steuert Chatnachrichten der NPCs.");
        npcLore.add(ChatColor.YELLOW + "Klicke zum Umschalten.");
        npcChatMeta.setLore(npcLore);
        npcChatItem.setItemMeta(npcChatMeta);
        settings.setItem(11, npcChatItem);
        
        // Coins Setting (Slot 12)
        boolean getCoins = Main.Frdb.getBoolean(player.getName() + ".GetCoins", false);
        ItemStack coinsItem;
        ItemMeta coinsMeta;
        
        if (getCoins) {
            coinsItem = new ItemStack(Material.GREEN_WOOL, 1, (short) 1);
            coinsMeta = coinsItem.getItemMeta();
            coinsMeta.setDisplayName(ChatColor.GREEN + "Bekomme 80 Coins pro Stunde");
        } else {
            coinsItem = new ItemStack(Material.RED_WOOL, 1, (short) 1);
            coinsMeta = coinsItem.getItemMeta();
            coinsMeta.setDisplayName(ChatColor.RED + "Bekomme einen Duellpunkt pro Stunde");
        }
        coinsItem.setItemMeta(coinsMeta);
        settings.setItem(12, coinsItem);
        
        // Gamemode Verwaltung (Slot 14) - nur für Admins
        if (player.hasPermission("Noneless.Admin.Gamemode")) {
            ItemStack gamemodeItem = new ItemStack(Material.NETHER_STAR);
            ItemMeta gamemodeMeta = gamemodeItem.getItemMeta();
            gamemodeMeta.setDisplayName(ChatColor.GOLD + "Gamemode Verwaltung");
            List<String> gmLore = new ArrayList<>();
            gmLore.add(ChatColor.GRAY + "Verwalte Standard-Gamemodes");
            gmLore.add(ChatColor.GRAY + "pro Welt und Creative-Override.");
            gmLore.add(ChatColor.YELLOW + "Klicke für das Admin-Menü.");
            gamemodeMeta.setLore(gmLore);
            gamemodeItem.setItemMeta(gamemodeMeta);
            settings.setItem(14, gamemodeItem);
        } else {
            ItemStack noPermItem = new ItemStack(Material.LIGHT_GRAY_WOOL, 1, (short) 1);
            ItemMeta noPermMeta = noPermItem.getItemMeta();
            noPermMeta.setDisplayName(ChatColor.GRAY + "Gamemode Verboten");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + "Nur für Admins zugänglich");
            noPermMeta.setLore(lore);
            noPermItem.setItemMeta(noPermMeta);
            settings.setItem(14, noPermItem);
        }
        
        // Admin Online Setting (Slot 16) - nur fÃ¼r Admins
        if (player.hasPermission("Noneless.Admin")) {
            boolean adminOnlineVisible = Main.AOnline.getBoolean(player.getName() + ".Enable", false);
            ItemStack adminItem;
            ItemMeta adminMeta;
            
            if (adminOnlineVisible) {
                adminItem = new ItemStack(Material.GREEN_WOOL, 1, (short) 1);
                adminMeta = adminItem.getItemMeta();
                adminMeta.setDisplayName(ChatColor.GREEN + "Im Befehl /Hilfe Anzeigen");
            } else {
                adminItem = new ItemStack(Material.RED_WOOL, 1, (short) 1);
                adminMeta = adminItem.getItemMeta();
                adminMeta.setDisplayName(ChatColor.RED + "im Befehl /Hilfe nicht Anzeigen");
            }
            adminItem.setItemMeta(adminMeta);
            settings.setItem(16, adminItem);
        }
        
        // NPC Verwaltung (Slot 20) - nur Admins
        if (player.hasPermission("nonelesslobby.npc.admin")) {
            ItemStack npcItem = new ItemStack(Material.NETHER_STAR);
            ItemMeta npcMeta = npcItem.getItemMeta();
            npcMeta.setDisplayName(ChatColor.DARK_AQUA + "NPC Verwaltung");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "NPCs neu spawnen, Namen & Chats bearbeiten");
            npcMeta.setLore(lore);
            npcItem.setItemMeta(npcMeta);
            settings.setItem(20, npcItem);
        }

        // Punkte Verwaltung (Slot 24) - nur Admins
        if (player.hasPermission("Noneless.Admin")) {
            ItemStack pointsItem = new ItemStack(Material.GOLD_BLOCK);
            ItemMeta pointsMeta = pointsItem.getItemMeta();
            pointsMeta.setDisplayName(ChatColor.GOLD + "Punkte Verwaltung");
            List<String> pointsLore = new ArrayList<>();
            pointsLore.add(ChatColor.GRAY + "Linksklick: +100 | Shift: +10");
            pointsLore.add(ChatColor.GRAY + "Rechtsklick: -100 | Shift: -10");
            pointsLore.add(ChatColor.YELLOW + "Öffnet die Spieler-Liste");
            pointsMeta.setLore(pointsLore);
            pointsItem.setItemMeta(pointsMeta);
            settings.setItem(24, pointsItem);
        } else {
            ItemStack noPermPoints = new ItemStack(Material.LIGHT_GRAY_WOOL);
            ItemMeta noPermMeta = noPermPoints.getItemMeta();
            noPermMeta.setDisplayName(ChatColor.DARK_GRAY + "Punkte Verwaltung");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Nur für Admins verfügbar");
            noPermMeta.setLore(lore);
            noPermPoints.setItemMeta(noPermMeta);
            settings.setItem(24, noPermPoints);
        }
        
        // ZurÃ¼ck-Button (Slot 22)
        ItemStack backItem = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "ZurÃ¼ck");
        backItem.setItemMeta(backMeta);
        settings.setItem(22, backItem);
        
        // Ã–ffne das MenÃ¼
        player.openInventory(settings);
    }
}


