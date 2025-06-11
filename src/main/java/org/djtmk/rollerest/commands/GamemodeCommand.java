package org.djtmk.rollerest.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.djtmk.rollerest.utils.MessageUtils;

/**
 * Command to change a player's gamemode
 */
public class GamemodeCommand implements CommandExecutor {

    private static final String PERMISSION = "rollertest.gamemode";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender has permission
        if (!MessageUtils.hasPermission(sender, PERMISSION)) {
            return true;
        }

        // Check if there are enough arguments
        if (args.length < 1) {
            MessageUtils.sendMessage(sender, "messages.gamemode.usage");
            return true;
        }

        // Parse the gamemode
        GameMode gameMode = parseGameMode(args[0]);
        if (gameMode == null) {
            MessageUtils.sendMessage(sender, "messages.gamemode.invalid-gamemode");
            return true;
        }

        // If a player is specified, change their gamemode
        if (args.length > 1) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtils.sendMessage(sender, "messages.player-not-found");
                return true;
            }

            // Change the target's gamemode
            target.setGameMode(gameMode);

            // Send messages
            MessageUtils.sendMessage(sender, "messages.gamemode.changed-other", 
                    "%player%", target.getName(), 
                    "%gamemode%", formatGameMode(gameMode));
            
            if (!sender.equals(target)) {
                MessageUtils.sendMessage(target, "messages.gamemode.changed-by-other", 
                        "%gamemode%", formatGameMode(gameMode), 
                        "%player%", sender.getName());
            }
        } else {
            // If no player is specified, the sender must be a player
            Player player = MessageUtils.checkPlayer(sender);
            if (player == null) {
                return true;
            }

            // Change the sender's gamemode
            player.setGameMode(gameMode);
            MessageUtils.sendMessage(player, "messages.gamemode.changed-self", 
                    "%gamemode%", formatGameMode(gameMode));
        }

        return true;
    }

    /**
     * Parse a gamemode from a string
     * @param mode The string to parse
     * @return The gamemode, or null if invalid
     */
    private GameMode parseGameMode(String mode) {
        mode = mode.toLowerCase();
        
        switch (mode) {
            case "0":
            case "s":
            case "survival":
                return GameMode.SURVIVAL;
            case "1":
            case "c":
            case "creative":
                return GameMode.CREATIVE;
            case "2":
            case "a":
            case "adventure":
                return GameMode.ADVENTURE;
            case "3":
            case "sp":
            case "spectator":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }

    /**
     * Format a gamemode for display
     * @param gameMode The gamemode to format
     * @return The formatted gamemode
     */
    private String formatGameMode(GameMode gameMode) {
        return gameMode.name().toLowerCase();
    }
}