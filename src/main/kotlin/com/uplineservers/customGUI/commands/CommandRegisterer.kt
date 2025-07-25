package com.uplineservers.customGUI.commands

import org.bukkit.plugin.java.JavaPlugin

class CommandRegisterer(private val plugin: JavaPlugin) {
    /**
     * Register all commands
     */
    init {
        plugin.logger.info("Registering commands...")

        plugin.getCommand("guitest")?.setExecutor(TestCommand())
        plugin.getCommand("guiinfo")?.setExecutor(InfoCommand(plugin))

        // Add other commands here as you create them
        // getCommand("confirmdialog")?.setExecutor(ConfirmDialogCommand(serviceManager))
        // getCommand("dynamicgui")?.setExecutor(DynamicGUICommand(serviceManager))

        plugin.logger.info("Commands registered successfully!")
    }
}