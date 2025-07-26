package com.uplineservers.customGUI.storage

import com.uplineservers.customGUI.models.GUI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class GUIStorage {
    companion object {
        val guis: MutableMap<String, GUI> = mutableMapOf()
        val playersGUI: MutableMap<Player, String> = mutableMapOf()

        fun getById(id: String): GUI? {
            return guis[id]
        }

        fun getByPlayer(player: Player): GUI? {
            val guiId = playersGUI[player] ?: return null
            return guis[guiId]
        }

        fun getByInventory(inventory: Inventory): GUI? {
            return guis.values.find { it.inventory == inventory }
        }

        fun add(gui: GUI) {
            guis[gui.id] = gui
        }

        fun findPlayers(id: String): List<Player> {
            return playersGUI.filterValues { it == id }.keys.toList()
        }

        fun addPlayer(id: String, player: Player) {
            playersGUI[player] = id
        }

        fun removePlayer(player: Player) {
            playersGUI.remove(player) ?: return
        }

        fun scheduleRemoval(gui: GUI, plugin: JavaPlugin) {
            gui.removalTask?.cancel()
            
            gui.removalTask = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                if (findPlayers(gui.id).isEmpty()) {
                    plugin.logger.info("Removing GUI after timeout: ${gui.title}")
                    this.remove(gui)
                }
            }, gui.removalDelay * 20L)
        }

        fun cancelRemoval(gui: GUI) {
            gui.removalTask?.cancel()
            gui.removalTask = null
        }

        fun remove(gui: GUI) {
            guis.remove(gui.id)
        }
    }
}