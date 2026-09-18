package com.uplineservers.uplineguiapi.examples

import com.uplineservers.uplineguiapi.models.Gui
import com.uplineservers.uplineguiapi.services.GuiManager
import org.bukkit.entity.Player

fun MoveableGui(player: Player) {
    val existing = GuiManager.getById("movable_gui")
    if (existing != null) return existing.open(player)

    val gui = Gui(
        id = "movable_gui",
        title = "§bMovable GUI",
        size = 9,
        isPutable = true,
        isTakeable = true
    )

    gui.onOpen = { it.player.sendMessage("§bYou can move items in this GUI.") }

    gui.build()
    gui.open(player)
}