package com.uplineservers.customgui.examples

import com.uplineservers.customgui.models.Gui
import com.uplineservers.customgui.services.GuiBuild
import com.uplineservers.customgui.services.GuiStorage
import org.bukkit.Material
import org.bukkit.entity.Player

fun ItemSavedGui(player: Player) {
    val existing = GuiStorage.getById( "item_saved_gui")
    if (existing != null) return existing.open(player)

    val playerHand = player.inventory.itemInMainHand
    if(playerHand.type == Material.AIR) {
        player.sendMessage("§cYou must hold an item to save it in the GUI.")
        return
    }

    val gui = Gui(
        id = "item_saved_gui",
        title = "§eItem Saved GUI",
        size = 9,
        isPutable = true,
        isTakeable = true,
        dataItem = playerHand
    )

    GuiBuild.build(gui)
    player.openInventory(gui.inventory!!)
}