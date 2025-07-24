package com.uplineservers.customGUI.commands

import com.uplineservers.customGUI.entities.GUIEntity
import com.uplineservers.customGUI.entities.GUIItem
import com.uplineservers.customGUI.services.GUIBuild
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

/**
 * Example command showing how to use services with dependency injection
 */
class TestCommand() : CommandExecutor {
    
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be used by players!")
            return true
        }

        // Create a new GUI using the service manager
        val gui = GUIEntity(
            title = "§6§lMain Menu",
            size = 27
        )

        // Set up GUI items using the GUI build service
        val itemStack = ItemStack(Material.DIAMOND_SWORD)
        val itemMeta = itemStack.itemMeta
        itemMeta?.setDisplayName("§bExample Item")
        itemMeta?.lore = listOf("§7Click me!")
        itemStack.itemMeta = itemMeta

        // Add item to the GUI items map instead of directly to inventory
        gui.items[13] = GUIItem(
            item = itemStack,
            onClick = { event ->
                sender.sendMessage("§aYou clicked the example item!")
            }
        )

        gui.items[0] = GUIItem(
            item = ItemStack(Material.AIR),
            isMovable = true
        )

        gui.items[1] = GUIItem(
            item = ItemStack(Material.DIAMOND_BLOCK),
            onClick = {
                sender.give(ItemStack(Material.DIAMOND_BLOCK, 1))
                gui.title = "§6§lUpdated Menu"
            }
        )

        gui.items[7] = GUIItem(
            item = ItemStack(Material.DIAMOND_BLOCK),
            isMovable = true,
            onClick = { event ->
                event.isCancelled = true
            }
        )
        
        // Build the inventory using GUIBuild service
        GUIBuild().build(gui)

        // Set up open handler
        gui.onOpen = { event ->
            event.player.sendMessage("§7Welcome to the main menu!")
        }
        
        // Set up close handler
        gui.onClose = { event ->
            event.player.sendMessage("§7Thanks for using the main menu!")
        }
        
        // Create and open the GUI using the GUI manager
        sender.openInventory(gui.inventory!!)
        
        return true
    }
}
