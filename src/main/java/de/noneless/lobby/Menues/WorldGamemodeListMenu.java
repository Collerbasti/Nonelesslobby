package de.noneless.lobby.Menues;

import Config.GamemodeSettingsConfig;
import de.noneless.lobby.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class WorldGamemodeListMenu {

    public static final String TITLE = ChatColor.DARK_GREEN + "Welt Gamemodes";
    private static final int PAGE_SIZE = 45;
    private static final NamespacedKey META_KEY = new NamespacedKey(Main.getInstance(), "gm_meta");

    public void open(Player player, int page) {
        List<World> worlds = Bukkit.getWorlds();
        int totalPages = Math.max(1, (int) Math.ceil(worlds.size() / (double) PAGE_SIZE));
        page = Math.max(0, Math.min(page, totalPages - 1));
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        int startIndex = page * PAGE_SIZE;
        for (int slot = 0; slot < PAGE_SIZE; slot++) {
            int worldIndex = startIndex + slot;
            if (worldIndex >= worlds.size()) {
                break;
            }
            World world = worlds.get(worldIndex);
            inv.setItem(slot, createWorldItem(world, page));
        }

        if (page > 0) {
            inv.setItem(45, createNavItem(ChatColor.YELLOW + "<- Vorherige Seite", page - 1, "prev"));
        }
        if (page < totalPages - 1) {
            inv.setItem(53, createNavItem(ChatColor.YELLOW + "Naechste Seite ->", page + 1, "next"));
        }

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "Zurueck");
        applyMetadata(backMeta, "back:" + page);
        back.setItemMeta(backMeta);
        inv.setItem(49, back);

        ItemStack pageInfo = new ItemStack(Material.OAK_SIGN);
        ItemMeta infoMeta = pageInfo.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + "Seite " + (page + 1) + "/" + totalPages);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Klicke auf eine Welt");
        lore.add(ChatColor.GRAY + "um den Gamemode zu aendern.");
        infoMeta.setLore(lore);
        pageInfo.setItemMeta(infoMeta);
        inv.setItem(50, pageInfo);

        player.openInventory(inv);
    }

    private ItemStack createWorldItem(World world, int page) {
        ItemStack item = new ItemStack(materialForEnvironment(world.getEnvironment()));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + world.getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Standard: " + ChatColor.YELLOW +
                GamemodeSettingsConfig.resolveGamemodeForWorld(world.getName()).name());
        lore.add(ChatColor.DARK_GRAY + "Umgebung: " + world.getEnvironment().name());
        lore.add(ChatColor.YELLOW + "Klicke zum Bearbeiten");
        applyMetadata(meta, lore, "world:" + world.getName() + ";page:" + page);
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

    private Material materialForEnvironment(World.Environment environment) {
        switch (environment) {
            case NETHER:
                return Material.NETHERRACK;
            case THE_END:
                return Material.END_STONE;
            default:
                return Material.GRASS_BLOCK;
        }
    }

    private void applyMetadata(ItemMeta meta, String data) {
        applyMetadata(meta, null, data);
    }

    private void applyMetadata(ItemMeta meta, List<String> lore, String data) {
        if (meta == null || data == null) {
            return;
        }
        meta.setLocalizedName(data);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(META_KEY, PersistentDataType.STRING, data);
        List<String> updatedLore = lore == null
                ? (meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>())
                : new ArrayList<>(lore);
        updatedLore.add(ChatColor.BLACK + "META:" + data);
        meta.setLore(updatedLore);
    }
}