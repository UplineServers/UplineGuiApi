package com.uplineservers.customGUI.entities

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

data class CustomInventory(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Custom Inventory",
    val type: InventoryType? = null,
    val size: Int = 0,
    val items: MutableMap<Int, ItemStack> = mutableMapOf(),
    var _inventory: Inventory? = null,

    // Event listeners - these can be set per GUI instance
    var onCloseListener: ((Player, CustomInventory) -> Unit)? = null,
    var onOpenListener: ((Player, CustomInventory) -> Unit)? = null,
    var onClickListener: ((Player, CustomInventory, Int, ItemStack?) -> Unit)? = null
) {

    val inventory: Inventory?
        get() = _inventory

    fun setItem(slot: Int, item: ItemStack?) {
        require(slot in 0 until this.size) { "Slot $slot is out of bounds for inventory size $this.size" }

        if (item == null || isAirItem(item)) {
            this.items.remove(slot)
        } else {
            this.items[slot] = item
        }
    }

    private fun isAirItem(item: ItemStack): Boolean {
        return try {
            item.type == Material.AIR
        } catch (e: ExceptionInInitializerError) {
            // In test environments where Material.AIR can't be initialized,
            // assume it's not an air item
            false
        }
    }

    fun getItem(slot: Int): ItemStack? {
        require(slot in 0 until this.size) { "Slot $slot is out of bounds for inventory size $this.size" }
        return this.items[slot]
    }

    // Event trigger methods - these will be called by your event handling system
    fun onClose(player: Player) {
        onCloseListener?.invoke(player, this)
    }

    fun onOpen(player: Player) {
        onOpenListener?.invoke(player, this)
    }

    fun onClick(player: Player, slot: Int) {
        val clickedItem = getItem(slot)
        onClickListener?.invoke(player, this, slot, clickedItem)
    }

    fun build(): Inventory {
        val inventoryType = type ?: InventoryType.CHEST

        val inventory = if (size > 0) {
            Bukkit.createInventory(null, inventoryType, Component.text(title))
        } else {
            Bukkit.createInventory(null, inventoryType, Component.text(title))
        }
        
        items.forEach { (slot, item) ->
            inventory.setItem(slot, item)
        }

        this._inventory = inventory
        return inventory
    }
}