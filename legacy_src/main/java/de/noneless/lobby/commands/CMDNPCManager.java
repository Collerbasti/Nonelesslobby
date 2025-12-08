package de.noneless.lobby.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import de.noneless.lobby.Main;
import npc.NPCManager;

public class CMDNPCManager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        // Überprüfe Permissions
        if (!sender.hasPermission("nonelesslobby.admin")) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für diesen Command!");
            return true;
        }
        
        NPCManager npcManager = Main.getInstance().getNPCManager();
        
        if (npcManager == null) {
            sender.sendMessage(ChatColor.RED + "NPC Manager ist nicht verfügbar!");
            return true;
        }
        
        // Überprüfe ob Citizens verfügbar ist
        if (!npcManager.isCitizensAvailable()) {
            sender.sendMessage(ChatColor.RED + "Citizens2 Plugin ist nicht aktiv!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "spawn":
                handleSpawnCommand(sender, npcManager);
                break;
                
            case "remove":
                handleRemoveCommand(sender, npcManager);
                break;
                
            case "reload":
                handleReloadCommand(sender, npcManager);
                break;
                
            case "count":
                handleCountCommand(sender, npcManager);
                break;
                
            case "setlobby":
                handleSetLobbyCommand(sender, npcManager);
                break;
                
            case "info":
                handleInfoCommand(sender, npcManager);
                break;
                
            case "debug":
                handleDebugCommand(sender, npcManager);
                break;

            case "chat":
            case "talk":
            case "conversation":
                handleConversationCommand(sender, npcManager, args);
                break;
                
            default:
                sendHelpMessage(sender);
                break;
        }
        
        return true;
    }
    
    private void handleSpawnCommand(CommandSender sender, NPCManager npcManager) {
        sender.sendMessage(ChatColor.YELLOW + "Spawne Lobby NPCs...");
        npcManager.spawnLobbyNPCs();
        
        int count = npcManager.getActiveLobbyNPCCount();
        sender.sendMessage(ChatColor.GREEN + "Es wurden " + count + " NPCs gespawnt!");
    }
    
    private void handleRemoveCommand(CommandSender sender, NPCManager npcManager) {
        int count = npcManager.getActiveLobbyNPCCount();
        
        if (count == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Es sind keine NPCs zum Entfernen vorhanden.");
            return;
        }
        
        sender.sendMessage(ChatColor.YELLOW + "Entferne " + count + " NPCs...");
        npcManager.removeAllLobbyNPCs();
        sender.sendMessage(ChatColor.GREEN + "Alle NPCs wurden erfolgreich entfernt!");
    }
    
    private void handleReloadCommand(CommandSender sender, NPCManager npcManager) {
        sender.sendMessage(ChatColor.YELLOW + "Lade NPCs neu...");
        npcManager.reloadNPCs();
        sender.sendMessage(ChatColor.GREEN + "NPCs wurden erfolgreich neu geladen!");
    }
    
    private void handleCountCommand(CommandSender sender, NPCManager npcManager) {
        int count = npcManager.getActiveLobbyNPCCount();
        sender.sendMessage(ChatColor.AQUA + "Aktive Lobby NPCs: " + ChatColor.WHITE + count);
    }
    
    private void handleSetLobbyCommand(CommandSender sender, NPCManager npcManager) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Command kann nur von Spielern ausgeführt werden!");
            return;
        }
        
        Player player = (Player) sender;
        Location playerLocation = player.getLocation();
        
        sender.sendMessage(ChatColor.YELLOW + "Setze neue Lobby-Position und spawne NPCs...");
        npcManager.setLobbyLocation(playerLocation);
        
        sender.sendMessage(ChatColor.GREEN + "Neue Lobby-Position gesetzt: " + 
                          ChatColor.WHITE + String.format("%.1f, %.1f, %.1f", 
                          playerLocation.getX(), 
                          playerLocation.getY(), 
                          playerLocation.getZ()));
    }
    
    private void handleInfoCommand(CommandSender sender, NPCManager npcManager) {
        sender.sendMessage(ChatColor.GOLD + "=== NPC Manager Info ===");
        sender.sendMessage(ChatColor.AQUA + "Citizens2 Status: " + 
                          (npcManager.isCitizensAvailable() ? ChatColor.GREEN + "Verfügbar" : ChatColor.RED + "Nicht verfügbar"));
        sender.sendMessage(ChatColor.AQUA + "Aktive NPCs: " + ChatColor.WHITE + npcManager.getActiveLobbyNPCCount());
        sender.sendMessage(ChatColor.GOLD + "========================");
    }
    
    private void handleDebugCommand(CommandSender sender, NPCManager npcManager) {
        sender.sendMessage(ChatColor.GOLD + "=== NPC Debug Informationen ===");
        
        // Zeige gesetzte Lobby-Position
        try {
            Class<?> configManagerClass = Class.forName("Config.ConfigManager");
            Object lobbyLocation = configManagerClass.getMethod("getLobbyLocation").invoke(null);
            
            if (lobbyLocation != null) {
                Location loc = (Location) lobbyLocation;
                sender.sendMessage(ChatColor.GREEN + "Gesetzte Lobby-Position:");
                sender.sendMessage(ChatColor.YELLOW + "Welt: " + ChatColor.WHITE + loc.getWorld().getName());
                sender.sendMessage(ChatColor.YELLOW + "Position: " + ChatColor.WHITE + 
                                  String.format("%.1f, %.1f, %.1f", loc.getX(), loc.getY(), loc.getZ()));
            } else {
                sender.sendMessage(ChatColor.RED + "Keine Lobby-Position gesetzt!");
                sender.sendMessage(ChatColor.GRAY + "Standard-Position wird verwendet: world 0.5, 65, 0.5");
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Fehler beim Abrufen der Lobby-Position: " + e.getMessage());
        }
        
        // Zeige NPC Positionen (falls verfügbar)
        sender.sendMessage(ChatColor.AQUA + "Aktive NPCs: " + ChatColor.WHITE + npcManager.getActiveLobbyNPCCount());
        
        List<String> npcPositions = npcManager.getCurrentNPCPositions();
        if (!npcPositions.isEmpty()) {
            sender.sendMessage(ChatColor.AQUA + "NPC Positionen:");
            for (String position : npcPositions) {
                sender.sendMessage(ChatColor.WHITE + "  " + position);
            }
        } else {
            sender.sendMessage(ChatColor.GRAY + "Keine NPCs gespawnt oder Positionen nicht verfügbar.");
        }
        
        // Teste NPC-Spawn Bereich
        if (sender instanceof Player) {
            Player player = (Player) sender;
            sender.sendMessage(ChatColor.YELLOW + "Deine Position: " + ChatColor.WHITE + 
                              String.format("%.1f, %.1f, %.1f", 
                                          player.getLocation().getX(),
                                          player.getLocation().getY(),
                                          player.getLocation().getZ()));
        }
        
        sender.sendMessage(ChatColor.GOLD + "===============================");
    }

    private void handleConversationCommand(CommandSender sender, NPCManager npcManager, String[] args) {
        if (args.length >= 2) {
            String scriptId = args[1];
            if (npcManager.triggerConversationById(scriptId)) {
                sender.sendMessage(ChatColor.GREEN + "NPC-Konversation '" + scriptId + "' wurde gestartet.");
            } else {
                sender.sendMessage(ChatColor.RED + "Konversation '" + scriptId + "' konnte nicht gestartet werden.");
                List<String> ids = npcManager.getConversationScriptIds();
                if (!ids.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "Verfügbare IDs: " + ChatColor.GRAY + String.join(", ", ids));
                }
            }
        } else {
            if (npcManager.triggerConversationNow()) {
                sender.sendMessage(ChatColor.GREEN + "NPC-Konversation wurde gestartet.");
            } else {
                sender.sendMessage(ChatColor.RED + "Keine NPC-Konversation verfügbar (zu wenige NPCs oder keine Skripte).");
            }
        }
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== NPC Manager Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc spawn" + ChatColor.WHITE + " - Spawnt neue Lobby NPCs");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc remove" + ChatColor.WHITE + " - Entfernt alle NPCs");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc reload" + ChatColor.WHITE + " - Lädt NPCs neu");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc count" + ChatColor.WHITE + " - Zeigt Anzahl aktiver NPCs");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc setlobby" + ChatColor.WHITE + " - Setzt neue Lobby-Position");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc info" + ChatColor.WHITE + " - Zeigt NPC Manager Informationen");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc debug" + ChatColor.WHITE + " - Zeigt Debug-Informationen");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc conversation [id]" + ChatColor.WHITE + " - Startet eine bestimmte/zufällige Konversation");
        sender.sendMessage(ChatColor.GOLD + "===============================");
    }
}
