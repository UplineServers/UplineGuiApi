package com.uplineservers.customgui.listeners

import com.uplineservers.customgui.services.GuiStorage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin

class InventoryClose(private val plugin: JavaPlugin) : Listener {

    @EventHandler()
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return

        val gui = GuiStorage.getByInventory(event.inventory)
        if (gui == null) return

        gui.isUpdating = true
        gui.onClose?.invoke(event)

        if ((gui.dataItem != null || gui.dataEntity != null) && gui.inventory != null)
            GuiStorage.saveInventory(gui)

        GuiStorage.removePlayer(player)

        gui.isUpdating = false

        if (GuiStorage.findPlayers(gui.id).isEmpty()) {
            if (gui.removalDelay > 0)
                GuiStorage.scheduleRemoval(gui, plugin)
            else
                GuiStorage.remove(gui)
        }
    }
}
