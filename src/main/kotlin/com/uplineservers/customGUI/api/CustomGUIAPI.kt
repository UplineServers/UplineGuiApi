package com.uplineservers.customGUI.api

import com.uplineservers.customGUI.CustomGUI
import com.uplineservers.customGUI.entities.GUIEntity
import com.uplineservers.customGUI.services.ServiceManager
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Public API for other plugins to interact with CustomGUI
 * This provides a clean interface without exposing internal services
 */
class CustomGUIAPI {
    
    private val serviceManager: ServiceManager
        get() = CustomGUI.getInstance().getServiceManager()
    
    /**
     * Create a new GUI
     */
    fun createGUI(gui: GUIEntity): GUIEntity {
        return serviceManager.guiManager.createGUI(gui)
    }
    
    /**
     * Open a GUI for a player
     */
    fun openGUI(player: Player, gui: GUIEntity) {
        serviceManager.guiManager.openGUI(player, gui)
    }
    
    /**
     * Close a GUI for a player
     */
    fun closeGUI(player: Player, gui: GUIEntity) {
        serviceManager.guiManager.closeGUI(player, gui)
    }
    
    /**
     * Set an item in a GUI
     */
    fun setItem(gui: GUIEntity, slot: Int, item: ItemStack?) {
        serviceManager.guiBuild.setItem(gui, slot, item)
    }
    
    /**
     * Get an item from a GUI
     */
    fun getItem(gui: GUIEntity, slot: Int): ItemStack? {
        return serviceManager.guiGet.getItem(gui, slot)
    }
    
    /**
     * Get a GUI by its ID
     */
    fun getGUI(id: String): GUIEntity? {
        return serviceManager.guiManager.getGUI(id)
    }
    
    /**
     * Remove a GUI
     */
    fun removeGUI(id: String) {
        serviceManager.guiManager.removeGUI(id)
    }
    
    companion object {
        private var _instance: CustomGUIAPI? = null
        
        /**
         * Get the API instance (singleton)
         */
        fun getInstance(): CustomGUIAPI {
            if (_instance == null) {
                _instance = CustomGUIAPI()
            }
            return _instance!!
        }
    }
}
