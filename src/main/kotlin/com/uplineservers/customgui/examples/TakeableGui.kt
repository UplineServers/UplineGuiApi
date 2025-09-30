package com.uplineservers.customgui.examples

import com.uplineservers.customgui.models.Gui
import com.uplineservers.customgui.services.GuiBuild
import com.uplineservers.customgui.services.GuiStorage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun TakeableGui(player: Player) {
    val existing = GuiStorage.getById("takeable_gui")
    if (existing != null) return existing.open(player)

    val gui = Gui(
        id = "takeable_gui",
        title = "§aTakeable GUI",
        size = 9,
        isTakeable = true
    )

    gui.onOpen = { it.player.sendMessage("§aYou can place items here, but not take them out.") }
    GuiBuild.build(gui)

    // need to fill the inventory, not add guiItems, because guiItems are functional, and require isMovable to be false or true
    for (i in 0 until gui.size) {
        gui.inventory?.setItem(i, ItemStack(Material.GOLD_INGOT))
    }

    player.openInventory(gui.inventory!!)
}