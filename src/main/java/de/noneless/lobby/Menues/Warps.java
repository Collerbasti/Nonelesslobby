package de.noneless.lobby.Menues;

import de.noneless.lobby.Main;
import de.noneless.lobby.util.LobbyAbilities;
import de.noneless.lobby.util.LobbyAbilities.Ability;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Warps {

    public static final String ACTION_ESSENTIALS = "essentials";
    public static final String ACTION_FRIENDS = "friends";
    public static final String ACTION_SETTINGS = "settings";
    public static final String ACTION_FOOD = "food";
    public static final String ACTION_SKYBLOCK = "skyblock";
    public static final String ACTION_GAMES = "games";
    public static final String ACTION_NONELESS_GAME_MENU = "noneless_game_menu";
    public static final String ACTION_GUIDE = "guide";
    public static final String ACTION_ABILITIES = "abilities";
    public static final String ACTION_BACK = "back";
    public static final String ACTION_ABILITY_CLEAR = "ability_clear";
    public static final String ACTION_ADMIN_ABILITIES = "ability_admin";
    public static final String ACTION_ABILITY_TOGGLE_PREFIX = "ability_toggle:";
    public static final String ACTION_ADMIN_PLAYER_PREFIX = "ability_admin_player:";
    public static final String ACTION_ADMIN_TOGGLE_PREFIX = "ability_admin_toggle:";

    private static NamespacedKey warpKey;

    public void Spawn(Player player) {
        Component title = Component.text(player.getName(), NamedTextColor.AQUA)
                .append(Component.text(" Noneless Lobby", NamedTextColor.GOLD));
        MenuHolder holder = new MenuHolder();
        Inventory menu = Bukkit.createInventory(holder, 54, title);
        holder.bind(menu);

        menu.setItem(4, createWarpItem(
                Material.BEACON,
                Component.text("Noneless Lobby", NamedTextColor.GOLD),
                List.of(Component.text("Zentrale Lobby-Navigation", NamedTextColor.GRAY)),
                null
        ));

        menu.setItem(10, createWarpItem(
                Material.ENDER_PEARL,
                Component.text("Essentials Warps", NamedTextColor.GOLD),
                List.of(Component.text("Alle Essentials-Warps anzeigen", NamedTextColor.GRAY)),
                ACTION_ESSENTIALS
        ));

        menu.setItem(16, createWarpItem(
                Material.GOLDEN_PICKAXE,
                Component.text("SkyBlock", NamedTextColor.GOLD),
                List.of(Component.text("Besuche die SkyBlock Welt", NamedTextColor.GRAY)),
                ACTION_SKYBLOCK
        ));

        menu.setItem(19, createFriendsItem(player));

        menu.setItem(22, createWarpItem(
                Material.FEATHER,
                Component.text("Lobby-Fähigkeiten", NamedTextColor.AQUA),
                List.of(Component.text("Elytra, Feuerwerke und Effekte", NamedTextColor.GRAY)),
                ACTION_ABILITIES
        ));

        menu.setItem(25, createWarpItem(
                Material.BAKED_POTATO,
                Component.text("Essen", NamedTextColor.GREEN),
                List.of(Component.text("Stille deinen Hunger", NamedTextColor.GRAY)),
                ACTION_FOOD
        ));

        menu.setItem(28, createWarpItem(
                Material.NETHER_STAR,
                Component.text("NonelessGame", NamedTextColor.RED),
                List.of(Component.text("Öffne das Spiel-Menü", NamedTextColor.GRAY)),
                ACTION_NONELESS_GAME_MENU
        ));

        // Guide-Buch nur anzeigen wenn NonelessGame geladen ist
        if (Bukkit.getPluginManager().isPluginEnabled("NonelessGame")) {
            menu.setItem(34, createWarpItem(
                    Material.WRITTEN_BOOK,
                    Component.text("Kartenspiel-Anleitung", NamedTextColor.GOLD),
                    List.of(
                            Component.text("Grundlagen des Kartenspiels", NamedTextColor.GRAY),
                            Component.text("als Buch erhalten", NamedTextColor.GRAY)
                    ),
                    ACTION_GUIDE
            ));
        }

        menu.setItem(49, createWarpItem(
                Material.COMPASS,
                Component.text("Einstellungen", NamedTextColor.YELLOW),
                List.of(Component.text("Öffne die Einstellungen", NamedTextColor.GRAY)),
                ACTION_SETTINGS
        ));

        fillWithGlass(menu);
        player.openInventory(menu);
    }

    public void openAbilities(Player player) {
        Component title = Component.text("Lobby-Fähigkeiten", NamedTextColor.AQUA);
        MenuHolder holder = new MenuHolder();
        Inventory menu = Bukkit.createInventory(holder, 27, title);
        holder.bind(menu);

        int slot = 10;
        for (Ability ability : Ability.values()) {
            menu.setItem(slot++, createAbilityToggleItem(player, ability));
        }

        menu.setItem(16, createWarpItem(
                Material.BARRIER,
                Component.text("Fähigkeiten entfernen", NamedTextColor.RED),
                List.of(Component.text("Entfernt Lobby-Effekte und Items", NamedTextColor.GRAY)),
                ACTION_ABILITY_CLEAR
        ));

        if (player.hasPermission("nonelesslobby.abilities.admin")) {
            menu.setItem(18, createWarpItem(
                    Material.NETHER_STAR,
                    Component.text("Admin-Freischaltungen", NamedTextColor.GOLD),
                    List.of(Component.text("Spieler-Fähigkeiten verwalten", NamedTextColor.GRAY)),
                    ACTION_ADMIN_ABILITIES
            ));
        }

        menu.setItem(22, createWarpItem(
                Material.ARROW,
                Component.text("Zurück", NamedTextColor.GRAY),
                List.of(Component.text("Zum Warps-Menü", NamedTextColor.DARK_GRAY)),
                ACTION_BACK
        ));

        fillWithGlass(menu);
        player.openInventory(menu);
    }

    public void openAbilityAdminPlayers(Player admin) {
        Component title = Component.text("Fähigkeiten: Spieler", NamedTextColor.GOLD);
        MenuHolder holder = new MenuHolder();
        Inventory menu = Bukkit.createInventory(holder, 54, title);
        holder.bind(menu);

        int slot = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (slot >= 45) break;
            menu.setItem(slot++, createWarpItem(
                    Material.PLAYER_HEAD,
                    Component.text(target.getName(), NamedTextColor.AQUA),
                    List.of(Component.text("Freischaltungen bearbeiten", NamedTextColor.GRAY)),
                    ACTION_ADMIN_PLAYER_PREFIX + target.getUniqueId()
            ));
        }

        menu.setItem(49, createWarpItem(
                Material.ARROW,
                Component.text("Zurück", NamedTextColor.GRAY),
                List.of(Component.text("Zu Lobby-Fähigkeiten", NamedTextColor.DARK_GRAY)),
                ACTION_ABILITIES
        ));

        fillWithGlass(menu);
        admin.openInventory(menu);
    }

    public void openAbilityAdminPlayer(Player admin, UUID targetId) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetId);
        Component title = Component.text("Freischalten: ", NamedTextColor.GOLD)
                .append(Component.text(target.getName() != null ? target.getName() : targetId.toString(), NamedTextColor.AQUA));
        MenuHolder holder = new MenuHolder();
        Inventory menu = Bukkit.createInventory(holder, 27, title);
        holder.bind(menu);

        int slot = 10;
        for (Ability ability : Ability.values()) {
            boolean granted = LobbyAbilities.isAbilityGranted(targetId, ability);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Freigabe: ", NamedTextColor.GRAY)
                    .append(Component.text(granted ? "AN" : "AUS", granted ? NamedTextColor.GREEN : NamedTextColor.RED)));
            lore.add(Component.text("Klick: Freigabe umschalten", NamedTextColor.YELLOW));
            menu.setItem(slot++, createWarpItem(
                    granted ? ability.getIcon() : Material.GRAY_DYE,
                    Component.text(ability.getDisplayName(), granted ? NamedTextColor.GREEN : NamedTextColor.RED),
                    lore,
                    ACTION_ADMIN_TOGGLE_PREFIX + targetId + ":" + ability.getId()
            ));
        }

        menu.setItem(22, createWarpItem(
                Material.ARROW,
                Component.text("Zurück", NamedTextColor.GRAY),
                List.of(Component.text("Zur Spielerauswahl", NamedTextColor.DARK_GRAY)),
                ACTION_ADMIN_ABILITIES
        ));

        fillWithGlass(menu);
        admin.openInventory(menu);
    }

    public static boolean isWarpsInventory(InventoryView view) {
        if (view == null) {
            return false;
        }
        Inventory top = view.getTopInventory();
        return top != null && top.getHolder() instanceof MenuHolder;
    }

    public static NamespacedKey getWarpKey() {
        if (warpKey == null) {
            Main plugin = Main.getInstance();
            if (plugin == null) {
                throw new IllegalStateException("Warps menu requested before plugin initialisation.");
            }
            warpKey = new NamespacedKey(plugin, "warps-menu");
        }
        return warpKey;
    }

    private ItemStack createWarpItem(Material material, Component displayName, List<Component> lore, String actionId) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(displayName);
        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore);
        }
        if (actionId != null) {
            meta.getPersistentDataContainer().set(getWarpKey(), PersistentDataType.STRING, actionId);
        }
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createAbilityToggleItem(Player player, Ability ability) {
        boolean granted = LobbyAbilities.isAbilityGranted(player.getUniqueId(), ability);
        boolean active = LobbyAbilities.isActive(player.getUniqueId(), ability);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Freigabe: ", NamedTextColor.GRAY)
                .append(Component.text(granted ? "erteilt" : "nicht erteilt", granted ? NamedTextColor.GREEN : NamedTextColor.RED)));
        if (granted) {
            lore.add(Component.text("Status: ", NamedTextColor.GRAY)
                    .append(Component.text(active ? "aktiv" : "inaktiv", active ? NamedTextColor.GREEN : NamedTextColor.RED)));
            lore.add(Component.text("Klick: ein-/ausschalten", NamedTextColor.YELLOW));
        } else {
            lore.add(Component.text("Ein Admin muss diese Fähigkeit freischalten", NamedTextColor.DARK_GRAY));
        }
        return createWarpItem(
                granted ? ability.getIcon() : Material.GRAY_DYE,
                Component.text(ability.getDisplayName(), granted ? NamedTextColor.AQUA : NamedTextColor.DARK_GRAY),
                lore,
                ACTION_ABILITY_TOGGLE_PREFIX + ability.getId()
        );
    }

    private ItemStack createFriendsItem(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
            skullMeta.displayName(Component.text("Freunde", NamedTextColor.AQUA));
            skullMeta.lore(List.of(Component.text("Verwalte deine Freunde", NamedTextColor.GRAY)));
            skullMeta.getPersistentDataContainer().set(getWarpKey(), PersistentDataType.STRING, ACTION_FRIENDS);
            item.setItemMeta(skullMeta);
            return item;
        }
        return createWarpItem(
                Material.PLAYER_HEAD,
                Component.text("Freunde", NamedTextColor.AQUA),
                List.of(Component.text("Verwalte deine Freunde", NamedTextColor.GRAY)),
                ACTION_FRIENDS
        );
    }

    private void fillWithGlass(Inventory menu) {
        ItemStack filler = createWarpItem(
                Material.BLACK_STAINED_GLASS_PANE,
                Component.text(" ", NamedTextColor.DARK_GRAY),
                null,
                null
        );
        for (int i = 0; i < menu.getSize(); i++) {
            if (menu.getItem(i) == null) {
                menu.setItem(i, filler.clone());
            }
        }
    }

    private static final class MenuHolder implements InventoryHolder {
        private Inventory inventory;

        void bind(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
