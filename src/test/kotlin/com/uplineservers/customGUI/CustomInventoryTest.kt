package com.uplineservers.customGUI

import com.uplineservers.customGUI.entities.GUIEntity
import com.uplineservers.customGUI.services.GUIBuild
import com.uplineservers.customGUI.services.GUIGet
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


class CustomInventoryTest {

    private lateinit var guiBuild: GUIBuild
    private lateinit var guiGet: GUIGet
    private lateinit var guiPlayer: GUIPlayer
    private lateinit var mockPlayer: Player
    private lateinit var mockInventory: Inventory
    private lateinit var mockItemStack: ItemStack

    @BeforeEach
    fun setUp() {
        guiBuild = GUIBuild()
        guiGet = GUIGet()
        guiPlayer = GUIPlayer()
        mockPlayer = mock<Player>()
        mockInventory = mock<Inventory>()
        mockItemStack = mock<ItemStack>()
        
        // Setup basic mock behavior
        whenever(mockInventory.size).thenReturn(27)
        whenever(mockItemStack.type).thenReturn(Material.DIAMOND)
    }

    @Test
    fun `should create a simple GUI with basic properties and handle items`() {
        // Given
        val title = "Test GUI"
        val size = 27
        val guiId = "test-gui-123"
        
        // Create test GUI without Bukkit dependencies
        val gui = GUIEntity(
            id = guiId,
            title = title,
            size = size
        )
        
        // Manually set inventory to avoid Bukkit.createInventory issues in tests
        gui.inventory = mockInventory
        
        // Then - Test basic properties
        assertEquals(guiId, gui.id)
        assertEquals(title, gui.title)
        assertEquals(size, gui.size)
        assertTrue(gui.items.isEmpty())
        assertTrue(gui.players.isEmpty())
        
        // Test adding items to the GUI data structure
        gui.items[13] = mockItemStack
        assertEquals(mockItemStack, gui.items[13])
        assertEquals(1, gui.items.size)
    }

    @Test
    fun `should manage items in GUI data structure`() {
        // Given
        val gui = GUIEntity(title = "Item Test GUI", size = 27)
        gui.inventory = mockInventory
        val slot = 13
        
        // When - Add item directly to the GUI's items map
        gui.items[slot] = mockItemStack
        
        // Then - Verify the item is stored in the GUI's items map
        assertEquals(mockItemStack, gui.items[slot])
        assertEquals(1, gui.items.size)
        
        // When - Remove item
        gui.items.remove(slot)
        
        // Then - Verify item is removed
        assertFalse(gui.items.containsKey(slot))
        assertEquals(0, gui.items.size)
    }

}