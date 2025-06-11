package org.djtmk.rollerest.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.djtmk.rollerest.RollerTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manager for teleport requests
 */
public class TpaManager {
    private final RollerTest plugin;
    private final Map<UUID, UUID> tpaRequests = new HashMap<>(); // target -> sender
    private final Map<UUID, BukkitTask> tpaTimeoutTasks = new HashMap<>();
    private final int timeout;

    /**
     * Constructor for TpaManager
     * @param plugin The plugin instance
     */
    public TpaManager(RollerTest plugin) {
        this.plugin = plugin;
        this.timeout = plugin.getConfig().getInt("settings.tpa-timeout", 60);
    }

    /**
     * Send a teleport request from one player to another
     * @param sender The player sending the request
     * @param target The player receiving the request
     * @return True if the request was sent, false if there's already a pending request
     */
    public boolean sendRequest(Player sender, Player target) {
        UUID targetUUID = target.getUniqueId();
        UUID senderUUID = sender.getUniqueId();
        
        // Check if there's already a pending request
        if (tpaRequests.containsKey(targetUUID) && tpaRequests.get(targetUUID).equals(senderUUID)) {
            return false;
        }
        
        // Cancel any existing timeout task
        if (tpaTimeoutTasks.containsKey(targetUUID)) {
            tpaTimeoutTasks.get(targetUUID).cancel();
        }
        
        // Store the request
        tpaRequests.put(targetUUID, senderUUID);
        
        // Schedule a timeout task
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (tpaRequests.containsKey(targetUUID) && tpaRequests.get(targetUUID).equals(senderUUID)) {
                tpaRequests.remove(targetUUID);
                tpaTimeoutTasks.remove(targetUUID);
                
                Player senderPlayer = Bukkit.getPlayer(senderUUID);
                if (senderPlayer != null && senderPlayer.isOnline()) {
                    MessageUtils.sendMessage(senderPlayer, "messages.tpa.request-expired", "%player%", target.getName());
                }
            }
        }, timeout * 20L); // Convert seconds to ticks
        
        tpaTimeoutTasks.put(targetUUID, task);
        
        return true;
    }

    /**
     * Accept a teleport request
     * @param target The player who received the request
     * @return The sender of the request, or null if there's no pending request
     */
    public Player acceptRequest(Player target) {
        UUID targetUUID = target.getUniqueId();
        
        if (!tpaRequests.containsKey(targetUUID)) {
            return null;
        }
        
        UUID senderUUID = tpaRequests.get(targetUUID);
        Player sender = Bukkit.getPlayer(senderUUID);
        
        // Clean up
        tpaRequests.remove(targetUUID);
        if (tpaTimeoutTasks.containsKey(targetUUID)) {
            tpaTimeoutTasks.get(targetUUID).cancel();
            tpaTimeoutTasks.remove(targetUUID);
        }
        
        return sender;
    }

    /**
     * Deny a teleport request
     * @param target The player who received the request
     * @return The sender of the request, or null if there's no pending request
     */
    public Player denyRequest(Player target) {
        UUID targetUUID = target.getUniqueId();
        
        if (!tpaRequests.containsKey(targetUUID)) {
            return null;
        }
        
        UUID senderUUID = tpaRequests.get(targetUUID);
        Player sender = Bukkit.getPlayer(senderUUID);
        
        // Clean up
        tpaRequests.remove(targetUUID);
        if (tpaTimeoutTasks.containsKey(targetUUID)) {
            tpaTimeoutTasks.get(targetUUID).cancel();
            tpaTimeoutTasks.remove(targetUUID);
        }
        
        return sender;
    }

    /**
     * Check if a player has a pending teleport request
     * @param target The player to check
     * @return True if the player has a pending request, false otherwise
     */
    public boolean hasPendingRequest(Player target) {
        return tpaRequests.containsKey(target.getUniqueId());
    }

    /**
     * Get the sender of a teleport request
     * @param target The player who received the request
     * @return The sender of the request, or null if there's no pending request
     */
    public Player getRequestSender(Player target) {
        UUID targetUUID = target.getUniqueId();
        
        if (!tpaRequests.containsKey(targetUUID)) {
            return null;
        }
        
        UUID senderUUID = tpaRequests.get(targetUUID);
        return Bukkit.getPlayer(senderUUID);
    }

    /**
     * Clean up all requests for a player when they disconnect
     * @param player The player who disconnected
     */
    public void cleanupRequests(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        // Remove requests where the player is the target
        if (tpaRequests.containsKey(playerUUID)) {
            if (tpaTimeoutTasks.containsKey(playerUUID)) {
                tpaTimeoutTasks.get(playerUUID).cancel();
                tpaTimeoutTasks.remove(playerUUID);
            }
            tpaRequests.remove(playerUUID);
        }
        
        // Remove requests where the player is the sender
        tpaRequests.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(playerUUID)) {
                UUID targetUUID = entry.getKey();
                if (tpaTimeoutTasks.containsKey(targetUUID)) {
                    tpaTimeoutTasks.get(targetUUID).cancel();
                    tpaTimeoutTasks.remove(targetUUID);
                }
                return true;
            }
            return false;
        });
    }
}