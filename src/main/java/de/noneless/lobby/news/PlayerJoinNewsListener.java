package de.noneless.lobby.news;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PlayerJoinNewsListener implements Listener {

    private final NewsManager manager;
    private final JavaPlugin plugin;

    public PlayerJoinNewsListener(JavaPlugin plugin, NewsManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p == null) return;

        List<Integer> ids = manager.getUnseenIdsFor(p);
        if (ids.isEmpty()) return;

        // Schedule titles sequentially, each shown for 60 seconds (1200 ticks)
        long delay = 0L;
        final int stayTicks = 20 * 60; // 60 seconds
        final int fadeIn = 10;
        final int fadeOut = 10;

        for (Integer id : ids) {
            final Integer nid = id;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                String text = manager.getNewsText(nid);
                if (text == null || text.isEmpty()) return;
                // Show title
                p.sendTitle(text, "", fadeIn, stayTicks, fadeOut);
                // Play sound
                try { p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f); } catch (Exception ignored) {}
                // mark as shown so they won't see it again
                manager.markShown(p, nid);
            }, delay);
            delay += stayTicks + 20L; // small gap 1s between messages
        }
    }
}
