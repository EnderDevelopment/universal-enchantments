package com.enderdevelopment.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private EnchantCostManager costManager;

    @Override
    public void onEnable() {
        instance = this;

        EnchantmentLogic logic = new EnchantmentLogic(this);
        costManager = new EnchantCostManager(this);

        getCommand("enchantall").setExecutor(new EnchantAllCommand(this, logic, costManager));
        getCommand("enchant").setExecutor(new EnchantCommand(this, logic, costManager));
        getCommand("enchantlist").setExecutor(new EnchantListCommand(this, logic));
        getCommand("removeenchant").setExecutor(new RemoveEnchantCommand(this, logic));
        getCommand("enchantreload").setExecutor(new EnchantReloadCommand(this, costManager));
        getLogger().info("UniversalEnchant enabled. Version support: "
                + getClass().getPackage().getImplementationVersion());
    }

    @Override
    public void onDisable() {
        getLogger().info("UniversalEnchant disabled.");
    }

    public static Main getInstance() {
        return instance;
    }

    public EnchantCostManager getCostManager() {
        return costManager;
    }
}
