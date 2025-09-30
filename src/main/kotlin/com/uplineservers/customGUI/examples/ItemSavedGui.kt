package com.uplineservers.customGUI.examples

import com.uplineservers.customGUI.models.GUI
import com.uplineservers.customGUI.services.GUIBuild
import com.uplineservers.customGUI.services.GUIStorage
import org.bukkit.Material
import org.bukkit.entity.Player

fun ItemSavedGui(player: Player) {
    val existing = GUIStorage.getById( "item_saved_gui")
    if (existing != null) return existing.open(player)

    val playerHand = player.inventory.itemInMainHand
    if(playerHand.type == Material.AIR) {
        player.sendMessage("§cYou must hold an item to save it in the GUI.")
        return
    }

    val gui = GUI(
        id = "item_saved_gui",
        title = "§eItem Saved GUI",
        size = 9,
        isPutable = true,
        isTakeable = true,
        dataItem = playerHand
    )

    GUIBuild.build(gui)
    player.openInventory(gui.inventory!!)
}