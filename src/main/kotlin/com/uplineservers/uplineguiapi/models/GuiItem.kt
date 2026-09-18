package com.uplineservers.uplineguiapi.models

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * One slot of a [Gui]: the stack shown there and what happens when it is clicked.
 *
 * ```
 * gui.items[13] = GuiItem(ItemStack(Material.DIAMOND)) { event ->
 *     event.whoClicked.sendMessage("clicked!")
 * }
 * ```
 *
 * A non-movable item gets a random `minecraft:item_uuid` written into its persistent data, so
 * identical-looking buttons never stack with each other or with a player's own items.
 *
 * @property item The stack rendered in the slot.
 * @property isMovable `false` locks the slot: the item can be clicked but never taken or
 * replaced, whatever [Gui.isPutable] and [Gui.isTakeable] say. `true` allows both, again
 * overriding the GUI-wide flags.
 * @property onClick Runs before the put/take rules are evaluated; cancel the event to stop all
 * further handling.
 */
data class GuiItem(
    var item: ItemStack,
    val isMovable: Boolean = false,
    val onClick: (InventoryClickEvent) -> Unit = { }
) {
    init{
        if(!isMovable){
            val meta = item.itemMeta

            meta.persistentDataContainer.set(
                org.bukkit.NamespacedKey.minecraft("item_uuid"),
                org.bukkit.persistence.PersistentDataType.STRING,
                java.util.UUID.randomUUID().toString()
            )

            item.itemMeta = meta
        }
    }
}