package com.uplineservers.uplineguiapi.examples

import com.uplineservers.uplineguiapi.models.Gui
import com.uplineservers.uplineguiapi.services.GuiManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun TakeableGui(player: Player) {
    val existing = GuiManager.getById("takeable_gui")
    if (existing != null) return existing.open(player)

    val gui = Gui(
        id = "takeable_gui",
        title = "§aTakeable GUI",
        size = 9,
        isTakeable = true
    )

    gui.onOpen = {
        for (i in 0 until gui.size) {
            gui.inventory?.setItem(i, ItemStack(Material.GOLD_INGOT))
        }
        it.player.sendMessage("§aYou can place items here, but not take them out.")
    }

    gui.build()
    gui.open(player)
}