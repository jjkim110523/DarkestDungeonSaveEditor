@echo off
REM ===================================================================
REM Darkest Dungeon Save Editor - Release Build Script (Windows)
REM ===================================================================
REM This script builds the complete release package including:
REM - DDSaveEditor.jar (fat JAR with all dependencies)
REM - Documentation (README.md, LICENSE)
REM - Distribution ZIP file
REM ===================================================================

echo.
echo ========================================
echo Darkest Dungeon Save Editor
echo Release Build Script
echo ========================================
echo.

REM Check if gradlew exists
if not exist "gradlew.bat" (
    echo ERROR: gradlew.bat not found!
    echo Please run this script from the project root directory.
    pause
    exit /b 1
)

REM Clean previous builds
echo [1/4] Cleaning previous builds...
call gradlew.bat clean
if errorlevel 1 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)

REM Build fat JAR (includes all dependencies)
echo.
echo [2/4] Building fat JAR with all dependencies...
call gradlew.bat fatJar
if errorlevel 1 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

REM Create distribution package
echo.
echo [3/4] Creating distribution package...
call gradlew.bat dist
if errorlevel 1 (
    echo ERROR: Distribution creation failed!
    pause
    exit /b 1
)

REM Show results
echo.
echo [4/4] Build completed successfully!
echo.
echo ========================================
echo Build Artifacts:
echo ========================================
echo JAR file:
dir build\libs\*.jar /b
echo.
echo Distribution ZIP:
dir build\dist\*.zip /b
echo.
echo ========================================
echo.
echo The JAR file can be run with:
echo   java -jar build\libs\DDSaveEditor.jar
echo.
echo The distribution ZIP contains:
echo   - DDSaveEditor.jar
echo   - README.md
echo   - LICENSE
echo   - Documentation
echo.
pause
