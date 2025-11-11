#!/bin/bash
###################################################################
# Darkest Dungeon Save Editor - Release Build Script (Linux/Mac)
###################################################################
# This script builds the complete release package including:
# - DDSaveEditor.jar (fat JAR with all dependencies)
# - Documentation (README.md, LICENSE)
# - Distribution ZIP file
###################################################################

set -e  # Exit on error

echo ""
echo "========================================"
echo "Darkest Dungeon Save Editor"
echo "Release Build Script"
echo "========================================"
echo ""

# Check if gradlew exists
if [ ! -f "gradlew" ]; then
    echo "ERROR: gradlew not found!"
    echo "Please run this script from the project root directory."
    exit 1
fi

# Make gradlew executable
chmod +x gradlew

# Clean previous builds
echo "[1/4] Cleaning previous builds..."
./gradlew clean

# Build fat JAR (includes all dependencies)
echo ""
echo "[2/4] Building fat JAR with all dependencies..."
./gradlew fatJar

# Create distribution package
echo ""
echo "[3/4] Creating distribution package..."
./gradlew dist

# Show results
echo ""
echo "[4/4] Build completed successfully!"
echo ""
echo "========================================"
echo "Build Artifacts:"
echo "========================================"
echo "JAR file:"
ls -lh build/libs/*.jar
echo ""
echo "Distribution ZIP:"
ls -lh build/dist/*.zip
echo ""
echo "========================================"
echo ""
echo "The JAR file can be run with:"
echo "  java -jar build/libs/DDSaveEditor.jar"
echo ""
echo "The distribution ZIP contains:"
echo "  - DDSaveEditor.jar"
echo "  - README.md"
echo "  - LICENSE"
echo "  - Documentation"
echo ""
