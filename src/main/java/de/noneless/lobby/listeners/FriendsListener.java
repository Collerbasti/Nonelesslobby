package de.noneless.lobby.listeners;

import de.noneless.lobby.Main;
import de.noneless.lobby.Menues.FriendsMenu;
import friends.FriendManager;
import friends.FriendManager.FriendRequestResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class FriendsListener implements Listener {

    private final FriendsMenu friendsMenu = new FriendsMenu();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();

        if (title.equals(FriendsMenu.MAIN_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();

            if (type == Material.PLAYER_HEAD) {
                handleFriendHeadClick(player, clicked, event.isLeftClick(), event.isRightClick());
            } else if (type == Material.EMERALD) {
                friendsMenu.openAdd(player);
            } else if (type == Material.BOOK) {
                friendsMenu.openRequests(player);
            } else if (type == Material.BARRIER) {
                player.closeInventory();
            } else if (type == Material.BLAZE_POWDER) {
                friendsMenu.openMain(player);
            }
        } else if (title.equals(FriendsMenu.ADD_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            if (type == Material.PLAYER_HEAD) {
                addFriendFromHead(player, clicked);
            } else if (type == Material.ARROW) {
                friendsMenu.openMain(player);
            } else if (type == Material.BARRIER) {
                player.closeInventory();
            }
        } else if (title.equals(FriendsMenu.REQUEST_TITLE)) {
            event.setCancelled(true);
            if (clicked == null || !clicked.hasItemMeta()) return;
            Material type = clicked.getType();
            if (type == Material.PLAYER_HEAD) {
                handleRequestHead(player, clicked, event.isLeftClick(), event.isRightClick());
            } else if (type == Material.ARROW) {
                friendsMenu.openMain(player);
            } else if (type == Material.BARRIER) {
                player.closeInventory();
            }
        }
    }

    private void handleFriendHeadClick(Player player, ItemStack item, boolean leftClick, boolean rightClick) {
        String rawName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (rawName == null || rawName.isEmpty()) return;
        if (leftClick) {
            attemptTeleport(player, rawName);
        } else if (rightClick) {
            FriendManager.removeFriend(player.getName(), rawName);
            player.sendMessage(ChatColor.RED + rawName + " wurde aus deiner Freundesliste entfernt.");
            friendsMenu.openMain(player);
        }
    }

    private void attemptTeleport(Player player, String targetName) {
        if (!FriendManager.areFriends(player.getName(), targetName)) {
            player.sendMessage(ChatColor.RED + "Ihr seid keine Freunde.");
            return;
        }

        boolean allowed = Main.Frdb.getBoolean(targetName + ".AllowFriendsTp", false);
        if (!allowed) {
            player.sendMessage(ChatColor.RED + targetName + " erlaubt keine Teleports von Freunden.");
            return;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.YELLOW + targetName + " ist nicht online.");
            return;
        }

        player.teleport(target.getLocation());
        player.sendMessage(ChatColor.GREEN + "Du wurdest zu " + targetName + " teleportiert.");
    }

    private void addFriendFromHead(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String targetName = ChatColor.stripColor(meta.getDisplayName());
        if (targetName == null || targetName.isEmpty()) return;

        FriendRequestResult result = FriendManager.sendRequest(player.getName(), targetName);
        switch (result) {
            case SENT:
                player.sendMessage(ChatColor.GREEN + "Anfrage an " + targetName + " gesendet.");
                Player target = Bukkit.getPlayerExact(targetName);
                if (target != null && target.isOnline()) {
                    target.sendMessage(ChatColor.AQUA + player.getName() + " möchte dein Freund sein. Öffne das Freunde-Menü, um anzunehmen.");
                }
                break;
            case ALREADY_FRIENDS:
                player.sendMessage(ChatColor.YELLOW + "Ihr seid bereits Freunde.");
                break;
            case ALREADY_PENDING:
                player.sendMessage(ChatColor.YELLOW + "Es besteht bereits eine Anfrage zwischen euch.");
                break;
            case SELF:
                player.sendMessage(ChatColor.RED + "Du kannst dich nicht selbst hinzufügen.");
                break;
        }
        friendsMenu.openMain(player);
    }

    private void handleRequestHead(Player player, ItemStack item, boolean leftClick, boolean rightClick) {
        String requester = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (requester == null || requester.isEmpty()) return;

        if (leftClick) {
            if (FriendManager.acceptRequest(player.getName(), requester)) {
                player.sendMessage(ChatColor.GREEN + "Du bist jetzt mit " + requester + " befreundet.");
                Player other = Bukkit.getPlayerExact(requester);
                if (other != null && other.isOnline()) {
                    other.sendMessage(ChatColor.AQUA + player.getName() + " hat deine Freundschaftsanfrage akzeptiert.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Diese Anfrage existiert nicht mehr.");
            }
            friendsMenu.openRequests(player);
        } else if (rightClick) {
            if (FriendManager.denyRequest(player.getName(), requester)) {
                player.sendMessage(ChatColor.YELLOW + "Du hast die Anfrage von " + requester + " abgelehnt.");
            } else {
                player.sendMessage(ChatColor.RED + "Diese Anfrage existiert nicht mehr.");
            }
            friendsMenu.openRequests(player);
        }
    }
}
