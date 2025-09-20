package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.models.GUI
import com.uplineservers.customGUI.storage.GUIStorage

class GUISync {
    companion object {
        fun fullSync(gui: GUI) {
            val players = GUIStorage.findPlayers(gui.id)
            if (gui.inventory == null || players.isEmpty()) return

            gui.isUpdating = true
            for (player in players) {
                for (slot in 0 until gui.inventory!!.size) {
                    val item = gui.inventory!!.getItem(slot)
                    player.openInventory.setItem(slot, item?.clone())
                }
            }
            gui.isUpdating = false
        }

        fun slotSync(gui: GUI, slot: Int) {
            val players = GUIStorage.findPlayers(gui.id)
            if (gui.inventory == null || players.isEmpty()) return

            gui.isUpdating = true
            for (player in players) {
                val item = gui.inventory!!.getItem(slot)
                player.openInventory.setItem(slot, item?.clone())
            }
            gui.isUpdating = false
        }
    }
}