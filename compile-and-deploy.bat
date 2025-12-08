@echo off
setlocal enabledelayedexpansion

echo.
echo =========================================
echo   NonelessLobby Kompilieren + Deployen
echo =========================================
echo.

REM Überprüfe, ob Maven installiert ist
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    color c
    echo [ERROR] Maven ist nicht installiert oder nicht im PATH!
    echo.
    pause
    exit /b 1
)

echo [INFO] Starte Kompilierung und Deployment...
echo.

REM Kompiliere
mvn clean package -DskipTests

if %errorlevel% neq 0 (
    color c
    echo.
    echo [ERROR] Kompilierung fehlgeschlagen!
    echo.
    pause
    exit /b 1
)

echo.
echo [INFO] Suche Plugin-Ordner...

REM Versuche das JAR in verschiedene Server-Plugin-Ordner zu kopieren
set "DEPLOYED=0"

REM Test-Server (OllamaChatUI minecraft-server)
if exist "C:\Users\webde\Desktop\Projekte\OllamaChatUI\minecraft-server\plugins" (
    echo [INFO] Kopiere JAR zum Test-Server...
    copy target\NonelessLobby-*.jar "C:\Users\webde\Desktop\Projekte\OllamaChatUI\minecraft-server\plugins\" >nul 2>nul
    if %errorlevel% equ 0 set "DEPLOYED=1"
)

REM Standard-Spigot-Ordner
if exist "..\..\server\plugins" (
    echo [INFO] Kopiere JAR nach ..\..\server\plugins\
    copy target\NonelessLobby-*.jar ..\..\server\plugins\ >nul 2>nul
    if %errorlevel% equ 0 set "DEPLOYED=1"
)

REM Alternative Pfade
if exist "..\server\plugins" (
    echo [INFO] Kopiere JAR nach ..\server\plugins\
    copy target\NonelessLobby-*.jar ..\server\plugins\ >nul 2>nul
    if %errorlevel% equ 0 set "DEPLOYED=1"
)

if !DEPLOYED! equ 1 (
    color 2
    echo.
    echo =========================================
    echo   SUCCESS! Kompilierung und Deploy OK!
    echo =========================================
    echo.
    echo Das Plugin wurde in den Server kopiert!
    echo Starten Sie den Server neu zum Laden.
    echo.
) else (
    color e
    echo.
    echo =========================================
    echo   WARNUNG: Plugin nicht gedeployt
    echo =========================================
    echo.
    echo Das JAR befindet sich in: target\
    echo Bitte manuell in plugins\ kopieren.
    echo.
)

pause
exit /b 0
