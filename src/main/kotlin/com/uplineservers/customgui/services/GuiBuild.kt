package com.uplineservers.customgui.services

import com.uplineservers.customgui.models.Gui
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

object GuiBuild {
    private fun createInventory(gui: Gui): Inventory {
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

    fun build(gui: Gui) {
        if(GuiManager.getById(gui.id) != null)
            throw IllegalArgumentException("GUI with id ${gui.id} already exists")

        val inventory = createInventory(gui)
        gui.items.forEach { (slot, item) ->
            inventory.setItem(slot, item.item)
        }

        gui.inventory = inventory
        if (gui.dataItem != null || gui.dataEntity != null)
            GuiStorage.load(gui)

        GuiManager.add(gui)
    }

    fun update(gui: Gui) {
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
        for (player in GuiManager.getPlayers(gui.id)) {
            player.closeInventory()
            player.openInventory(inventory)
        }
        gui.isUpdating = false
    }
}
