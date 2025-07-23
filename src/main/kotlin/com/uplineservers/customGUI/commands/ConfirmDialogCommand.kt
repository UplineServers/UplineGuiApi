package com.uplineservers.customGUI.commands

import com.uplineservers.customGUI.CustomGUI
import com.uplineservers.customGUI.examples.ConfirmationDialogGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Command to open a confirmation dialog GUI
 */
class ConfirmDialogCommand(private val plugin: CustomGUI) : CommandExecutor {
    
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be used by players!")
            return true
        }
        
        val title = if (args.isNotEmpty()) {
            args.joinToString(" ")
        } else {
            "Test Confirmation"
        }
        
        val confirmDialog = ConfirmationDialogGUI.create(
            title = title,
            onConfirm = { player ->
                player.sendMessage("§aYou confirmed the action!")
            },
            onCancel = { player ->
                player.sendMessage("§cYou cancelled the action!")
            }
        )
        
        plugin.guiManager.openGUI(sender, confirmDialog)
        
        return true
    }
}
