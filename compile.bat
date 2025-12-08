@echo off
setlocal enabledelayedexpansion

REM Farben für die Konsole
REM Green=2, Red=C, Yellow=E, White=F

echo.
echo =========================================
echo   NonelessLobby Kompilierungs-Tool
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

echo [INFO] Maven gefunden!
echo [INFO] Starte Kompilierung...
echo.

REM Führe Maven aus
mvn clean package -DskipTests

REM Überprüfe, ob die Kompilierung erfolgreich war
if %errorlevel% equ 0 (
    color 2
    echo.
    echo =========================================
    echo   SUCCESS! Kompilierung erfolgreich!
    echo =========================================
    echo.
    echo Das JAR-File befindet sich in: target\
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
