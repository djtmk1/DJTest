package org.djtmk.djtest.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.djtmk.djtest.utils.MessageUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Command to open a trash inventory
 */
public class TrashCommand implements CommandExecutor, Listener {

    private static final String PERMISSION = "rollertest.trash";
    private static final String TRASH_TITLE = "Trash";
    private final Set<UUID> openTrashInventories = new HashSet<>();

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

        // Create a trash inventory
        Inventory trashInventory = Bukkit.createInventory(null, 36, TRASH_TITLE);

        // Open the inventory for the player
        player.openInventory(trashInventory);
        
        // Track this inventory as a trash inventory
        openTrashInventories.add(player.getUniqueId());

        // Send message
        MessageUtils.sendMessage(player, "messages.trash.opened");

        return true;
    }

    /**
     * Handle inventory close events to clear trash inventories
     * @param event The inventory close event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if this is a trash inventory
        if (openTrashInventories.contains(playerUUID) && 
            event.getView().getTitle().equals(TRASH_TITLE)) {
            
            // Remove the player from the tracking set
            openTrashInventories.remove(playerUUID);
            
            // Send message
            MessageUtils.sendMessage(player, "messages.trash.closed");
            
            // Note: We don't need to clear the inventory as it's not persistent
            // and will be garbage collected when no longer referenced
        }
    }
}