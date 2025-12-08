# NonelessLobby Plugin - Build & Test Guide

A comprehensive Minecraft lobby plugin with advanced features including friends system, points leaderboard, NPC management, and world mover functionality.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Minecraft Server 1.20.4 (Paper or Spigot recommended)

### Automated Setup (Recommended)
```bash
# From the project root directory
./setup-test-server.sh
```

This script will:
1. Compile the plugin
2. Download Paper server
3. Configure server settings
4. Install the plugin
5. Create start scripts

### Manual Setup
See [SERVER_SETUP_GUIDE.md](SERVER_SETUP_GUIDE.md) for detailed instructions.

## 📦 Building the Plugin

### Standard Build
```bash
mvn clean package
```

The compiled plugin will be at: `target/NonelessLobby-3.1.jar`

### Quick Build (Skip Tests)
```bash
mvn clean package -DskipTests
```

### Build Issues?
See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for common build problems and solutions.

## 🎮 Testing the Plugin

### Start Test Server
```bash
cd minecraft-test-server
./start.sh          # Optimized with Aikar's flags
# or
./start-simple.sh   # Basic startup
```

### Connect to Server
- **Address**: localhost:25565
- **Version**: 1.20.4

### Test Commands

#### Basic User Commands
```
/lobby             # Teleport to lobby spawn
/profile [player]  # View player profile
/punkte            # View points leaderboard
/friend <action>   # Manage friends
/settings          # Open settings menu
/warps             # View available warps
/serverinfo        # Server information
```

#### Admin Commands (Requires OP)
```
/setlobby          # Set lobby spawn point
/lobbynpc <action> # Manage NPCs (requires Citizens)
/punkteadmin       # Points system management
/punktegeben <player> <amount>  # Give points
/worldmover <direction> <blocks> [world]  # Move world vertically
```

## 🔧 Configuration

### Plugin Configuration
Edit `plugins/NonelessLobby/config.yml`:

```yaml
# MySQL Database (Optional - required for points system)
mysql:
  enabled: false
  host: localhost
  port: 3306
  database: nonelesslobby
  username: lobby
  password: change_me

# Lobby Settings
lobby:
  spawn-on-join: true
  protect-lobby-world: true
  disable-damage: true
  disable-hunger: true

# Features
scoreboard:
  enabled: true
  update-interval: 20

friends:
  enabled: true
  max-friends: 50

npcs:
  enabled: false  # Requires Citizens plugin
  auto-spawn: true
```

### Server Configuration
The setup script creates an optimized `server.properties`:
- Online mode: disabled (for testing)
- Game mode: adventure
- Difficulty: peaceful
- PVP: disabled
- Spawn protection: disabled
- Flight: enabled

## 📚 Features

### Core Features
- **Lobby System**: Customizable spawn point with /lobby command
- **Friends System**: Add, remove, and manage friends
- **Points/Leaderboard**: Track player points and rankings
- **Settings Menu**: Customizable player preferences
- **Warps System**: Quick teleportation points
- **Scoreboard**: Real-time player statistics

### Advanced Features
- **NPC Management**: Interactive NPCs (requires Citizens plugin)
- **World Mover**: Gradually move entire worlds vertically in background
- **Gamemode Enforcement**: Automatic gamemode management
- **MySQL Integration**: Persistent data storage
- **HikariCP**: Optimized database connection pooling

### Optional Dependencies
- **Citizens**: NPC creation and management
- **Vault**: Economy and permissions integration
- **PlaceholderAPI**: Custom placeholder support
- **DecentHolograms**: Holographic displays
- **Multiverse-Core**: Multi-world management

## 🔍 Quality Assurance

### Recent Fixes
- ✅ Fixed MySQL connector dependency (updated to new Maven coordinates)
- ✅ Added comprehensive error handling
- ✅ Optimized database operations with HikariCP
- ✅ Implemented async operations for performance

### Testing Checklist
See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for complete testing checklist

### Known Issues
- None currently reported

## 📖 Documentation

- **[SERVER_SETUP_GUIDE.md](SERVER_SETUP_GUIDE.md)**: Detailed setup instructions
- **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)**: Common issues and solutions
- **[NPC_INTEGRATION_README.md](NPC_INTEGRATION_README.md)**: NPC system documentation

## 🛠️ Development

