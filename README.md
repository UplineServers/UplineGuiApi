# CustomGUI Plugin

A comprehensive Minecraft plugin that provides an advanced API for creating custom GUIs with sophisticated features like granular permissions, security monitoring, database persistence, and slot-specific restrictions.

## Features

### 🎨 GUI Management
- **Create custom inventories** with any size (multiples of 9, up to 54 slots)
- **Database persistence** - GUIs and their contents are saved automatically
- **Real-time updates** - Changes to GUIs are immediately reflected to all viewers
- **Automatic cleanup** - Handles player disconnections and plugin shutdowns gracefully

### 🔒 Advanced Permissions
- **Player-specific access control** - Grant or deny individual players
- **Permission node support** - Integration with permission plugins
- **Flexible restrictions** - Allow/deny specific players while using general rules

### 🛡️ Security Features
- **Failed attempt tracking** - Monitor and prevent brute force access attempts
- **Temporary lockouts** - Automatically lock out players after repeated violations
- **Comprehensive logging** - Track all GUI interactions for auditing
- **Suspicious activity detection** - Identify and log unusual patterns

### ⚙️ Granular Restrictions
- **Slot-specific controls** - Block specific slots or allow only certain operations
- **Item interaction rules** - Control insertion, extraction, and movement independently  
- **Click type restrictions** - Block shift-clicking, number keys, dropping items, etc.
- **Auto-close timers** - Automatically close GUIs after inactivity

### 📊 Database Support
- **SQLite** (default) - Zero configuration, perfect for single servers
- **MySQL** - For multi-server setups and advanced deployments
- **Automatic schema management** - Creates and maintains database structure

## Quick Start

### Installation
1. Download the latest `CustomGUI-1.0-SNAPSHOT.jar` from the releases
2. Place it in your server's `plugins` folder
3. Start your server
4. The plugin will create a default configuration in `plugins/CustomGUI/config.yml`

### Basic API Usage

```kotlin
// Get the API instance
val api = CustomGUI.api

// Create a simple shop GUI
val restrictions = GUIRestriction().apply {
    allowItemInsertion = false  // Players can't put items in
    allowItemExtraction = true  // Players can take items out
    blockedSlots.add(0)        // Block first slot
}

val gui = api.createGUI(
    title = "My Shop",
    size = 27,
    creator = "MyPlugin",
    restrictions = restrictions
)

// Add items and open for players
gui?.let {
    api.setItem(it.id, 10, ItemStack(Material.DIAMOND))
    api.addPlayerPermission(it.id, player)
    api.openGUI(player, it.id)
}
```

## Administrative Commands

All commands require the `customgui.admin` permission:

```
/customgui create <title> <size> <creator>    # Create a new GUI
/customgui delete <gui-id>                    # Delete a GUI  
/customgui list [creator]                     # List all GUIs
/customgui info <gui-id>                      # Show GUI details
/customgui open <gui-id>                      # Open a GUI
/customgui permission <gui-id> add <player>   # Give access
/customgui permission <gui-id> remove <player> # Remove access
/customgui security stats <player>           # Security statistics
/customgui reload                            # Reload configuration
```

## Configuration

The plugin automatically creates `config.yml`:

```yaml
# Database settings
database:
  type: sqlite  # or mysql
  
# Security settings  
security:
  max-failed-attempts: 5
  attempt-time-window: 60000  # 1 minute
  lockout-duration: 300000    # 5 minutes

# Performance settings
performance:
  gui-cache-size: 100
  auto-save-interval: 1200
```

## Permissions

```
customgui.*              # All permissions
customgui.admin          # Administrative access
customgui.api.use        # API access for plugins
customgui.create         # Create GUIs
customgui.delete         # Delete GUIs
customgui.manage         # Manage GUI settings
customgui.security.manage # Security management
```

## API Documentation

### Core Classes

#### `CustomGUIAPI`
Main API interface for other plugins to interact with CustomGUI.

#### `GUIRestriction` 
Configures all restrictions and permissions for a GUI:
- `allowItemInsertion/Extraction/Movement` - Control item interactions
- `blockedSlots` - Completely blocked slots
- `insertionBlockedSlots/extractionBlockedSlots` - Slot-specific restrictions
- `requirePermission/permissionNode` - Permission requirements
- `allowedPlayers/deniedPlayers` - Player-specific access
- `logAllActions` - Enable comprehensive logging
- `preventShiftClick/NumberKeys/DropItems` - Security restrictions
- `autoCloseAfter` - Auto-close timer

### Example Plugin Integration

```kotlin
class MyPlugin : JavaPlugin() {
    override fun onEnable() {
        // Wait for CustomGUI to initialize
        server.scheduler.runTaskLater(this, Runnable {
            createMyGUIs()
        }, 20L) // 1 second delay
    }
    
    private fun createMyGUIs() {
        val api = CustomGUI.api
        
        // Create a secure admin panel
        val adminRestrictions = GUIRestriction().apply {
            requirePermission = true
            permissionNode = "myplugin.admin"
            logAllActions = true
            preventShiftClick = true
            preventDropItems = true
            autoCloseAfter = 300 // 5 minutes
        }
        
        val adminGUI = api.createGUI(
            "Admin Panel", 
            36, 
            "MyPlugin", 
            adminRestrictions
        )
        
        adminGUI?.let { gui ->
            // Add admin tools
            api.setItem(gui.id, 10, createAdminTool("Player Manager"))
            api.setItem(gui.id, 11, createAdminTool("Server Settings"))
            // Store GUI ID for later use
            config.set("admin-gui-id", gui.id)
            saveConfig()
        }
    }
}
```

## Database Schema

The plugin automatically creates these tables:

- **`custom_guis`** - GUI definitions and metadata
- **`gui_items`** - Individual item contents for each GUI  
- **`gui_permissions`** - Player-specific permissions
- **`gui_logs`** - Comprehensive action logging

## Performance Considerations

- **Caching**: GUIs are cached in memory after first load
- **Async Operations**: Database operations use async processing where possible
- **Resource Management**: Automatic cleanup of unused resources
- **Optimized Queries**: Efficient database queries with proper indexing

## Security Features

### Access Control
- Failed attempt tracking with configurable thresholds
- Temporary lockouts for suspicious behavior
- Comprehensive audit logging
- Permission-based access with multiple validation layers

### Monitoring
- Real-time security event logging
- Suspicious activity pattern detection
- Administrative tools for security management
- Integration with server logging systems

## Building from Source

Requirements:
- JDK 21+
- Gradle 8.0+

```bash
git clone <repository-url>
cd CustomGUI
./gradlew clean build
```

The built JAR will be in `build/libs/CustomGUI-1.0-SNAPSHOT-all.jar`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Support

- **Issues**: Report bugs and feature requests on GitHub
- **Documentation**: See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for detailed API reference
- **Examples**: Check the `examples` package for implementation examples

## Changelog

### Version 1.0-SNAPSHOT
- Initial release
- Core GUI management system
- Advanced permission system
- Security monitoring and logging
- Database persistence (SQLite/MySQL)
- Comprehensive API for plugin integration
- Administrative command interface
