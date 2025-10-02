package com.uplineservers.customgui.listeners

import com.uplineservers.customgui.services.GuiManager
import com.uplineservers.customgui.services.InventoryManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.plugin.java.JavaPlugin

class InventoryOpen(plugin: JavaPlugin) : Listener {

    @EventHandler()
    fun onInventoryOpen(event: InventoryOpenEvent) {
        val player = event.player as? Player ?: return
        val gui = GuiManager.getByInventory(event.inventory) ?: return

        GuiManager.addPlayer(gui.id, player)

        if(gui.extraSize != null) {
            InventoryManager.save(player, gui.extraSize)
            gui.items
                .filterKeys { it > 53 }
                .forEach { (slot, item) ->
                    val targetSlot = if (slot >= 81) slot - 81 else slot - 45
                    player.inventory.setItem(targetSlot, item.item)
                }
        }

        if (gui.onOpen != null)
            gui.onOpen!!.invoke(event)
    }
}