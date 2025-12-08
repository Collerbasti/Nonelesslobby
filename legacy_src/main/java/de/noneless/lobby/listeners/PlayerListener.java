package de.noneless.lobby.listeners;

import Config.ConfigManager;
import Config.GamemodeSettingsConfig;
import de.noneless.lobby.Main;
import de.noneless.lobby.Menues.FriendsMenu;
import de.noneless.lobby.Menues.Warps;
import de.noneless.lobby.scoreboard.LobbyScoreboard;
import de.noneless.lobby.util.GamemodeEnforcer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    private final FriendsMenu friendsMenu = new FriendsMenu();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        handleLobbyEntry(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> handleLobbyEntry(player), 1L);
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            GamemodeEnforcer.enforceImmediate(player);
            LobbyScoreboard.update(player);
        });
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            Player player = event.getPlayer();
            GamemodeEnforcer.enforceImmediate(player);
            LobbyScoreboard.update(player);
        });
    }
    
    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("NonelessLobby.bypassGamemode")) {
            return;
        }
        GameMode desired = getDesiredGamemode(player);
        if (event.getNewGameMode() != desired) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Dein Gamemode ist auf " + desired.name() + " fixiert.");
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        GamemodeEnforcer.clearPlayer(event.getPlayer());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), LobbyScoreboard::updateAll, 1L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (!isInLobbyWorld(player)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Material type = item.getType();
        if (type == Material.COMPASS) {
            event.setCancelled(true);
            openWarpsMenu(player);
        } else if (type == Material.BLAZE_POWDER) {
            event.setCancelled(true);
            friendsMenu.openMain(player);
        }
    }

    private void handleLobbyEntry(Player player) {
        Location lobbyLocation = ConfigManager.getLobbyLocation();
        if (lobbyLocation == null || lobbyLocation.getWorld() == null) {
            return;
        }
        if (!player.getWorld().equals(lobbyLocation.getWorld()) ||
            player.getLocation().distanceSquared(lobbyLocation) > 1.5) {
            player.teleport(lobbyLocation);
            player.sendMessage(ChatColor.GREEN + "Willkommen in der Lobby!");
        }
        GamemodeEnforcer.enforceImmediate(player);
        giveLobbyItems(player);
        LobbyScoreboard.update(player);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), LobbyScoreboard::updateAll, 1L);
    }
    
    private void applyGamemodeSetting(Player player) {
        if (player.hasPermission("NonelessLobby.bypassGamemode")) {
            return;
        }
        GameMode target = getDesiredGamemode(player);
        if (player.getGameMode() != target) {
            player.setGameMode(target);
            if (target == GameMode.CREATIVE) {
                player.sendMessage(ChatColor.GREEN + "Gamemode automatisch auf Creative eingestellt.");
            } else {
                player.sendMessage(ChatColor.GRAY + "Gamemode automatisch auf Adventure gewechselt.");
            }
        }
    }
    
    private GameMode getDesiredGamemode(Player player) {
        return GamemodeSettingsConfig.resolveGamemodeForPlayer(player.getUniqueId(), player.getWorld().getName());
    }

    private void giveLobbyItems(Player player) {
        player.getInventory().clear();

        ItemStack compass = createItem(Material.COMPASS, ChatColor.GOLD + "Navigator", ChatColor.GRAY + "Öffne das Warps-Menü");
        ItemStack chest = createItem(Material.CHEST, ChatColor.AQUA + "Kosmetika", ChatColor.GRAY + "Öffne deine Kosmetiken");
        ItemStack clock = createItem(Material.CLOCK, ChatColor.GREEN + "Lobby Selector", ChatColor.GRAY + "Wähle eine andere Lobby");
        ItemStack blazePowder = createItem(Material.BLAZE_POWDER, ChatColor.YELLOW + "Freunde", ChatColor.GRAY + "Verwalte deine Freunde");

        player.getInventory().setItem(0, compass);
        player.getInventory().setItem(1, chest);
        player.getInventory().setItem(4, clock);
        player.getInventory().setItem(8, blazePowder);
    }

    private ItemStack createItem(Material material, String displayName, String loreLine) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            if (loreLine != null && !loreLine.isEmpty()) {
                List<String> lore = new ArrayList<>();
                lore.add(loreLine);
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private void openWarpsMenu(Player player) {
        try {
            new Warps().Spawn(player);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Fehler beim Öffnen des Warps-Menüs.");
            Main.getInstance().getLogger().warning("Konnte Warps-Menü nicht öffnen: " + e.getMessage());
        }
    }

    private boolean isInLobbyWorld(Player player) {
        Location lobbyLocation = ConfigManager.getLobbyLocation();
        World lobbyWorld = lobbyLocation != null ? lobbyLocation.getWorld() : Bukkit.getWorld("world");
        if (lobbyWorld == null) {
            return false;
        }
        return player.getWorld().equals(lobbyWorld);
    }
}

