package com.uplineservers.customgui.commands

import com.uplineservers.customgui.examples.ItemSavedGui
import com.uplineservers.customgui.examples.MenuExampleGui
import com.uplineservers.customgui.examples.MoveableGui
import com.uplineservers.customgui.examples.PutableGui
import com.uplineservers.customgui.examples.TakeableGui
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestCommand : SubCommand{
    private val options = listOf("menu", "putable", "takeable", "moveable", "action", "item")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be used by players!")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§eUsage: /customgui test <${options.joinToString("|")}>")
            return true
        }

        when (args[0].lowercase()) {
            "menu" -> MenuExampleGui(sender)
            "putable" -> PutableGui(sender)
            "takeable" -> TakeableGui(sender)
            "moveable" -> MoveableGui(sender)
            "item" -> ItemSavedGui(sender)
            else -> sender.sendMessage("§cUnknown test GUI: ${args[0]}")
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> options.filter { it.startsWith(args[0].lowercase()) }
            else -> emptyList()
        }
    }
}