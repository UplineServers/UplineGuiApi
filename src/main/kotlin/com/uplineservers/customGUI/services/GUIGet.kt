package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.entities.GUIEntity
import org.bukkit.inventory.ItemStack

class GUIGet {

    fun getItem(gui: GUIEntity, slot: Int): ItemStack? {
        return gui.inventory?.getItem(slot)
    }

}