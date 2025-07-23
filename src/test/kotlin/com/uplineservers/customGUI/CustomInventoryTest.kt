package com.uplineservers.customGUI

import com.uplineservers.customGUI.entities.CustomInventory
import org.bukkit.Material
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.bukkit.inventory.ItemStack
import org.mockito.kotlin.mock
import org.bukkit.entity.Player

class CustomInventoryTest {

    @Test
    fun `should create a simple GUI with basic properties`() {
        // Create a simple custom inventory without using Bukkit-dependent operations
        val gui = CustomInventory(
            title = "Test Shop",
            size = 27,
        )

        // Verify basic properties
        assertEquals("Test Shop", gui.title)
        assertEquals(27, gui.size)
        assertNotNull(gui.id) // Should generate a UUID
        assertTrue(gui.items.isEmpty())
    }

    @Test
    fun `should add and retrieve items from GUI slots`() {
        // Create a simple inventory
        val gui = CustomInventory(
            title = "Item Test",
            size = 9,
        )

        val item = ItemStack(Material.CHEST)
        
        // Add item to slot
        gui.setItem(0, item)
        
        // Verify item was added
        assertEquals(item, gui.getItem(0))
        assertEquals(1, gui.items.size)
    }

    @Test
    fun `should handle slot bounds correctly`() {
        val gui = CustomInventory(
            title = "Bounds Test",
            size = 9,
        )

        // Test valid slot bounds
        assertDoesNotThrow {
            gui.setItem(0, mock<ItemStack>())
            gui.setItem(8, mock<ItemStack>())
        }

        // Test invalid slot bounds
        assertThrows(IllegalArgumentException::class.java) {
            gui.setItem(-1, mock<ItemStack>())
        }
        
        assertThrows(IllegalArgumentException::class.java) {
            gui.setItem(9, mock<ItemStack>())
        }
    }

    @Test
    fun `should set event listeners correctly`() {
        val gui = CustomInventory(
            title = "Event Test",
            size = 9
        )

        // Mock player for testing
        val mockPlayer = mock<Player>()
        var openCalled = false
        var closeCalled = false
        var clickCalled = false

        // Set event listeners
        gui.onOpenListener = { player, inventory ->
            openCalled = true
            assertEquals(mockPlayer, player)
            assertEquals(gui, inventory)
        }

        gui.onCloseListener = { player, inventory ->
            closeCalled = true
            assertEquals(mockPlayer, player)
            assertEquals(gui, inventory)
        }

        gui.onClickListener = { player, inventory, slot, item ->
            clickCalled = true
            assertEquals(mockPlayer, player)
            assertEquals(gui, inventory)
            assertEquals(0, slot)
        }

        // Trigger events
        gui.onOpen(mockPlayer)
        gui.onClose(mockPlayer)
        gui.onClick(mockPlayer, 0)

        // Verify events were called
        assertTrue(openCalled, "onOpen event should have been called")
        assertTrue(closeCalled, "onClose event should have been called")
        assertTrue(clickCalled, "onClick event should have been called")
    }

    @Test
    fun `should create a shop-like GUI similar to examples`() {
        // Create a GUI similar to the examples in the codebase
        val shopGUI = CustomInventory(
            title = "My Shop",
            size = 27, // 3 rows like a chest
            items = mutableMapOf()
        )

        // Mock player and items
        val mockPlayer = mock<Player>()
        val mockDiamondItem = mock<ItemStack>()
        val mockEmeraldItem = mock<ItemStack>()
        val mockGoldItem = mock<ItemStack>()

        // Add shop items to specific slots (like the examples do)
        shopGUI.setItem(10, mockDiamondItem) // Diamond in slot 10
        shopGUI.setItem(12, mockEmeraldItem) // Emerald in slot 12
        shopGUI.setItem(14, mockGoldItem)    // Gold in slot 14

        // Set up click listener like in the examples
        var lastClickedSlot = -1
        var lastClickedPlayer: Player? = null
        
        shopGUI.onClickListener = { player, inventory, slot, item ->
            lastClickedSlot = slot
            lastClickedPlayer = player
            
            when (slot) {
                10 -> {
                    System.out.printf("Diamond purchased by %s%n", player.name)
                    // Handle diamond purchase
                }
                12 -> {
                    // Handle emerald purchase
                    System.out.printf("Emerald purchased by %s%n", player.name)
                }
                14 -> {
                    // Handle gold purchase
                    System.out.printf("Gold purchased by %s%n", player.name)
                }
            }
        }

        // Set up open listener like in the examples
        var openMessageSent = false
        shopGUI.onOpenListener = { player, inventory ->
            openMessageSent = true
        }

        // Test the GUI behavior
        shopGUI.onOpen(mockPlayer)
        shopGUI.onClick(mockPlayer, 10)

        // Verify the GUI was set up correctly
        assertEquals("My Shop", shopGUI.title)
        assertEquals(27, shopGUI.size)
        assertEquals(3, shopGUI.items.size) // 3 items added
        assertEquals(mockDiamondItem, shopGUI.getItem(10))
        assertEquals(mockEmeraldItem, shopGUI.getItem(12))
        assertEquals(mockGoldItem, shopGUI.getItem(14))
        
        // Verify events worked
        assertTrue(openMessageSent, "Open event should have been triggered")
        assertEquals(10, lastClickedSlot, "Click event should have been triggered on slot 10")
        assertEquals(mockPlayer, lastClickedPlayer, "Click event should have received the correct player")
    }
}