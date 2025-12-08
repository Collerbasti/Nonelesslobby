@echo off
setlocal enabledelayedexpansion

echo.
echo =========================================
echo   NonelessLobby Schnell-Kompilierung
echo =========================================
echo.

REM Überprüfe, ob Maven installiert ist
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    color c
    echo [ERROR] Maven ist nicht installiert oder nicht im PATH!
    echo Bitte Maven installieren: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

echo [INFO] Starte schnelle Kompilierung (ohne Tests)...
echo.

REM Schnelle Kompilierung ohne Tests und ohne sauberen Build
mvn package -DskipTests

if %errorlevel% equ 0 (
    color 2
    echo.
    echo =========================================
    echo   SUCCESS! Kompilierung erfolgreich!
    echo =========================================
    echo.
    echo Das JAR-File: target\NonelessLobby-*.jar
    echo.
) else (
    color c
    echo.
    echo =========================================
    echo   ERROR! Kompilierung fehlgeschlagen!
    echo =========================================
    echo.
)

pause
exit /b %errorlevel%
