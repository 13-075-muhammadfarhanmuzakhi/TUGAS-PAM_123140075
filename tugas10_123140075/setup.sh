#!/bin/bash
# =============================================================
# Notes App - Week 10: Koin DI + Testing Setup Script
# Run: chmod +x setup.sh && ./setup.sh
# =============================================================

set -e
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
echo "Setting up Notes App at: $PROJECT_DIR"

# Create all directory structure
mkdir -p composeApp/src/commonMain/kotlin/com/notes/{data/{local,repository},di,domain/model,presentation/{screen,viewmodel}}
mkdir -p composeApp/src/commonTest/kotlin/com/notes/{data,presentation,di}
mkdir -p composeApp/src/androidMain/kotlin/com/notes
mkdir -p composeApp/src/androidInstrumentedTest/kotlin/com/notes

echo "✅ Directories created"
echo "✅ Setup complete! Open project in Android Studio."
echo ""
echo "Run tests with:"
echo "  ./gradlew :composeApp:testDebugUnitTest"
echo "  ./gradlew :composeApp:connectedAndroidTest"
echo "  ./gradlew :composeApp:koverHtmlReportDebug"
