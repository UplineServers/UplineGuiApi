# UplineGuiApi Tutorial

A step-by-step guide from an empty menu to inventory overlays and persistent storage.
Every snippet is Kotlin and assumes these imports:

```kotlin
import com.uplineservers.uplineguiapi.models.Gui
import com.uplineservers.uplineguiapi.models.GuiItem
import com.uplineservers.uplineguiapi.models.EXTRA_INVENTORY
import com.uplineservers.uplineguiapi.models.SHIFT_PROTECTION
import com.uplineservers.uplineguiapi.services.GuiManager
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
```

New to the project? Start with the [README](../README.md) for installation and the dependency setup.

---

## 1. Create a GUI

A `Gui` is a plain object. Give it a stable `id`, a MiniMessage `title`, and a `size` that is a
multiple of 9 (chest inventories only).

```kotlin
val gui = Gui(
    id = "shop_main",
    title = "<gold><bold>Shop",
    size = 27,
)
```

The `id` is the key the GUI is registered under. Pick a **shared id** when every viewer should
see the same live contents, and a **per-player id** when each player needs their own copy:

```kotlin
val gui = Gui(id = "shop_main_" + player.uniqueId, /* ... */)
```

For a non-chest screen, set `type` instead of relying on `size`:

```kotlin
val anvil = Gui(id = "rename", type = InventoryType.ANVIL, title = "<red>Rename")
```

---

## 2. Add items and click actions

Items go into `gui.items`, keyed by slot. Each one is a `GuiItem` with an optional click handler.

```kotlin
val sword = ItemStack(Material.DIAMOND_SWORD).apply {
    itemMeta = itemMeta.apply {
        displayName(Component.text("Starter sword"))
        lore(listOf(Component.text("Costs 100 coins")))
    }
}

gui.items[13] = GuiItem(sword) { event ->
    val buyer = event.whoClicked as? Player ?: return@GuiItem
    buyer.sendMessage("You bought a sword!")
}
```

By default a `GuiItem` is a **locked button**: clickable, but players cannot take it out or
replace it — even if the GUI itself allows moving items. Pass `isMovable = true` to opt a single
slot into free movement:

```kotlin
gui.items[22] = GuiItem(ItemStack(Material.PAPER), isMovable = true)
```

`isMovable` on an item always wins over the GUI-wide `isPutable` / `isTakeable` flags.

---

## 3. Configure the interaction rules

The GUI-wide flags decide what players may do with slots that have no explicit `GuiItem`:

```kotlin
val gui = Gui(
    id = "deposit_box",
    title = "<aqua>Deposit",
    size = 27,
    isPutable = true,                              // players may place items in
    isTakeable = false,                            // but never take anything out
    shiftProtection = SHIFT_PROTECTION.BLOCK,      // and shift-click is disabled entirely
)
```

| Goal | Flags |
|---|---|
| Pure menu, nothing movable | `isPutable = false`, `isTakeable = false` (defaults) |
| Deposit box | `isPutable = true`, `isTakeable = false` |
| Reward chest / kit dispenser | `isPutable = false`, `isTakeable = true` |
| Full container | `isPutable = true`, `isTakeable = true` |

Use `shiftProtection = SHIFT_PROTECTION.BLOCK` whenever item routing has to be strict — shift-click
moves whole stacks across inventories and easily bypasses slot-by-slot intentions.

Need an item that is untouchable everywhere, including slots you did not register? Write the
persistent-data flag the click listener looks for:

```kotlin
val filler = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
    itemMeta = itemMeta.apply {
        persistentDataContainer.set(
            NamespacedKey.minecraft("gui_blocked"),
            PersistentDataType.BOOLEAN,
            true,
        )
    }
}
```

---

## 4. Hook into the lifecycle

All five callbacks are nullable properties you can assign at any point:

```kotlin
gui.onCreate  = { g -> plugin.logger.info("GUI ${g.id} created") }
gui.onOpen    = { event -> event.player.sendMessage("Welcome!") }
gui.onClick   = { event -> if (event.isRightClick) event.isCancelled = true }
gui.onClose   = { event -> event.player.sendMessage("Bye!") }
gui.onDestroy = { g -> plugin.logger.info("GUI ${g.id} disposed") }
```

`onClick` runs **before** every rule check, so cancelling the event there stops all further
handling — it is the place for a global guard. `onCreate` fires during `build()`, `onDestroy`
when the last viewer closes the GUI and it is unregistered.

---

## 5. Build and open

```kotlin
gui.build()
gui.open(player)
```

`build()` creates the Bukkit inventory, loads persisted contents (if the GUI is bound to an item
or entity) and registers the GUI in `GuiManager`. Call it **once** — building a second GUI with an
id that is still registered throws `IllegalArgumentException`, and calling `open()` before
`build()` throws `IllegalStateException`.

