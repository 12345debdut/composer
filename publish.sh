#!/bin/bash

# Publishing script for Composer Library
# This script helps publish the library to GitHub Packages

set -e

echo "🚀 Publishing Composer Library to GitHub Packages"
echo ""

# Check if local.properties exists
if [ ! -f "local.properties" ]; then
    echo "❌ Error: local.properties file not found!"
    echo "Please create local.properties with the following properties:"
    echo "  GITHUB_USER=your-github-username"
    echo "  GITHUB_TOKEN=your-github-personal-access-token"
    echo "  VERSION_NAME=1.0.0"
    exit 1
fi

# Load properties from local.properties
source <(grep -v '^#' local.properties | sed 's/^/export /')

# Check if required properties are set
if [ -z "GITHUB_USER" ] || [ -z "GITHUB_TOKEN" ]; then
    echo "❌ Error: GITHUB_USER or GITHUB_TOKEN not set in local.properties"
    exit 1
fi

# Use VERSION_NAME from local.properties or default to 1.0.0
VERSION_NAME=${VERSION_NAME:-"1.0.0"}

echo "📦 Publishing version: $VERSION_NAME"
echo "👤 GitHub User: $GITHUB_USER"
echo ""

# Build the release variant first
echo "🔨 Building release variant..."
./gradlew :composer:assembleRelease

# Publish to GitHub Packages
echo "📤 Publishing to GitHub Packages..."
./gradlew :composer:publishReleasePublicationToGitHubPackagesRepository

echo ""
echo "✅ Successfully published version $VERSION_NAME to GitHub Packages!"
echo "🔗 View your package at: https://github.com/debdutsaha/composerlibrary/packages"
echo ""
