package com.uplineservers.customGUI.commands

import org.bukkit.plugin.java.JavaPlugin

class CommandRegisterer(plugin: JavaPlugin) {
    /**
     * Register all commands
     */
    init {
        plugin.logger.info("Registering commands...")

        plugin.getCommand("guitest")?.setExecutor(TestCommand())
        plugin.getCommand("guiinfo")?.setExecutor(InfoCommand())

        plugin.logger.info("Commands registered successfully!")
    }
}