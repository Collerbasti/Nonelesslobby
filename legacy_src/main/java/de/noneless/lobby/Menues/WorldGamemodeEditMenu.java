package de.noneless.lobby.Menues;

import Config.GamemodeSettingsConfig;
import de.noneless.lobby.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class WorldGamemodeEditMenu {

    public static final String TITLE_PREFIX = ChatColor.GOLD + "Gamemode: ";
    private static final Map<GameMode, Material> MODE_ICONS = new EnumMap<>(GameMode.class);
    private static final NamespacedKey META_KEY = new NamespacedKey(Main.getInstance(), "gm_meta");

    static {
        MODE_ICONS.put(GameMode.SURVIVAL, Material.IRON_SWORD);
        MODE_ICONS.put(GameMode.CREATIVE, Material.GRASS_BLOCK);
        MODE_ICONS.put(GameMode.ADVENTURE, Material.MAP);
        MODE_ICONS.put(GameMode.SPECTATOR, Material.ENDER_EYE);
    }

    public void open(Player player, String worldName, int returnPage) {
        Inventory inventory = Bukkit.createInventory(null, 27, TITLE_PREFIX + worldName);
        GameMode current = GamemodeSettingsConfig.resolveGamemodeForWorld(worldName);
        int slot = 10;
        for (GameMode mode : GameMode.values()) {
            ItemStack item = createModeItem(worldName, mode, current, returnPage);
            inventory.setItem(slot, item);
            slot += 2;
        }

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "Zurueck");
        applyMetadata(backMeta, "back:" + returnPage);
        back.setItemMeta(backMeta);
        inventory.setItem(22, back);

        player.openInventory(inventory);
    }

    private ItemStack createModeItem(String worldName, GameMode mode, GameMode current, int page) {
        Material icon = MODE_ICONS.getOrDefault(mode, Material.BOOK);
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        boolean active = current == mode;
        meta.setDisplayName((active ? ChatColor.GREEN : ChatColor.YELLOW) + mode.name());
        List<String> lore = new ArrayList<>();
        lore.add(active ? ChatColor.GREEN + "Aktueller Standard" : ChatColor.GRAY + "Klicke um zu setzen");
        applyMetadata(meta, lore, "mode:" + mode.name() + ";world:" + worldName + ";page:" + page);
        item.setItemMeta(meta);
        return item;
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