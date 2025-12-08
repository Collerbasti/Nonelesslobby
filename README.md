# NonelessLobby - Minecraft Lobby Plugin

[![Version](https://img.shields.io/badge/version-3.1-blue.svg)](https://github.com/Collerbasti/Nonelesslobby)
[![Minecraft](https://img.shields.io/badge/minecraft-1.20.4-green.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/java-17-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/license-Custom-red.svg)](LICENSE)

A comprehensive Minecraft lobby plugin with advanced features including friends system, points leaderboard, NPC management, and world mover functionality.

## 🚀 Quick Start

### Option 1: Automated Setup (Recommended)
```bash
./setup-test-server.sh
```

### Option 2: Manual Setup
See [SERVER_SETUP_GUIDE.md](SERVER_SETUP_GUIDE.md) for detailed instructions.

## 📋 Documentation

| Document | Description |
|----------|-------------|
| **[BUILD_AND_TEST.md](BUILD_AND_TEST.md)** | Quick start guide for building and testing |
| **[SERVER_SETUP_GUIDE.md](SERVER_SETUP_GUIDE.md)** | Comprehensive server setup walkthrough |
| **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** | Common issues and solutions |
| **[SECURITY_ADVISORY.md](SECURITY_ADVISORY.md)** | Security fixes and best practices |
| **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** | Complete project overview |
| **[NPC_INTEGRATION_README.md](NPC_INTEGRATION_README.md)** | NPC system documentation |

## ✨ Features

### Core Features
- 🏠 **Lobby System** - Customizable spawn point with `/lobby` command
- 👥 **Friends System** - Add, remove, and manage friends
- 🏆 **Points/Leaderboard** - Track player points and rankings
- ⚙️ **Settings Menu** - Customizable player preferences
- 🚪 **Warps System** - Quick teleportation points
- 📊 **Scoreboard** - Real-time player statistics

### Advanced Features
- 🤖 **NPC Management** - Interactive NPCs (requires Citizens plugin)
- 🌍 **World Mover** - Gradually move entire worlds vertically
- 🎮 **Gamemode Enforcement** - Automatic gamemode management
- 💾 **MySQL Integration** - Persistent data storage with HikariCP
- 🔒 **Security** - Prepared statements, permission checks, secure configuration

## 🔧 Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher (for building)
- Minecraft Server 1.20.4 (Paper or Spigot)

### Building from Source
```bash
# Clone the repository
git clone https://github.com/Collerbasti/Nonelesslobby.git
cd Nonelesslobby

# Build with Maven
mvn clean package

# Find the compiled plugin at:
# target/NonelessLobby-3.1.jar
```

### Installing on Server
1. Copy `NonelessLobby-3.1.jar` to your server's `plugins/` folder
2. Start the server to generate configuration files
3. Edit `plugins/NonelessLobby/config.yml` as needed
4. Restart the server

## ⚙️ Configuration

Basic configuration in `plugins/NonelessLobby/config.yml`:

```yaml
# MySQL Database (Optional)
mysql:
  enabled: false
  host: localhost
  port: 3306
  database: nonelesslobby
  username: lobby
  password: changeme

# Lobby Settings
lobby:
  spawn-on-join: true
  protect-lobby-world: true
  disable-damage: true
  disable-hunger: true
  allow-flight: true

# Features
friends:
  enabled: true
  max-friends: 50

scoreboard:
  enabled: true
  update-interval: 20
```

See [config.yml](src/main/resources/config.yml) for all available options.

## 🎮 Commands

### Player Commands
- `/lobby` - Teleport to lobby spawn
- `/profile [player]` - View player profile
- `/punkte` - View points leaderboard
- `/friend <action>` - Manage friends
- `/settings` - Open settings menu
- `/warps` - View available warps
- `/serverinfo` - Server information

### Admin Commands (Requires OP)
- `/setlobby` - Set lobby spawn point
- `/lobbynpc <action>` - Manage NPCs
- `/punkteadmin` - Points system management
- `/punktegeben <player> <amount>` - Give points to player
- `/worldmover <direction> <blocks> [world]` - Move world vertically

## 🔌 Dependencies

### Required
- Spigot/Paper API 1.20.4
- Java 17+

### Optional (Soft Dependencies)
- **Citizens** - For NPC features
- **Vault** - For economy/permissions integration
- **PlaceholderAPI** - For custom placeholders
- **DecentHolograms** - For holographic displays
- **Multiverse-Core** - For multi-world management

## 🔒 Security

### Recent Security Fixes
- ✅ **CRITICAL**: Removed hardcoded database credentials (2024-12-08)
- ✅ Implemented config-based credential management
- ✅ All SQL queries use prepared statements
- ✅ Permission checks on admin commands
- ✅ Resource pooling to prevent exhaustion

See [SECURITY_ADVISORY.md](SECURITY_ADVISORY.md) for details.

### Security Best Practices
1. Always change default passwords in `config.yml`
2. Use strong database credentials
3. Set proper file permissions: `chmod 600 config.yml`
4. Keep plugin and server updated
5. Review logs regularly

## 🛠️ Development

### Project Structure
```
src/main/java/
├── Config/              # Configuration management
├── Mysql/               # Database operations
├── friends/             # Friends system
├── npc/                 # NPC management
└── de/noneless/lobby/   # Main plugin code
    ├── commands/        # Command implementations
    ├── listeners/       # Event listeners
    ├── api/            # Public API
    └── scoreboard/     # Scoreboard system
```

### Building for Development
```bash
# Quick build (skip tests)
mvn clean package -DskipTests

# Copy to test server
cp target/NonelessLobby-3.1.jar minecraft-test-server/plugins/

# Reload plugin (in-game or console)
/reload confirm
```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=YourTestClass
```

## 📊 Performance

### Optimization Features
- ⚡ **HikariCP** - Fast database connection pooling
- 🔄 **Async Operations** - Non-blocking database and I/O
- 💾 **Caching** - Points and player data cached in memory
- 📦 **Minimized JAR** - Maven shade plugin optimization
- 🎯 **Efficient Queries** - Optimized database queries

### Performance Tips
- Enable MySQL for persistent storage
- Adjust `async-database` setting in config
- Monitor with `/timings` command
- Review HikariCP pool settings

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

### Code Style
- Follow existing code structure
- Use proper Java naming conventions
- Add comments for complex logic
- Test before committing

## 📝 Version History

### Version 3.1 (Current)
- ✅ Fixed critical security vulnerability (hardcoded credentials)
- ✅ Updated MySQL connector to new Maven coordinates
- ✅ Added comprehensive configuration system
- ✅ Improved error handling and logging
- ✅ Added extensive documentation

See [CHANGELOG.md](CHANGELOG.md) for full history (if available).

## 🐛 Troubleshooting

Having issues? Check out:
1. [TROUBLESHOOTING.md](TROUBLESHOOTING.md) - Common issues and solutions
2. Server logs: `logs/latest.log`
3. Plugin config: `plugins/NonelessLobby/config.yml`
4. Console errors during startup

### Common Issues
- **Plugin won't load**: Check Java version (needs 17+)
- **Commands not working**: Verify permissions
- **Database errors**: Check MySQL configuration
- **NPCs not spawning**: Install Citizens plugin

## 📞 Support

- **Documentation**: See docs folder and markdown files
- **Issues**: Create an issue on GitHub
- **Server Logs**: Check `logs/latest.log` for errors

## 📄 License

See [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Paper Team** - High-performance Minecraft server
- **Spigot** - Plugin API and community
- **HikariCP** - Fast connection pooling library
- **Citizens** - NPC framework

## 🌟 Star History

If you find this plugin useful, please consider giving it a star! ⭐

---

**Quick Links:**
- [Build & Test Guide](BUILD_AND_TEST.md)
- [Setup Guide](SERVER_SETUP_GUIDE.md)
- [Troubleshooting](TROUBLESHOOTING.md)
- [Security Advisory](SECURITY_ADVISORY.md)

Made with ❤️ by Noneless
