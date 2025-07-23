package com.uplineservers.customGUI.commands

import com.uplineservers.customGUI.entities.GUIEntity
import com.uplineservers.customGUI.services.ServiceManager
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Example command showing how to use services with dependency injection
 */
class MainMenuCommand(private val serviceManager: ServiceManager) : CommandExecutor {
    
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
        
        // Use the service to set items
        serviceManager.guiBuild.setItem(gui, 13, itemStack)
        
        // Set up click handler
        gui.onClick = { player, guiEntity, slot, item ->
            if (slot == 13) {
                player.sendMessage("§aYou clicked the example item!")
            }
        }
        
        // Set up open handler
        gui.onOpen = { player, guiEntity ->
            player.sendMessage("§7Welcome to the main menu!")
        }
        
        // Set up close handler
        gui.onClose = { player, guiEntity ->
            player.sendMessage("§7Thanks for using the main menu!")
        }
        
        // Create and open the GUI using the GUI manager
        serviceManager.guiManager.createGUI(gui)
        serviceManager.guiManager.openGUI(sender, gui)
        
        return true
    }
}
