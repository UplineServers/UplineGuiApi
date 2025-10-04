package com.uplineservers.customgui.examples

import com.uplineservers.customgui.models.Gui
import com.uplineservers.customgui.models.EXTRA_INVENTORY
import com.uplineservers.customgui.models.GuiItem
import com.uplineservers.customgui.services.GuiManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun InventoryMenu(player: Player){
    val existing = GuiManager.getById("inventoryMenu_" + player.uniqueId)
    if (existing != null) return existing.open(player)

    val gui = Gui(
        id = "inventoryMenu_" + player.uniqueId,
        title = "§bInventory Menu",
        size = 54,
        extraSize = EXTRA_INVENTORY.MAIN,
        isTakeable = false,
        isPutable = false
    )

    for (i in 0 until 9) {
        gui.items[54 + i] = GuiItem(ItemStack(Material.WOODEN_SWORD)) {
            if (it.whoClicked is Player)
                (it.whoClicked as Player).inventory.addItem(ItemStack(Material.OAK_LOG))
        }
        gui.items[63 + i] = GuiItem(ItemStack(Material.STONE_SWORD)) {
            if (it.whoClicked is Player)
                (it.whoClicked as Player).inventory.addItem(ItemStack(Material.COBBLESTONE))
        }
        gui.items[72 + i] = GuiItem(ItemStack(Material.GOLDEN_SWORD)) {
            if (it.whoClicked is Player)
                (it.whoClicked as Player).inventory.addItem(ItemStack(Material.GOLD_INGOT))
        }
    }

    gui.build()
    gui.open(player)
}