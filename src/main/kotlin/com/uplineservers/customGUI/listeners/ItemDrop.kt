package com.uplineservers.customGUI.listeners

import com.uplineservers.customGUI.storage.GUIStorage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.plugin.Plugin

class ItemDrop(plugin: Plugin) : Listener {

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent){
        val player = event.player
        val gui = GUIStorage.getByPlayer(player) ?: return

        if(gui.isUpdating) {
            event.isCancelled = true
            return
        }
    }
}