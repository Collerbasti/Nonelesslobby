# NonelessLobby Common Issues and Fixes

This document covers common issues found in the NonelessLobby plugin and their fixes.

## Fixed Issues

### 1. MySQL Connector Dependency (FIXED)
**Issue**: The plugin used the deprecated `mysql:mysql-connector-java` dependency.

**Error Message**:
```
The artifact mysql:mysql-connector-java:jar:8.0.33 has been relocated to com.mysql:mysql-connector-j:jar:8.0.33
```

**Fix**: Updated `pom.xml` to use the new Maven coordinates:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
    <scope>compile</scope>
</dependency>
```

**Status**: ✅ Fixed in commit

## Potential Issues to Monitor

### 2. Package Structure
**Observation**: The plugin has some packages at root level (`Config`, `Mysql`, `npc`, etc.) instead of under `de.noneless.lobby`.

**Current Structure**:
```
src/main/java/
├── Config/
├── Mysql/
├── commands/
├── friends/
├── gui/
├── npc/
├── player/
└── de/noneless/lobby/
    ├── commands/
    ├── listeners/
    └── ...
```

**Impact**: 
- Not critical for functionality
- Follows Java package naming conventions inconsistently
- Could cause confusion in larger projects

**Recommendation**: 
- For new code, use reverse domain notation consistently
- Existing code works fine, so leave as-is unless refactoring

### 3. Soft Dependencies
**Observation**: The plugin has several soft dependencies that may not be available:

- Citizens (for NPC management)
- Vault (for permissions/economy)
- PlaceholderAPI (for placeholder support)
- DecentHolograms (for hologram display)
- Multiverse-Core (for world management)

**Impact**: Plugin will load but some features won't be available

**Fix**: Plugin already handles this gracefully with checks like:
```java
if (npcManager.isCitizensAvailable()) {
    npcManager.spawnLobbyNPCs();
}
```

**Recommendation**: Document required vs optional dependencies clearly

### 4. Database Configuration
**Observation**: MySQL database is required for points/stats system

**Potential Issues**:
- Connection failures if database not configured
- Missing tables on first run

**Check for**:
- Proper error handling in `Mysql.Punkte.initializeDatabase()`
- Auto-creation of required tables
- Graceful degradation if database unavailable

**Testing Steps**:
1. Test without database configured (should work without points system)
2. Test with incorrect credentials (should log error, not crash)
3. Test with correct database (should create tables automatically)

### 5. World Requirements
**Observation**: Plugin expects a world named "world" to exist

**Code Location**: `Main.java` line 87-90
```java
World world = getServer().getWorld("world");
if (world != null) {
    lobbyLocation = world.getSpawnLocation();
}
```

**Potential Issue**: If server uses different world name, spawn location may not be set

**Fix**: Already handled with null check, but could log a warning

**Recommendation**: Add configuration option for world name

### 6. Plugin Version in plugin.yml
**Observation**: 
- `pom.xml` version: 3.1
- `plugin.yml` version: 4.0
- `plugin.yml` api-version: 1.21

**Issue**: Version mismatch could cause confusion

**Impact**: Low - mostly affects versioning and compatibility checks

**Recommendation**: 
- Sync version numbers
- Or use Maven filtering to inject version automatically

### 7. API Version Compatibility
**Observation**: 
- `plugin.yml` declares `api-version: 1.21`
- Dependencies use `1.20.4-R0.1-SNAPSHOT`

**Potential Issue**: May have compatibility issues with 1.21 features

**Recommendation**: 
- Test on actual 1.20.4 server
- Update to 1.20 if issues occur
- Or update dependencies to 1.21 if using 1.21 features

## Testing Checklist

### Basic Functionality
- [ ] Plugin loads without errors
- [ ] All commands register successfully
- [ ] Event listeners register successfully
- [ ] Configuration files generate correctly

### Commands Testing
- [ ] `/lobby` - Teleports player to lobby spawn
- [ ] `/setlobby` - Sets lobby spawn point (requires OP)
- [ ] `/profile` - Shows player profile
- [ ] `/punkte` - Shows leaderboard
- [ ] `/friend` - Opens friends menu
- [ ] `/settings` - Opens settings GUI
- [ ] `/serverinfo` - Shows server info
- [ ] `/help` - Shows help menu

### Features Testing
- [ ] Player join - spawns at lobby, gets scoreboard
- [ ] Friend system - can send/accept/deny requests
- [ ] Settings menu - can change preferences
- [ ] Warps system - can access warps
- [ ] NPC system (with Citizens) - NPCs spawn and are interactive
- [ ] Points system (with MySQL) - points are tracked and saved

### Error Conditions
- [ ] Plugin loads without Citizens installed
- [ ] Plugin loads without Vault installed
- [ ] Plugin loads without MySQL configured
- [ ] No errors on player join
- [ ] No errors on player quit
- [ ] Proper error messages for missing permissions

### Performance
- [ ] No lag spikes on player join
- [ ] Scoreboard updates smoothly
- [ ] Database queries don't block main thread
- [ ] World mover works in background without lag

## Running Tests

### 1. Start Test Server
```bash
./setup-test-server.sh
cd minecraft-test-server
./start.sh
```

### 2. Watch for Errors
```bash
# In another terminal
tail -f minecraft-test-server/logs/latest.log | grep -i "error\|exception\|warn"
```

### 3. Connect and Test
Connect to `localhost:25565` and run through the testing checklist

### 4. Check Console Output
Look for:
- "NonelessLobby erfolgreich gestartet!" (successful start)
- "Alle Commands und TabCompleter erfolgreich registriert!" (commands registered)
- "Event Listener erfolgreich registriert." (listeners registered)

## Common Error Patterns

### ClassNotFoundException
**Cause**: Missing dependency or wrong package name

**Example**:
```
java.lang.ClassNotFoundException: de.noneless.lobby.SomeClass
```

**Solution**: Check classpath and ensure all classes are compiled

### NullPointerException on Join
**Cause**: Plugin not fully initialized, or world not found

**Check**:
- Is lobby location set?
- Does "world" exist?
- Are all managers initialized?

### Command Not Found
**Cause**: Command not registered in plugin.yml or executor not set

**Check**:
- Command exists in `plugin.yml`
- `getCommand("name").setExecutor(...)` is called
- Command name matches exactly (case-sensitive)

### Database Connection Failed
**Cause**: MySQL not running or wrong credentials

**Check**:
```sql
# Test connection manually
mysql -u lobby -p -h localhost nonelesslobby
```

**Solution**: Update `config.yml` with correct credentials or disable MySQL

### Citizens Not Found
**Cause**: Citizens plugin not installed

**Expected**: Plugin should still load, NPC features disabled

**Check**:
```
[NonelessLobby] Citizens plugin not found, NPC features will be disabled
```

## Logs to Check

### Server Startup
```bash
grep "NonelessLobby" minecraft-test-server/logs/latest.log
```

### Errors Only
```bash
grep -i "error" minecraft-test-server/logs/latest.log | grep "NonelessLobby"
```

### Warnings
```bash
grep -i "warn" minecraft-test-server/logs/latest.log | grep "NonelessLobby"
```

### Exceptions
```bash
grep -A 10 "Exception" minecraft-test-server/logs/latest.log
```

## Security Considerations

### SQL Injection
**Check**: All database queries use prepared statements

**Review**: Files in `Mysql/` package

### Permission Bypass
**Check**: All admin commands verify permissions

**Test**:
1. Try admin commands without OP
2. Should see "No permission" message

### Resource Exhaustion
**Check**: 
- Database connection pooling (HikariCP)
- Async operations for heavy tasks
- Proper cleanup on disable

## Performance Optimization

### Already Implemented
- ✅ HikariCP for connection pooling
- ✅ Async database operations (where possible)
- ✅ Scheduled tasks for scoreboard updates
- ✅ Maven shade plugin minimizes JAR size

### Could Improve
- Cache frequently accessed data
- Batch database operations
- Use async tasks for file I/O
- Profile with timings report

## Next Steps

1. ✅ Fix MySQL dependency → DONE
2. ✅ Create setup scripts → DONE
3. ✅ Document common issues → DONE
4. ⏳ Test on actual server → Requires manual setup
5. ⏳ Fix any runtime errors found
6. ⏳ Performance profiling
7. ⏳ Security audit

## Resources

- [Paper API Documentation](https://docs.papermc.io/)
- [Spigot Plugin Development](https://www.spigotmc.org/wiki/spigot-plugin-development/)
- [Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP)

## Support

For issues:
1. Check logs: `logs/latest.log`
2. Check config: `plugins/NonelessLobby/config.yml`
3. Verify dependencies are installed
4. Test with minimal setup (no optional plugins)
5. Report issues with full error log
