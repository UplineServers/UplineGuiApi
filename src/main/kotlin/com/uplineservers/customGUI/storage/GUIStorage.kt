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

        fun add(gui: GUIEntity) {
            guis[gui.id] = gui
        }

        fun addPlayer(gui: GUIEntity, player: Player) {
            if (!gui.players.contains(player)) {
                gui.players.add(player)
                playersGUI[player] = gui.id // Store player-GUI mapping
            }
        }

        fun removePlayer(player: Player) {
            val guiId = playersGUI.remove(player) ?: return
            val gui = guis[guiId] ?: return
            gui.players.remove(player)
            if (gui.players.isEmpty()) {
                guis.remove(guiId) // Remove GUI if no players are left
            }
        }

        fun remove(gui: GUIEntity) {
            guis.remove(gui.id)
        }
    }
}