package de.noneless.lobby.listeners;

import de.noneless.lobby.Main;
import npc.NPCManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class NPCInteractionListener implements Listener {

    @EventHandler
    public void onNPCInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        NPCManager manager = Main.getInstance().getNPCManager();
        if (manager == null) {
            return;
        }
        UUID entityId = event.getRightClicked().getUniqueId();
        boolean handled = manager.triggerNPCInteraction(player, entityId);
        if (handled) {
            event.setCancelled(true);
        }
    }
}
