package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EnchantReloadCommand implements CommandExecutor {

    private final Main plugin;
    private final EnchantCostManager costManager;

    public EnchantReloadCommand(Main plugin, EnchantCostManager costManager) {
        this.plugin = plugin;
        this.costManager = costManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        costManager.loadConfig();
        sender.sendMessage(Component.text("UniversalEnchant configuration reloaded.")
                .color(NamedTextColor.GREEN));
        return true;
    }
}
