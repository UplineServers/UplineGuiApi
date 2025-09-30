package com.uplineservers.customgui.examples

import com.uplineservers.customgui.models.Gui
import com.uplineservers.customgui.models.GuiItem
import com.uplineservers.customgui.services.GuiBuild
import com.uplineservers.customgui.services.GuiStorage
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.collections.set

fun MenuExampleGui(player: Player) {
    val existing = GuiStorage.getById("test_menu")
    if (existing != null) return existing.open(player)

    val gui = Gui(id = "test_menu", title = "§6§lMain Menu", size = 27)

    val sword = ItemStack(Material.DIAMOND_SWORD).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("§bExample Item"))
            lore(listOf(Component.text("§7This is an example item.")))
        }
    }

    gui.items[13] = GuiItem(sword) {
        it.whoClicked.sendMessage("§aYou clicked the example item!")
    }

    gui.items[1] = GuiItem(ItemStack(Material.DIAMOND_BLOCK)) {
        if (it.whoClicked is Player)
            (it.whoClicked as Player).inventory.addItem(ItemStack(Material.DIAMOND_BLOCK))
        gui.title = "§6§lUpdated Menu"
    }

    gui.items[4] = GuiItem(ItemStack(Material.FIRE_CHARGE)) {
        val p = it.whoClicked as? Player ?: return@GuiItem
        p.fireTicks = 60
        p.sendMessage("§cYou clicked the fire item!")
    }

    gui.onOpen = { it.player.sendMessage("§7Welcome to the main menu!") }
    gui.onClose = { it.player.sendMessage("§7Thanks for using the main menu!") }

    GuiBuild.build(gui)

    player.openInventory(gui.inventory!!)
}