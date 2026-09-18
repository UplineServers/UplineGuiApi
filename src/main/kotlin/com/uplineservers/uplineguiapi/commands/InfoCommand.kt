package com.uplineservers.uplineguiapi.commands

import com.uplineservers.uplineguiapi.UplineGuiApi
import com.uplineservers.uplineguiapi.services.GuiManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class InfoCommand() : SubCommand {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("§6§l=== ${UplineGuiApi.instance.name} Info ===")
        sender.sendMessage("§7Players in Guis: §a${GuiManager.getPlayers().size}")
        sender.sendMessage("§7Guis Loaded: §a${GuiManager.getGuis().size}")
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return emptyList()
    }
}
