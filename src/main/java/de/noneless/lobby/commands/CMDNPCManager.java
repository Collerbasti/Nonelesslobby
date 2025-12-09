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
            case "addspawn":
                handleAddSpawnCommand(sender, npcManager, args);
                break;

            case "removespawn":
                handleRemoveSpawnCommand(sender, npcManager, args);
                break;

            case "listspawns":
                handleListSpawnsCommand(sender, npcManager);
                break;

            case "spawnat":
            case "spawnnow":
                handleSpawnAtCommand(sender, npcManager, args);
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

            case "setpoi":
                handleSetPOICommand(sender, npcManager, args);
                break;
                
            case "removepoi":
                handleRemovePOICommand(sender, npcManager, args);
                break;
                
            case "listpois":
            case "pois":
                handleListPOIsCommand(sender, npcManager);
                break;
                
            case "poiinfo":
                handlePOIInfoCommand(sender, npcManager, args);
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

    private void handleAddSpawnCommand(CommandSender sender, NPCManager npcManager, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Command kann nur von Spielern ausgeführt werden!");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Verwendung: /lobbynpc addspawn <name>");
            return;
        }
        String name = args[1].trim();
        Player player = (Player) sender;
        Location loc = player.getLocation();
        if (npcManager.addSpawnPoint(name, loc)) {
            sender.sendMessage(ChatColor.GREEN + "Spawn-Punkt '" + name + "' wurde hinzugefügt und NPC gespawnt.");
        } else {
            sender.sendMessage(ChatColor.RED + "Spawn-Punkt konnte nicht hinzugefügt werden (Name existiert?).");
        }
    }

    private void handleRemoveSpawnCommand(CommandSender sender, NPCManager npcManager, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Verwendung: /lobbynpc removespawn <name>");
            return;
        }
        String name = args[1].trim();
        if (npcManager.removeSpawnPoint(name)) {
            sender.sendMessage(ChatColor.GREEN + "Spawn-Punkt '" + name + "' wurde entfernt und zugehöriger NPC gelöscht.");
        } else {
            sender.sendMessage(ChatColor.RED + "Spawn-Punkt '" + name + "' nicht gefunden.");
        }
    }

    private void handleListSpawnsCommand(CommandSender sender, NPCManager npcManager) {
        List<String> spawns = npcManager.listSpawnPoints();
        if (spawns.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "Keine gespeicherten Spawn-Punkte vorhanden.");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "Gespeicherte Spawn-Punkte: " + ChatColor.WHITE + String.join(", ", spawns));
    }

    private void handleSpawnAtCommand(CommandSender sender, NPCManager npcManager, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Verwendung: /lobbynpc spawnat <name>");
            return;
        }
        String name = args[1].trim();
        if (npcManager.spawnAt(name)) {
            sender.sendMessage(ChatColor.GREEN + "NPC an Spawn-Punkt '" + name + "' gespawnt/teleportiert.");
        } else {
            sender.sendMessage(ChatColor.RED + "Spawn-Punkt '" + name + "' nicht gefunden oder Citizens fehlt.");
        }
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

    private void handleSetPOICommand(CommandSender sender, NPCManager npcManager, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Command kann nur von Spielern ausgeführt werden!");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Verwendung: /lobbynpc setpoi <name>");
            return;
        }
        String poiName = args[1].trim();
        Player player = (Player) sender;
        Location loc = player.getLocation();
        
        // Prüfe ob POI bereits existiert
        NPCManager.POIInfo existing = npcManager.getPOI(poiName);
        if (existing != null) {
            // POI existiert -> Location aktualisieren
            if (npcManager.updatePOILocation(poiName, loc)) {
                sender.sendMessage(ChatColor.GREEN + "POI '" + poiName + "' Location wurde aktualisiert:");
                sender.sendMessage(ChatColor.GRAY + "  Welt: " + ChatColor.WHITE + loc.getWorld().getName());
                sender.sendMessage(ChatColor.GRAY + "  Position: " + ChatColor.WHITE + 
                    String.format("%.1f, %.1f, %.1f", loc.getX(), loc.getY(), loc.getZ()));
            } else {
                sender.sendMessage(ChatColor.RED + "Fehler beim Aktualisieren des POI!");
            }
        } else {
            // Neuen POI erstellen
            if (npcManager.createPOI(poiName, loc)) {
                sender.sendMessage(ChatColor.GREEN + "POI '" + poiName + "' wurde erstellt!");
                sender.sendMessage(ChatColor.GRAY + "  Welt: " + ChatColor.WHITE + loc.getWorld().getName());
                sender.sendMessage(ChatColor.GRAY + "  Position: " + ChatColor.WHITE + 
                    String.format("%.1f, %.1f, %.1f", loc.getX(), loc.getY(), loc.getZ()));
                sender.sendMessage(ChatColor.YELLOW + "Verwende die NPC-Verwaltung um NPCs zuzuweisen.");
            } else {
                sender.sendMessage(ChatColor.RED + "Fehler beim Erstellen des POI!");
            }
        }
    }

    private void handleRemovePOICommand(CommandSender sender, NPCManager npcManager, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Verwendung: /lobbynpc removepoi <name>");
            return;
        }
        String poiName = args[1].trim();
        if (npcManager.removePOI(poiName)) {
            sender.sendMessage(ChatColor.GREEN + "POI '" + poiName + "' wurde entfernt!");
            sender.sendMessage(ChatColor.GRAY + "NPCs wurden zur Lobby zurückgeschickt.");
        } else {
            sender.sendMessage(ChatColor.RED + "POI '" + poiName + "' nicht gefunden!");
        }
    }

    private void handleListPOIsCommand(CommandSender sender, NPCManager npcManager) {
        List<NPCManager.POIInfo> pois = npcManager.getAllPOIs();
        if (pois.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "Keine Points of Interest vorhanden.");
            sender.sendMessage(ChatColor.YELLOW + "Erstelle einen mit: /lobbynpc setpoi <name>");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "=== Points of Interest ===");
        for (NPCManager.POIInfo poi : pois) {
            String worldName = poi.location.getWorld() != null ? poi.location.getWorld().getName() : "?";
            sender.sendMessage(ChatColor.AQUA + poi.name + ChatColor.GRAY + " @ " + 
                ChatColor.WHITE + worldName + " " + 
                String.format("(%.0f, %.0f, %.0f)", poi.location.getX(), poi.location.getY(), poi.location.getZ()));
            if (!poi.allowedNPCNames.isEmpty()) {
                sender.sendMessage(ChatColor.GRAY + "  NPCs: " + ChatColor.YELLOW + String.join(", ", poi.allowedNPCNames));
            } else {
                sender.sendMessage(ChatColor.GRAY + "  NPCs: " + ChatColor.DARK_GRAY + "(keine zugewiesen)");
            }
        }
        sender.sendMessage(ChatColor.GOLD + "==========================");
    }

    private void handlePOIInfoCommand(CommandSender sender, NPCManager npcManager, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Verwendung: /lobbynpc poiinfo <name>");
            return;
        }
        String poiName = args[1].trim();
        NPCManager.POIInfo poi = npcManager.getPOI(poiName);
        if (poi == null) {
            sender.sendMessage(ChatColor.RED + "POI '" + poiName + "' nicht gefunden!");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "=== POI: " + poi.name + " ===");
        String worldName = poi.location.getWorld() != null ? poi.location.getWorld().getName() : "?";
        sender.sendMessage(ChatColor.AQUA + "Welt: " + ChatColor.WHITE + worldName);
        sender.sendMessage(ChatColor.AQUA + "Position: " + ChatColor.WHITE + 
            String.format("%.2f, %.2f, %.2f", poi.location.getX(), poi.location.getY(), poi.location.getZ()));
        sender.sendMessage(ChatColor.AQUA + "Yaw/Pitch: " + ChatColor.WHITE + 
            String.format("%.1f / %.1f", poi.location.getYaw(), poi.location.getPitch()));
        if (!poi.allowedNPCNames.isEmpty()) {
            sender.sendMessage(ChatColor.AQUA + "Erlaubte NPCs: " + ChatColor.YELLOW + String.join(", ", poi.allowedNPCNames));
        } else {
            sender.sendMessage(ChatColor.AQUA + "Erlaubte NPCs: " + ChatColor.DARK_GRAY + "(keine zugewiesen)");
        }
        sender.sendMessage(ChatColor.GOLD + "==========================");
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== NPC Manager Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc spawn" + ChatColor.WHITE + " - Spawnt neue Lobby NPCs");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc remove" + ChatColor.WHITE + " - Entfernt alle NPCs");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc reload" + ChatColor.WHITE + " - Lädt NPCs neu");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc count" + ChatColor.WHITE + " - Zeigt Anzahl aktiver NPCs");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc setlobby" + ChatColor.WHITE + " - Setzt neue Lobby-Position");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc addspawn <name>" + ChatColor.WHITE + " - Speichert Spawn-Punkt und erstellt NPC hier");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc removespawn <name>" + ChatColor.WHITE + " - Entfernt gespeicherten Spawn und NPC");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc listspawns" + ChatColor.WHITE + " - Zeigt gespeicherte Spawn-Punkte");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc spawnat <name>" + ChatColor.WHITE + " - Spawnt/teleportiert NPC an gespeicherten Spawn");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc info" + ChatColor.WHITE + " - Zeigt NPC Manager Informationen");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc debug" + ChatColor.WHITE + " - Zeigt Debug-Informationen");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc conversation [id]" + ChatColor.WHITE + " - Startet eine bestimmte/zufällige Konversation");
        sender.sendMessage(ChatColor.GOLD + "--- Points of Interest ---");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc setpoi <name>" + ChatColor.WHITE + " - Erstellt/aktualisiert einen POI hier");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc removepoi <name>" + ChatColor.WHITE + " - Entfernt einen POI");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc pois" + ChatColor.WHITE + " - Listet alle POIs auf");
        sender.sendMessage(ChatColor.YELLOW + "/lobbynpc poiinfo <name>" + ChatColor.WHITE + " - Zeigt POI Details");
        sender.sendMessage(ChatColor.GOLD + "===============================");
    }
}
