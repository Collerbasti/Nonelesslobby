package de.noneless.lobby.Menues;

import de.noneless.lobby.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EssentialsWarpMenu {

    private static final int MENU_SIZE = 54;
    private static final int ITEMS_PER_PAGE = 45;
    private static final String NAV_PREVIOUS = "nav:previous";
    private static final String NAV_NEXT = "nav:next";
    private static final String NAV_BACK = "nav:back";
    private static NamespacedKey dataKey;

    public void open(Player player, int page) {
        Main plugin = Main.getInstance();
        if (plugin == null) {
            player.sendMessage(Component.text("Plugin ist noch nicht geladen.", NamedTextColor.RED));
            return;
        }

        List<String> warps = loadEssentialsWarpNames();
        int maxPage = Math.max(0, (warps.size() - 1) / ITEMS_PER_PAGE);
        int safePage = Math.max(0, Math.min(page, maxPage));
        int startIndex = safePage * ITEMS_PER_PAGE;
        int endIndex = Math.min(warps.size(), startIndex + ITEMS_PER_PAGE);

        Component title = Component.text("Essentials Warps ", NamedTextColor.AQUA)
                .append(Component.text("(" + (safePage + 1) + "/" + (maxPage + 1) + ")", NamedTextColor.GRAY));

        MenuHolder holder = new MenuHolder(safePage);
        Inventory menu = Bukkit.createInventory(holder, MENU_SIZE, title);
        holder.bind(menu);

        ItemStack filler = buildItem(Material.GRAY_STAINED_GLASS_PANE, Component.text(" ", NamedTextColor.DARK_GRAY), null, null);
        for (int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, filler.clone());
        }

        List<String> visibleWarps = warps.subList(startIndex, endIndex);
        for (int slot = 0; slot < visibleWarps.size(); slot++) {
            String warpName = visibleWarps.get(slot);
            Component display = Component.text(warpName, NamedTextColor.GOLD);
            menu.setItem(slot, buildItem(Material.ENDER_EYE, display,
                    List.of(Component.text("Teleportiere zu " + warpName, NamedTextColor.GRAY)),
                    "warp:" + warpName));
        }

        if (safePage > 0) {
            menu.setItem(45, buildItem(Material.ARROW, Component.text("Vorherige Seite", NamedTextColor.YELLOW),
                    null, NAV_PREVIOUS));
        }

        menu.setItem(49, buildItem(Material.BARRIER, Component.text("Zurück", NamedTextColor.RED),
                List.of(Component.text("Zurück zum Warps-Menü", NamedTextColor.GRAY)), NAV_BACK));

        if (safePage < maxPage) {
            menu.setItem(53, buildItem(Material.ARROW, Component.text("Nächste Seite", NamedTextColor.YELLOW),
                    null, NAV_NEXT));
        }

        player.openInventory(menu);
    }

    public static boolean isEssentialsWarpInventory(InventoryView view) {
        if (view == null) {
            return false;
        }
        Inventory top = view.getTopInventory();
        return top != null && top.getHolder() instanceof MenuHolder;
    }

    public static NamespacedKey getDataKey() {
        if (dataKey == null) {
            Main plugin = Main.getInstance();
            if (plugin == null) {
                throw new IllegalStateException("EssentialsWarpMenu requested before plugin initialisation.");
            }
            dataKey = new NamespacedKey(plugin, "essentials-warp-menu");
        }
        return dataKey;
    }

    private ItemStack buildItem(Material material, Component displayName, List<Component> lore, String dataValue) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }
        meta.displayName(displayName);
        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore);
        }
        if (dataValue != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(getDataKey(), PersistentDataType.STRING, dataValue);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    private List<String> loadEssentialsWarpNames() {
        Main plugin = Main.getInstance();
        if (plugin == null) {
            return Collections.emptyList();
        }
        File pluginDir = plugin.getDataFolder().getParentFile();
        File warpsDir = new File(pluginDir, "Essentials/warps");
        if (!warpsDir.exists() || !warpsDir.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = warpsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        List<String> names = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();
            if (name.toLowerCase().endsWith(".yml")) {
                name = name.substring(0, name.length() - 4);
            }
            names.add(name);
        }

        names.sort(Comparator.naturalOrder());
        return names;
    }

    public static final class MenuHolder implements InventoryHolder {
        private final int page;
        private Inventory inventory;

        private MenuHolder(int page) {
            this.page = page;
        }

        public int getPage() {
            return page;
        }

        void bind(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
