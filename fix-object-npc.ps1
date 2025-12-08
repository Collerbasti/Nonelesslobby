# Replace all "Object npc" with "NPC npc" in method signatures

$filePath = "c:\Users\webde\Desktop\Projekte\Nonelesslobby\src\main\java\npc\NPCManager.java"

# Read as UTF-8
$content = [System.IO.File]::ReadAllText($filePath, [System.Text.Encoding]::UTF8)

# Replace all occurrences of "Object npc" with "NPC npc" in method signatures
$content = $content -replace '\(Object npc\)', '(NPC npc)'
$content = $content -replace '\(Object npc,', '(NPC npc,'
$content = $content -replace ', Object npc\)', ', NPC npc)'
$content = $content -replace ', Object npc,', ', NPC npc,'

# Also replace other "Object entity" and "Object" types used for NPCs
$content = $content -replace 'Object entity = npc\.getEntity\(\)', 'Entity entity = npc.getEntity()'
$content = $content -replace 'Object entity = .*\.getEntity\(\)', 'Entity entity = npc.getEntity()'

# Write back
[System.IO.File]::WriteAllText($filePath, $content, [System.Text.Encoding]::UTF8)

Write-Host "Alle Object npc Parameter wurden zu NPC npc umgewandelt!"
