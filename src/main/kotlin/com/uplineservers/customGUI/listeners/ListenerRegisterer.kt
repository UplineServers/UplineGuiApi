package com.uplineservers.customGUI.listeners

import org.bukkit.plugin.java.JavaPlugin

class ListenerRegisterer(private val plugin: JavaPlugin) {
    init {
        plugin.logger.info("Registering event listeners...")

        val pluginManager = plugin.server.pluginManager
        pluginManager.registerEvents(InventoryListener(plugin), plugin)

        plugin.logger.info("Event listeners registered successfully!")
    }
}