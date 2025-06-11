package org.djtmk.rollerest.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.djtmk.rollerest.RollerTest;

/**
 * Utility class for handling plugin messages
 */
public class MessageUtils {
    private static RollerTest plugin;
    private static FileConfiguration config;

    /**
     * Initialize the MessageUtils with the plugin instance
     * @param instance The plugin instance
     */
    public static void initialize(RollerTest instance) {
        plugin = instance;
        config = plugin.getConfig();
    }

    /**
     * Get a message from the config
     * @param path The path to the message in the config
     * @return The formatted message with color codes translated
     */
    public static String getMessage(String path) {
        String message = config.getString(path);
        if (message == null) {
            return ChatColor.RED + "Missing message: " + path;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Get a message from the config with the prefix
     * @param path The path to the message in the config
     * @return The formatted message with prefix and color codes translated
     */
    public static String getMessageWithPrefix(String path) {
        String prefix = getMessage("messages.prefix");
        String message = getMessage(path);
        return prefix + message;
    }

    /**
     * Send a message to a command sender
     * @param sender The command sender
     * @param path The path to the message in the config
     */
    public static void sendMessage(CommandSender sender, String path) {
        sender.sendMessage(getMessageWithPrefix(path));
    }

    /**
     * Send a message to a command sender with replacements
     * @param sender The command sender
     * @param path The path to the message in the config
     * @param replacements The replacements in format [placeholder1, value1, placeholder2, value2, ...]
     */
    public static void sendMessage(CommandSender sender, String path, String... replacements) {
        String message = getMessageWithPrefix(path);
        
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        
        sender.sendMessage(message);
    }

    /**
     * Check if the sender has permission and send a message if not
     * @param sender The command sender
     * @param permission The permission to check
     * @return True if the sender has permission, false otherwise
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sendMessage(sender, "messages.no-permission");
            return false;
        }
        return true;
    }

    /**
     * Check if the sender is a player and send a message if not
     * @param sender The command sender
     * @return The player if the sender is a player, null otherwise
     */
    public static Player checkPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "messages.player-only");
            return null;
        }
        return (Player) sender;
    }

    /**
     * Check if the sender is a player and has permission
     * @param sender The command sender
     * @param permission The permission to check
     * @return The player if the sender is a player and has permission, null otherwise
     */
    public static Player checkPlayerAndPermission(CommandSender sender, String permission) {
        Player player = checkPlayer(sender);
        if (player == null) {
            return null;
        }
        
        if (!hasPermission(player, permission)) {
            return null;
        }
        
        return player;
    }
}