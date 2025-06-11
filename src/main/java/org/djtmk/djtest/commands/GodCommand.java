package org.djtmk.djtest.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.djtmk.djtest.utils.MessageUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Command to toggle god mode for a player
 */
public class GodCommand implements CommandExecutor {

    private static final String PERMISSION = "rollertest.god";
    private final Set<UUID> godModePlayers = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender has permission
        if (!MessageUtils.hasPermission(sender, PERMISSION)) {
            return true;
        }

        // If a player is specified, toggle their god mode
        if (args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtils.sendMessage(sender, "messages.player-not-found");
                return true;
            }

            // Toggle god mode for the target
            boolean godMode = toggleGodMode(target);

            // Send messages
            if (godMode) {
                MessageUtils.sendMessage(sender, "messages.god.enabled-other", "%player%", target.getName());
                if (!sender.equals(target)) {
                    MessageUtils.sendMessage(target, "messages.god.enabled-by-other", "%player%", sender.getName());
                }
            } else {
                MessageUtils.sendMessage(sender, "messages.god.disabled-other", "%player%", target.getName());
                if (!sender.equals(target)) {
                    MessageUtils.sendMessage(target, "messages.god.disabled-by-other", "%player%", sender.getName());
                }
            }
        } else {
            // If no player is specified, the sender must be a player
            Player player = MessageUtils.checkPlayer(sender);
            if (player == null) {
                return true;
            }

            // Toggle god mode for the sender
            boolean godMode = toggleGodMode(player);

            // Send message
            if (godMode) {
                MessageUtils.sendMessage(player, "messages.god.enabled-self");
            } else {
                MessageUtils.sendMessage(player, "messages.god.disabled-self");
            }
        }

        return true;
    }

    /**
     * Toggle god mode for a player
     * @param player The player to toggle god mode for
     * @return True if god mode is now enabled, false if disabled
     */
    public boolean toggleGodMode(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (godModePlayers.contains(uuid)) {
            godModePlayers.remove(uuid);
            player.setInvulnerable(false);
            return false;
        } else {
            godModePlayers.add(uuid);
            player.setInvulnerable(true);
            return true;
        }
    }

    /**
     * Check if a player has god mode enabled
     * @param player The player to check
     * @return True if the player has god mode enabled, false otherwise
     */
    public boolean hasGodMode(Player player) {
        return godModePlayers.contains(player.getUniqueId());
    }

    /**
     * Remove a player from god mode when they disconnect
     * @param player The player who disconnected
     */
    public void removePlayer(Player player) {
        godModePlayers.remove(player.getUniqueId());
    }
}