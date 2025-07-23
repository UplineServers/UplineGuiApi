package com.uplineservers.customGUI

import com.uplineservers.customGUI.commands.GUIHelpCommand
import com.uplineservers.customGUI.commands.GUITestCommand
import com.uplineservers.customGUI.listeners.InventoryListener
import org.bukkit.plugin.java.JavaPlugin

class CustomGUI : JavaPlugin() {
    
    // Service manager for dependency injection

    override fun onEnable() {
        logger.info("Enabling CustomGUI plugin...")
        
        // Register event listeners
        registerListeners()
        
        // Register commands
        registerCommands()
        
        // Save default config
        saveDefaultConfig()
        
        logger.info("CustomGUI plugin has been enabled successfully!")
    }

    override fun onDisable() {
        logger.info("Disabling CustomGUI plugin...")
    }
    
    /**
     * Register all event listeners
     */
    private fun registerListeners() {
        logger.info("Registering event listeners...")
        
        val pluginManager = server.pluginManager
        pluginManager.registerEvents(InventoryListener(this), this)
        
        logger.info("Event listeners registered successfully!")
    }

    /**
     * Register all commands
     */
    private fun registerCommands() {
        logger.info("Registering commands...")
        
        this.getCommand("guihelp")?.setExecutor(GUIHelpCommand(this))
        this.getCommand("guitest")?.setExecutor(GUITestCommand())
        
        // Add other commands here as you create them
        // getCommand("confirmdialog")?.setExecutor(ConfirmDialogCommand(serviceManager))
        // getCommand("dynamicgui")?.setExecutor(DynamicGUICommand(serviceManager))
        
        logger.info("Commands registered successfully!")
    }
    
    /**
     * Get the service manager for dependency injection
     */

    companion object {
        private lateinit var _instance: CustomGUI

        fun getInstance(): CustomGUI = _instance
    }
    
    init {
        _instance = this
    }
}
