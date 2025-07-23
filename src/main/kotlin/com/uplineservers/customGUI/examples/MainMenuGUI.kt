package com.uplineservers.customGUI.examples

import com.uplineservers.customGUI.entities.CustomInventory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Example main menu with custom items and actions
 */
object MainMenuGUI {

    /**
     * Creates a simple menu with custom items and actions
     */
    fun create(player: Player): CustomInventory {
        val gui = CustomInventory(
            title = "Main Menu",
            size = 27 // 3 rows
        )

        // Add some example items
        val teleportItem = ItemStack(Material.ENDER_PEARL).apply {
            val meta = itemMeta
            meta?.displayName(Component.text("Teleport").color(NamedTextColor.AQUA))
            meta?.lore(listOf(Component.text("Click to open teleport menu").color(NamedTextColor.GRAY)))
            itemMeta = meta
        }
        val shopItem = ItemStack(Material.EMERALD).apply {
            val meta = itemMeta
            meta?.displayName(Component.text("Shop").color(NamedTextColor.GREEN))
            meta?.lore(listOf(Component.text("Click to open shop").color(NamedTextColor.GRAY)))
            itemMeta = meta
        }

        val playerInfoItem = ItemStack(Material.PLAYER_HEAD).apply {
            val meta = itemMeta
            meta?.displayName(Component.text("Player Info").color(NamedTextColor.YELLOW))
            meta?.lore(listOf(
                Component.text("§eName: ${player.name}"),
                Component.text("§eLevel: ${player.level}"),
                Component.text("§eHealth: ${player.health}/${player.maxHealth}")
            ))
            itemMeta = meta
        }

        gui.setItem(10, teleportItem)
        gui.setItem(12, shopItem)
        gui.setItem(14, playerInfoItem)

        // Set up event listeners
        gui.onOpenListener = { openPlayer, _ ->
            openPlayer.sendMessage("§aWelcome to the main menu, ${openPlayer.name}!")
        }

        gui.onClickListener = { clickPlayer, _, slot, item ->
            when (slot) {
                10 -> {
                    clickPlayer.sendMessage("§bTeleport menu would open here!")
                    // You could open another GUI here
                }

                12 -> {
                    clickPlayer.sendMessage("§aShop would open here!")
                    // You could open another GUI here
                }

                14 -> {
                    clickPlayer.sendMessage("§ePlayer: ${clickPlayer.name}")
                    clickPlayer.sendMessage("§eLevel: ${clickPlayer.level}")
                    clickPlayer.sendMessage("§eHealth: ${clickPlayer.health}/${clickPlayer.maxHealth}")
                }
            }
        }

        gui.onCloseListener = { closePlayer, _ ->
            closePlayer.sendMessage("§7Thanks for using the menu!")
        }

        return gui
    }
}
