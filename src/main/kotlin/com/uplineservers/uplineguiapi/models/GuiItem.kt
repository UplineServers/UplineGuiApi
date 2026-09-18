package com.uplineservers.uplineguiapi.models

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

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