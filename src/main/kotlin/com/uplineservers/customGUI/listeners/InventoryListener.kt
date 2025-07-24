package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.java.JavaPlugin

/**
 * Handles inventory events for custom GUIs
 */
class InventoryListener(private val plugin: JavaPlugin) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryOpen(event: org.bukkit.event.inventory.InventoryOpenEvent) {
        val player = event.player as Player

        // Check if this is a custom GUI using the global registry
        val gui = GUIStorage.getByInventory(event.inventory)
        plugin.logger.info("Player ${player.name} opened a GUI: ${gui?.title ?: "Unknown"}")
        if (gui != null && gui.onOpen != null) {
            // Call the onOpen callback if it exists
            GUIStorage.addPlayer(gui, player);
            gui.onOpen!!.invoke(player)
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val gui = GUIStorage.getByPlayer(player) ?: return

        val clickedSlot = event.rawSlot
        val clickedItem = event.currentItem

        // Handle clicks only in GUI inventory area (not player inventory)
        if (clickedSlot < gui.size) {
            val guiItem = gui.items[clickedSlot]

            // Allow per-slot click
            guiItem?.onClick?.invoke(event)
            gui.onClick?.invoke(player, clickedSlot)

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
            val hotbarSlot = event.hotbarButton
            val guiSlot = event.rawSlot

            if (guiSlot < gui.size) {
                val guiItem = gui.items[guiSlot]
                val isMovable = guiItem?.isMovable ?: gui.isPutable

                if (!isMovable) {
                    event.isCancelled = true
                }
            }
        }
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

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player

        val gui = GUIStorage.getByPlayer(player as Player)
        if (gui != null && gui.onClose != null) {
            gui.onClose!!.invoke(player)
            if (gui.players.isEmpty()) {
                plugin.logger.info("All players have closed the GUI: ${gui.title}. Cleaning up.")
                GUIStorage.remove(gui);
            } else {
                plugin.logger.info("Player ${player.name} closed the GUI: ${gui.title}. Remaining players: ${gui.players.size}")
            }
        }
        GUIStorage.removePlayer(player)
    }
}
