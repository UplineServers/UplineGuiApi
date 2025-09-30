package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.services.GUIStorage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin

class InventoryClose(private val plugin: JavaPlugin) : Listener {

    @EventHandler()
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return

        val gui = GUIStorage.getByInventory(event.inventory)
        if (gui == null) return

        gui.isUpdating = true
        gui.onClose?.invoke(event)

        if ((gui.dataItem != null || gui.dataEntity != null) && gui.inventory != null)
            GUIStorage.saveInventory(gui)

        GUIStorage.removePlayer(player)

        gui.isUpdating = false

        if (GUIStorage.findPlayers(gui.id).isEmpty()) {
            if (gui.removalDelay > 0)
                GUIStorage.scheduleRemoval(gui, plugin)
            else
                GUIStorage.remove(gui)
        }
    }
}
