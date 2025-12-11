package de.noneless.lobby.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.noneless.lobby.Main;
import npc.NPCManager;

public class NPCTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        if (args.length == 1) {
            // Erste Argument: NPC-Verwaltungscommands
            return filterList(Arrays.asList(
                "spawn", "remove", "reload", "count", "setlobby", "addspawn", "removespawn", "listspawns", "spawnat",
                "info", "list", "help", "debug", "conversation", "cleanup",
                "setpoi", "removepoi", "pois", "poiinfo",
                "topoi", "tolobby", "wherenpc"
            ), args[0]);
        }
        
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "spawn":
                    // Anzahl der zu spawnenden NPCs
                    return filterList(Arrays.asList("1", "3", "5", "10"), args[1]);
                    
                case "remove":
                    // Optionen für das Entfernen
                    return filterList(Arrays.asList("all", "lobby"), args[1]);
                    
                case "setlobby":
                    return new ArrayList<>();
                    
                case "conversation":
                case "chat":
                case "talk":
                    return filterList(getConversationIds(), args[1]);
                    
                case "removespawn":
                case "spawnat":
                    // Spawn-Index
                    return filterList(Arrays.asList("0", "1", "2", "3", "4", "5"), args[1]);
                    
                case "info":
                    // NPC-Namen
                    return filterList(getNPCNames(), args[1]);
                    
                case "setpoi":
                    // POI-Name eingeben (keine Completion, da neuer Name)
                    return new ArrayList<>();
                    
                case "removepoi":
                case "poiinfo":
                    // Bestehende POI-Namen
                    return filterList(getPOINames(), args[1]);
                    
                case "topoi":
                    // NPC-Name oder "all"
                    List<String> npcAndAll = new ArrayList<>(getNPCNames());
                    npcAndAll.add(0, "all");
                    return filterList(npcAndAll, args[1]);
                    
                case "tolobby":
                    // NPC-Name oder "all"
                    List<String> npcAndAllLobby = new ArrayList<>(getNPCNames());
                    npcAndAllLobby.add(0, "all");
                    return filterList(npcAndAllLobby, args[1]);
                    
                case "wherenpc":
                    // NPC-Namen
                    return filterList(getNPCNames(), args[1]);
                    
                default:
                    return new ArrayList<>();
            }
        }
        
        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "topoi":
                    // POI-Name für das Ziel
                    return filterList(getPOINames(), args[2]);
                    
                default:
                    return new ArrayList<>();
            }
        }
        
        return new ArrayList<>();
    }
    
    private List<String> filterList(List<String> list, String arg) {
        return list.stream()
                .filter(item -> item.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<String> getConversationIds() {
        NPCManager manager = Main.getInstance().getNPCManager();
        if (manager == null) {
            return Collections.emptyList();
        }
        return manager.getConversationScriptIds();
    }
    
    private List<String> getNPCNames() {
        NPCManager manager = Main.getInstance().getNPCManager();
        if (manager == null) {
            return Collections.emptyList();
        }
        return manager.getAllActiveNPCNames();
    }
    
    private List<String> getPOINames() {
        NPCManager manager = Main.getInstance().getNPCManager();
        if (manager == null) {
            return Collections.emptyList();
        }
        return manager.getAllPOINames();
    }
}

