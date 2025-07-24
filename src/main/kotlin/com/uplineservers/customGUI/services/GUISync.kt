package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.entities.GUIEntity

class GUISync {
    fun syncInventoryToAllPlayers(gui: GUIEntity) {
        if (gui.inventory == null || gui.players.size < 2) return

        gui.isUpdating = true
        // Update inventory contents for all players viewing this GUI
        for (player in gui.players) {
            // Copy the current inventory contents to each player's view
            for (slot in 0 until gui.inventory!!.size) {
                val item = gui.inventory!!.getItem(slot)
                player.openInventory.setItem(slot, item?.clone())
            }
        }
        gui.isUpdating = false
    }
}