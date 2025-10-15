package com.uplineservers.customgui.listeners

import com.uplineservers.customgui.services.GuiManager
import com.uplineservers.customgui.services.GuiStore
import org.bukkit.Bukkit
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

//        player.sendMessage("Closing GUI: ${gui.id}")

        gui.isUpdating = true
        gui.onClose?.invoke(event)
        GuiManager.removePlayer(player)
        gui.isUpdating = false
    }
}
