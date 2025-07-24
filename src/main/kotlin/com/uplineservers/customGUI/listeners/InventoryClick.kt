package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.services.GUISync
import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.java.JavaPlugin

class InventoryClick(private val plugin: JavaPlugin) : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val gui = GUIStorage.getByPlayer(player) ?: return

        if(gui.isUpdating) {
            // Ignore clicks while the GUI is updating
            event.isCancelled = true
            return
        }

        val clickedSlot = event.rawSlot
        val clickedItem = event.currentItem

        // Handle clicks only in GUI inventory area (not player inventory)
        if (clickedSlot < gui.size) {
            val guiItem = gui.items[clickedSlot]

            // Allow per-slot click
            guiItem?.onClick?.invoke(event)
            gui.onClick?.invoke(event)

            if(event.isCancelled) return

            // Handle item pickup from GUI
            if (event.cursor.type != Material.AIR && !(guiItem?.isMovable ?: gui.isPutable)) {
                event.isCancelled = true
            }

            // Handle item taking from GUI
            if (clickedItem != null && clickedItem.type != Material.AIR && !(guiItem?.isMovable ?: gui.isTakeable)) {
                event.isCancelled = true
            }
        }

        if(event.clickedInventory?.type == InventoryType.PLAYER){
            gui.onPlayerInventoryClick?.invoke(event)
            if(event.isCancelled) return
        }

        if (event.isShiftClick && event.clickedInventory?.type == InventoryType.PLAYER) {
            event.isCancelled = true

            if( clickedItem == null || clickedItem.type == Material.AIR)
                return

            // Try manually placing the item in a valid slot
            for (slot in 0 until gui.size) {
                val guiItem = gui.items[slot]
                val slotItem = gui.inventory?.getItem(slot)

                val isPutable = guiItem?.isMovable ?: gui.isPutable

                val canStack = slotItem != null &&
                        slotItem.type == clickedItem.type &&
                        slotItem.amount < slotItem.maxStackSize

                val isEmpty = slotItem == null || slotItem.type == Material.AIR

                if (!isPutable) continue
                if (!(canStack || isEmpty)) continue

                val toInsert = clickedItem.clone()
                if (canStack) {
                    val spaceLeft = slotItem.maxStackSize - slotItem.amount
                    val toAdd = toInsert.amount.coerceAtMost(spaceLeft)

                    slotItem.amount += toAdd
                    clickedItem.amount -= toAdd

                    if (clickedItem.amount <= 0) {
                        player.inventory.setItem(event.slot, null)
                    } else {
                        player.inventory.setItem(event.slot, clickedItem)
                    }

                } else if (isEmpty) {
                    val toInsert = clickedItem.clone()
                    val toPlace = toInsert.amount.coerceAtMost(clickedItem.maxStackSize)

                    toInsert.amount = toPlace
                    gui.inventory?.setItem(slot, toInsert)

                    clickedItem.amount -= toPlace
                    if (clickedItem.amount <= 0) {
                        player.inventory.setItem(event.slot, null)
                    } else {
                        player.inventory.setItem(event.slot, clickedItem)
                    }
                }

                player.inventory.setItem(event.slot, clickedItem)

                break
            }
        }

        // Handle number key (hotbar swap)
        if (event.click == ClickType.NUMBER_KEY) {
            val guiSlot = event.rawSlot

            if (guiSlot < gui.size) {
                val guiItem = gui.items[guiSlot]
                val isMovable = guiItem?.isMovable ?: gui.isPutable

                if (!isMovable) {
                    event.isCancelled = true
                }
            }
        }

        GUISync().syncInventoryToAllPlayers(gui)
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked as? Player ?: return
        val gui = GUIStorage.getByPlayer(player) ?: return

        for (slot in event.rawSlots) {
            if (slot < gui.size) {
                val guiItem = gui.items[slot]
                if (!(guiItem?.isMovable ?: gui.isPutable)) {
                    event.isCancelled = true
                    return
                }
            }
        }
    }
}