Since a GUI is disposed as soon as its last viewer closes it, the standard entry point looks like
this:

```kotlin
fun openShop(player: Player) {
    GuiManager.getById("shop_main")?.let { return it.open(player) }

    val gui = Gui(id = "shop_main", title = "<gold><bold>Shop", size = 27)
    // ... fill items and callbacks ...
    gui.build()
    gui.open(player)
}
```

---

## 6. Update a GUI that is already open

```kotlin
gui.title = "<gold><bold>Shop <gray>(sold out)"   // reopens the screen for every viewer
gui.items[13] = GuiItem(newIcon) { /* ... */ }
gui.updateSlot(13, player)                        // push one slot back into the inventory
gui.update()                                      // recreate + reopen for everyone
```

Assigning a new `title` triggers `update()` automatically, which recreates the inventory and
reopens it for all viewers — the client briefly redraws the screen, so avoid doing it every tick.

For bulk changes, set `gui.isUpdating = true` first: while it is true every click, drag and drop
is cancelled, so players cannot interact with a half-updated screen. Remember to set it back to
`false`.

You can also write straight into the live inventory when you do not need a `GuiItem` wrapper:

```kotlin
gui.inventory?.setItem(4, ItemStack(Material.GOLD_INGOT))
```

---

## 7. Extend the GUI into the player's inventory

Set `extraSize` and use slots above 53. The player's real inventory is backed up and cleared on
open, then restored on close; item pickup is blocked while such a GUI is open.

```kotlin
val gui = Gui(
    id = "loadout_" + player.uniqueId,
    title = "<aqua>Loadout",
    size = 54,
    extraSize = EXTRA_INVENTORY.MAIN,   // takes over player storage slots 9-35
)

for (i in 0 until 9) {
    gui.items[54 + i] = GuiItem(ItemStack(Material.WOODEN_SWORD)) { event ->
        (event.whoClicked as? Player)?.sendMessage("Row 1, column ${i + 1}")
    }
}
```

| Slot range | Maps to | Requires |
|---|---|---|
| `54` – `80` | Player storage slots `9` – `35` | `EXTRA_INVENTORY.MAIN` or `FULL` |
| `81` – `89` | Player hotbar slots `0` – `8` | `EXTRA_INVENTORY.FULL` |

`MAIN` leaves the hotbar and armour alone; `FULL` clears the entire inventory and restores the
previously held slot on close. Using a slot above 53 without `extraSize`, or above 80 without
`FULL`, throws at `build()` time.

---

## 8. Persist the contents

Bind the GUI to an item or an entity before `build()` and its contents survive closing, reopening
and server restarts:

```kotlin
val held = player.inventory.itemInMainHand
if (held.type == Material.AIR) {
    player.sendMessage("<red>Hold the backpack you want to open.")
    return
}

val gui = Gui(
    id = "backpack_" + player.uniqueId,
    title = "<yellow>Backpack",
    size = 27,
    isPutable = true,
    isTakeable = true,
    dataItem = held,          // or dataEntity = someEntity
)

gui.build()   // loads whatever was stored in the item
gui.open(player)
```

Contents are serialized to JSON in the holder's persistent data container under
`uplineguiapi:stored_inventory`, saved when a viewer closes the GUI and again on server shutdown.
Payloads longer than the `maxSize` setting in `config.yml` (default `32767`) are skipped with a
warning, so keep persistent GUIs reasonably small. While the GUI is open, the bound item itself
cannot be moved, dropped or hotbar-swapped.

---

## 9. Inspect and manage GUIs at runtime

`GuiManager` is the registry:

```kotlin
GuiManager.getById("shop_main")          // Gui?
GuiManager.getByPlayer(player)           // the GUI this player currently has open
GuiManager.getByInventory(inventory)     // reverse lookup from a Bukkit inventory
GuiManager.getPlayers("shop_main")       // everyone viewing that GUI
GuiManager.getGuis()                     // every registered GUI
GuiManager.removeAll(gui)                // force-close it for everyone
```

`/uplineguiapi info` prints the same counters in-game.

---

## 10. Try the built-in demos

```
/uplineguiapi test menu
/uplineguiapi test putable
/uplineguiapi test takeable
/uplineguiapi test moveable
/uplineguiapi test item
/uplineguiapi test inventory
```

Each one maps to a short source file in
`src/main/kotlin/com/uplineservers/uplineguiapi/examples/` — read them alongside
[EXAMPLES.md](EXAMPLES.md) and adapt the closest pattern to your plugin.

Full field-by-field reference: [API.md](API.md).
