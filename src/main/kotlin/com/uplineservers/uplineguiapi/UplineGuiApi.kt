package com.uplineservers.uplineguiapi

import com.uplineservers.uplineguiapi.commands.CommandRegisterer
import com.uplineservers.uplineguiapi.listeners.ListenerRegisterer
import com.uplineservers.uplineguiapi.services.GuiManager
import com.uplineservers.uplineguiapi.services.GuiStore
import org.bukkit.plugin.java.JavaPlugin

class UplineGuiApi : JavaPlugin() {

    companion object {
        lateinit var instance: UplineGuiApi
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
