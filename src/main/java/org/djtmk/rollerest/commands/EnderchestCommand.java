package org.djtmk.rollerest.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.djtmk.rollerest.utils.MessageUtils;

/**
 * Command to open a player's enderchest
 */
public class EnderchestCommand implements CommandExecutor {

    private static final String PERMISSION = "rollertest.enderchest";
    private static final String PERMISSION_OTHERS = "rollertest.enderchest.others";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender has permission
        if (!MessageUtils.hasPermission(sender, PERMISSION)) {
            return true;
        }

        // The sender must be a player
        Player player = MessageUtils.checkPlayer(sender);
        if (player == null) {
            return true;
        }

        // If a player is specified, open their enderchest
        if (args.length > 0) {
            // Check if the sender has permission to open other players' enderchests
            if (!MessageUtils.hasPermission(sender, PERMISSION_OTHERS)) {
                return true;
            }

            // Get the target player
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtils.sendMessage(sender, "messages.player-not-found");
                return true;
            }

            // Open the target's enderchest
            player.openInventory(target.getEnderChest());

            // Send messages
            MessageUtils.sendMessage(player, "messages.enderchest.opened-other", "%player%", target.getName());
            if (!player.equals(target)) {
                MessageUtils.sendMessage(target, "messages.enderchest.opened-by-other", "%player%", player.getName());
            }
        } else {
            // Open the sender's enderchest
            player.openInventory(player.getEnderChest());
            
            // Send message
            MessageUtils.sendMessage(player, "messages.enderchest.opened-self");
        }

        return true;
    }
}