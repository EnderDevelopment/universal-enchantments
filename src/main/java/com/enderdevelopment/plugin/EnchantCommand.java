package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnchantCommand implements CommandExecutor {

    private final Main plugin;
    private final EnchantmentLogic logic;
    private final EnchantCostManager costManager;

    public EnchantCommand(Main plugin, EnchantmentLogic logic, EnchantCostManager costManager) {
        this.plugin = plugin;
        this.logic = logic;
        this.costManager = costManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(Component.text("Usage: /enchant <enchantment> [level]").color(NamedTextColor.RED));
            return true;
        }

        // Provide cost info when asked.
        if (args[0].equalsIgnoreCase("cost") || args[0].equalsIgnoreCase("info")) {
            costManager.sendCostInfo(player);
            return true;
        }

        Enchantment enchantment = logic.resolveEnchantment(args[0]);
        if (enchantment == null) {
            player.sendMessage(Component.text("Unknown enchantment: " + args[0]).color(NamedTextColor.RED));
            return true;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType().isAir()) {
            player.sendMessage(Component.text("Hold an item in your main hand first.").color(NamedTextColor.RED));
            return true;
        }

        // Enforce cooldown.
        if (!costManager.checkSingleCooldown(player)) {
            return true;
        }

        int level = 1;
        if (args.length > 1) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("Invalid level: " + args[1]).color(NamedTextColor.RED));
                return true;
            }
        }
        if (level < 1) {
            level = 1;
        }

        // Pay the cost first, then apply.
        if (!costManager.payCost(player, 1)) {
            return true;
        }

        int naturalMax = logic.getNaturalMaxLevel(enchantment, held);
        boolean natural = naturalMax > 0;

        if (logic.applyEnchantment(held, enchantment, level)) {
            if (natural) {
                player.sendMessage(Component.text("Applied " + enchantment.getKey()
                        + " at level " + level + " to " + held.getType().name()
                        + " (naturally compatible, max " + naturalMax + ").")
                        .color(NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("Applied " + enchantment.getKey()
                        + " at level " + level + " to " + held.getType().name()
                        + " (forced - not naturally compatible with this item).")
                        .color(NamedTextColor.GOLD));
            }
        } else {
            player.sendMessage(Component.text("Failed to apply the enchantment.").color(NamedTextColor.RED));
        }
        return true;
    }
}
