package com.uplineservers.uplineguiapi.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

interface SubCommand : CommandExecutor, TabCompleter

class CommandRegisterer(private val plugin: JavaPlugin) : SubCommand {
    private val subCommands: Map<String, SubCommand> = mapOf(
        "test" to TestCommand(),
        "info" to InfoCommand()
    )

    init {
        plugin.logger.info("Registering commands...")
        plugin.getCommand("uplineguiapi")?.setExecutor(this)
        plugin.getCommand("uplineguiapi")?.tabCompleter = this
        plugin.logger.info("Commands registered successfully!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§eUsage: /uplineguiapi <${subCommands.keys.joinToString("|")}> [args]")
            return true
        }

        val subCommand = subCommands[args[0].lowercase()]
        return subCommand?.onCommand(sender, command, label, args.drop(1).toTypedArray())
            ?: run {
                sender.sendMessage("§cUnknown subcommand: ${args[0]}")
                true
            }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.isEmpty()) return subCommands.keys.toList()

        return when (args.size) {
            1 -> subCommands.keys.filter { it.startsWith(args[0].lowercase()) }
            else -> subCommands[args[0].lowercase()]?.onTabComplete(sender, command, alias, args.drop(1).toTypedArray()) ?: emptyList()
        }
    }
}