package com.uplineservers.customgui.services

import com.uplineservers.customgui.models.Gui
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.UUID

object GuiManager {

    /** Registered GUIs by ID */
    private val guis: MutableMap<String, Gui> = mutableMapOf()

    /** Tracks which GUI a player currently has open */
    private val playersGUI: MutableMap<UUID, String> = mutableMapOf()


    // ─── Retrieval ───────────────────────────────────────────────

    fun getById(id: String): Gui? = guis[id]

    fun getByPlayer(player: Player): Gui? {
        val guiId = playersGUI[player.uniqueId] ?: return null
        return guis[guiId]
    }

    fun getByInventory(inventory: Inventory): Gui? {
        return guis.values.find { it.inventory == inventory }
    }

    fun getPlayers() = playersGUI.values
    fun getGuis() = guis.values

    // ─── Management ──────────────────────────────────────────────

    fun add(gui: Gui) {
        gui.onCreate?.invoke(gui)
        guis[gui.id] = gui
    }

    fun delete(gui: Gui) {
        gui.onDestroy?.invoke(gui)
        guis.remove(gui.id)
        playersGUI.entries.forEach { player ->
            removePlayer(Bukkit.getPlayer(player.key) ?: return@forEach)
        }
    }

    // ─── Players ─────────────────────────────────────────────────

    fun addPlayer(id: String, player: Player) {
        playersGUI[player.uniqueId] = id
    }

    fun removePlayer(player: Player) {
        playersGUI.remove(player.uniqueId) ?: return
        InventoryManager.load(player)
    }

    fun getPlayers(id: String): List<Player> {
        return playersGUI
            .filterValues { it == id }
            .keys
            .mapNotNull { Bukkit.getPlayer(it) }
    }
}