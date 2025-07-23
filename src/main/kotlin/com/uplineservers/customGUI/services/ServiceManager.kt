package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.CustomGUI

/**
 * Service Manager for dependency injection
 * This replaces Spring's @Autowired functionality
 */
class ServiceManager(private val plugin: CustomGUI) {
    
    // Service instances
    private lateinit var _guiBuild: GUIBuild
    private lateinit var _guiGet: GUIGet
    private lateinit var _guiPlayer: GUIPlayer
    private lateinit var _guiManager: GUIManager
    
    // Public accessors (like @Autowired beans)
    val guiBuild: GUIBuild get() = _guiBuild
    val guiGet: GUIGet get() = _guiGet
    val guiPlayer: GUIPlayer get() = _guiPlayer
    val guiManager: GUIManager get() = _guiManager
    
    /**
     * Initialize all services with dependency injection
     */
    fun initialize() {
        plugin.logger.info("Initializing services...")
        
        // Initialize services in correct dependency order
        _guiBuild = GUIBuild()
        _guiGet = GUIGet()
        _guiPlayer = GUIPlayer()
        
        // GUIManager depends on other services
        _guiManager = GUIManager(
            guiBuild = _guiBuild,
            guiGet = _guiGet,
            guiPlayer = _guiPlayer
        )
        
        plugin.logger.info("All services initialized successfully!")
    }
    
    /**
     * Clean up services on plugin disable
     */
    fun shutdown() {
        plugin.logger.info("Shutting down services...")
        // Add any cleanup logic here if needed
    }
    
    companion object {
        private lateinit var _instance: ServiceManager
        
        /**
         * Get the service manager instance (singleton pattern)
         */
        fun getInstance(): ServiceManager = _instance
        
        /**
         * Initialize the service manager (called from main plugin class)
         */
        fun initialize(plugin: CustomGUI): ServiceManager {
            _instance = ServiceManager(plugin)
            _instance.initialize()
            return _instance
        }
    }
}
