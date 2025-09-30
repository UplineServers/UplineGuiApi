package com.uplineservers.customgui.examples

import com.uplineservers.customgui.models.Gui
import com.uplineservers.customgui.services.GuiManager
import org.bukkit.entity.Player

fun InventoryMenu(player: Player){
    val existing = GuiManager.getById("inventoryMenu_" + player.uniqueId)
    if (existing != null) return existing.open(player)

    val gui = Gui(
        id = "inventoryMenu_" + player.uniqueId,
        title = "§aInventory Menu",
        size = 81, // Full 2chest + 3 rows of player inventory
        isTakeable = false,
        isPutable = false
    )

    gui.build()
    gui.open(player)
}