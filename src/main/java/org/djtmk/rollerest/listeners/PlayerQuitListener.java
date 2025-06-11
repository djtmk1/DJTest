package org.djtmk.rollerest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.djtmk.rollerest.commands.GodCommand;
import org.djtmk.rollerest.utils.TpaManager;

/**
 * Listener for player quit events
 */
public class PlayerQuitListener implements Listener {

    private final TpaManager tpaManager;
    private final GodCommand godCommand;

    /**
     * Constructor for PlayerQuitListener
     * @param tpaManager The TPA manager
     * @param godCommand The god command instance
     */
    public PlayerQuitListener(TpaManager tpaManager, GodCommand godCommand) {
        this.tpaManager = tpaManager;
        this.godCommand = godCommand;
    }

    /**
     * Handle player quit events
     * @param event The player quit event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Clean up TPA requests
        tpaManager.cleanupRequests(player);
        
        // Remove player from god mode
        godCommand.removePlayer(player);
    }
}