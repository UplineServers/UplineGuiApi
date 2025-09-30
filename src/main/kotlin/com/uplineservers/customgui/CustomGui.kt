package com.uplineservers.customgui

import com.uplineservers.customgui.commands.CommandRegisterer
import com.uplineservers.customgui.listeners.ListenerRegisterer
import com.uplineservers.customgui.services.GuiStorage
import org.bukkit.plugin.java.JavaPlugin

class CustomGui : JavaPlugin() {

    companion object {
        lateinit var instance: CustomGui
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

        GuiStorage.guis.values.forEach { gui ->
            if ((gui.dataItem != null || gui.dataEntity != null) && gui.inventory != null) {
                GuiStorage.saveInventory(gui)
            }
        }
    }

}
