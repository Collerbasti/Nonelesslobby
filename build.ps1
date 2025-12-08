# Build-Skript für NonelessLobby
Write-Host "========================================" -ForegroundColor Green
Write-Host "NonelessLobby Build" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Setze Java und Maven PATH
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH;..\MiniCardGame\apache-maven-3.9.6\bin"

Write-Host "Kompiliere NonelessLobby..." -ForegroundColor Cyan

# Kompiliere das Plugin
mvn clean compile package

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "NonelessLobby Build erfolgreich!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Plugin wurde deployed zu: C:\testserver\plugins\NonelessLobby.jar" -ForegroundColor Green
    Write-Host ""
    
    # Aktualisiere auch das lokale Repository für MiniCardGame
    $localRepoPath = "$env:USERPROFILE\.m2\repository\de\noneless\NonelessLobby\3.0"
    New-Item -ItemType Directory -Path $localRepoPath -Force | Out-Null
    Copy-Item "target\NonelessLobby-3.0.jar" "$localRepoPath\NonelessLobby-3.0.jar" -Force
    Write-Host "NonelessLobby JAR ins lokale Repository kopiert!" -ForegroundColor Green
    
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "Build fehlgeschlagen!" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    exit 1
}