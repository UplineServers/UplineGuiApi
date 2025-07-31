package com.uplineservers.customGUI

import com.uplineservers.customGUI.commands.CommandRegisterer
import com.uplineservers.customGUI.listeners.ListenerRegisterer
import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.plugin.java.JavaPlugin

class CustomGUI : JavaPlugin() {

    companion object {
        lateinit var instance: CustomGUI
            private set
    }

    override fun onEnable() {
        instance = this
        logger.info("Enabling CustomGUI plugin...")
        
        // Register event listeners
        ListenerRegisterer(this)
        
        // Register commands
        CommandRegisterer(this)
        
        logger.info("CustomGUI plugin has been enabled successfully!")
    }

    override fun onDisable() {
        logger.info("Disabling CustomGUI plugin...")

        GUIStorage.guis.values.forEach { gui ->
            if (gui.dataItem != null && gui.inventory != null) {
                GUIStorage.saveInventoryToItem(gui)
            }
        }
    }
}
