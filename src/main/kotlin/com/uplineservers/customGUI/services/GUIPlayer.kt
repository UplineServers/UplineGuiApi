package com.uplineservers.customGUI.services

import com.uplineservers.customGUI.entities.GUIEntity
import org.bukkit.entity.Player

class GUIPlayer {
    fun addPlayer(gui: GUIEntity, player: Player) {
        gui.players = gui.players + player
    }

    fun removePlayer(gui: GUIEntity, player: Player) {
        gui.players = gui.players - player
    }
}