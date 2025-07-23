package com.uplineservers.customGUI.managers

import com.uplineservers.customGUI.CustomGUI
import com.uplineservers.customGUI.commands.ConfirmDialogCommand
import com.uplineservers.customGUI.commands.DynamicInventoryCommand
import com.uplineservers.customGUI.commands.GUIHelpCommand
import com.uplineservers.customGUI.commands.MainMenuCommand
import org.bukkit.command.CommandExecutor

/**
 * Central command manager that registers all CustomGUI commands
 */
class CommandManager(private val plugin: CustomGUI) {

    // Map of command names to their executors
    private val commands: Map<String, CommandExecutor> = mapOf(
        "mainmenu" to MainMenuCommand(plugin),
        "confirmdialog" to ConfirmDialogCommand(plugin),
        "dynamicgui" to DynamicInventoryCommand(plugin),
        "guihelp" to GUIHelpCommand(plugin)
        // Note: Removed guitest for cleaner structure - use individual commands instead
    )

    /**
     * Registers all commands with the plugin
     */
    fun registerCommands() {
        commands.forEach { (commandName, executor) ->
            val command = plugin.getCommand(commandName)
            if (command != null) {
                command.setExecutor(executor)
                plugin.logger.info("Registered command: /$commandName")
            } else {
                plugin.logger.warning("Failed to register command: /$commandName (not found in plugin.yml)")
            }
        }
    }
}