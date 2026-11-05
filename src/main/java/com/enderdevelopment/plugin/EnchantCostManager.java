package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages per-player cooldowns and XP costs for enchantment applications.
 * Costs and cooldowns are configurable via the plugin config file.
 */
public class EnchantCostManager {

    private final Main plugin;
    private final Map<UUID, Long> lastEnchantTime = new HashMap<>();
    private final Map<UUID, Long> lastEnchantAllTime = new HashMap<>();

    /** Default cost in XP levels per enchantment application. */
    private int xpCostPerEnchant;

    /** Default cooldown in seconds between individual enchant applications. */
    private int cooldownSecondsSingle;

    /** Default cooldown in seconds between /enchantall uses. */
    private int cooldownSecondsAll;

    /** Whether enchanting requires XP at all. */
    private boolean costEnabled;

    /** Whether cooldowns are enabled at all. */
    private boolean cooldownEnabled;

    public EnchantCostManager(Main plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    /**
     * Loads (or reloads) settings from the plugin config file.
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        costEnabled = plugin.getConfig().getBoolean("cost.enabled", true);
        xpCostPerEnchant = plugin.getConfig().getInt("cost.xp-levels-per-enchant", 5);
        cooldownEnabled = plugin.getConfig().getBoolean("cooldown.enabled", true);
        cooldownSecondsSingle = plugin.getConfig().getInt("cooldown.seconds-single", 1);
        cooldownSecondsAll = plugin.getConfig().getInt("cooldown.seconds-all", 5);
    }

    public boolean isCostEnabled() {
        return costEnabled;
    }

    public int getXpCostPerEnchant() {
        return xpCostPerEnchant;
    }

    public boolean isCooldownEnabled() {
        return cooldownEnabled;
    }

    public int getCooldownSecondsSingle() {
        return cooldownSecondsSingle;
    }

    public int getCooldownSecondsAll() {
        return cooldownSecondsAll;
    }

    /**
     * Returns true if the player has enough XP levels to pay the cost for the
     * given number of enchantments. When cost is disabled, always true.
     */
    public boolean hasEnoughLevels(Player player, int enchantCount) {
        if (!costEnabled) {
            return true;
        }
        int total = Math.max(1, enchantCount) * xpCostPerEnchant;
        return player.getLevel() >= total;
    }

    /**
     * Deducts XP levels from the player for applying the given number of
     * enchantments. Does nothing when cost is disabled or the player lacks levels.
     *
     * @return true if the cost was paid (or cost is disabled).
     */
    public boolean payCost(Player player, int enchantCount) {
        if (!costEnabled) {
            return true;
        }
        int total = Math.max(1, enchantCount) * xpCostPerEnchant;
        if (player.getLevel() < total) {
            player.sendMessage(Component.text("You need at least " + total + " XP levels. You have "
                            + player.getLevel() + ".")
                    .color(NamedTextColor.RED));
            return false;
        }
        player.setLevel(player.getLevel() - total);
        return true;
    }

    /**
     * Sends the player a message with the current cost and cooldown info.
     */
    public void sendCostInfo(Player player) {
        StringBuilder info = new StringBuilder();
        if (costEnabled) {
            info.append("XP cost: ").append(xpCostPerEnchant).append(" level(s) per enchantment. ");
        } else {
            info.append("XP cost: disabled. ");
        }
        if (cooldownEnabled) {
            info.append("Cooldown: ").append(cooldownSecondsSingle)
                    .append("s per enchant, ").append(cooldownSecondsAll)
                    .append("s for /enchantall.");
        } else {
            info.append("Cooldown: disabled.");
        }
        player.sendMessage(Component.text(info.toString()).color(NamedTextColor.AQUA));
    }

    /**
     * Checks and enforces the cooldown for a single enchant application.
     * Returns true when the player may proceed (no active cooldown), false otherwise.
     */
    public boolean checkSingleCooldown(Player player) {
        return checkCooldown(player, lastEnchantTime, cooldownSecondsSingle);
    }

    /**
     * Checks and enforces the cooldown for an /enchantall use.
     * Returns true when the player may proceed, false otherwise.
     */
    public boolean checkAllCooldown(Player player) {
        return checkCooldown(player, lastEnchantAllTime, cooldownSecondsAll);
    }

    private boolean checkCooldown(Player player, Map<UUID, Long> store, int seconds) {
        if (!cooldownEnabled || seconds <= 0) {
            return true;
        }
        UUID id = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long last = store.get(id);
        if (last != null) {
            long elapsedMs = now - last;
            long cooldownMs = seconds * 1000L;
            if (elapsedMs < cooldownMs) {
                long remaining = (cooldownMs - elapsedMs) / 1000 + 1;
                player.sendMessage(Component.text("Please wait " + remaining
                                + "s before using this again.")
                        .color(NamedTextColor.RED));
                return false;
            }
        }
        store.put(id, now);
        return true;
    }

    /**
     * Removes the cooldown entry for a player when they log off.
     */
    public void clearCooldowns(Player player) {
        UUID id = player.getUniqueId();
        lastEnchantTime.remove(id);
        lastEnchantAllTime.remove(id);
    }
}
