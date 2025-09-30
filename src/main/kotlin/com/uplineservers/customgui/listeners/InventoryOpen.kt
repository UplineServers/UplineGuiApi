package com.uplineservers.customgui.listeners

import com.uplineservers.customgui.services.GuiStorage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.plugin.java.JavaPlugin

class InventoryOpen(plugin: JavaPlugin) : Listener {

    @EventHandler()
    fun onInventoryOpen(event: InventoryOpenEvent) {
        val player = event.player as? Player ?: return
        val gui = GuiStorage.getByInventory(event.inventory)

        if (gui == null)  return
        if (gui.isUpdating) return

        GuiStorage.cancelRemoval(gui)
        GuiStorage.addPlayer(gui.id, player)

        if (gui.onOpen != null)
            gui.onOpen!!.invoke(event)
    }
}