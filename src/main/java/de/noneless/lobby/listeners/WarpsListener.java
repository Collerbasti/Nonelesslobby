package de.noneless.lobby.listeners;

import de.noneless.lobby.Menues.Settings;
import de.noneless.lobby.Menues.Warps;
import de.noneless.lobby.util.GameGuideBook;
import de.noneless.lobby.util.LobbyAbilities;
import de.noneless.lobby.util.LobbyAbilities.Ability;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class WarpsListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!Warps.isWarpsInventory(event.getView())) {
            return;
        }

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();
        NamespacedKey warpKey = Warps.getWarpKey();
        String action = meta.getPersistentDataContainer().get(warpKey, PersistentDataType.STRING);
        if (action == null) {
            return;
        }

        handleAction(player, action);
    }

    private void handleAction(Player player, String action) {
        switch (action) {
            case Warps.ACTION_ESSENTIALS -> {
                player.closeInventory();
                new de.noneless.lobby.Menues.EssentialsWarpMenu().open(player, 0);
            }
            case Warps.ACTION_FRIENDS -> {
                player.closeInventory();
                player.performCommand("friend");
            }
            case Warps.ACTION_SETTINGS -> {
                player.closeInventory();
                try {
                    new Settings().Spawn(player);
                } catch (Exception ex) {
                    player.sendMessage(ChatColor.RED + "Fehler beim Oeffnen der Einstellungen!");
                }
            }
            case Warps.ACTION_FOOD -> {
                player.closeInventory();
                player.setFoodLevel(20);
                player.setSaturation(20.0f);
                player.sendMessage(ChatColor.GREEN + "Du wurdest gesaettigt!");
            }
            case Warps.ACTION_SKYBLOCK -> {
                player.closeInventory();
                player.sendMessage(ChatColor.GOLD + "Teleportiere zu SkyBlock...");
                player.sendMessage(ChatColor.GRAY + "SkyBlock-Teleportation wird implementiert!");
            }
            case Warps.ACTION_GAMES -> {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Oeffne Games-Menue...");
                player.sendMessage(ChatColor.GRAY + "Games-Menue wird implementiert!");
            }
            case Warps.ACTION_GUIDE -> {
                player.closeInventory();
                player.openBook(GameGuideBook.create());
            }
            case Warps.ACTION_ABILITIES -> new Warps().openAbilities(player);
            case Warps.ACTION_BACK -> new Warps().Spawn(player);
            case Warps.ACTION_ABILITY_CLEAR -> {
                player.closeInventory();
                LobbyAbilities.clear(player);
            }
            case Warps.ACTION_ADMIN_ABILITIES -> {
                if (!player.hasPermission("nonelesslobby.abilities.admin")) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung fuer diese Verwaltung.");
                    return;
                }
                new Warps().openAbilityAdminPlayers(player);
            }
            default -> {
                handleDynamicAction(player, action);
            }
        }
    }

    private void handleDynamicAction(Player player, String action) {
        if (action.startsWith(Warps.ACTION_ABILITY_TOGGLE_PREFIX)) {
            String abilityId = action.substring(Warps.ACTION_ABILITY_TOGGLE_PREFIX.length());
            Ability ability = Ability.fromId(abilityId);
            if (ability == null) {
                return;
            }
            LobbyAbilities.toggleForPlayer(player, ability);
            new Warps().openAbilities(player);
            return;
        }

        if (action.startsWith(Warps.ACTION_ADMIN_PLAYER_PREFIX)) {
            if (!player.hasPermission("nonelesslobby.abilities.admin")) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung fuer diese Verwaltung.");
                return;
            }
            UUID targetId = parseUuid(action.substring(Warps.ACTION_ADMIN_PLAYER_PREFIX.length()));
            if (targetId != null) {
                new Warps().openAbilityAdminPlayer(player, targetId);
            }
            return;
        }

        if (action.startsWith(Warps.ACTION_ADMIN_TOGGLE_PREFIX)) {
            if (!player.hasPermission("nonelesslobby.abilities.admin")) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung fuer diese Verwaltung.");
                return;
            }
            String rest = action.substring(Warps.ACTION_ADMIN_TOGGLE_PREFIX.length());
            String[] parts = rest.split(":", 2);
            if (parts.length != 2) {
                return;
            }
            UUID targetId = parseUuid(parts[0]);
            Ability ability = Ability.fromId(parts[1]);
            if (targetId == null || ability == null) {
                return;
            }
            boolean granted = LobbyAbilities.isAbilityGranted(targetId, ability);
            LobbyAbilities.setAbilityGranted(targetId, ability, !granted);
            player.sendMessage((granted ? ChatColor.RED + "Entzogen: " : ChatColor.GREEN + "Freigeschaltet: ")
                    + ability.getDisplayName());
            new Warps().openAbilityAdminPlayer(player, targetId);
        }
    }

    private UUID parseUuid(String raw) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
