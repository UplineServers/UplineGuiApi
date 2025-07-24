package com.uplineservers.customGUI.storage

import com.uplineservers.customGUI.entities.GUIEntity
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class GUIStorage {
    companion object {
        val guis = ConcurrentHashMap<String, GUIEntity>()
        val playersGUI = ConcurrentHashMap<Player, String>()

        fun getById(id: String): GUIEntity? {
            return guis[id]
        }

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
        }

        fun scheduleRemoval(gui: GUIEntity, plugin: JavaPlugin) {
            // Cancel any existing removal task
            gui.removalTask?.cancel()
            
            // Schedule a new removal task
            gui.removalTask = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                if (gui.players.isEmpty()) {
                    plugin.logger.info("Removing GUI after timeout: ${gui.title}")
                    guis.remove(gui.id)
                }
            }, gui.removalDelay * 20L) // Convert seconds to ticks (20 ticks = 1 second)
        }

        fun cancelRemoval(gui: GUIEntity) {
            gui.removalTask?.cancel()
            gui.removalTask = null
        }

        fun remove(gui: GUIEntity) {
            guis.remove(gui.id)
        }

        fun toNBT(): Map<String, Any> {
            // Convert the GUI storage to a serializable format
            return guis.mapValues { (_, gui) ->
                mapOf(
                    "id" to gui.id,
                    "title" to gui.title,
                    "size" to gui.size,
                    "items" to gui.inventory?.contents?.map { it?.serialize() ?: emptyMap<Any, Any>() },
                )
            }
        }
    }
}