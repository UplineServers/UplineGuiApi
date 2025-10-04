package com.uplineservers.customgui.listeners

import com.uplineservers.customgui.services.GuiManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.plugin.Plugin

class ItemDrop(plugin: Plugin) : Listener {

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent){
        val player = event.player
        val gui = GuiManager.getByPlayer(player) ?: return

        if(gui.isUpdating)
            event.isCancelled = true
    }
}