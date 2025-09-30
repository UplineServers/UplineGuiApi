package com.uplineservers.customgui.listeners

import com.uplineservers.customgui.services.GuiManager
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

        val gui = GuiManager.getByInventory(event.inventory) ?: return

        gui.isUpdating = true
        gui.onClose?.invoke(event)

        if ((gui.dataItem != null || gui.dataEntity != null) && gui.inventory != null)
            GuiStorage.save(gui)

        GuiManager.removePlayer(player)

        gui.isUpdating = false

        if (GuiManager.getPlayers(gui.id).isEmpty()) {
            if (gui.removalDelay > 0)
                GuiManager.scheduleRemoval(gui, plugin)
            else
                GuiManager.remove(gui)
        }
    }
}
