package com.uplineservers.uplineguiapi.services

import com.uplineservers.uplineguiapi.UplineGuiApi
import com.uplineservers.uplineguiapi.models.Gui
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.UUID

/**
 * Registry of every built [Gui] and of which player is viewing which one.
 *
 * ```
 * GuiManager.getById("main_menu")?.open(player)
 * ```
 *
 * A GUI is removed from the registry when its last viewer closes it, so always look one up
 * before rebuilding it.
 */
object GuiManager {

    /** Registered GUIs by ID */
    private val guis: MutableMap<String, Gui> = mutableMapOf()

    /** Tracks which GUI a player currently has open */
    private val playersGUI: MutableMap<UUID, String> = mutableMapOf()


    // ─── Retrieval ───────────────────────────────────────────────

    /** The registered GUI with this id, or `null`. */
    fun getById(id: String): Gui? = guis[id]

    /** The GUI [player] currently has open, or `null`. */
    fun getByPlayer(player: Player): Gui? {
        val guiId = playersGUI[player.uniqueId] ?: return null
        return guis[guiId]
    }

    /** Reverse lookup from a Bukkit inventory to the GUI that owns it. */
    fun getByInventory(inventory: Inventory): Gui? {
        return guis.values.find { it.inventory == inventory }
    }

    /** The GUI id of every current viewer; its `size` is the number of players inside a GUI. */
    fun getPlayers() = playersGUI.values

    /** Every registered GUI. */
    fun getGuis() = guis.values

    // ─── Management ──────────────────────────────────────────────

    /** Fires [Gui.onCreate] and registers the GUI. Normally called by [Gui.build]. */
    fun add(gui: Gui) {
        gui.onCreate?.invoke(gui)
        guis[gui.id] = gui
    }

    /** Fires [Gui.onDestroy] and unregisters the GUI. */
    fun delete(gui: Gui) {
        gui.onDestroy?.invoke(gui)
        guis.remove(gui.id)
    }

    // ─── Players ─────────────────────────────────────────────────

    /** Marks [player] as a viewer of [gui]. */
    fun addPlayer(gui: Gui, player: Player) {
        playersGUI[player.uniqueId] = gui.id
    }

    /** The online viewers of the GUI with this id. */
    fun getPlayers(id: String): List<Player> {
        return playersGUI
            .filterValues { it == id }
            .keys
            .mapNotNull { Bukkit.getPlayer(it) }
    }

    /** Force-removes every viewer of [gui], restoring their real inventories. */
    fun removeAll(gui: Gui) {
        val players = getPlayers(gui.id)
        players.forEach { removePlayer(it, true) }
    }

    /**
     * Removes [player] as a viewer: persists the contents if the GUI is bound to an item or
     * entity, deletes the GUI when this was the last viewer, and restores the real inventory.
     *
     * @param force restore the inventory immediately instead of one tick later. The delay lets a
     * player move straight from one GUI into another without their backup being clobbered.
     */
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
            Bukkit.getScheduler().runTaskLater(UplineGuiApi.instance, Runnable {
                if (playersGUI.containsKey(player.uniqueId)) return@Runnable

                InventoryManager.load(player)
            }, 1L)
        }
    }

}