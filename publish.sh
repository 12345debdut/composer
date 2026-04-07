#!/bin/bash

# Local publishing script for Composer Library.
# Publishes all four artifacts (composer, composer-compose, composer-fragment,
# composer-bom) to the Sonatype Central Portal staging repo. After this
# completes, run upload-to-central-auth.sh to promote the staging repo into
# a Central Portal deployment, then click "Publish" on
# https://central.sonatype.com/publishing.
#
# Required local config in ~/.gradle/gradle.properties (NEVER commit):
#   signingInMemoryKeyId=<8-char hex key id>
#   signingInMemoryKeyPassword=<gpg passphrase>
#   signingInMemoryKey=<full ASCII-armored private key with real newlines>
#   SONATYPE_USERNAME=<central portal user token name>
#   SONATYPE_PASSWORD=<central portal user token secret>

set -e

VERSION_NAME=$(grep "^LIBRARY_VERSION=" gradle.properties | cut -d'=' -f2)
echo "── Publishing Composer Library v$VERSION_NAME to Sonatype ──"
echo

echo "── Running unit tests ──"
./gradlew :composer:test :composer-compose:test

echo "── Checking binary API compatibility ──"
./gradlew :composer:apiCheck :composer-compose:apiCheck

echo "── Publishing all four modules to Sonatype staging repo ──"
./gradlew \
    :composer:publishReleasePublicationToSonatypeRepository \
    :composer-compose:publishReleasePublicationToSonatypeRepository \
    :composer-fragment:publishReleasePublicationToSonatypeRepository \
    :composer-bom:publishBomPublicationToSonatypeRepository \
    --no-configuration-cache

echo
echo "── Upload complete ──"
echo "Next steps:"
echo "  1. Run ./upload-to-central-auth.sh to promote the staging repo"
echo "  2. Open https://central.sonatype.com/publishing"
echo "  3. Find the new deployment and click 'Publish'"
