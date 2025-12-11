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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {

    private final FriendsMenu friendsMenu = new FriendsMenu();
    
    // Track if we're currently setting gamemode to avoid recursion
    private static final java.util.Set<java.util.UUID> settingGamemode = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Enforce gamemode immediately on join
        forceCorrectGamemode(player);
        handleLobbyEntry(player);
        
        // Double-check after a short delay
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (player.isOnline()) {
                forceCorrectGamemode(player);
            }
        }, 5L);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            forceCorrectGamemode(player);
            handleLobbyEntry(player);
        }, 1L);
    }
    
    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        
        // Check if teleporting to a different world
        Location from = event.getFrom();
        Location to = event.getTo();
        boolean worldChange = (to != null && from != null && 
                              to.getWorld() != null && from.getWorld() != null &&
                              !to.getWorld().equals(from.getWorld()));
        
        // For world changes, we need to enforce the gamemode for the TARGET world
        if (worldChange && to != null && to.getWorld() != null) {
            final String targetWorld = to.getWorld().getName();
            
            // Multiple delayed checks to ensure gamemode is correct
            for (int delay : new int[]{1, 5, 10, 20}) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    if (player.isOnline()) {
                        forceGamemodeForWorld(player, targetWorld);
                        LobbyScoreboard.update(player);
                    }
                }, delay);
            }
        } else {
            // Same world teleport - quick check
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (player.isOnline()) {
                    forceCorrectGamemode(player);
                    LobbyScoreboard.update(player);
                }
            }, 2L);
        }
    }
    
    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String newWorld = player.getWorld().getName();
        
        // Immediate enforcement for new world
        forceGamemodeForWorld(player, newWorld);
        
        // Multiple delayed checks for safety
        for (int delay : new int[]{1, 5, 10, 20}) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (player.isOnline()) {
                    forceCorrectGamemode(player);
                    LobbyScoreboard.update(player);
                }
            }, delay);
        }
    }
    
    @EventHandler(priority = org.bukkit.event.EventPriority.LOWEST, ignoreCancelled = false)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        
        // Skip if we're the ones setting it
        if (settingGamemode.contains(player.getUniqueId())) {
            return;
        }
        
        // Gamemode wird für ALLE erzwungen - kein Bypass
        
        GameMode desired = getDesiredGamemode(player);
        GameMode newMode = event.getNewGameMode();
        
        // Block ANY change that doesn't match desired gamemode
        if (newMode != desired) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Gamemode-Wechsel nicht erlaubt! Dein Gamemode ist " + desired.name() + ".");
            
            // Force correct gamemode after a tick (in case something bypassed)
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (player.isOnline() && player.getGameMode() != desired) {
                    forceCorrectGamemode(player);
                }
            }, 1L);
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        GamemodeEnforcer.clearPlayer(event.getPlayer());
        lobbyWarningCooldown.remove(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), LobbyScoreboard::updateAll, 1L);
    }
    
    // Cooldown für Lobby-Warnung (verhindert Spam)
    private static final Map<UUID, Long> lobbyWarningCooldown = new ConcurrentHashMap<>();
    private static final long WARNING_COOLDOWN_MS = 3000; // 3 Sekunden Cooldown
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        // Warnung zeigen wenn in Lobby (aber Bauen erlaubt)
        if (isInLobbyWorld(player)) {
            showLobbyWarning(player, "abbauen");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        // Warnung zeigen wenn in Lobby (aber Bauen erlaubt)
        if (isInLobbyWorld(player)) {
            showLobbyWarning(player, "platzieren");
        }
    }
    
    /**
     * Zeigt eine große Title-Warnung an, dass man in der Lobby ist.
     */
    private void showLobbyWarning(Player player, String action) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long lastWarning = lobbyWarningCooldown.get(uuid);
        
        // Cooldown prüfen um Spam zu vermeiden
        if (lastWarning != null && (now - lastWarning) < WARNING_COOLDOWN_MS) {
            return;
        }
        
        lobbyWarningCooldown.put(uuid, now);
        
        // Lauter Warn-Sound
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.5f);
        
        // Große Title-Nachricht
        player.sendTitle(
            ChatColor.RED + "" + ChatColor.BOLD + "⚠ ACHTUNG ⚠",
            ChatColor.GOLD + "" + ChatColor.BOLD + "Du baust gerade in der LOBBY!",
            5,   // fadeIn (0.25s)
            80,  // stay (4s)
            20   // fadeOut (1s)
        );
        
        // Zusätzliche Chat-Nachricht
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "⚠ WARNUNG: " + ChatColor.YELLOW + "Du baust in der Lobby-Welt!");
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
        // Force correct gamemode
        forceCorrectGamemode(player);
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
    
    /**
     * Erzwingt den korrekten Gamemode für den Spieler basierend auf seiner aktuellen Welt.
     */
    private void forceCorrectGamemode(Player player) {
        if (player == null || !player.isOnline()) return;
        // Gamemode wird für ALLE erzwungen
        
        GameMode target = getDesiredGamemode(player);
        if (target == null) target = GameMode.ADVENTURE;
        
        if (player.getGameMode() != target) {
            try {
                settingGamemode.add(player.getUniqueId());
                player.setGameMode(target);
            } finally {
                // Remove after a short delay to ensure event processing is complete
                final GameMode finalTarget = target;
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    settingGamemode.remove(player.getUniqueId());
                }, 2L);
            }
        }
    }
    
    /**
     * Erzwingt den Gamemode für eine bestimmte Zielwelt (für Teleports).
     */
    private void forceGamemodeForWorld(Player player, String worldName) {
        if (player == null || !player.isOnline() || worldName == null) return;
        // Gamemode wird für ALLE erzwungen
        
        GameMode target = GamemodeSettingsConfig.resolveGamemodeForPlayer(player.getUniqueId(), worldName);
        if (target == null) target = GameMode.ADVENTURE;
        
        if (player.getGameMode() != target) {
            try {
                settingGamemode.add(player.getUniqueId());
                player.setGameMode(target);
            } finally {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    settingGamemode.remove(player.getUniqueId());
                }, 2L);
            }
        }
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

    /**
     * Teleportiert Spieler zur Lobby wenn sie in der Lobby-Welt unter Y=10 fallen.
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Nur prüfen wenn sich Y-Position geändert hat (Performance)
        if (event.getFrom().getBlockY() == event.getTo().getBlockY()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Nur in der Lobby-Welt
        if (!isInLobbyWorld(player)) {
            return;
        }
        
        // Unter Y=10 gefallen?
        if (event.getTo().getY() < 10) {
            // /hub Command ausführen
            player.performCommand("hub");
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

