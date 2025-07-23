package com.uplineservers.customGUI

import com.uplineservers.customGUI.commands.GUIHelpCommand
import com.uplineservers.customGUI.commands.MainMenuCommand
import com.uplineservers.customGUI.listeners.InventoryListener
import com.uplineservers.customGUI.services.ServiceManager
import org.bukkit.plugin.java.JavaPlugin

class CustomGUI : JavaPlugin() {
    
    // Service manager for dependency injection
    private lateinit var serviceManager: ServiceManager

    override fun onEnable() {
        logger.info("Enabling CustomGUI plugin...")
        
        // Initialize service manager and all services
        serviceManager = ServiceManager.initialize(this)
        
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
        
        // Clean up services
        if (::serviceManager.isInitialized) {
            serviceManager.guiManager.clearAll()
            serviceManager.shutdown()
        }
        
        logger.info("CustomGUI plugin has been disabled!")
    }
    
    /**
     * Register all event listeners
     */
    private fun registerListeners() {
        logger.info("Registering event listeners...")
        
        val pluginManager = server.pluginManager
        pluginManager.registerEvents(InventoryListener(serviceManager.guiManager), this)
        
        logger.info("Event listeners registered successfully!")
    }
    
    /**
     * Register all commands
     */
    private fun registerCommands() {
        logger.info("Registering commands...")
        
        getCommand("guihelp")?.setExecutor(GUIHelpCommand(this))
        getCommand("mainmenu")?.setExecutor(MainMenuCommand(serviceManager))
        
        // Add other commands here as you create them
        // getCommand("confirmdialog")?.setExecutor(ConfirmDialogCommand(serviceManager))
        // getCommand("dynamicgui")?.setExecutor(DynamicGUICommand(serviceManager))
        
        logger.info("Commands registered successfully!")
    }
    
    /**
     * Get the service manager for dependency injection
     */
    fun getServiceManager(): ServiceManager = serviceManager
    
    companion object {
        private lateinit var _instance: CustomGUI

        fun getInstance(): CustomGUI = _instance
    }
    
    init {
        _instance = this
    }
}
