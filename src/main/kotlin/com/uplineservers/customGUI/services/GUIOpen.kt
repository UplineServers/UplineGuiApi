package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.entity.Player

class GUIOpen {
    companion object {
        fun openIfExists(player: Player, id: String): Boolean {
            val existingGui = GUIStorage.getById(id)
            if (existingGui == null) return false

            if (existingGui.inventory == null)
                throw IllegalStateException("GUI inventory is null for id: $id")

            player.openInventory(existingGui.inventory!!)
            return true
        }
    }
}