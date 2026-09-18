package com.uplineservers.uplineguiapi.commands

import com.uplineservers.uplineguiapi.examples.InventoryMenu
import com.uplineservers.uplineguiapi.examples.ItemSavedGui
import com.uplineservers.uplineguiapi.examples.MenuExampleGui
import com.uplineservers.uplineguiapi.examples.MoveableGui
import com.uplineservers.uplineguiapi.examples.PutableGui
import com.uplineservers.uplineguiapi.examples.TakeableGui
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestCommand : SubCommand{
    private val options = listOf("menu", "putable", "takeable", "moveable", "item", "inventory")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be used by players!")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§eUsage: /uplineguiapi test <${options.joinToString("|")}>")
            return true
        }

        when (args[0].lowercase()) {
            "menu" -> MenuExampleGui(sender)
            "putable" -> PutableGui(sender)
            "takeable" -> TakeableGui(sender)
            "moveable" -> MoveableGui(sender)
            "item" -> ItemSavedGui(sender)
            "inventory" -> InventoryMenu(sender)
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