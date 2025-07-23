package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.entities.GUIEntity
import org.bukkit.entity.Player

/**
 * GUI Player Service - Handles player-GUI interactions
 * Single responsibility: Manage player relationships with GUIs
 */
class GUIPlayer {
    
    /**
     * Add a player to a GUI
     */
    fun addPlayer(gui: GUIEntity, player: Player) {
        if (!gui.players.contains(player)) {
            gui.players.add(player)
        }
    }

    /**
     * Remove a player from a GUI
     */
    fun removePlayer(gui: GUIEntity, player: Player) {
        gui.players.remove(player)
    }

    /**
     * Open a GUI for a player
     */
    fun openGUI(gui: GUIEntity, player: Player) {
        addPlayer(gui, player)
        gui.onOpen?.invoke(player)
        player.openInventory(gui.inventory ?: return)
    }
}