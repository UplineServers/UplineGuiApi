package com.uplineservers.customgui.examples

import com.uplineservers.customgui.models.Gui
import com.uplineservers.customgui.services.GuiBuild
import com.uplineservers.customgui.services.GuiStorage
import org.bukkit.entity.Player

fun PutableGui(player: Player) {
    val existing = GuiStorage.getById("placeable_gui")
    if (existing != null) return existing.open(player)

    val gui = Gui(
        id = "placeable_gui",
        title = "§aPlaceable GUI",
        size = 9,
        isPutable = true,
    )

    gui.onOpen = { it.player.sendMessage("§aYou can place items here, but not take them out.") }
    GuiBuild.build(gui)
    player.openInventory(gui.inventory!!)
}