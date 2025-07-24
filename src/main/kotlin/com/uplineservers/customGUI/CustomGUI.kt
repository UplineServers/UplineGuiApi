package com.uplineservers.customGUI

import com.uplineservers.customGUI.commands.CommandRegisterer
import com.uplineservers.customGUI.listeners.ListenerRegisterer
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
        
        // Save default config
        saveDefaultConfig()
        
        logger.info("CustomGUI plugin has been enabled successfully!")
    }

    override fun onDisable() {
        logger.info("Disabling CustomGUI plugin...")
    }
}
