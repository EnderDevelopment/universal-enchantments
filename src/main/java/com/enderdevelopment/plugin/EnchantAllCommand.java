package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnchantAllCommand implements CommandExecutor {

    private final Main plugin;
    private final EnchantmentLogic logic;
    private final EnchantCostManager costManager;

    public EnchantAllCommand(Main plugin, EnchantmentLogic logic, EnchantCostManager costManager) {
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

        // Provide cost info when asked.
        if (args.length > 0 && (args[0].equalsIgnoreCase("cost") || args[0].equalsIgnoreCase("info"))) {
            costManager.sendCostInfo(player);
            return true;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType().isAir()) {
            player.sendMessage(Component.text("Hold an item in your main hand first.").color(NamedTextColor.RED));
            return true;
        }

        // Enforce cooldown for /enchantall.
        if (!costManager.checkAllCooldown(player)) {
            return true;
        }

        int level = 1;
        if (args.length > 0) {
            try {
                level = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text("Invalid level: " + args[0]).color(NamedTextColor.RED));
                return true;
            }
        }
        if (level < 1) {
            level = 1;
        }

        String forceArg = args.length > 1 ? args[1].toLowerCase() : "";
        boolean force = forceArg.equals("force") || forceArg.equals("all");

        // Estimate the number of enchantments that will be applied for cost purposes.
        int enchantCount = force
                ? countAllEnchantments()
                : countNaturalEnchantments(held);

        // Pay the cost for all enchantments.
        if (!costManager.payCost(player, enchantCount)) {
            return true;
        }

        int count;
        if (force) {
            count = logic.applyAllForce(held, level);
            player.sendMessage(Component.text("Applied every enchantment (force) at level " + level
                    + " to " + held.getType().name() + " (" + count + " enchantments).")
                    .color(NamedTextColor.GREEN));
        } else {
            count = logic.applyAllNatural(held, level);
            player.sendMessage(Component.text("Applied " + count + " naturally-compatible enchantments at level "
                    + level + " to " + held.getType().name() + ".")
                    .color(NamedTextColor.GREEN));
        }
        return true;
    }

    private int countNaturalEnchantments(ItemStack item) {
        int count = 0;
        for (org.bukkit.enchantments.Enchantment enchantment : org.bukkit.Registry.ENCHANTMENT) {
            if (enchantment != null && enchantment.canEnchantItem(item)) {
                count++;
            }
        }
        return count;
    }

    private int countAllEnchantments() {
        int count = 0;
        for (org.bukkit.enchantments.Enchantment enchantment : org.bukkit.Registry.ENCHANTMENT) {
            if (enchantment != null) {
                count++;
            }
        }
        return count;
    }
}
