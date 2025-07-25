package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.entities.GUIEntity
import com.uplineservers.customGUI.storage.GUIStorage
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import kotlin.collections.component1
import kotlin.collections.component2

class GUIBuild {
    companion object {

        private fun createInventory(gui: GUIEntity): org.bukkit.inventory.Inventory {
            if (gui.type == null)
                gui.type = InventoryType.CHEST

            return when (gui.type) {
                InventoryType.CHEST -> {
                    require(gui.size % 9 == 0) { "Chest size must be a multiple of 9" }
                    Bukkit.createInventory(null, gui.size, Component.text(gui.title))
                }

                else -> {
                    Bukkit.createInventory(null, gui.type!!, Component.text(gui.title))
                }
            }
        }

        fun build(gui: GUIEntity) {
            val inventory = createInventory(gui)
            gui.items.forEach { (slot, item) ->
                inventory.setItem(slot, item.item)
            }
            gui.inventory = inventory
            GUIStorage.add(gui)
        }

        fun update(gui: GUIEntity) {
            if (gui.inventory == null) return
            val oldContents = gui.inventory!!.contents.copyOf()

            val inventory = createInventory(gui)

            // Keep all previous contents
            oldContents.forEachIndexed { index, item ->
                if (item != null) {
                    inventory.setItem(index, item.clone())
                }
            }

            gui.inventory = inventory

            gui.isUpdating = true
            for (player in gui.players) {
                player.closeInventory()
                player.openInventory(inventory)
            }
            gui.isUpdating = false
        }
    }
}
