package com.uplineservers.customGUI.commands

import com.uplineservers.customGUI.models.GUI
import com.uplineservers.customGUI.models.GUIItem
import com.uplineservers.customGUI.services.GUIBuild
import com.uplineservers.customGUI.services.GUIOpen
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
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

        // Check if gui already exists
        if(GUIOpen.openIfExists(sender, "test_menu"))
            return true

        // Create a new GUI using the service manager
        val gui = GUI(
            id = "test_menu",
            title = "§6§lMain Menu",
            size = 27,
            removalDelay = 5
        )

        // Set up GUI items using the GUI build service
        val itemStack = ItemStack(Material.DIAMOND_SWORD)
        itemStack.itemMeta.displayName(Component.text("§bExample Item"))
        itemStack.itemMeta.lore(listOf(Component.text("§7This is an example item.")))

        gui.items[13] = GUIItem(
            item = itemStack,
            onClick = { event ->
                event.whoClicked.sendMessage("§aYou clicked the example item!")
            }
        )

        gui.items[0] = GUIItem(
            item = ItemStack(Material.AIR),
            isMovable = true
        )

        gui.items[1] = GUIItem(
            item = ItemStack(Material.DIAMOND_BLOCK),
            onClick = { event ->
                val player = event.whoClicked
                if(player is Player)
                    player.give(ItemStack(Material.DIAMOND_BLOCK, 1))
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

        gui.onOpen = { event ->
            event.player.sendMessage("§7Welcome to the main menu!")
        }
        
        gui.onClose = { event ->
            event.player.sendMessage("§7Thanks for using the main menu!")
        }

        GUIBuild.build(gui)

        sender.openInventory(gui.inventory!!)
        
        return true
    }
}
