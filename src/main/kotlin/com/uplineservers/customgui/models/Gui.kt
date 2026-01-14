package com.uplineservers.customgui.models

import com.uplineservers.customgui.services.GuiBuild
import com.uplineservers.customgui.services.GuiManager
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.util.*

enum class SHIFT_PROTECTION {
    NONE,
    BLOCK,
}

enum class EXTRA_INVENTORY {
    MAIN,
    FULL,
}

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

    var title: String
        get() = _title
        set(value) {
            if (_title != value) {
                _title = value
                if(this.inventory != null) this.update()
            }
        }

    fun open(player: Player) {
        if(this.inventory == null)
            throw IllegalStateException("GUI inventory is null for id: $id")
        player.openInventory(this.inventory!!)
    }

    fun build(){
        GuiBuild.build(this)
    }

    fun update(){
        GuiBuild.update(this)
    }

    fun updateSlot(slot: Int, player: Player){
        GuiBuild.updateSlot(this, slot, player);
    }

    fun addPlayer(player: Player) {
        GuiManager.addPlayer(this, player)
    }
}