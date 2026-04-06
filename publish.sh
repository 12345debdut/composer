#!/bin/bash

# Publishing script for Composer Library
# Publishes all three artifacts (composer, composer-compose, composer-bom) to Maven Central.

set -e

echo "Publishing Composer Library to Maven Central"
echo ""

# Verify local.properties has credentials (for local publishing)
if [ -f "local.properties" ]; then
    if ! grep -q "mavenCentralUsername" local.properties; then
        echo "Warning: mavenCentralUsername not found in local.properties."
        echo "Add Maven Central credentials to local.properties or export them as environment variables:"
        echo "  ORG_GRADLE_PROJECT_mavenCentralUsername=..."
        echo "  ORG_GRADLE_PROJECT_mavenCentralPassword=..."
        echo "  ORG_GRADLE_PROJECT_signingInMemoryKey=..."
        echo "  ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=..."
        echo ""
    fi
fi

# Load VERSION_NAME from gradle.properties
VERSION_NAME=$(grep "^VERSION_NAME=" gradle.properties | cut -d'=' -f2)
echo "Version: $VERSION_NAME"
echo ""

# Run tests
echo "Running tests..."
./gradlew :composer:test :composer-compose:test

# Check binary API compatibility
echo "Checking binary API compatibility..."
./gradlew :composer:apiCheck :composer-compose:apiCheck

# Publish all three modules atomically
echo "Publishing all modules to Maven Central..."
./gradlew \
    :composer:publishAndReleaseToMavenCentral \
    :composer-compose:publishAndReleaseToMavenCentral \
    :composer-bom:publishAndReleaseToMavenCentral \
    --no-configuration-cache

echo ""
echo "Successfully published version $VERSION_NAME to Maven Central."
echo "Verify at: https://central.sonatype.com/artifact/io.github.debdutsaha/composer"
echo ""