### Project Structure
```
src/main/java/
├── Config/              # Configuration management
├── Mysql/               # Database operations
├── commands/            # Legacy command handlers
├── friends/             # Friends system
├── gui/                 # GUI menus
├── npc/                 # NPC management
├── player/              # Player data management
└── de/noneless/lobby/   # Main plugin code
    ├── commands/        # Command implementations
    ├── listeners/       # Event listeners
    ├── api/            # Public API
    ├── scoreboard/     # Scoreboard system
    ├── util/           # Utility classes
    └── world/          # World management
```

### Building for Production
```bash
# Clean build with all optimizations
mvn clean package

# Copy to production server
cp target/NonelessLobby-3.1.jar /path/to/server/plugins/
```

### Hot Reload (Development)
```bash
# Build
mvn clean package -DskipTests

# Copy to test server
cp target/NonelessLobby-3.1.jar minecraft-test-server/plugins/

# Reload (in server console or game)
/reload confirm
```

## 🔒 Security

### Implemented Security Features
- ✅ Prepared statements for SQL queries (SQL injection prevention)
- ✅ Permission checks on all admin commands
- ✅ Resource pooling to prevent exhaustion
- ✅ Input validation on user commands
- ✅ Async operations to prevent main thread blocking

### Security Best Practices
- Change default database passwords
- Use strong credentials for MySQL
- Regularly update dependencies
- Review logs for suspicious activity
- Keep plugin and server up to date

## 📊 Performance

### Optimization Features
- **HikariCP**: Fast database connection pooling
- **Async Operations**: Database and I/O operations run async
- **Caching**: Points and player data cached in memory
- **Batch Operations**: Multiple operations grouped together
- **Maven Shade**: Minimized JAR size

### Performance Testing
```bash
# Generate timing report (in-game or console)
/timings on
# ... perform actions ...
/timings paste
```

## 🤝 Contributing

### Code Style
- Use proper Java naming conventions
- Add comments for complex logic
- Follow existing code structure
- Test before committing

### Reporting Issues
1. Check [TROUBLESHOOTING.md](TROUBLESHOOTING.md) first
2. Include full error logs
3. Describe steps to reproduce
4. Mention server version and plugin configuration

## 📝 Version Information

- **Plugin Version**: 3.1
- **API Version**: 1.21
- **Minecraft Version**: 1.20.4
- **Java Version**: 17

## 📞 Support

### Useful Commands for Debugging
```bash
# View plugin information
# In console: version NonelessLobby

# Check logs
tail -f logs/latest.log

# Find errors
grep -i "error\|exception" logs/latest.log | grep NonelessLobby

# Test database connection (if MySQL enabled)
mysql -u lobby -p -h localhost nonelesslobby
```

### Getting Help
1. Review documentation files
2. Check server logs: `logs/latest.log`
3. Verify configuration: `plugins/NonelessLobby/config.yml`
4. Test with minimal setup (no optional plugins)
5. Create detailed issue report with logs

## 🎯 Next Steps After Setup

1. **Set Lobby Spawn**: `/setlobby` at desired location
2. **Configure Database**: Edit config.yml if using MySQL
3. **Install Optional Plugins**: Citizens, Vault, etc. for extra features
4. **Test Commands**: Run through all commands to verify functionality
5. **Configure Permissions**: Set up permission groups if needed
6. **Customize Settings**: Adjust config.yml to your needs
7. **Add Warps**: Set up warp points for players
8. **Create NPCs**: If Citizens installed, add interactive NPCs

## 🌟 Features Showcase

### Friends System
- Send/accept/deny friend requests
- View online friends
- Teleport to friends
- Friend list management

### Points System
- Earn points for activities
- View leaderboard
- Admin commands for managing points
- Persistent storage in MySQL

### World Mover
- Gradually move entire worlds up/down
- Background processing
- No lag or interruption
- Resume after restart

### Settings Menu
- Customizable player preferences
- GUI-based configuration
- Per-player settings
- Persistent across sessions

## 📄 License

See project license file for details.

## 🙏 Acknowledgments

- **Paper**: High-performance Minecraft server
- **Spigot**: Plugin API and community
- **HikariCP**: Fast connection pooling
- **Citizens**: NPC framework

---

**Happy Testing! 🎮**

For detailed setup instructions, see [SERVER_SETUP_GUIDE.md](SERVER_SETUP_GUIDE.md)
For troubleshooting, see [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
