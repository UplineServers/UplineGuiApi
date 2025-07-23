package com.uplineservers.customGUI.entities

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Simple GUI entity - just data
 */
data class GUIEntity(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "Custom GUI",
    val type: InventoryType? = null,
    val size: Int = 27,
    val items: MutableMap<Int, ItemStack> = mutableMapOf(),
    var inventory: Inventory? = null,
    var players: List<Player> = emptyList(),
    
    // Simple event callbacks
    var onOpen: ((Player, GUIEntity) -> Unit)? = null,
    var onClose: ((Player, GUIEntity) -> Unit)? = null,
    var onClick: ((Player, GUIEntity, Int, ItemStack?) -> Unit)? = null
)
