package com.uplineservers.customgui.listeners

import org.bukkit.plugin.java.JavaPlugin

class ListenerRegisterer(plugin: JavaPlugin) {
    init {
        plugin.logger.info("Registering event listeners...")

        val pluginManager = plugin.server.pluginManager
        pluginManager.registerEvents(InventoryOpen(plugin), plugin)
        pluginManager.registerEvents(InventoryClose(plugin), plugin)
        pluginManager.registerEvents(InventoryClick(plugin), plugin)
        pluginManager.registerEvents(InventoryDrag(plugin), plugin)
        pluginManager.registerEvents(ItemDrop(plugin), plugin)
        
        plugin.logger.info("Event listeners registered successfully!")
    }
}