package com.uplineservers.customGUI.entities

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import java.util.*

/**
 * Simple GUI entity - just data
 */
data class GUIEntity(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "Custom GUI",
    var type: InventoryType? = null,

    val size: Int = 27,
    val items: MutableMap<Int, GUIItem> = mutableMapOf(),

    val isPutable: Boolean = false,
    val isTakeable: Boolean = false,

    var inventory: Inventory? = null,

    var players: MutableList<Player> = mutableListOf(),

    // Simple event callbacks
    var onOpen: ((Player) -> Unit)? = null,
    var onClose: ((Player) -> Unit)? = null,
    var onClick: ((Player, Int?) -> Unit)? = null
)
