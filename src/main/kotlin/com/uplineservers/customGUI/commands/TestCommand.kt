package com.uplineservers.customGUI.commands

import com.uplineservers.customGUI.models.GUI
import com.uplineservers.customGUI.models.GUIItem
import com.uplineservers.customGUI.models.SHIFT_PROTECTION
import com.uplineservers.customGUI.services.GUIBuild
import com.uplineservers.customGUI.services.GUIOpen
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cThis command can only be used by players!")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§eUsage: /guitest <menu|placeable|takeable|movable|action|item>")
            return true
        }

        when (args[0].lowercase()) {
            "menu" -> openMainMenu(sender)
            "placeable" -> openPlaceableGUI(sender)
            "takeable" -> openTakeableGUI(sender)
            "movable" -> openMovableGUI(sender)
            "action" -> openActionGUI(sender)
            "item" -> openItemSavedGUI(sender)
            else -> sender.sendMessage("§cUnknown test GUI: ${args[0]}")
        }

        return true
    }

    private fun openMainMenu(player: Player) {
        if (GUIOpen.openIfExists(player, "test_menu")) return

        val gui = GUI(id = "test_menu", title = "§6§lMain Menu", size = 27)

        val sword = ItemStack(Material.DIAMOND_SWORD).apply {
            itemMeta = itemMeta.apply {
                displayName(Component.text("§bExample Item"))
                lore(listOf(Component.text("§7This is an example item.")))
            }
        }

        gui.items[13] = GUIItem(sword) {
            it.whoClicked.sendMessage("§aYou clicked the example item!")
        }

        gui.items[1] = GUIItem(ItemStack(Material.DIAMOND_BLOCK)) {
            if (it.whoClicked is Player)
                (it.whoClicked as Player).inventory.addItem(ItemStack(Material.DIAMOND_BLOCK))
            gui.title = "§6§lUpdated Menu"
        }

        gui.onOpen = { it.player.sendMessage("§7Welcome to the main menu!") }
        gui.onClose = { it.player.sendMessage("§7Thanks for using the main menu!") }

        GUIBuild.build(gui)

        player.openInventory(gui.inventory!!)
    }

    private fun openMovableGUI(player: Player) {
        if (GUIOpen.openIfExists(player, "movable_gui")) return

        val gui = GUI(
            id = "movable_gui",
            title = "§bMovable GUI",
            size = 9,
            isPutable = true,
            isTakeable = true
        )
        gui.onOpen = { it.player.sendMessage("§bYou can move items in this GUI.") }
        GUIBuild.build(gui)
        player.openInventory(gui.inventory!!)
    }

    private fun openTakeableGUI(player: Player) {
        if (GUIOpen.openIfExists(player, "takeable_gui")) return

        val gui = GUI(
            id = "takeable_gui",
            title = "§aTakeable GUI",
            size = 9,
            isPutable = false,
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

    private fun openPlaceableGUI(player: Player) {
        if (GUIOpen.openIfExists(player, "placeable_gui")) return

        val gui = GUI(
            id = "placeable_gui",
            title = "§aPlaceable GUI",
            size = 9,
            isPutable = true,
            isTakeable = false
        )
        gui.onOpen = { it.player.sendMessage("§aYou can place items here, but not take them out.") }
        GUIBuild.build(gui)
        player.openInventory(gui.inventory!!)
    }

    private fun openActionGUI(player: Player) {
        if (GUIOpen.openIfExists(player, "action_gui")) return

        val gui = GUI(
            id = "action_gui",
            title = "§dClick Action GUI",
            size = 9
        )

        gui.items[4] = GUIItem(ItemStack(Material.FIRE_CHARGE)) {
            val p = it.whoClicked as? Player ?: return@GUIItem
            p.setFireTicks(60)
            p.sendMessage("§cYou clicked the fire item!")
        }

        GUIBuild.build(gui)
        player.openInventory(gui.inventory!!)
    }

    private fun openItemSavedGUI(player: Player) {
        if (GUIOpen.openIfExists(player, "item_saved_gui")) return

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
}