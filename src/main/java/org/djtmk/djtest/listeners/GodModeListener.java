package org.djtmk.djtest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.djtmk.djtest.commands.GodCommand;

/**
 * Listener for god mode events
 */
public class GodModeListener implements Listener {

    private final GodCommand godCommand;

    /**
     * Constructor for GodModeListener
     * @param godCommand The god command instance
     */
    public GodModeListener(GodCommand godCommand) {
        this.godCommand = godCommand;
    }

    /**
     * Handle damage events for players in god mode
     * @param event The entity damage event
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            // Cancel damage for players in god mode
            if (godCommand.hasGodMode(player)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handle food level change events for players in god mode
     * @param event The food level change event
     */
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            // Cancel hunger for players in god mode
            if (godCommand.hasGodMode(player)) {
                event.setCancelled(true);
                
                // Ensure food level stays at maximum
                player.setFoodLevel(20);
                player.setSaturation(20);
            }
        }
    }
}