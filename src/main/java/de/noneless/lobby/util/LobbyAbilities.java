package de.noneless.lobby.util;

import Config.ConfigManager;
import de.noneless.lobby.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class LobbyAbilities {

    public enum Ability {
        ELYTRA("elytra", "Elytra", Material.ELYTRA),
        FIREWORKS("fireworks", "Feuerwerke", Material.FIREWORK_ROCKET),
        SPEED("speed", "Schnelles Laufen", Material.SUGAR),
        ULTRA_JUMP("ultra_jump", "Ultrasprung", Material.SLIME_BALL),
        SLOW_FALLING("slow_falling", "Sanfter Fall", Material.FEATHER),
        NIGHT_VISION("night_vision", "Nachtsicht", Material.GLOWSTONE_DUST);

        private final String id;
        private final String displayName;
        private final Material icon;

        Ability(String id, String displayName, Material icon) {
            this.id = id;
            this.displayName = displayName;
            this.icon = icon;
        }

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getIcon() {
            return icon;
        }

        public static Ability fromId(String id) {
            if (id == null) return null;
            String normalized = id.trim().toLowerCase(Locale.ROOT);
            for (Ability ability : values()) {
                if (ability.id.equals(normalized) || ability.name().equalsIgnoreCase(normalized)) {
                    return ability;
                }
            }
            return null;
        }
    }

    private static final int EFFECT_DURATION_TICKS = 20 * 60 * 30;
    private static final Set<UUID> activeElytra = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> activeFireworks = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> activeSpeed = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> activeUltraJump = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> activeSlowFalling = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> activeNightVision = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> airborneFireworkUsers = ConcurrentHashMap.newKeySet();

    private static Main plugin;
    private static NamespacedKey itemKey;
    private static File dataFile;
    private static FileConfiguration data;

    private LobbyAbilities() {
    }

    public static void initialize(Main main) {
        plugin = main;
        itemKey = new NamespacedKey(main, "lobby-ability-item");
        dataFile = new File(main.getDataFolder(), "lobby_abilities.yml");
        if (!main.getDataFolder().exists()) {
            main.getDataFolder().mkdirs();
        }
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                main.getLogger().warning("Konnte lobby_abilities.yml nicht erstellen: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public static boolean grantAbility(UUID playerId, Ability ability) {
        ensureInitialized();
        if (playerId == null || ability == null) return false;
        Set<Ability> granted = getGrantedAbilities(playerId);
        if (!granted.add(ability)) {
            return false;
        }
        saveGrantedAbilities(playerId, granted);
        return true;
    }

    public static boolean grantAbility(UUID playerId, String abilityId) {
        return grantAbility(playerId, Ability.fromId(abilityId));
    }

    public static boolean revokeAbility(UUID playerId, Ability ability) {
        ensureInitialized();
        if (playerId == null || ability == null) return false;
        Set<Ability> granted = getGrantedAbilities(playerId);
        if (!granted.remove(ability)) {
            return false;
        }
        deactivate(playerId, ability, true);
        saveGrantedAbilities(playerId, granted);
        return true;
    }

    public static boolean revokeAbility(UUID playerId, String abilityId) {
        return revokeAbility(playerId, Ability.fromId(abilityId));
    }

    public static boolean setAbilityGranted(UUID playerId, Ability ability, boolean granted) {
        return granted ? grantAbility(playerId, ability) : revokeAbility(playerId, ability);
    }

    public static boolean isAbilityGranted(UUID playerId, Ability ability) {
        return playerId != null && ability != null && getGrantedAbilities(playerId).contains(ability);
    }

    public static List<String> getAvailableAbilityIds() {
        return Arrays.stream(Ability.values())
                .map(Ability::getId)
                .toList();
    }

    public static Set<Ability> getGrantedAbilities(UUID playerId) {
        ensureInitialized();
        Set<Ability> abilities = EnumSet.noneOf(Ability.class);
        if (playerId == null || data == null) {
            return abilities;
        }
        for (String id : data.getStringList(path(playerId))) {
            Ability ability = Ability.fromId(id);
            if (ability != null) {
                abilities.add(ability);
            }
        }
        return abilities;
    }

    public static Set<Ability> getActiveAbilities(UUID playerId) {
        ensureInitialized();
        Set<Ability> abilities = EnumSet.noneOf(Ability.class);
        if (playerId == null || data == null) {
            return abilities;
        }
        for (String id : data.getStringList(activePath(playerId))) {
            Ability ability = Ability.fromId(id);
            if (ability != null && isAbilityGranted(playerId, ability)) {
                abilities.add(ability);
            }
        }
        return abilities;
    }

    public static boolean toggleForPlayer(Player player, Ability ability) {
        if (player == null || ability == null) return false;
        if (!requireLobbyWorld(player) || !requireGranted(player, ability)) {
            return false;
        }
        if (!requireOnGround(player)) {
            return false;
        }
        if (isActive(player.getUniqueId(), ability)) {
            deactivate(player, ability, true);
            player.sendMessage(ChatColor.GRAY + ability.getDisplayName() + " deaktiviert.");
            return false;
        }
        activate(player, ability);
        return true;
    }

    public static boolean isActive(UUID playerId, Ability ability) {
        if (playerId == null || ability == null) return false;
        return getActiveSet(ability).contains(playerId) || getActiveAbilities(playerId).contains(ability);
    }

    public static boolean isInLobbyWorld(Player player) {
        if (player == null) {
            return false;
        }
        Location lobbyLocation = ConfigManager.getLobbyLocation();
        World lobbyWorld = lobbyLocation != null ? lobbyLocation.getWorld() : Bukkit.getWorld("world");
        return lobbyWorld != null && player.getWorld().equals(lobbyWorld);
    }

    public static void clear(Player player) {
        if (player == null) {
            return;
        }
        for (Ability ability : Ability.values()) {
            deactivate(player, ability, true);
        }
        player.sendMessage(ChatColor.GRAY + "Lobby-Fähigkeiten entfernt.");
    }

    public static void enforceLobbyOnly(Player player) {
        if (player == null || isInLobbyWorld(player)) {
            return;
        }
        for (Ability ability : Ability.values()) {
            deactivateRuntime(player, ability);
        }
    }

    public static void restoreActiveAbilities(Player player) {
        if (player == null || !isInLobbyWorld(player)) {
            return;
        }
        for (Ability ability : getActiveAbilities(player.getUniqueId())) {
            if (getActiveSet(ability).contains(player.getUniqueId()) && hasRuntimeAbility(player, ability)) {
                continue;
            }
            activateRuntime(player, ability);
            getActiveSet(ability).add(player.getUniqueId());
        }
    }

    public static void handleLanding(Player player) {
        if (player == null || !isInLobbyWorld(player) || !isActive(player.getUniqueId(), Ability.FIREWORKS)) {
            return;
        }

        UUID playerId = player.getUniqueId();
        if (!player.isOnGround()) {
            airborneFireworkUsers.add(playerId);
            return;
        }

        if (airborneFireworkUsers.remove(playerId)) {
            setLobbyFireworkAmount(player, 3);
        }
    }

    public static boolean isLobbyAbilityItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(getItemKey(), PersistentDataType.BYTE);
    }

    private static void activate(Player player, Ability ability) {
        activateRuntime(player, ability);
        getActiveSet(ability).add(player.getUniqueId());
        saveActiveAbility(player.getUniqueId(), ability, true);
    }

    private static void activateRuntime(Player player, Ability ability) {
        switch (ability) {
            case ELYTRA -> equipElytra(player);
            case FIREWORKS -> giveFireworks(player);
            case SPEED -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, EFFECT_DURATION_TICKS, 2, true, false, true));
                player.sendMessage(ChatColor.GREEN + "Lobby-Speed aktiviert.");
            }
            case ULTRA_JUMP -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, EFFECT_DURATION_TICKS, 4, true, false, true));
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Ultrasprung aktiviert.");
            }
            case SLOW_FALLING -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, EFFECT_DURATION_TICKS, 0, true, false, true));
                player.sendMessage(ChatColor.WHITE + "Sanfter Fall aktiviert.");
            }
            case NIGHT_VISION -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, EFFECT_DURATION_TICKS, 0, true, false, true));
                player.sendMessage(ChatColor.YELLOW + "Nachtsicht aktiviert.");
            }
        }
    }

    private static void deactivate(UUID playerId, Ability ability, boolean saveActiveState) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            deactivate(player, ability, saveActiveState);
            return;
        }
        getActiveSet(ability).remove(playerId);
        if (saveActiveState) {
            saveActiveAbility(playerId, ability, false);
        }
    }

    private static void deactivate(Player player, Ability ability, boolean saveActiveState) {
        if (player == null || ability == null) return;
        getActiveSet(ability).remove(player.getUniqueId());
        if (saveActiveState) {
            saveActiveAbility(player.getUniqueId(), ability, false);
        }
        deactivateRuntime(player, ability);
    }

    private static void deactivateRuntime(Player player, Ability ability) {
        if (player == null || ability == null) return;
        switch (ability) {
            case ELYTRA -> removeElytra(player);
            case FIREWORKS -> {
                airborneFireworkUsers.remove(player.getUniqueId());
                removeLobbyAbilityItems(player, Material.FIREWORK_ROCKET);
            }
            case SPEED -> player.removePotionEffect(PotionEffectType.SPEED);
            case ULTRA_JUMP -> player.removePotionEffect(PotionEffectType.JUMP_BOOST);
            case SLOW_FALLING -> player.removePotionEffect(PotionEffectType.SLOW_FALLING);
            case NIGHT_VISION -> player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

    private static boolean hasRuntimeAbility(Player player, Ability ability) {
        if (player == null || ability == null) {
            return false;
        }
        return switch (ability) {
            case ELYTRA -> {
                ItemStack chestplate = player.getInventory().getChestplate();
                yield isLobbyAbilityItem(chestplate) && chestplate.getType() == Material.ELYTRA;
            }
            case FIREWORKS -> hasLobbyAbilityItem(player, Material.FIREWORK_ROCKET);
            case SPEED -> player.hasPotionEffect(PotionEffectType.SPEED);
            case ULTRA_JUMP -> player.hasPotionEffect(PotionEffectType.JUMP_BOOST);
            case SLOW_FALLING -> player.hasPotionEffect(PotionEffectType.SLOW_FALLING);
            case NIGHT_VISION -> player.hasPotionEffect(PotionEffectType.NIGHT_VISION);
        };
    }

    private static boolean hasLobbyAbilityItem(Player player, Material type) {
        ItemStack chestplate = player.getInventory().getChestplate();
        if (isLobbyAbilityItem(chestplate) && (type == null || chestplate.getType() == type)) {
            return true;
        }

        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (isLobbyAbilityItem(item) && (type == null || item.getType() == type)) {
                return true;
            }
        }

        ItemStack offhand = player.getInventory().getItemInOffHand();
        return isLobbyAbilityItem(offhand) && (type == null || offhand.getType() == type);
    }

    private static void equipElytra(Player player) {
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        ItemMeta meta = elytra.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Lobby-Elytra");
            meta.setLore(List.of(ChatColor.GRAY + "Nur in der Lobbywelt nutzbar"));
            markLobbyAbilityItem(meta);
            elytra.setItemMeta(meta);
        }

        ItemStack currentChestplate = player.getInventory().getChestplate();
        if (currentChestplate != null && currentChestplate.getType() != Material.AIR && !isLobbyAbilityItem(currentChestplate)) {
            player.getInventory().addItem(currentChestplate);
        }
        player.getInventory().setChestplate(elytra);
        player.sendMessage(ChatColor.AQUA + "Lobby-Elytra ausgerüstet.");
    }

    private static void giveFireworks(Player player) {
        ItemStack fireworks = new ItemStack(Material.FIREWORK_ROCKET, 3);
        FireworkMeta meta = (FireworkMeta) fireworks.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Lobby-Feuerwerke");
            meta.setLore(List.of(ChatColor.GRAY + "Nur in der Lobbywelt nutzbar"));
            meta.setPower(2);
            meta.addEffect(FireworkEffect.builder()
                    .withColor(Color.AQUA, Color.YELLOW)
                    .withFade(Color.WHITE)
                    .with(FireworkEffect.Type.BALL)
                    .trail(false)
                    .flicker(false)
                    .build());
            markLobbyAbilityItem(meta);
            fireworks.setItemMeta(meta);
        }
        removeLobbyAbilityItems(player, Material.FIREWORK_ROCKET);
        player.getInventory().addItem(fireworks);
        player.sendMessage(ChatColor.GOLD + "Du hast 3 Lobby-Feuerwerke erhalten.");
    }

    private static boolean requireLobbyWorld(Player player) {
        if (isInLobbyWorld(player)) {
            return true;
        }
        player.sendMessage(ChatColor.RED + "Lobby-Fähigkeiten funktionieren nur in der gesetzten Lobbywelt.");
        return false;
    }

    private static boolean requireOnGround(Player player) {
        if (player.isOnGround()) {
            return true;
        }
        player.sendMessage(ChatColor.RED + "Du musst auf dem Boden stehen, um Lobby-Perks zu aktivieren oder deaktivieren.");
        return false;
    }

    private static boolean requireGranted(Player player, Ability ability) {
        if (isAbilityGranted(player.getUniqueId(), ability)) {
            return true;
        }
        player.sendMessage(ChatColor.RED + "Diese Lobby-Fähigkeit wurde dir noch nicht freigeschaltet.");
        return false;
    }

    private static void removeElytra(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        if (isLobbyAbilityItem(chestplate)) {
            player.getInventory().setChestplate(null);
        }
        removeLobbyAbilityItems(player, Material.ELYTRA);
    }

    private static void removeLobbyAbilityItems(Player player, Material type) {
        ItemStack[] contents = player.getInventory().getStorageContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (isLobbyAbilityItem(item) && (type == null || item.getType() == type)) {
                contents[i] = null;
            }
        }
        player.getInventory().setStorageContents(contents);

        if (isLobbyAbilityItem(player.getInventory().getItemInOffHand())) {
            ItemStack offhand = player.getInventory().getItemInOffHand();
            if (type == null || offhand.getType() == type) {
                player.getInventory().setItemInOffHand(null);
            }
        }
    }

    private static void setLobbyFireworkAmount(Player player, int amount) {
        removeLobbyAbilityItems(player, Material.FIREWORK_ROCKET);
        if (amount <= 0) {
            return;
        }

        ItemStack fireworks = new ItemStack(Material.FIREWORK_ROCKET, amount);
        FireworkMeta meta = (FireworkMeta) fireworks.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Lobby-Feuerwerke");
            meta.setLore(List.of(ChatColor.GRAY + "Nur in der Lobbywelt nutzbar"));
            meta.setPower(2);
            meta.addEffect(FireworkEffect.builder()
                    .withColor(Color.AQUA, Color.YELLOW)
                    .withFade(Color.WHITE)
                    .with(FireworkEffect.Type.BALL)
                    .trail(false)
                    .flicker(false)
                    .build());
            markLobbyAbilityItem(meta);
            fireworks.setItemMeta(meta);
        }
        player.getInventory().addItem(fireworks);
    }

    private static Set<UUID> getActiveSet(Ability ability) {
        return switch (ability) {
            case ELYTRA -> activeElytra;
            case FIREWORKS -> activeFireworks;
            case SPEED -> activeSpeed;
            case ULTRA_JUMP -> activeUltraJump;
            case SLOW_FALLING -> activeSlowFalling;
            case NIGHT_VISION -> activeNightVision;
        };
    }

    private static void saveGrantedAbilities(UUID playerId, Set<Ability> abilities) {
        ensureInitialized();
        Set<String> ids = new LinkedHashSet<>();
        for (Ability ability : abilities) {
            ids.add(ability.getId());
        }
        data.set(path(playerId), ids.stream().toList());
        saveData();
    }

    private static void saveActiveAbility(UUID playerId, Ability ability, boolean active) {
        ensureInitialized();
        if (playerId == null || ability == null) return;
        Set<Ability> activeAbilities = getActiveAbilities(playerId);
        if (active) {
            activeAbilities.add(ability);
        } else {
            activeAbilities.remove(ability);
        }

        Set<String> ids = new LinkedHashSet<>();
        for (Ability activeAbility : activeAbilities) {
            ids.add(activeAbility.getId());
        }
        data.set(activePath(playerId), ids.stream().toList());
        saveData();
    }

    private static void saveData() {
        if (data == null || dataFile == null) return;
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte lobby_abilities.yml nicht speichern: " + e.getMessage());
        }
    }

    private static String path(UUID playerId) {
        return "players." + playerId + ".granted";
    }

    private static String activePath(UUID playerId) {
        return "players." + playerId + ".active";
    }

    private static void markLobbyAbilityItem(ItemMeta meta) {
        meta.getPersistentDataContainer().set(getItemKey(), PersistentDataType.BYTE, (byte) 1);
    }

    private static NamespacedKey getItemKey() {
        ensureInitialized();
        return itemKey;
    }

    private static void ensureInitialized() {
        if (plugin == null || data == null || itemKey == null) {
            Main main = Main.getInstance();
            if (main == null) {
                throw new IllegalStateException("LobbyAbilities before plugin initialisation.");
            }
            initialize(main);
        }
    }
}
