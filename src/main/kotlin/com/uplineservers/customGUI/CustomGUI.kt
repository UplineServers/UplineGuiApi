package com.uplineservers.customGUI

import com.uplineservers.customGUI.managers.CommandManager
import com.uplineservers.customGUI.listeners.InventoryListener
import com.uplineservers.customGUI.managers.GUIManager
import org.bukkit.plugin.java.JavaPlugin

class CustomGUI : JavaPlugin() {
    
    lateinit var guiManager: GUIManager private set
    lateinit var commandManager: CommandManager private set

    override fun onEnable() {
        // Initialize the GUI manager
        guiManager = GUIManager(this)
        
        // Initialize the command manager
        commandManager = CommandManager(this)
        
        // Register event listeners
        server.pluginManager.registerEvents(InventoryListener(guiManager), this)
        
        // Register commands
        commandManager.registerCommands()
        
        logger.info("CustomGUI plugin has been enabled!")
    }

    override fun onDisable() {
        logger.info("CustomGUI plugin has been disabled!")
    }
    
    companion object {
        private lateinit var _instance: CustomGUI

        fun getInstance(): CustomGUI = _instance
    }
    
    init {
        _instance = this
    }
}
