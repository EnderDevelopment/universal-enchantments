package com.enderdevelopment.plugin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the "logic" of enchantment application: mapping every
 * enchantment to the item categories it naturally applies to, and
 * allowing the player to force any enchantment onto any item.
 */
public class EnchantmentLogic {

    private final Main plugin;

    public EnchantmentLogic(Main plugin) {
        this.plugin = plugin;
    }

    public Main getPlugin() {
        return plugin;
    }

    /**
     * Determines the natural enchantment level cap for an enchantment on a given item.
     * Returns 0 when the enchantment does not naturally apply to the item.
     */
    public int getNaturalMaxLevel(Enchantment enchantment, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return 0;
        }
        if (enchantment == null) {
            return 0;
        }
        if (enchantment.canEnchantItem(item)) {
            return enchantment.getMaxLevel();
        }
        return 0;
    }

    /**
     * Applies an enchantment to the item at the given level, bypassing natural
     * compatibility (force mode). Returns true on success.
     */
    public boolean applyEnchantment(ItemStack item, Enchantment enchantment, int level) {
        if (item == null || item.getType().isAir() || enchantment == null) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        int finalLevel = Math.max(1, level);
        // Clamp level to a sane upper bound to prevent overflow issues.
        finalLevel = Math.min(finalLevel, 32767);
        meta.addEnchant(enchantment, finalLevel, true);
        item.setItemMeta(meta);
        return true;
    }

    /**
     * Applies every enchantment that naturally applies to the given item
     * (or completes the item if force mode is on), at the given level.
     * Returns the number of enchantments applied.
     */
    public int applyAllNatural(ItemStack item, int level) {
        if (item == null || item.getType().isAir()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }

        int finalLevel = Math.max(1, level);
        finalLevel = Math.min(finalLevel, 32767);
        int count = 0;

        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment == null) {
                continue;
            }
            if (enchantment.canEnchantItem(item)) {
                meta.addEnchant(enchantment, finalLevel, true);
                count++;
            }
        }

        if (count > 0) {
            item.setItemMeta(meta);
        }
        return count;
    }

    /**
     * Applies EVERY enchantment in the game to the item, even ones that do not
     * naturally apply to it. Returns the number of enchantments applied.
     */
    public int applyAllForce(ItemStack item, int level) {
        if (item == null || item.getType().isAir()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }

        int finalLevel = Math.max(1, level);
        finalLevel = Math.min(finalLevel, 32767);
        int count = 0;

        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment == null) {
                continue;
            }
            meta.addEnchant(enchantment, finalLevel, true);
            count++;
        }

        if (count > 0) {
            item.setItemMeta(meta);
        }
        return count;
    }

    /**
     * Removes all enchantments from an item. Returns the number removed.
     */
    public int removeAllEnchantments(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }
        Map<Enchantment, Integer> enchants = new HashMap<>(meta.getEnchants());
        if (enchants.isEmpty()) {
            return 0;
        }
        for (Enchantment enchantment : enchants.keySet()) {
            meta.removeEnchant(enchantment);
        }
        item.setItemMeta(meta);
        return enchants.size();
    }

    /**
     * Returns a human-readable list of all enchantments in the game with
     * their natural applicability to the given item (or universal when null).
     */
    public List<String> listEnchantments(ItemStack item) {
        List<String> lines = new ArrayList<>();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment == null) {
                continue;
            }
            String key = enchantment.getKey().toString();
            String applies;
            if (item == null || item.getType().isAir()) {
                applies = "universal";
            } else {
                applies = enchantment.canEnchantItem(item) ? "yes" : "no";
            }
            lines.add(key + " (max " + enchantment.getMaxLevel() + ") applies: " + applies);
        }
        return lines;
    }

    /**
     * Resolves an enchantment name (for example "sharpness",
     * "minecraft:sharpness") to an Enchantment, or null if unknown.
     */
    public Enchantment resolveEnchantment(String name) {
        if (name == null) {
            return null;
        }
        String trimmed = name.trim().toLowerCase();

        NamespacedKey key = null;
        if (trimmed.contains(":")) {
            key = NamespacedKey.fromString(trimmed);
        } else {
            key = NamespacedKey.minecraft(trimmed);
        }
        if (key == null) {
            return null;
        }
        return Registry.ENCHANTMENT.get(key);
    }
}
