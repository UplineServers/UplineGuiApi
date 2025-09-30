package com.uplineservers.customGUI.examples

import com.uplineservers.customGUI.models.GUI
import com.uplineservers.customGUI.services.GUIBuild
import com.uplineservers.customGUI.services.GUIStorage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun TakeableGui(player: Player) {
    val existing = GUIStorage.getById("takeable_gui")
    if (existing != null) return existing.open(player)

    val gui = GUI(
        id = "takeable_gui",
        title = "§aTakeable GUI",
        size = 9,
        isTakeable = true
    )

    gui.onOpen = { it.player.sendMessage("§aYou can place items here, but not take them out.") }
    GUIBuild.build(gui)

    // need to fill the inventory, not add guiItems, because guiItems are functional, and require isMovable to be false or true
    for (i in 0 until gui.size) {
        gui.inventory?.setItem(i, ItemStack(Material.GOLD_INGOT))
    }

    player.openInventory(gui.inventory!!)
}