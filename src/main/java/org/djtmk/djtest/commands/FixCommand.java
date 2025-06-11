package org.djtmk.djtest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.djtmk.djtest.utils.MessageUtils;

/**
 * Command to repair the item in the player's hand
 */
public class FixCommand implements CommandExecutor {

    private static final String PERMISSION = "rollertest.fix";

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

        // Get the item in the player's main hand
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if the player is holding an item
        if (item == null || item.getType().isAir()) {
            MessageUtils.sendMessage(player, "messages.fix.no-item");
            return true;
        }

        // Check if the item has durability
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            MessageUtils.sendMessage(player, "messages.fix.not-repairable");
            return true;
        }

        // Check if the item is already at full durability
        Damageable damageable = (Damageable) meta;
        if (!damageable.hasDamage()) {
            MessageUtils.sendMessage(player, "messages.fix.not-repairable");
            return true;
        }

        // Repair the item
        damageable.setDamage(0);
        item.setItemMeta(meta);

        // Send message
        MessageUtils.sendMessage(player, "messages.fix.success");

        return true;
    }
}