package com.uplineservers.customgui.services

import com.uplineservers.customgui.models.Gui
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

object GuiManager {
    val guis: MutableMap<String, Gui> = mutableMapOf()
    val playersGUI: MutableMap<Player, String> = mutableMapOf()

    fun getById(id: String): Gui? {
        return guis[id]
    }

    fun getByPlayer(player: Player): Gui? {
        val guiId = playersGUI[player] ?: return null
        return guis[guiId]
    }

    fun getByInventory(inventory: Inventory): Gui? {
        return guis.values.find { it.inventory == inventory }
    }

    fun add(gui: Gui) {
        gui.onCreate?.invoke(gui)
        guis[gui.id] = gui
    }

    fun getPlayers(id: String): List<Player> {
        return playersGUI.filterValues { it == id }.keys.toList()
    }

    fun addPlayer(id: String, player: Player) {
        playersGUI[player] = id

        // cancel removal
        guis[id]?.removalTask?.cancel()
        guis[id]?.removalTask = null
    }

    fun removePlayer(player: Player) {
        playersGUI.remove(player) ?: return
    }

    fun scheduleRemoval(gui: Gui, plugin: JavaPlugin) {
        gui.removalTask?.cancel()

        gui.removalTask = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (getPlayers(gui.id).isEmpty())
                this.remove(gui)
        }, gui.removalDelay * 20L)
    }

    fun remove(gui: Gui) {
        gui.onDestroy?.invoke(gui)
        guis.remove(gui.id)

        // close all inventories of players using this GUI
        getPlayers(gui.id).forEach { player ->
            player.closeInventory()
        }
    }
}