package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.entities.GUIEntity
import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.inventory.ItemStack

class GUIGet {

    /**
     * Get item from GUI by slot
     * Returns null if the slot is empty or GUI is not found
     */
    fun getItem(gui: GUIEntity, slot: Int): ItemStack? {
        return gui.inventory?.getItem(slot)
    }

    /**
     * Get GUI by ID
     */
    fun getGUI(id: String): GUIEntity? {
        return GUIStorage.guis.get(id)
    }

}