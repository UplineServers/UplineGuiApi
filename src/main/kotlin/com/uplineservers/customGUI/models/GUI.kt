package com.uplineservers.customGUI.models

import com.uplineservers.customGUI.services.GUIBuild
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
    SMART,
}

class GUI(
    val id: String = UUID.randomUUID().toString(),
    var type: InventoryType? = null,
    title: String = "Custom GUI",

    val size: Int = 27,
    val items: MutableMap<Int, GUIItem> = mutableMapOf(),

    val isPutable: Boolean = false,
    val isTakeable: Boolean = false,
    var isUpdating: Boolean = false,
    val shiftProtection: SHIFT_PROTECTION = SHIFT_PROTECTION.SMART,

    var inventory: Inventory? = null,
    var dataItem: ItemStack? = null,

    // Timeout for deleing the gui
    var removalTask: BukkitTask? = null,
    var removalDelay: Long = 0L,

    // Simple event callbacks
    var onOpen: ((InventoryOpenEvent) -> Unit)? = null,
    var onClose: ((InventoryCloseEvent) -> Unit)? = null,
    var onClick: ((InventoryClickEvent) -> Unit)? = null,
    var onPlayerInventoryClick: ((InventoryClickEvent) -> Unit)? = null,
) {
    private var _title: String = title

    var title: String
        get() = _title
        set(value) {
            if (_title != value) {
                _title = value
                GUIBuild.update(this)
            }
        }
}