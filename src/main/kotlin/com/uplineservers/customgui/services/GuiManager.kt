package com.uplineservers.customgui.services

import com.uplineservers.customgui.CustomGui
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
    }

    // ─── Players ─────────────────────────────────────────────────

    fun addPlayer(gui: Gui, player: Player) {
        playersGUI[player.uniqueId] = gui.id
    }

    fun getPlayers(id: String): List<Player> {
        return playersGUI
            .filterValues { it == id }
            .keys
            .mapNotNull { Bukkit.getPlayer(it) }
    }

    fun removeAll(gui: Gui) {
        val players = getPlayers(gui.id)
        players.forEach { removePlayer(it, true) }
    }

    fun removePlayer(player: Player, force: Boolean = false) {
        val gui = this.getByPlayer(player) ?: return

        if (gui.dataItem != null || gui.dataEntity != null)
            GuiStore.save(gui)

        playersGUI.remove(player.uniqueId) ?: return

        if (getPlayers(gui.id).isEmpty())
            delete(gui)

        if (force) {
            InventoryManager.load(player)
        } else {
            Bukkit.getScheduler().runTaskLater(CustomGui.instance, Runnable {
                if (playersGUI.containsKey(player.uniqueId)) return@Runnable

                player.sendMessage("§cThe GUI has been closed.")
                InventoryManager.load(player)
            }, 1L)
        }
    }

}