package com.uplineservers.uplineguiapi.examples

import com.uplineservers.uplineguiapi.models.Gui
import com.uplineservers.uplineguiapi.services.GuiManager
import org.bukkit.entity.Player

fun PutableGui(player: Player) {
    val existing = GuiManager.getById("placeable_gui")
    if (existing != null) return existing.open(player)

    val gui = Gui(
        id = "placeable_gui",
        title = "§aPlaceable GUI",
        size = 9,
        isPutable = true,
    )

    gui.onOpen = { it.player.sendMessage("§aYou can place items here, but not take them out.") }

    gui.build()
    gui.open(player)
}