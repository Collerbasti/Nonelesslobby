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
                "spawn", "remove", "reload", "count", "setlobby", "info", "list", "help", "conversation"
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
}

