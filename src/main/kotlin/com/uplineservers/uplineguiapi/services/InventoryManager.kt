package com.uplineservers.uplineguiapi.services

import com.uplineservers.uplineguiapi.models.EXTRA_INVENTORY
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID

/**
 * In-memory backup of players' real inventories while an overlay GUI
 * (see [com.uplineservers.uplineguiapi.models.EXTRA_INVENTORY]) is open.
 *
 * Backups do not survive a server crash.
 */
object InventoryManager {
    private val inventories: MutableMap<UUID, Array<ItemStack?>> = mutableMapOf()
    private val heldSlot: MutableMap<UUID, Int> = mutableMapOf()

    /** Save the player's inventory depending on type */
    fun save(player: Player, type: EXTRA_INVENTORY) {
        if(inventories.containsKey(player.uniqueId)) return

        when (type) {
            EXTRA_INVENTORY.FULL -> {
                inventories[player.uniqueId] = player.inventory.contents.clone()
                heldSlot[player.uniqueId] = player.inventory.heldItemSlot
                player.inventory.clear()
            }

            EXTRA_INVENTORY.MAIN -> {
                inventories[player.uniqueId] = player.inventory.contents.copyOfRange(9, 36)

                for (i in 9 until 36) {
                    player.inventory.setItem(i, null)
                }
            }
        }
    }

    /** Load the player's saved inventory, if it exists */
    fun load(player: Player) {
        val saved = inventories[player.uniqueId] ?: return

        if (saved.size == 27) {
            for (i in 0 until 27) {
                player.inventory.setItem(i + 9, saved[i])
            }
        }else {
            player.inventory.contents = saved
        }

        if(heldSlot.containsKey(player.uniqueId))
            player.inventory.heldItemSlot = heldSlot[player.uniqueId]!!
        inventories.remove(player.uniqueId)
    }
}