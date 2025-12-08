package friends;

import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.*;

/**
 * Verwaltet aktive Freundschaftsanfrage-Sessions für Spieler
 */
public class FriendRequestSession {
    private static final Map<UUID, FriendRequestData> activeSessions = new HashMap<>();
    private static final long SESSION_TIMEOUT_MS = 120000; // 2 Minuten

    public static class FriendRequestData {
        public final String requester;
        public final long createdAt;

        public FriendRequestData(String requester) {
            this.requester = requester;
            this.createdAt = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - createdAt > SESSION_TIMEOUT_MS;
        }
    }

    /**
     * Erstellt eine neue Freundschaftsanfrage-Session für einen Spieler
     */
    public static void createSession(Player player, String requester) {
        activeSessions.put(player.getUniqueId(), new FriendRequestData(requester));
        showRequestPrompt(player, requester);
    }

    /**
     * Ruft die aktive Anfrage ab oder null, wenn keine existiert
     */
    public static FriendRequestData getSession(Player player) {
        FriendRequestData data = activeSessions.get(player.getUniqueId());
        if (data != null && data.isExpired()) {
            activeSessions.remove(player.getUniqueId());
            return null;
        }
        return data;
    }

    /**
     * Beendet die aktive Session
     */
    public static void endSession(Player player) {
        activeSessions.remove(player.getUniqueId());
    }

    /**
     * Zeigt die Freundschaftsanfrage mit Buttons an
     */
    private static void showRequestPrompt(Player player, String requester) {
        // Header
        player.sendMessage("");
        player.sendMessage("§6§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Title line
        TextComponent title = new TextComponent("§e" + requester + " §7hat dir eine Freundschaftsanfrage gesendet!");
        player.spigot().sendMessage(title);

        player.sendMessage("");

        // Clickable Accept button
        TextComponent accept = new TextComponent("§a[✔ Annehmen] ");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/annehmen"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Klicke um die Anfrage anzunehmen").create()));

        // Clickable Deny button
        TextComponent deny = new TextComponent("§c[✘ Ablehnen]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ablehnen"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Klicke um die Anfrage abzulehnen").create()));

        // Combine and send
        TextComponent combined = new TextComponent("");
        combined.addExtra(accept);
        combined.addExtra(new TextComponent(" "));
        combined.addExtra(deny);
        player.spigot().sendMessage(combined);

        player.sendMessage("");
        player.sendMessage("§7(Die Anfrage verfällt nach 2 Minuten)");
        player.sendMessage("§6§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("");
    }

    /**
     * Zeigt eine Test-Anfrage an (für Admins)
     */
    public static void showTestRequest(Player player, String requesterName) {
        createSession(player, requesterName);
    }
}
