package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.plugin.java.JavaPlugin

class InventoryOpen(plugin: JavaPlugin) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryOpen(event: InventoryOpenEvent) {
        val player = event.player
        if (player !is Player) return

        val gui = GUIStorage.getByInventory(event.inventory)
        if (gui == null)  return
        if (gui.isUpdating) return

        GUIStorage.cancelRemoval(gui)
        GUIStorage.addPlayer(gui.id, player)

        if (gui.onOpen != null)
            gui.onOpen!!.invoke(event)
    }
}