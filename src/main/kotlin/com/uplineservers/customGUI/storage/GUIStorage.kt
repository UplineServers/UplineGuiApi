package com.uplineservers.customGUI.storage

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.uplineservers.customGUI.CustomGUI
import com.uplineservers.customGUI.models.GUI
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class GUIStorage {
    companion object {
        private val gson = Gson()
        private val key = NamespacedKey(CustomGUI.instance, "stored_inventory")

        val guis: MutableMap<String, GUI> = mutableMapOf()
        val playersGUI: MutableMap<Player, String> = mutableMapOf()

        fun getById(id: String): GUI? {
            return guis[id]
        }

        fun getByPlayer(player: Player): GUI? {
            val guiId = playersGUI[player] ?: return null
            return guis[guiId]
        }

        fun getByInventory(inventory: Inventory): GUI? {
            return guis.values.find { it.inventory == inventory }
        }

        fun add(gui: GUI) {
            guis[gui.id] = gui
        }

        fun findPlayers(id: String): List<Player> {
            return playersGUI.filterValues { it == id }.keys.toList()
        }

        fun addPlayer(id: String, player: Player) {
            playersGUI[player] = id
        }

        fun removePlayer(player: Player) {
            playersGUI.remove(player) ?: return
        }

        fun scheduleRemoval(gui: GUI, plugin: JavaPlugin) {
            gui.removalTask?.cancel()
            
            gui.removalTask = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                if (findPlayers(gui.id).isEmpty())
                    this.remove(gui)
            }, gui.removalDelay * 20L)
        }

        fun cancelRemoval(gui: GUI) {
            gui.removalTask?.cancel()
            gui.removalTask = null
        }

        fun remove(gui: GUI) {
            guis.remove(gui.id)

            if(gui.dataItem != null && gui.inventory != null)
                saveInventoryToItem(gui.dataItem!!, gui.inventory!!)
        }

        fun loadStoredInventoryIntoGui(item: ItemStack, inventory: Inventory?) {
            if (inventory == null) return

            val meta = item.itemMeta ?: return
            val json = meta.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return

            val type = object : TypeToken<List<Map<String, Any>?>>() {}.type
            val itemDataList: List<Map<String, Any>?> = gson.fromJson(json, type)

            itemDataList.mapIndexed { index, map ->
                val item = map?.let { ItemStack.deserialize(it) }
                if (index < inventory.size)
                    inventory.setItem(index, item)
            }
        }

        fun saveInventoryToItem(item: ItemStack, inventory: Inventory) {
            val meta = item.itemMeta ?: return

            val serializedItems = inventory.contents.map { it?.serialize() }
            val json = gson.toJson(serializedItems)

            meta.persistentDataContainer.set(key, PersistentDataType.STRING, json)
            item.itemMeta = meta
        }
    }
}