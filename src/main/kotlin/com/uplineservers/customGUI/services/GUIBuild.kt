package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.entities.GUIEntity
import com.uplineservers.customGUI.storage.GUIStorage
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import kotlin.collections.component1
import kotlin.collections.component2

class GUIBuild {
    fun build(gui: GUIEntity) {
        if(gui.type == null)
            gui.type = InventoryType.CHEST

        val inventory = when (gui.type) {
            InventoryType.CHEST -> {
                require(gui.size % 9 == 0) { "Chest size must be a multiple of 9" }
                Bukkit.createInventory(null, gui.size, Component.text(gui.title))
            }
            else -> {
                Bukkit.createInventory(null, gui.type!!, Component.text(gui.title))
            }
        }

        gui.items.forEach { (slot, item) ->
            inventory.setItem(slot, item.item)
        }

        gui.inventory = inventory
        GUIStorage.add(gui)
    }
}