package com.uplineservers.uplineguiapi.models

import com.uplineservers.uplineguiapi.services.GuiBuild
import com.uplineservers.uplineguiapi.services.GuiManager
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * How a [Gui] reacts to shift-clicks, which move whole stacks across inventories and easily
 * bypass slot-by-slot rules.
 */
enum class SHIFT_PROTECTION {
    /** Shift-clicks follow the normal put/take rules. */
    NONE,

    /** Every shift-click inside the GUI is cancelled. */
    BLOCK,
}

/**
 * How much of the player's own inventory a [Gui] takes over when it uses slots above 53.
 *
 * The real inventory is backed up on open and restored on close, and item pickup is blocked
 * while such a GUI is open.
 */
enum class EXTRA_INVENTORY {
    /** Takes over storage slots `9`-`35`; GUI slots `54`-`80` map onto them. */
    MAIN,

    /** Takes over the whole inventory; adds GUI slots `81`-`89` for hotbar slots `0`-`8`. */
    FULL,
}

/**
 * A custom inventory screen: its layout, its interaction rules and its callbacks.
 *
 * Fill [items], call [build] once to create and register the inventory, then [open] it for a
 * player:
 *
 * ```
 * val gui = Gui(id = "main_menu", title = "<gold><bold>Main Menu", size = 27)
 * gui.items[13] = GuiItem(icon) { it.whoClicked.sendMessage("clicked!") }
 * gui.build()
 * gui.open(player)
 * ```
 *
 * A GUI is unregistered as soon as its last viewer closes it, so the usual entry point looks up
 * the existing instance first:
 *
 * ```
 * GuiManager.getById("main_menu")?.let { return it.open(player) }
 * ```
 *
 * @property id Unique key in [GuiManager]. Building twice with the same id throws.
 * @property type Bukkit inventory type. `CHEST` uses [size]; other types use their own layout.
 * @property size Chest slot count. Must be a multiple of 9.
 * @property extraSize Enables the player-inventory overlay; `null` disables it.
 * @property items Slot to item mapping. Slots above 53 require [extraSize].
 * @property isPutable Default rule for placing items into GUI slots.
 * @property isTakeable Default rule for taking items out of GUI slots.
 * @property isUpdating While `true`, all clicks and item drops are cancelled.
 * @property shiftProtection Whether shift-clicks are allowed at all.
 * @property inventory The live Bukkit inventory; `null` until [build] is called.
 * @property dataItem When set, contents are persisted into this item's persistent data.
 * @property dataEntity When set, contents are persisted into this entity's persistent data.
 * @property onOpen Called after a viewer is registered and the overlay is applied.
 * @property onClose Called before a viewer is removed and their inventory restored.
 * @property onClick Called first on every click; cancel the event to stop all further handling.
 * @property onCreate Called when the GUI is registered, during [build].
 * @property onDestroy Called when the last viewer leaves and the GUI is unregistered.
 * @see GuiItem
 * @see GuiManager
 */
class Gui(
    var id: String = UUID.randomUUID().toString(),
    var type: InventoryType = InventoryType.CHEST,
    title: String = "Custom GUI",

    val size: Int = 27,
    val extraSize: EXTRA_INVENTORY? = null,
    val items: MutableMap<Int, GuiItem> = mutableMapOf(),

    val isPutable: Boolean = false,
    val isTakeable: Boolean = false,
    var isUpdating: Boolean = false,
    val shiftProtection: SHIFT_PROTECTION = SHIFT_PROTECTION.NONE,

    var inventory: Inventory? = null,
    var dataItem: ItemStack? = null,
    var dataEntity: Entity? = null,

    // Simple event callbacks
    var onOpen: ((InventoryOpenEvent) -> Unit)? = null,
    var onClose: ((InventoryCloseEvent) -> Unit)? = null,
    var onClick: ((InventoryClickEvent) -> Unit)? = null,

    var onCreate: ((gui: Gui) -> Unit)? = null,
    var onDestroy: ((gui: Gui) -> Unit)? = null,
) {

    private var _title: String = title

    /**
     * The screen title, as a [MiniMessage](https://docs.advntr.dev/minimessage/format.html) string.
     *
     * Assigning a different value calls [update], which recreates the inventory and reopens it
     * for every viewer, so avoid changing it every tick.
     */
    var title: String
        get() = _title
        set(value) {
            if (_title != value) {
                _title = value
                if(this.inventory != null) this.update()
            }
        }

    /**
     * Shows this GUI to [player].
     *
     * @throws IllegalStateException if [build] has not been called yet.
     */
    fun open(player: Player) {
        if(this.inventory == null)
            throw IllegalStateException("GUI inventory is null for id: $id")
        player.openInventory(this.inventory!!)
    }

    /**
     * Creates the Bukkit inventory, loads persisted contents when [dataItem] or [dataEntity] is
     * set, and registers the GUI (firing [onCreate]). Call this exactly once.
     *
     * @throws IllegalArgumentException if the id is already registered, if [size] is not a
     * multiple of 9, or if a slot in [items] is out of range for [extraSize].
     */
    fun build(){
        GuiBuild.build(this)
    }

    /**
     * Recreates the inventory from the current [title], [type] and [size], carries the existing
     * contents over and reopens it for every viewer.
     */
    fun update(){
        GuiBuild.update(this)
    }

    /**
     * Pushes a single entry of [items] back into the live inventory, or into [player]'s own
     * inventory when [slot] belongs to the overlay.
     */
    fun updateSlot(slot: Int, player: Player){
        GuiBuild.updateSlot(this, slot, player);
    }

    /** Registers [player] as a viewer. Normally handled by the open listener. */
    fun addPlayer(player: Player) {
        GuiManager.addPlayer(this, player)
    }
}