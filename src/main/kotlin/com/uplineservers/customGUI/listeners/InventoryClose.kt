package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin

class InventoryClose(private val plugin: JavaPlugin) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player
        if (player !is Player) return

        val gui = GUIStorage.getByInventory(event.inventory)
        if (gui == null) return
        if (gui.isUpdating) return

        GUIStorage.removePlayer(player)

        if (gui.onClose != null)
            gui.onClose!!.invoke(event)

        if (gui.dataItem != null && gui.inventory != null){
//            if (player.gameMode == GameMode.CREATIVE)
//                player.sendMessage("§cYou cannot save a custom inventory to an item in creative mode.")
            GUIStorage.saveInventoryToItem(gui)
        }

        if (GUIStorage.findPlayers(gui.id).isEmpty()) {
            if (gui.removalDelay > 0)
                GUIStorage.scheduleRemoval(gui, plugin)
            else
                GUIStorage.remove(gui)
        }

    }
}
