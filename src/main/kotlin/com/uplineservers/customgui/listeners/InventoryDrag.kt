package com.uplineservers.customgui.listeners;

import com.uplineservers.customgui.services.GuiManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.plugin.Plugin

class InventoryDrag(plugin: Plugin) : Listener {
    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked as? Player ?: return
        val gui = GuiManager.getByPlayer(player) ?: return

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
