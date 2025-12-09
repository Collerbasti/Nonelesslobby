package de.noneless.lobby.news;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Sound;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NewsManager {

    private final JavaPlugin plugin;
    private final File newsFile;
    private FileConfiguration newsConfig;

    // id -> message
    private final LinkedHashMap<Integer, String> news = new LinkedHashMap<>();
    // playerUUID -> set of seen ids
    private final Map<UUID, Set<Integer>> seen = new HashMap<>();

    private int nextId = 1;

    public NewsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.newsFile = new File(plugin.getDataFolder(), "news.yml");
        load();
    }

    private void load() {
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            if (!newsFile.exists()) newsFile.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte news.yml nicht erstellen: " + e.getMessage());
        }
        newsConfig = YamlConfiguration.loadConfiguration(newsFile);
        news.clear();
        seen.clear();

        List<?> entries = newsConfig.getList("news", new ArrayList<>());
        for (Object o : entries) {
            if (o instanceof Map) {
                Map<?,?> m = (Map<?,?>) o;
                Object idObj = m.get("id");
                Object textObj = m.get("text");
                if (idObj instanceof Number && textObj instanceof String) {
                    int id = ((Number) idObj).intValue();
                    news.put(id, (String) textObj);
                    nextId = Math.max(nextId, id + 1);
                }
            }
        }

        if (newsConfig.isConfigurationSection("seen")) {
            org.bukkit.configuration.ConfigurationSection seenSection = newsConfig.getConfigurationSection("seen");
            for (String key : seenSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    List<Integer> list = seenSection.getIntegerList(key);
                    Set<Integer> set = new HashSet<>(list);
                    seen.put(uuid, set);
                } catch (Exception ignored) { }
            }
        }
    }

    private void save() {
        if (newsConfig == null) return;
        List<Map<String,Object>> list = new ArrayList<>();
        for (Map.Entry<Integer,String> e : news.entrySet()) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", e.getKey());
            m.put("text", e.getValue());
            list.add(m);
        }
        newsConfig.set("news", list);

        // save seen
        Map<String, List<Integer>> seenMap = new LinkedHashMap<>();
        for (Map.Entry<UUID, Set<Integer>> e : seen.entrySet()) {
            List<Integer> l = new ArrayList<>(e.getValue());
            seenMap.put(e.getKey().toString(), l);
        }
        newsConfig.set("seen", seenMap);

        try {
            newsConfig.save(newsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte news.yml nicht speichern: " + e.getMessage());
        }
    }

    public synchronized int addNews(String text) {
        if (text == null || text.isBlank()) return -1;
        int id = nextId++;
        news.put(id, text.trim());
        save();
        return id;
    }

    public synchronized List<Integer> getUnseenIdsFor(Player player) {
        if (player == null) return Collections.emptyList();
        UUID u = player.getUniqueId();
        Set<Integer> s = seen.getOrDefault(u, Collections.emptySet());
        List<Integer> out = new ArrayList<>();
        for (Integer id : news.keySet()) {
            if (!s.contains(id)) out.add(id);
        }
        return out;
    }

    public synchronized String getNewsText(int id) {
        return news.get(id);
    }

    public synchronized void markShown(Player player, int id) {
        if (player == null) return;
        UUID u = player.getUniqueId();
        seen.computeIfAbsent(u, k -> new HashSet<>()).add(id);
        save();
    }

    public synchronized List<String> listAllNews() {
        return new ArrayList<>(news.values());
    }

    public synchronized LinkedHashMap<Integer, String> getNewsEntries() {
        return new LinkedHashMap<>(news);
    }

    public synchronized boolean deleteNewsById(int id) {
        if (!news.containsKey(id)) return false;
        news.remove(id);
        save();
        return true;
    }

    // no helper required
}
