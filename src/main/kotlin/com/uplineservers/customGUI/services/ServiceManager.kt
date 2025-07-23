package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.CustomGUI

/**
 * Service Manager for dependency injection
 * This replaces Spring's @Autowired functionality
 */
class ServiceManager(private val plugin: CustomGUI) {
    
    // Service instances (GUIManager removed - replaced by GUIRegistry)
    private lateinit var _guiBuild: GUIBuild
    private lateinit var _guiGet: GUIGet
    private lateinit var _guiPlayer: GUIPlayer
    
    // Public accessors (like @Autowired beans)
    val guiBuild: GUIBuild get() = _guiBuild
    val guiGet: GUIGet get() = _guiGet
    val guiPlayer: GUIPlayer get() = _guiPlayer

    /**
     * Initialize all services with dependency injection
     */
    fun initialize() {
        plugin.logger.info("Initializing services...")
        
        // Initialize individual services (no more GUIManager dependency injection)
        _guiBuild = GUIBuild()
        _guiGet = GUIGet()
        _guiPlayer = GUIPlayer()
        
        // Note: GUIManager is removed - GUIRegistry handles coordination
        // Each service works independently with GUIRegistry as central storage
        
        plugin.logger.info("All services initialized successfully!")
        plugin.logger.info("Using GUIRegistry for centralized GUI management")
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
