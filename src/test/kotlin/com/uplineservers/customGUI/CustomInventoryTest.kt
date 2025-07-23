package com.uplineservers.customGUI

import com.uplineservers.customGUI.entities.GUIEntity
import com.uplineservers.customGUI.services.GUIBuild
import com.uplineservers.customGUI.services.GUIGet
import com.uplineservers.customGUI.services.GUIManager
import com.uplineservers.customGUI.services.GUIPlayer
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
    private lateinit var guiManager: GUIManager
    private lateinit var mockPlayer: Player
    private lateinit var mockInventory: Inventory
    private lateinit var mockItemStack: ItemStack

    @BeforeEach
    fun setUp() {
        guiBuild = GUIBuild()
        guiGet = GUIGet()
        guiPlayer = GUIPlayer()
        guiManager = GUIManager(guiBuild, guiGet, guiPlayer)
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

    @Test
    fun `should manage players in GUI`() {
        // Given
        val gui = GUIEntity(title = "Player Test GUI", size = 27)
        
        // When - Add player
        gui.players = gui.players + mockPlayer
        
        // Then
        assertTrue(gui.players.contains(mockPlayer))
        assertEquals(1, gui.players.size)
        
        // When - Remove player
        gui.players = gui.players - mockPlayer
        
        // Then
        assertFalse(gui.players.contains(mockPlayer))
        assertEquals(0, gui.players.size)
    }

    @Test
    fun `should handle GUI click events with callbacks`() {
        // Given
        val gui = GUIEntity(title = "Click Test GUI", size = 27)
        gui.inventory = mockInventory
        var clickHandled = false
        var clickedSlot = -1
        var clickedPlayer: Player? = null
        
        gui.onClick = { player, guiEntity, slot, item ->
            clickHandled = true
            clickedSlot = slot
            clickedPlayer = player
        }
        
        val slot = 5
        
        // When - Simulate click event
        gui.onClick?.invoke(mockPlayer, gui, slot, mockItemStack)
        
        // Then
        assertTrue(clickHandled)
        assertEquals(slot, clickedSlot)
        assertEquals(mockPlayer, clickedPlayer)
    }

    @Test
    fun `should set up GUI with multiple items and verify structure`() {
        // Given
        val gui = GUIEntity(
            title = "Multi-Item GUI",
            size = 54
        )
        gui.inventory = mockInventory
        
        val mockItems = mapOf(
            0 to mock<ItemStack>(),
            8 to mock<ItemStack>(),
            45 to mock<ItemStack>(),
            53 to mock<ItemStack>()
        )
        
        // When - Add multiple items
        mockItems.forEach { (slot, item) ->
            gui.items[slot] = item
        }
        
        // Then
        assertEquals(4, gui.items.size)
        mockItems.forEach { (slot, expectedItem) ->
            assertEquals(expectedItem, gui.items[slot])
        }
        
        // Verify empty slots don't exist in map
        assertFalse(gui.items.containsKey(1))
        assertFalse(gui.items.containsKey(26))
    }

    @Test
    fun `should handle GUI open and close callbacks`() {
        // Given
        val gui = GUIEntity(title = "Callback Test GUI", size = 27)
        gui.inventory = mockInventory
        var openCalled = false
        var closeCalled = false
        var callbackPlayer: Player? = null
        
        gui.onOpen = { player, guiEntity ->
            openCalled = true
            callbackPlayer = player
        }
        
        gui.onClose = { player, guiEntity ->
            closeCalled = true
            callbackPlayer = player
        }
        
        // When - Simulate opening GUI
        gui.onOpen?.invoke(mockPlayer, gui)
        
        // Then
        assertTrue(openCalled)
        assertEquals(mockPlayer, callbackPlayer)
        
        // When - Simulate closing GUI
        gui.onClose?.invoke(mockPlayer, gui)
        
        // Then
        assertTrue(closeCalled)
        assertEquals(mockPlayer, callbackPlayer)
    }

    @Test
    fun `should demonstrate simple GUI creation workflow`() {
        // Given - Create a simple GUI setup
        val gui = GUIEntity(
            id = "simple-menu",
            title = "§6§lSimple Menu",
            size = 27
        )
        
        // Mock some items
        val diamondSword = mock<ItemStack>()
        val emerald = mock<ItemStack>()
        val barrier = mock<ItemStack>()
        
        // When - Set up the GUI
        gui.items[10] = diamondSword  // Left item
        gui.items[13] = emerald       // Center item  
        gui.items[16] = barrier       // Right item
        
        var clickedItem: ItemStack? = null
        var clickedSlot = -1
        
        gui.onClick = { player, guiEntity, slot, item ->
            clickedItem = item
            clickedSlot = slot
        }
        
        // Then - Verify GUI structure
        assertEquals("simple-menu", gui.id)
        assertEquals("§6§lSimple Menu", gui.title)
        assertEquals(27, gui.size)
        assertEquals(3, gui.items.size)
        
        assertEquals(diamondSword, gui.items[10])
        assertEquals(emerald, gui.items[13])
        assertEquals(barrier, gui.items[16])
        
        // Simulate clicking center item
        gui.onClick?.invoke(mockPlayer, gui, 13, emerald)
        
        assertEquals(emerald, clickedItem)
        assertEquals(13, clickedSlot)
    }

}