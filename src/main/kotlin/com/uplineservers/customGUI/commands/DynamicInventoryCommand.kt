package com.uplineservers.customGUI.commands

import com.uplineservers.customGUI.CustomGUI
import com.uplineservers.customGUI.examples.DynamicInventoryGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Command to open a dynamic inventory GUI
 */
class DynamicInventoryCommand(private val plugin: CustomGUI) : CommandExecutor {
    
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be used by players!")
            return true
        }
        
        val dynamicGUI = DynamicInventoryGUI.create()
        plugin.guiManager.openGUI(sender, dynamicGUI)
        
        return true
    }
}
