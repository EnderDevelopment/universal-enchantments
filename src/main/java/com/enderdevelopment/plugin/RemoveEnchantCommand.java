package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RemoveEnchantCommand implements CommandExecutor {

    private final Main plugin;
    private final EnchantmentLogic logic;

    public RemoveEnchantCommand(Main plugin, EnchantmentLogic logic) {
        this.plugin = plugin;
        this.logic = logic;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return true;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType().isAir()) {
            player.sendMessage(Component.text("Hold an item in your main hand first.").color(NamedTextColor.RED));
            return true;
        }

        int removed = logic.removeAllEnchantments(held);
        if (removed > 0) {
            player.sendMessage(Component.text("Removed " + removed + " enchantment"
                    + (removed == 1 ? "" : "s") + " from " + held.getType().name() + ".")
                    .color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("That item has no enchantments.").color(NamedTextColor.YELLOW));
        }
        return true;
    }
}
