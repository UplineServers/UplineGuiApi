package com.uplineservers.customgui.examples

import com.uplineservers.customgui.models.Gui
import com.uplineservers.customgui.services.GuiBuild
import com.uplineservers.customgui.services.GuiStorage
import org.bukkit.entity.Player

fun MoveableGui(player: Player) {
    val existing = GuiStorage.getById("movable_gui")
    if (existing != null) return existing.open(player)

    val gui = Gui(
        id = "movable_gui",
        title = "§bMovable GUI",
        size = 9,
        isPutable = true,
        isTakeable = true
    )

    gui.onOpen = { it.player.sendMessage("§bYou can move items in this GUI.") }

    GuiBuild.build(gui)

    player.openInventory(gui.inventory!!)
}