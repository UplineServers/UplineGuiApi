package com.uplineservers.customGUI.models

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

data class GUIItem(
    var item: ItemStack,
    val isMovable: Boolean = false,
    val onClick: (InventoryClickEvent) -> Unit = { }
) {
    init{
        if(!isMovable){
            val meta = item.itemMeta

            meta.persistentDataContainer.set(
                org.bukkit.NamespacedKey.minecraft("gui_uuid"),
                org.bukkit.persistence.PersistentDataType.STRING,
                java.util.UUID.randomUUID().toString()
            )

            item.itemMeta = meta
        }
    }
}