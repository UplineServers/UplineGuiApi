package com.uplineservers.customGUI.examples

import com.uplineservers.customGUI.models.GUI
import com.uplineservers.customGUI.services.GUIBuild
import com.uplineservers.customGUI.services.GUIStorage
import org.bukkit.entity.Player

fun MoveableGui(player: Player) {
    val existing = GUIStorage.getById("movable_gui")
    if (existing != null) return existing.open(player)

    val gui = GUI(
        id = "movable_gui",
        title = "§bMovable GUI",
        size = 9,
        isPutable = true,
        isTakeable = true
    )

    gui.onOpen = { it.player.sendMessage("§bYou can move items in this GUI.") }

    GUIBuild.build(gui)

    player.openInventory(gui.inventory!!)
}