package com.uplineservers.customGUI.commands

import com.uplineservers.customGUI.CustomGUI
import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

/**
 * Command to display status information about the plugin.
 */
class InfoCommand(private val plugin: JavaPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("§6§l=== CustomGUI Info ===")
        sender.sendMessage("§eGuis Loaded: §a${GUIStorage.guis.size}")
        sender.sendMessage("§Player Guis References Loaded: §a${GUIStorage.playersGUI.size}")
        return true
    }
}
