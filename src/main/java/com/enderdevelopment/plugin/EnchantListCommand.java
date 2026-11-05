package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnchantListCommand implements CommandExecutor {

    private final Main plugin;
    private final EnchantmentLogic logic;

    public EnchantListCommand(Main plugin, EnchantmentLogic logic) {
        this.plugin = plugin;
        this.logic = logic;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        ItemStack held = null;
        if (sender instanceof Player player) {
            held = player.getInventory().getItemInMainHand();
            if (held.getType().isAir()) {
                held = null;
            }
        }

        List<String> lines = logic.listEnchantments(held);
        sender.sendMessage(Component.text("=== Enchantments" + (held == null ? "" : " for " + held.getType().name()) + " ===")
                .color(NamedTextColor.AQUA));
        for (String line : lines) {
            sender.sendMessage(Component.text(line).color(NamedTextColor.WHITE));
        }
        return true;
    }
}
