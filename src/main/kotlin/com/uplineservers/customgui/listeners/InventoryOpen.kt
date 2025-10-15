package com.uplineservers.customgui.listeners

import com.uplineservers.customgui.services.GuiBuild
import com.uplineservers.customgui.services.GuiManager
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

//        player.sendMessage("Opening GUI: ${gui.id}")

        gui.addPlayer(player)
        GuiBuild.buildExtra(gui, player)

        if (gui.onOpen != null)
            gui.onOpen!!.invoke(event)
    }
}