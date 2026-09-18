# API Reference

Every public type in `com.uplineservers.uplineguiapi`, version `1.4`.

- [`models.Gui`](#modelsgui)
- [`models.GuiItem`](#modelsguiitem)
- [`models.SHIFT_PROTECTION`](#modelsshift_protection)
- [`models.EXTRA_INVENTORY`](#modelsextra_inventory)
- [`services.GuiManager`](#servicesguimanager)
- [`services.GuiBuild`](#servicesguibuild)
- [`services.GuiStore`](#servicesguistore)
- [`services.InventoryManager`](#servicesinventorymanager)
- [`UplineGuiApi`](#uplineguiapi)
- [Click resolution order](#click-resolution-order)
- [Persistent data keys](#persistent-data-keys)
- [Exceptions](#exceptions)

---

## `models.Gui`

```kotlin
class Gui(
    var id: String = UUID.randomUUID().toString(),
    var type: InventoryType = InventoryType.CHEST,
    title: String = "Custom GUI",
    val size: Int = 27,
    val extraSize: EXTRA_INVENTORY? = null,
    val items: MutableMap<Int, GuiItem> = mutableMapOf(),
    val isPutable: Boolean = false,
    val isTakeable: Boolean = false,
    var isUpdating: Boolean = false,
    val shiftProtection: SHIFT_PROTECTION = SHIFT_PROTECTION.NONE,
    var inventory: Inventory? = null,
    var dataItem: ItemStack? = null,
    var dataEntity: Entity? = null,
    var onOpen: ((InventoryOpenEvent) -> Unit)? = null,
    var onClose: ((InventoryCloseEvent) -> Unit)? = null,
    var onClick: ((InventoryClickEvent) -> Unit)? = null,
    var onCreate: ((gui: Gui) -> Unit)? = null,
    var onDestroy: ((gui: Gui) -> Unit)? = null,
)
```

### Properties

| Name | Type | Mutable | Description |
|---|---|---|---|
| `id` | `String` | yes | Registry key. Must be unique among registered GUIs. Defaults to a random UUID. |
| `type` | `InventoryType` | yes | Bukkit inventory type. `CHEST` uses `size`; every other type uses its own fixed layout. |
| `title` | `String` | yes | MiniMessage string. **Assigning a different value calls `update()`** when the inventory already exists, which recreates it and reopens it for all viewers. |
| `size` | `Int` | no | Chest slot count; must be a multiple of 9 or `build()` throws. |
| `extraSize` | `EXTRA_INVENTORY?` | no | `null` disables the player-inventory overlay. |
| `items` | `MutableMap<Int, GuiItem>` | contents | Slot → item. Edit freely before `build()` and after; call `updateSlot`/`update` to push changes to open screens. |
| `isPutable` | `Boolean` | no | Default "players may place items into GUI slots". |
| `isTakeable` | `Boolean` | no | Default "players may take items out of GUI slots". |
| `isUpdating` | `Boolean` | yes | While `true`, clicks and item drops are cancelled. Set automatically around close handling. |
| `shiftProtection` | `SHIFT_PROTECTION` | no | `BLOCK` cancels every shift-click in the GUI. |
| `inventory` | `Inventory?` | yes | The live Bukkit inventory. `null` until `build()`. |
| `dataItem` | `ItemStack?` | yes | When set, contents are persisted into this item's PDC. |
| `dataEntity` | `Entity?` | yes | When set (and `dataItem` is `null`), contents are persisted into this entity's PDC. |

### Callbacks

| Name | Signature | When it fires | Notes |
|---|---|---|---|
| `onCreate` | `(Gui) -> Unit` | Inside `GuiManager.add`, during `build()` | Runs before the GUI is in the registry. |
| `onOpen` | `(InventoryOpenEvent) -> Unit` | After the viewer is registered and the overlay applied | One call per viewer. |
| `onClick` | `(InventoryClickEvent) -> Unit` | First step of click handling | Cancel the event to stop all further processing. |
| `onClose` | `(InventoryCloseEvent) -> Unit` | Before the viewer is removed | `isUpdating` is `true` for the duration. |
| `onDestroy` | `(Gui) -> Unit` | When the last viewer leaves and the GUI is unregistered | Last chance to read contents. |

### Methods

#### `fun build()`
Creates the inventory, applies `items` for slots `0`–`53`, loads persisted contents when
`dataItem`/`dataEntity` is set, and registers the GUI (firing `onCreate`).

Throws:
- `IllegalArgumentException` if a GUI with the same `id` is already registered.
- `IllegalArgumentException` if `size % 9 != 0` for a chest.
- `IllegalArgumentException` if a slot `> 53` is used without `extraSize`, or `> 80` without `EXTRA_INVENTORY.FULL`.

#### `fun open(player: Player)`
Opens the built inventory. Throws `IllegalStateException` if `inventory` is `null`
(i.e. `build()` was not called). Viewer registration and the overlay happen in the
`InventoryOpenEvent` listener, not here.

#### `fun update()`
Recreates the inventory from the current `title`/`type`/`size`, copies the existing contents over
and reopens it for every viewer. Called automatically when `title` changes.

#### `fun updateSlot(slot: Int, player: Player)`
Writes `items[slot]` (or clears the slot when absent) into the live inventory for `slot < 54`, or
into the player's own inventory for overlay slots. No-op when the GUI has no inventory.

#### `fun addPlayer(player: Player)`
Marks `player` as a viewer of this GUI. Called by the open listener; only needed for manual
bookkeeping.

---

## `models.GuiItem`

```kotlin
data class GuiItem(
    var item: ItemStack,
    val isMovable: Boolean = false,
    val onClick: (InventoryClickEvent) -> Unit = { },
)
```

| Member | Description |
|---|---|
| `item` | The stack rendered in the slot. |
| `isMovable` | `false` (default) locks the slot: the item cannot be taken or replaced, whatever the GUI flags say. `true` allows both, again overriding the GUI flags. |
| `onClick` | Runs on every click on this slot, before the put/take rules are evaluated. Cancel the event to stop further handling. |

When `isMovable` is `false` the constructor writes a random UUID into the item's persistent data
container under `minecraft:item_uuid`, so identical-looking buttons never stack with each other or
with a player's own items.

---

## `models.SHIFT_PROTECTION`

```kotlin
enum class SHIFT_PROTECTION { NONE, BLOCK }
```

| Value | Effect |
|---|---|
| `NONE` | Shift-clicks follow the normal put/take rules. |
| `BLOCK` | Every shift-click inside the GUI is cancelled. |

---

## `models.EXTRA_INVENTORY`

```kotlin
enum class EXTRA_INVENTORY { MAIN, FULL }
```

| Value | Backed up & cleared on open | Usable extra slots |
|---|---|---|
| `MAIN` | Player storage slots `9`–`35` | `54`–`80` → player `9`–`35` |
| `FULL` | The entire player inventory, plus the held-item slot | `54`–`80` → player `9`–`35`, `81`–`89` → player hotbar `0`–`8` |

Mapping: `playerSlot = slot - 45` for slots below 81, `playerSlot = slot - 81` from 81 up.
The saved inventory is restored when the GUI closes, and `EntityPickupItemEvent` is cancelled for
viewers while an overlay GUI is open.

---

## `services.GuiManager`

Registry of GUIs and their viewers (`object`, no instantiation).

| Member | Returns | Description |
|---|---|---|
| `getById(id: String)` | `Gui?` | Registered GUI with that id. |
| `getByPlayer(player: Player)` | `Gui?` | The GUI this player currently has open. |
| `getByInventory(inventory: Inventory)` | `Gui?` | Reverse lookup from a Bukkit inventory. |
| `getPlayers()` | `Collection<String>` | The GUI id of every current viewer — `size` is the number of players inside a GUI. |
| `getPlayers(id: String)` | `List<Player>` | The online viewers of one GUI. |
| `getGuis()` | `Collection<Gui>` | Every registered GUI. |
| `add(gui: Gui)` | `Unit` | Fires `onCreate` and registers the GUI. Normally called by `build()`. |
| `delete(gui: Gui)` | `Unit` | Fires `onDestroy` and unregisters the GUI. |
| `addPlayer(gui: Gui, player: Player)` | `Unit` | Marks a viewer. |
| `removePlayer(player: Player, force: Boolean = false)` | `Unit` | Persists (if bound), removes the viewer, deletes the GUI when it was the last one, and restores the player's real inventory. With `force = false` the restore is deferred one tick so that reopening another GUI immediately does not clobber it. |
| `removeAll(gui: Gui)` | `Unit` | Force-removes every viewer of a GUI. |

---

## `services.GuiBuild`

Inventory construction, used internally by `Gui`. Call the `Gui` methods instead unless you need
the lower level.

| Member | Description |
|---|---|
| `build(gui: Gui)` | Implementation behind `Gui.build()`. |
| `update(gui: Gui)` | Implementation behind `Gui.update()`. |
| `updateSlot(gui: Gui, slot: Int, player: Player)` | Implementation behind `Gui.updateSlot()`. |
| `buildExtra(gui: Gui, player: Player)` | Saves the player's inventory and writes the overlay slots into it. Called by the open listener. |

Titles are deserialized with `MiniMessage.miniMessage().deserialize(gui.title)`.

---

## `services.GuiStore`

Persistence of GUI contents into an item or entity.

| Member | Description |
|---|---|
| `save(gui: Gui)` | Serializes non-empty slots to JSON and writes it to `dataItem`'s or `dataEntity`'s PDC under `uplineguiapi:stored_inventory`. Skipped with a warning when the JSON is longer than `maxSize` from `config.yml`. |
| `load(gui: Gui)` | Reads that JSON back into the inventory. Called by `build()` when the GUI is bound. |

Saving happens automatically when a viewer closes a bound GUI and for every bound GUI on plugin
disable.

---

## `services.InventoryManager`

Backup and restore of real player inventories for overlay GUIs.

| Member | Description |
|---|---|
| `save(player: Player, type: EXTRA_INVENTORY)` | Stores and clears the player's inventory (`MAIN`: slots 9–35, `FULL`: everything plus the held slot). No-op if a backup already exists. |
| `load(player: Player)` | Restores the backup and drops it from memory. |

Backups live in memory only — a server crash while a player has an overlay GUI open loses them.

---

## `UplineGuiApi`

```kotlin
class UplineGuiApi : JavaPlugin() {
    companion object { val instance: UplineGuiApi }
}
```

`UplineGuiApi.instance` is the plugin singleton, available from `onEnable` onwards — useful for
`NamespacedKey`s, the scheduler and `config`. On disable, every bound GUI is saved and all viewers
are removed (restoring their inventories).

---

## Click resolution order

`InventoryClick` evaluates a click on a GUI in this order. The first rule that cancels wins.

1. The clicked stack is the GUI's `dataItem` (directly or via a hotbar number key) → cancel.
2. `gui.isUpdating` → cancel.
3. `gui.onClick` → if it cancelled the event, stop.
4. Click outside any inventory (`rawSlot < 0`) → cancel.
5. The stack carries `minecraft:gui_blocked = true` → cancel.
6. Shift-click while `shiftProtection == BLOCK` → cancel.
7. Click inside the player's inventory:
   - with an overlay (`extraSize != null`): run the overlay item's `onClick`, then cancel.
   - without: shift-clicks are cancelled unless `isPutable`; plain clicks are left alone.
8. Click inside a GUI slot: run `GuiItem.onClick`, then apply
   `guiItem.isMovable ?: gui.isPutable` for placing and `guiItem.isMovable ?: gui.isTakeable` for
   taking — including hotbar-swap clicks. Shift-clicks additionally require `isTakeable`.

Drags are cancelled when any affected slot is not movable (`GuiItem.isMovable`, falling back to
`gui.isPutable`). Item drops are cancelled while `isUpdating`, and pickups while an overlay GUI is
open.

---

## Persistent data keys

| Key | Type | Written by | Meaning |
|---|---|---|---|
| `minecraft:item_uuid` | `STRING` | `GuiItem` init, for non-movable items | Makes each locked button unique. |
| `minecraft:gui_blocked` | `BOOLEAN` | Your code | Any item tagged `true` is click-proof inside every GUI. |
| `uplineguiapi:stored_inventory` | `STRING` (JSON) | `GuiStore.save` | Serialized contents of a bound GUI. |

---

## Exceptions

| Exception | Cause |
|---|---|
| `IllegalArgumentException: GUI with id <id> already exists` | `build()` on an id that is still registered. Look it up with `GuiManager.getById` first. |
| `IllegalArgumentException: Chest size must be a multiple of 9 ...` | `size` is not a multiple of 9 for a chest GUI. |
| `IllegalArgumentException: Cannot set item in slot <n> ... extraSize is not set` | Slot above 53 without `extraSize`. |
| `IllegalArgumentException: Cannot set item in slot <n> ... extraSize is not FULL` | Slot above 80 without `EXTRA_INVENTORY.FULL`. |
| `IllegalStateException: GUI inventory is null for id: <id>` | `open()` called before `build()`. |

---

[README](../README.md) · [TUTORIAL](TUTORIAL.md) · [EXAMPLES](EXAMPLES.md)
