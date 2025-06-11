package org.djtmk.djtest.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.djtmk.djtest.utils.MessageUtils;

/**
 * Command to open another player's inventory
 */
public class OpenInvCommand implements CommandExecutor {

    private static final String PERMISSION = "rollertest.openinv";

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

        // Check if there are enough arguments
        if (args.length < 1) {
            MessageUtils.sendMessage(sender, "messages.openinv.usage");
            return true;
        }

        // Get the target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendMessage(sender, "messages.player-not-found");
            return true;
        }

        // Check if the player is trying to open their own inventory
        if (player.equals(target)) {
            player.openInventory(player.getInventory());
            return true;
        }

        // Create a copy of the target's inventory
        Inventory inventory = Bukkit.createInventory(
                target, // Set the target as the holder so changes affect them
                36, // Player inventory size (excluding armor and offhand)
                target.getName() + "'s Inventory"
        );

        // Copy the target's inventory contents
        inventory.setContents(target.getInventory().getContents());

        // Open the inventory for the player
        player.openInventory(inventory);

        // Send message
        MessageUtils.sendMessage(player, "messages.openinv.opened", "%player%", target.getName());

        return true;
    }
}