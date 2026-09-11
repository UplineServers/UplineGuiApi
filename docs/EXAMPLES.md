# Example Usages

These examples match the built-in demos in `/customgui test`.

## Menu (`menu`)

File: `MenuExample.kt`

Shows a classic static menu with:
- clickable action buttons
- title update while open
- open/close callbacks

Use this for shops, selector menus, and simple hub UIs.

## Putable (`putable`)

File: `PutableGui.kt`

`isPutable = true`, `isTakeable = false`.

Players can place items in, but cannot remove them. Useful for deposit-style screens.

## Takeable (`takeable`)

File: `TakeableGui.kt`

`isPutable = false`, `isTakeable = true`.

GUI can provide items players are allowed to take.

## Moveable (`moveable`)

File: `MoveableGui.kt`

`isPutable = true`, `isTakeable = true`.

Fully interactive container behavior.

## Item-backed persistence (`item`)

File: `ItemSavedGui.kt`

Binds GUI storage to the item in the player's hand (`dataItem`).

Use this for backpacks, containers, or bound-tool inventories.

## Inventory overlay (`inventory`)

File: `InventoryMenu.kt`

Uses `extraSize = EXTRA_INVENTORY.MAIN` plus slots above 53 to map action rows into player inventory space.

Use this for RPG loadouts, class kits, or quick-action hotbar systems.

## Pattern recommendations

- Reuse GUI ids when you want shared state.
- Use per-player ids when state should be isolated.
- Keep non-movable action buttons as default `GuiItem` (locked).
- Use `shiftProtection = SHIFT_PROTECTION.BLOCK` when item routing must be strict.
