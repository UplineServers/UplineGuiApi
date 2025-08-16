package com.uplineservers.customGUI.storage

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.uplineservers.customGUI.CustomGUI
import com.uplineservers.customGUI.models.GUI
import org.bukkit.Bukkit
import org.bukkit.Material
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

            // close all inventories of players using this GUI
            findPlayers(gui.id).forEach { player ->
                player.closeInventory()
            }
        }

        fun saveInventoryToItem(gui: GUI) {
            val inventory = gui.inventory ?: return
            val item = gui.dataItem ?: return
            val meta = item.itemMeta

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
                CustomGUI.instance.logger.warning("NBT data too large to save to item!")
                return
            }

            meta.persistentDataContainer.set(key, PersistentDataType.STRING, json)
            item.itemMeta = meta
        }

        fun loadStoredInventoryIntoGui(gui: GUI) {
            val item = gui.dataItem ?: return
            val inventory = gui.inventory ?: return
            val meta = item.itemMeta ?: return
            val json = meta.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return

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
}