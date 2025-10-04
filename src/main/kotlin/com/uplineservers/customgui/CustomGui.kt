package com.uplineservers.customgui

import com.uplineservers.customgui.commands.CommandRegisterer
import com.uplineservers.customgui.listeners.ListenerRegisterer
import com.uplineservers.customgui.services.GuiManager
import com.uplineservers.customgui.services.GuiStore
import org.bukkit.plugin.java.JavaPlugin

class CustomGui : JavaPlugin() {

    companion object {
        lateinit var instance: CustomGui
            private set
    }

    override fun onEnable() {
        instance = this
        logger.info("Enabling ${this.name} plugin...")

        saveDefaultConfig()

        // Register event listeners
        ListenerRegisterer(this)
        
        // Register commands
        CommandRegisterer(this)
        
        logger.info("${this.name} plugin has been enabled successfully!")
    }

    override fun onDisable() {
        logger.info("Disabling ${this.name} plugin...")

        // Make sure to save every gui
        GuiManager.getGuis().forEach { gui ->
            if (gui.dataItem != null || gui.dataEntity != null)
                GuiStore.save(gui)
            GuiManager.removeAll(gui)
        }
    }

}
