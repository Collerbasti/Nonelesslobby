# NonelessLobby Test Server Setup Guide

This guide will help you compile the plugin and set up a test Minecraft server.

## Prerequisites

- Java 17 or higher installed
- Maven 3.6 or higher installed
- Internet connection for downloading server and dependencies

## Step 1: Compile the Plugin

```bash
cd /path/to/Nonelesslobby
mvn clean package
```

The compiled plugin will be located at: `target/NonelessLobby-3.1.jar`

### Known Issues Fixed

1. **MySQL Connector Dependency**: Updated from deprecated `mysql:mysql-connector-java` to `com.mysql:mysql-connector-j` (new Maven coordinates)

## Step 2: Download Minecraft Server

You have several options:

### Option A: Paper Server (Recommended)
Paper is a high-performance fork of Spigot with better plugin support.

```bash
# Create server directory
mkdir minecraft-test-server
cd minecraft-test-server

# Download Paper 1.20.4 (Build 497)
wget https://api.papermc.io/v2/projects/paper/versions/1.20.4/builds/497/downloads/paper-1.20.4-497.jar -O server.jar
```

### Option B: Spigot Server
Build Spigot using BuildTools:

```bash
# Create server directory
mkdir minecraft-test-server
cd minecraft-test-server

# Download BuildTools
wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar

# Build Spigot 1.20.4
java -jar BuildTools.jar --rev 1.20.4

# Rename the built JAR
mv spigot-1.20.4.jar server.jar
```

## Step 3: Initial Server Setup

1. **Start the server once to generate files:**
```bash
java -Xms2G -Xmx2G -jar server.jar nogui
```

2. **Accept the EULA:**
The server will stop after creating `eula.txt`. Edit the file:
```bash
echo "eula=true" > eula.txt
```

3. **Configure server properties** (optional):
Edit `server.properties`:
```properties
online-mode=false
gamemode=adventure
difficulty=peaceful
max-players=20
view-distance=10
spawn-protection=0
```

## Step 4: Install the Plugin

1. **Copy the plugin to the plugins folder:**
```bash
cp /path/to/Nonelesslobby/target/NonelessLobby-3.1.jar plugins/
```

2. **Create plugin configuration directory:**
```bash
mkdir -p plugins/NonelessLobby
```

## Step 5: Optional Dependencies

The plugin has soft dependencies on these plugins (optional but recommended):

- **Citizens** (for NPC support): https://ci.citizensnpcs.co/job/Citizens2/
- **Vault** (for economy/permissions): https://www.spigotmc.org/resources/vault.34315/
- **PlaceholderAPI**: https://www.spigotmc.org/resources/placeholderapi.6245/
- **DecentHolograms**: https://www.spigotmc.org/resources/decentholograms.96927/
- **Multiverse-Core** (for world management): https://dev.bukkit.org/projects/multiverse-core

Download and place these JARs in the `plugins/` folder if needed.

## Step 6: MySQL Database Setup (Optional)

If you want to use the points/stats system:

1. **Install MySQL/MariaDB**

2. **Create a database:**
```sql
CREATE DATABASE nonelesslobby;
CREATE USER 'lobby'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON nonelesslobby.* TO 'lobby'@'localhost';
FLUSH PRIVILEGES;
```

3. **Configure the plugin:**
Create/edit `plugins/NonelessLobby/config.yml`:
```yaml
mysql:
  enabled: true
  host: localhost
  port: 3306
  database: nonelesslobby
  username: lobby
  password: your_password
```

## Step 7: Start the Server

```bash
java -Xms2G -Xmx2G -jar server.jar nogui
```

## Step 8: Verify Plugin Loading

Check the console for:
```
[NonelessLobby] NonelessLobby erfolgreich gestartet!
```

## Step 9: In-Game Testing

1. **Connect to the server** (localhost:25565)

2. **Test basic commands:**
   - `/lobby` - Teleport to lobby
   - `/help` - View all commands
   - `/serverinfo` - View server information
   - `/settings` - Open settings menu

3. **Admin commands** (requires OP):
   - `/setlobby` - Set lobby spawn point
   - `/lobbynpc` - Manage NPCs (if Citizens is installed)
   - `/punkteadmin` - Manage points system

## Common Issues and Solutions

### Issue 1: Plugin doesn't load
- **Check server version**: Must be 1.20.4 or compatible
- **Check logs**: Look for errors in `logs/latest.log`
- **Verify Java version**: Must be Java 17

### Issue 2: NPCs not working
- **Install Citizens**: The NPC features require the Citizens plugin
- **Check Citizens compatibility**: Ensure Citizens supports your server version

### Issue 3: Database connection errors
- **Verify MySQL is running**: `systemctl status mysql`
- **Check credentials**: Ensure username/password are correct
- **Test connection**: Use `mysql -u lobby -p` to verify access

### Issue 4: Permission errors
- **Grant OP**: `/op <playername>` for testing
- **Install Vault + Permission Plugin**: For production use

### Issue 5: Commands not working
- **Check permissions**: Ensure you have the required permission nodes
- **View plugin.yml**: Check command aliases and usage
- **Check console**: Look for command registration errors

## Quick Test Script

Save this as `test-setup.sh`:

```bash
#!/bin/bash

# Create test server directory
mkdir -p minecraft-test-server
cd minecraft-test-server

# Download Paper server
echo "Downloading Paper server..."
wget -q https://api.papermc.io/v2/projects/paper/versions/1.20.4/builds/497/downloads/paper-1.20.4-497.jar -O server.jar

# Accept EULA
echo "eula=true" > eula.txt

# Create plugins directory
mkdir -p plugins

# Copy plugin
echo "Copying NonelessLobby plugin..."
cp ../target/NonelessLobby-3.1.jar plugins/

# Configure server
cat > server.properties << EOF
online-mode=false
gamemode=adventure
difficulty=peaceful
max-players=20
view-distance=8
spawn-protection=0
motd=NonelessLobby Test Server
EOF

echo "Setup complete! Start server with:"
echo "java -Xms2G -Xmx2G -jar server.jar nogui"
```

Make it executable and run:
```bash
chmod +x test-setup.sh
./test-setup.sh
```

## Development Testing

For rapid testing during development:

1. **Build plugin:**
```bash
mvn clean package -DskipTests
```

2. **Copy to server:**
```bash
cp target/NonelessLobby-3.1.jar minecraft-test-server/plugins/
```

3. **Reload plugin** (in-game or console):
```
/reload confirm
```

Or use `plugman` plugin for hot-reloading without server restart.

## Troubleshooting Commands

```bash
# View server logs
tail -f logs/latest.log

# Check for errors
grep -i error logs/latest.log

# View plugin list
# In server console: plugins

# Check plugin version
# In server console: version NonelessLobby
```

## Additional Resources

- Paper Documentation: https://docs.papermc.io/
- Spigot Plugin Development: https://www.spigotmc.org/wiki/spigot-plugin-development/
- Bukkit API Reference: https://hub.spigotmc.org/javadocs/bukkit/

## Support

For issues specific to NonelessLobby:
1. Check the console logs
2. Review `plugins/NonelessLobby/config.yml`
3. Verify all dependencies are met
4. Check plugin.yml for version compatibility
