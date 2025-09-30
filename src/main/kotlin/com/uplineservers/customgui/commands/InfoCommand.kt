package com.uplineservers.customgui.commands

import com.uplineservers.customgui.services.GuiManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class InfoCommand() : SubCommand {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("§6§l=== CustomGUI Info ===")
        sender.sendMessage("§7Players in GUIs: §a${GuiManager.playersGUI.size}")
        sender.sendMessage("§7Guis Loaded: §a${GuiManager.guis.size}")
        for (gui in GuiManager.guis.values) {
            sender.sendMessage("§7- §e${gui.id} §7(${gui.title})")
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return emptyList()
    }
}
