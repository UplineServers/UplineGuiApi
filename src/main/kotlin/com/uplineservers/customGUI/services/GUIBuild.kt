package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.entities.GUIEntity
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.collections.component1
import kotlin.collections.component2

class GUIBuild {

    fun build(gui: GUIEntity) {
        val inventory = if (gui.type != null) {
            Bukkit.createInventory(null, gui.type, Component.text(gui.title))
        } else {
            Bukkit.createInventory(null, gui.size, Component.text(gui.title))
        }

        // Set items
        gui.items.forEach { (slot, item) ->
            inventory.setItem(slot, item)
        }

        gui.inventory = inventory
    }

    fun setItem(gui: GUIEntity, slot: Int, item: ItemStack?) {
        if (item == null || item.type == Material.AIR) {
            gui.items.remove(slot)
        } else {
            gui.items[slot] = item
        }
        gui.inventory?.setItem(slot, item)
    }
}