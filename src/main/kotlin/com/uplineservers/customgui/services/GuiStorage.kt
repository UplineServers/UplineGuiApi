package com.uplineservers.customgui.services

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.uplineservers.customgui.CustomGui
import com.uplineservers.customgui.models.Gui
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object GuiStorage {
    private val gson = Gson()
    private val key = NamespacedKey(CustomGui.Companion.instance, "stored_inventory")

    fun save(gui: Gui) {
        val inventory = gui.inventory ?: return

        val serializedItems = inventory.contents.mapIndexedNotNull { index, itemStack ->
            if (itemStack != null && itemStack.type != Material.AIR) {
                mapOf(
                    "slot" to index,
                    "item" to itemStack.serialize()
                )
            } else null
        }

        val json = GsonBuilder().create().toJson(serializedItems)

        if (json.length > 32767) {
            CustomGui.instance.logger.warning("NBT data too large to save!")
            return
        }

        when {
            gui.dataItem != null -> {
                val item = gui.dataItem!!
                val meta = item.itemMeta ?: return
                meta.persistentDataContainer.set(key, PersistentDataType.STRING, json)
                item.itemMeta = meta
            }
            gui.dataEntity != null -> {
                val entity = gui.dataEntity!!
                entity.persistentDataContainer.set(key, PersistentDataType.STRING, json)
                gui.dataEntity = entity
            }
            else -> return
        }
    }

    fun load(gui: Gui) {
        val inventory = gui.inventory ?: return
        val json: String = when {
            gui.dataItem != null -> {
                val meta = gui.dataItem!!.itemMeta ?: return
                meta.persistentDataContainer.get(key, PersistentDataType.STRING)
            }
            gui.dataEntity != null -> {
                val entity = gui.dataEntity ?: return
                entity.persistentDataContainer.get(key, PersistentDataType.STRING)
            }
            else -> return
        } ?: return

        CustomGui.instance.logger.info("Loading stored inventory for GUI ${gui.id}")

        val type = object : TypeToken<List<Map<String, Any>>>() {}.type
        val savedList: List<Map<String, Any>> = gson.fromJson(json, type)

        for (entry in savedList) {
            val slot = (entry["slot"] as? Double)?.toInt() ?: continue
            val itemData = entry["item"] as? Map<String, Any> ?: continue

            if (slot < inventory.size) {
                inventory.setItem(slot, ItemStack.deserialize(itemData))
            }
        }
    }
}