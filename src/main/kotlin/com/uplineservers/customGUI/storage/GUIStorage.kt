package com.uplineservers.customGUI.storage

import com.uplineservers.customGUI.entities.GUIEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.concurrent.ConcurrentHashMap

class GUIStorage {
    companion object {
        val guis = ConcurrentHashMap<String, GUIEntity>()
        val playersGUI = ConcurrentHashMap<Player, String>()

        fun getByPlayer(player: Player): GUIEntity? {
            val guiId = playersGUI[player] ?: return null
            return guis[guiId]
        }

        fun getByInventory(inventory: Inventory): GUIEntity? {
            return guis.values.find { it.inventory == inventory }
        }

        fun cleanup() {
            // Remove all players from GUIs
            guis.values.forEach { gui ->
                gui.players.clear()
            }
            // Clear all stored GUIs
            guis.clear()
            // Clear player GUI mappings
            playersGUI.clear()
        }
    }
}