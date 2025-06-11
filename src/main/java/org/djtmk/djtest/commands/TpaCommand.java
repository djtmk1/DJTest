package org.djtmk.djtest.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.djtmk.djtest.utils.MessageUtils;
import org.djtmk.djtest.utils.TpaManager;

/**
 * Command to send teleport requests to other players
 */
public class TpaCommand implements CommandExecutor {

    private static final String PERMISSION = "rollertest.tpa";
    private final TpaManager tpaManager;

    /**
     * Constructor for TpaCommand
     * @param tpaManager The TPA manager
     */
    public TpaCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check which command was used
        if (command.getName().equalsIgnoreCase("tpa")) {
            return handleTpaCommand(sender, args);
        } else if (command.getName().equalsIgnoreCase("tpaccept")) {
            return handleTpAcceptCommand(sender);
        } else if (command.getName().equalsIgnoreCase("tpdeny")) {
            return handleTpDenyCommand(sender);
        }
        
        return false;
    }

    /**
     * Handle the /tpa command
     * @param sender The command sender
     * @param args The command arguments
     * @return True if the command was handled, false otherwise
     */
    private boolean handleTpaCommand(CommandSender sender, String[] args) {
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
            MessageUtils.sendMessage(player, "messages.tpa.usage");
            return true;
        }

        // Get the target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtils.sendMessage(player, "messages.player-not-found");
            return true;
        }

        // Check if the player is trying to teleport to themselves
        if (player.equals(target)) {
            MessageUtils.sendMessage(player, "messages.target-self");
            return true;
        }

        // Send the teleport request
        boolean success = tpaManager.sendRequest(player, target);
        if (!success) {
            MessageUtils.sendMessage(player, "messages.tpa.already-sent");
            return true;
        }

        // Send messages
        MessageUtils.sendMessage(player, "messages.tpa.request-sent", "%player%", target.getName());
        MessageUtils.sendMessage(target, "messages.tpa.request-received", "%player%", player.getName());
        MessageUtils.sendMessage(target, "messages.tpa.request-accept-instructions");

        return true;
    }

    /**
     * Handle the /tpaccept command
     * @param sender The command sender
     * @return True if the command was handled, false otherwise
     */
    private boolean handleTpAcceptCommand(CommandSender sender) {
        // The sender must be a player
        Player player = MessageUtils.checkPlayer(sender);
        if (player == null) {
            return true;
        }

        // Check if the player has any pending requests
        if (!tpaManager.hasPendingRequest(player)) {
            MessageUtils.sendMessage(player, "messages.tpa.no-pending-requests");
            return true;
        }

        // Accept the request
        Player requester = tpaManager.acceptRequest(player);
        if (requester == null || !requester.isOnline()) {
            MessageUtils.sendMessage(player, "messages.player-not-found");
            return true;
        }

        // Send messages
        MessageUtils.sendMessage(player, "messages.tpa.request-accepted", "%player%", requester.getName());
        MessageUtils.sendMessage(requester, "messages.tpa.request-accepted-sender", "%player%", player.getName());
        MessageUtils.sendMessage(requester, "messages.tpa.teleporting", "%player%", player.getName());

        // Teleport the requester to the player
        requester.teleport(player.getLocation());

        return true;
    }

    /**
     * Handle the /tpdeny command
     * @param sender The command sender
     * @return True if the command was handled, false otherwise
     */
    private boolean handleTpDenyCommand(CommandSender sender) {
        // The sender must be a player
        Player player = MessageUtils.checkPlayer(sender);
        if (player == null) {
            return true;
        }

        // Check if the player has any pending requests
        if (!tpaManager.hasPendingRequest(player)) {
            MessageUtils.sendMessage(player, "messages.tpa.no-pending-requests");
            return true;
        }

        // Deny the request
        Player requester = tpaManager.denyRequest(player);
        if (requester == null || !requester.isOnline()) {
            MessageUtils.sendMessage(player, "messages.player-not-found");
            return true;
        }

        // Send messages
        MessageUtils.sendMessage(player, "messages.tpa.request-denied", "%player%", requester.getName());
        MessageUtils.sendMessage(requester, "messages.tpa.request-denied-sender", "%player%", player.getName());

        return true;
    }
}