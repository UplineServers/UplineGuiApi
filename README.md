# CustomGui

CustomGui is a Paper plugin for building interactive Minecraft GUIs with Kotlin.

It provides a compact API for:
- click actions per slot
- movable/locked GUI items
- put/take permissions
- optional player-inventory integration
- saving GUI contents to item/entity persistent data

## Requirements

- Java 21+
- Paper 1.21+

## Installation

1. Download the latest jar from your release/build output.
2. Put it in your server `plugins/` folder.
3. Start the server once to generate `config.yml`.
4. (If needed) adjust `maxSize` in `plugins/CustomGui/config.yml`.

## Commands

- `/customgui info` — shows loaded GUI/player stats
- `/customgui test <menu|putable|takeable|moveable|item|inventory>` — opens built-in examples

Permission: `customgui.use`

## Developer Quick Start

1. Create a `Gui` with an id, title, and size.
2. Fill `gui.items[slot]` with `GuiItem` objects.
3. Add callbacks (`onOpen`, `onClose`, `onClick`) as needed.
4. Call `gui.build()` once, then `gui.open(player)`.

For step-by-step setup, see:
- [`docs/TUTORIAL.md`](docs/TUTORIAL.md)
- [`docs/EXAMPLES.md`](docs/EXAMPLES.md)

## Build from Source

```bash
./gradlew build
```

Output jar:
- `build/libs/CustomGui-<version>-all.jar`

## Local Test Server

```bash
./gradlew runServer
```

## Configuration

`config.yml`

```yml
maxSize: 32767
```

`maxSize` limits serialized GUI data size when saving to item/entity NBT.

## Contributing

Issues and pull requests are welcome. Please include clear reproduction steps for bugs and expected behavior for feature requests.
