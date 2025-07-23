package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.entities.GUIEntity
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
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
}