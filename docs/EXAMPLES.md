# Examples

The six demos shipped with the plugin. Each is a single function you can run in-game with
`/uplineguiapi test <name>` and read in
[`src/main/kotlin/com/uplineservers/uplineguiapi/examples/`](../src/main/kotlin/com/uplineservers/uplineguiapi/examples).

| Demo | File | Shows |
|---|---|---|
| [`menu`](#menu) | `MenuExample.kt` | Static menu, per-item actions, live title change |
| [`putable`](#putable) | `PutableGui.kt` | Deposit-only container |
| [`takeable`](#takeable) | `TakeableGui.kt` | Withdraw-only container |
| [`moveable`](#moveable) | `MoveableGui.kt` | Fully interactive container |
| [`item`](#item) | `ItemSavedGui.kt` | Contents persisted into the held item |
| [`inventory`](#inventory) | `InventoryMenu.kt` | Player-inventory overlay |

Every demo starts with the same reuse guard, since a GUI is unregistered once its last viewer
closes it:

```kotlin
val existing = GuiManager.getById("my_id")
if (existing != null) return existing.open(player)
```

---

## menu

**Classic button menu.** `MenuExample.kt` builds a 27-slot chest with three locked buttons: one
that just sends a message, one that gives the player a diamond block *and* rewrites the GUI title
while it is open, and one that sets the clicker on fire. `onOpen` and `onClose` send greetings.

```kotlin
val gui = Gui(id = "test_menu", title = "<gold><bold>Main Menu", size = 27)

gui.items[13] = GuiItem(sword) { it.whoClicked.sendMessage("You clicked the example item!") }

gui.items[1] = GuiItem(ItemStack(Material.DIAMOND_BLOCK)) {
    (it.whoClicked as? Player)?.inventory?.addItem(ItemStack(Material.DIAMOND_BLOCK))
    gui.title = "<gold><bold>Updated Menu"   // reopens the screen for every viewer
}

gui.onOpen = { it.player.sendMessage("Welcome to the main menu!") }
gui.onClose = { it.player.sendMessage("Thanks for using the main menu!") }
```

Use it for: shops, warp selectors, settings screens, hub menus.

---

## putable

**Deposit box.** `isPutable = true`, `isTakeable = false`. Players can drop items in and never get
them back out through the GUI.

```kotlin
val gui = Gui(id = "placeable_gui", title = "<green>Placeable GUI", size = 9, isPutable = true)
```

Use it for: donation boxes, mail drops, submission slots, crafting inputs you validate yourself.
Pair it with `shiftProtection = SHIFT_PROTECTION.BLOCK` if items must go in one at a time.

---

## takeable

**Reward chest.** `isTakeable = true`, `isPutable = false`. The GUI fills itself in `onOpen` and
players may only remove things.

```kotlin
val gui = Gui(id = "takeable_gui", title = "<green>Takeable GUI", size = 9, isTakeable = true)

gui.onOpen = {
    for (i in 0 until gui.size) gui.inventory?.setItem(i, ItemStack(Material.GOLD_INGOT))
    it.player.sendMessage("Take what you need.")
}
```

Filling in `onOpen` means every viewer sees a freshly stocked GUI. Use it for: kit dispensers,
loot previews, quest rewards.

---

## moveable

**Full container.** Both flags on, no per-item restrictions — a virtual chest.

```kotlin
val gui = Gui(
    id = "movable_gui",
    title = "<aqua>Movable GUI",
    size = 9,
    isPutable = true,
    isTakeable = true,
)
```

Note that contents are **in memory only**: when the last viewer closes it, the GUI is disposed and
whatever was inside is gone. Combine it with `dataItem` / `dataEntity` (see below) when the
contents need to survive.

---

## item

**Item-backed persistence.** `ItemSavedGui.kt` binds a 9-slot container to whatever the player is
holding. Contents are written into the item's persistent data on close and read back on build.

```kotlin
val playerHand = player.inventory.itemInMainHand
if (playerHand.type == Material.AIR) {
    player.sendMessage("<red>You must hold an item to save it in the GUI.")
    return
}

val gui = Gui(
    id = "item_saved_gui",
    title = "<yellow>Item Saved GUI",
    size = 9,
    isPutable = true,
    isTakeable = true,
    dataItem = playerHand,
)
```

While the GUI is open, the bound item cannot be moved, dropped or hotbar-swapped, so its storage
can never be separated from it. Swap `dataItem` for `dataEntity` to bind the same storage to a
villager, armour stand or any other entity.

Use it for: backpacks, bound tools, portable containers, per-entity chests.

> The demo uses the fixed id `item_saved_gui`. For real backpacks use a per-item or per-player id
> (for example `"backpack_" + player.uniqueId`) so two players never share one instance.

---

## inventory

**Player-inventory overlay.** `InventoryMenu.kt` opens a 54-slot chest *and* takes over the
player's storage rows with three rows of action buttons.

```kotlin
val gui = Gui(
    id = "inventoryMenu_" + player.uniqueId,
    title = "<aqua>Inventory Menu",
    size = 54,
    extraSize = EXTRA_INVENTORY.MAIN,
    isTakeable = false,
    isPutable = false,
)

for (i in 0 until 9) {
    gui.items[54 + i] = GuiItem(ItemStack(Material.WOODEN_SWORD)) { /* row 1 */ }
    gui.items[63 + i] = GuiItem(ItemStack(Material.STONE_SWORD))  { /* row 2 */ }
    gui.items[72 + i] = GuiItem(ItemStack(Material.GOLDEN_SWORD)) { /* row 3 */ }
}
```

Slots `54`–`80` map onto player storage slots `9`–`35`; with `EXTRA_INVENTORY.FULL` you also get
`81`–`89` for the hotbar. The real inventory is saved and cleared on open and restored on close,
and pickups are blocked meanwhile.

Use it for: RPG loadouts, class selectors, big catalogues that need more than 54 slots.

---

## Patterns worth copying

- **Reuse ids deliberately.** A shared id means shared live contents; a per-player id
  (`id + player.uniqueId`) keeps state isolated.
- **Always guard with `GuiManager.getById`** before rebuilding — `build()` throws on a duplicate id.
- **Leave buttons locked.** A default `GuiItem` (`isMovable = false`) cannot be stolen, and gets a
  unique `minecraft:item_uuid` tag so it never merges with a player's own items.
- **Use `shiftProtection = SHIFT_PROTECTION.BLOCK`** whenever item routing has to be exact.
- **Fill dynamic contents in `onOpen`**, not at construction time, so every viewer sees current data.
- **Guard bulk updates** with `gui.isUpdating = true` — clicks, drags and drops are cancelled while
  it is set.
- **Persist with `dataItem` / `dataEntity`**, not with the GUI object: GUIs are disposed when the
  last viewer closes them.

More detail: [TUTORIAL.md](TUTORIAL.md) · [API.md](API.md) · [README](../README.md)
