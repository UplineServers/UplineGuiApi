package com.uplineservers.customgui.services

import com.uplineservers.customgui.models.EXTRA_INVENTORY
import com.uplineservers.customgui.models.Gui
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

object GuiBuild {
    private fun createInventory(gui: Gui): Inventory {
        val title = Component.text(gui.title)

        return when (gui.type) {
            InventoryType.CHEST -> {
                require(gui.size % 9 == 0) { "Chest size must be a multiple of 9 for gui: ${gui.id}" }
                Bukkit.createInventory(null, gui.size, title)
            }

            else -> {
                Bukkit.createInventory(null, gui.type, title)
            }
        }
    }

    fun build(gui: Gui) {
        if(GuiManager.getById(gui.id) != null)
            throw IllegalArgumentException("GUI with id ${gui.id} already exists")

        val inventory = createInventory(gui)

        gui.items.forEach { (slot, item) ->
            if(slot > 53 && gui.extraSize == null)
                throw IllegalArgumentException("Cannot set item in slot $slot for gui: ${gui.id} because extraSize is not set")
            else if(slot > 80 && gui.extraSize != EXTRA_INVENTORY.FULL)
                throw IllegalArgumentException("Cannot set item in slot $slot for gui: ${gui.id} because extraSize is not FULL")

            if(slot > 53) return@forEach
            inventory.setItem(slot, item.item)
        }

        gui.inventory = inventory
        if (gui.dataItem != null || gui.dataEntity != null)
            GuiStore.load(gui)

        GuiManager.add(gui)
    }

    fun update(gui: Gui) {
        if (gui.inventory == null) return

        val oldContents = gui.inventory!!.contents.copyOf()
        val inventory = createInventory(gui)

        oldContents.forEachIndexed { index, item ->
            if (item != null) {
                inventory.setItem(index, item.clone())
            }
        }

        gui.inventory = inventory

        gui.isUpdating = true
        for (player in GuiManager.getPlayers(gui.id)) {
            player.closeInventory()
            player.openInventory(inventory)
        }
        gui.isUpdating = false
    }
}
