package com.uplineservers.customGUI.examples

import com.uplineservers.customGUI.models.GUI
import com.uplineservers.customGUI.services.GUIBuild
import com.uplineservers.customGUI.services.GUIStorage
import org.bukkit.entity.Player

fun PutableGui(player: Player) {
    val existing = GUIStorage.getById("placeable_gui")
    if (existing != null) return existing.open(player)

    val gui = GUI(
        id = "placeable_gui",
        title = "§aPlaceable GUI",
        size = 9,
        isPutable = true,
    )

    gui.onOpen = { it.player.sendMessage("§aYou can place items here, but not take them out.") }
    GUIBuild.build(gui)
    player.openInventory(gui.inventory!!)
}