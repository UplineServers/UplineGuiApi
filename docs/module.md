# Module UplineGuiApi

Advanced custom GUI API for Paper plugins.

Build a [com.uplineservers.uplineguiapi.models.Gui], fill its `items` map with
[com.uplineservers.uplineguiapi.models.GuiItem] entries, then call `build()` and `open(player)`.
The plugin handles click filtering, drag protection, player-inventory overlays and optional
persistence of the contents into an item or entity.

See the [tutorial](https://github.com/UplineServers/UplineGuiApi/blob/main/docs/TUTORIAL.md) for a
step-by-step walkthrough.

# Package com.uplineservers.uplineguiapi.models

The types you work with directly: `Gui`, `GuiItem` and the `SHIFT_PROTECTION` / `EXTRA_INVENTORY`
behaviour enums.

# Package com.uplineservers.uplineguiapi.services

Registry and machinery behind the models. `GuiManager` is the public entry point for looking up
GUIs and their viewers; the rest is called for you by `Gui`.

# Package com.uplineservers.uplineguiapi.listeners

Bukkit event listeners that enforce the interaction rules. Registered automatically on enable.

# Package com.uplineservers.uplineguiapi.commands

The `/uplineguiapi` command and its subcommands.

# Package com.uplineservers.uplineguiapi.examples

Runnable demo GUIs behind `/uplineguiapi test <demo>`. Read these as copy-paste starting points.
