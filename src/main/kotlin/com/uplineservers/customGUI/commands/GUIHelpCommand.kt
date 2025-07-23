package com.uplineservers.customGUI.commands

import com.uplineservers.customGUI.CustomGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

/**
 * Command to display help information about all available GUI commands
 */
class GUIHelpCommand(private val plugin: CustomGUI) : CommandExecutor {
    
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("§6§l=== CustomGUI Help ===")
        sender.sendMessage("§e/mainmenu §7- Opens the main menu GUI")
        sender.sendMessage("§e/confirmdialog [title] §7- Opens a confirmation dialog")
        sender.sendMessage("§e/dynamicgui §7- Opens a dynamic inventory that updates")
        sender.sendMessage("§e/guihelp §7- Shows this help message")
        sender.sendMessage("")
        sender.sendMessage("§7Plugin version: ${plugin.pluginMeta.version}")
        sender.sendMessage("§7Author: ${plugin.pluginMeta.authors.joinToString(", ")}")
        
        return true
    }
}
