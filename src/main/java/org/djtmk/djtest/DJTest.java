package org.djtmk.djtest;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.djtmk.djtest.commands.*;
import org.djtmk.djtest.listeners.GodModeListener;
import org.djtmk.djtest.listeners.PlayerQuitListener;
import org.djtmk.djtest.utils.MessageUtils;
import org.djtmk.djtest.utils.TpaManager;

public final class DJTest extends JavaPlugin {
    private TpaManager tpaManager;
    private GodCommand godCommand;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize utilities
        MessageUtils.initialize(this);
        tpaManager = new TpaManager(this);

        // Register commands
        godCommand = new GodCommand();
        getCommand("gamemode").setExecutor(new GamemodeCommand());
        getCommand("god").setExecutor(godCommand);
        getCommand("openinv").setExecutor(new OpenInvCommand());
        getCommand("enderchest").setExecutor(new EnderchestCommand());
        getCommand("fix").setExecutor(new FixCommand());

        // Register TPA commands with the same executor
        TpaCommand tpaCommand = new TpaCommand(tpaManager);
        getCommand("tpa").setExecutor(tpaCommand);
        getCommand("tpaccept").setExecutor(tpaCommand);
        getCommand("tpdeny").setExecutor(tpaCommand);

        // Register trash command and its listener
        TrashCommand trashCommand = new TrashCommand();
        getCommand("trash").setExecutor(trashCommand);
        getServer().getPluginManager().registerEvents(trashCommand, this);

        // Register other listeners
        getServer().getPluginManager().registerEvents(new GodModeListener(godCommand), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(tpaManager, godCommand), this);

        getLogger().info("DJTest has been enabled!");
    }

    @Override
    public void onDisable() {
        // Unregister all listeners
        HandlerList.unregisterAll(this);

        getLogger().info("DJTest has been disabled!");
    }
}