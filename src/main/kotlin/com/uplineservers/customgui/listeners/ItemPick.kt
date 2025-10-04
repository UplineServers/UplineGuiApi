package com.uplineservers.customgui.listeners

import com.uplineservers.customgui.services.GuiManager
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.plugin.Plugin

class ItemPick(plugin: Plugin) : Listener {
    @EventHandler
    fun onItemPick(event: EntityPickupItemEvent){
        val player = event.entity
        if(player.type != EntityType.PLAYER) return

        val gui = GuiManager.getByPlayer(player as Player) ?: return

        if(gui.extraSize != null)
            event.isCancelled = true
    }
}