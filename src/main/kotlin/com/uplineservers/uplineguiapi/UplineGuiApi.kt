package com.uplineservers.uplineguiapi

import com.uplineservers.uplineguiapi.commands.CommandRegisterer
import com.uplineservers.uplineguiapi.listeners.ListenerRegisterer
import com.uplineservers.uplineguiapi.services.GuiManager
import com.uplineservers.uplineguiapi.services.GuiStore
import org.bukkit.plugin.java.JavaPlugin

/**
 * Plugin entry point. Registers the listeners and commands, and on disable saves every bound
 * GUI and restores the inventories of anyone still viewing one.
 */
class UplineGuiApi : JavaPlugin() {

    companion object {
        /** The running plugin instance, available from `onEnable` onwards. */
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
