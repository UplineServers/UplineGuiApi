# CustomGui Tutorial

This tutorial walks through creating and opening your first GUI.

## 1) Create a GUI

Create a `Gui` with a stable id, title, and size (multiple of 9 for chest inventories).

## 2) Add items with actions

Set items in `gui.items[slot]` using `GuiItem`.

Use:
- `isMovable = false` for locked buttons
- `isMovable = true` for slots players can move items in

Attach per-item click behavior with the `onClick` callback in `GuiItem`.

## 3) Configure interaction rules

Use `Gui` flags to define behavior:
- `isPutable`: allow placing items into GUI slots
- `isTakeable`: allow taking items from GUI slots
- `shiftProtection`: block shift-click transfers (`SHIFT_PROTECTION.BLOCK`)
- `extraSize`: map GUI slots into player inventory (`EXTRA_INVENTORY.MAIN` or `FULL`)

## 4) Add GUI-level callbacks

Optional callbacks:
- `onOpen`
- `onClose`
- `onClick`
- `onCreate`
- `onDestroy`

Use these for lifecycle behavior, messages, analytics, or shared click guards.

## 5) Build and open

Call `gui.build()` once, then `gui.open(player)`.

If you reopen frequently, reuse an existing GUI from `GuiManager` instead of rebuilding every time.

## 6) Update after creation

- `gui.update()` to refresh title/inventory instance
- `gui.updateSlot(slot, player)` to refresh a single slot

## 7) Persist inventory contents (optional)

To persist item contents, set one of:
- `dataItem` (save into held item persistent data)
- `dataEntity` (save into entity persistent data)

CustomGui auto-loads saved contents on build and saves on close/disable.

## 8) Try built-in examples

Run in-game:

- `/customgui test menu`
- `/customgui test putable`
- `/customgui test takeable`
- `/customgui test moveable`
- `/customgui test item`
- `/customgui test inventory`

Then inspect files under `src/main/kotlin/com/uplineservers/customgui/examples` to adapt patterns to your plugin.
