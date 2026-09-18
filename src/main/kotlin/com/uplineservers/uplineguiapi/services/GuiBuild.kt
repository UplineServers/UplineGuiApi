package com.uplineservers.uplineguiapi.services

import com.uplineservers.uplineguiapi.models.EXTRA_INVENTORY
import com.uplineservers.uplineguiapi.models.Gui
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

object GuiBuild {
    private fun createInventory(gui: Gui): Inventory {
        val title = MiniMessage.miniMessage().deserialize(gui.title)

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

    fun buildExtra(gui: Gui, player: Player){
        if(gui.extraSize == null) return

        InventoryManager.save(player, gui.extraSize)
        gui.items
            .filterKeys { it > 53 }
            .forEach { (slot, item) ->
                val targetSlot = if (slot >= 81) slot - 81 else slot - 45
                player.inventory.setItem(targetSlot, item.item)
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

    // TODO: Update old inventory?
    fun update(gui: Gui) {
        if (gui.inventory == null) return

        val newInventory = createInventory(gui)
        gui.inventory!!.contents.withIndex().forEach { (slot, item) ->
            newInventory.setItem(slot, item)
        }
        gui.inventory = newInventory

        for (player in GuiManager.getPlayers(gui.id))
            player.openInventory(newInventory)
    }

    fun updateSlot(gui: Gui, slot: Int, player: Player) {
        if(slot < 54) {
            if(gui.inventory == null) return
            gui.inventory!!.setItem(slot, gui.items[slot]?.item)
        }

        else if (gui.extraSize != null) {
            val targetSlot = if (slot >= 81) slot - 81 else slot - 45
            player.inventory.setItem(targetSlot, gui.items[slot]?.item)
        }
    }

}
