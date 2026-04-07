#!/usr/bin/env bash
# Promote a staged Sonatype repo into a Central Portal "deployment".
#
# Usage:
#   MAVEN_CENTRAL_USERNAME=<token-user> \
#   MAVEN_CENTRAL_PASSWORD=<token-pass> \
#   NAMESPACE=io.github.12345debdut \
#     ./upload-to-central-auth.sh
#
# Run this AFTER `./gradlew publishAllPublicationsToSonatypeRepository`
# from the same machine (it finds the staging repo by client IP).

set -e

USERNAME="${MAVEN_CENTRAL_USERNAME:?Set MAVEN_CENTRAL_USERNAME}"
PASSWORD="${MAVEN_CENTRAL_PASSWORD:?Set MAVEN_CENTRAL_PASSWORD}"
NAMESPACE="${NAMESPACE:?Set NAMESPACE (e.g. io.github.12345debdut)}"

AUTH=$(printf '%s:%s' "$USERNAME" "$PASSWORD" | base64)
API="https://ossrh-staging-api.central.sonatype.com"

echo "── Searching for staging repository by client IP ──"
RESULT=$(curl -sSf -H "Authorization: Bearer $AUTH" "$API/manual/search/repositories?ip=client")
echo "$RESULT" | jq '.' 2>/dev/null || echo "$RESULT"
KEY=$(echo "$RESULT" | jq -r '.repositories[0].key // empty' 2>/dev/null || echo "")

if [ -n "$KEY" ]; then
  echo "── Promoting staging repo $KEY ──"
  curl -sSf -X POST \
    -H "Authorization: Bearer $AUTH" \
    -H "Content-Type: application/json" \
    "$API/manual/upload/repository/$KEY?publishing_type=user_managed"
else
  echo "No staging repo found by IP. Falling back to defaultRepository for namespace $NAMESPACE"
  curl -sSf -X POST \
    -H "Authorization: Bearer $AUTH" \
    -H "Content-Type: application/json" \
    "$API/manual/upload/defaultRepository/$NAMESPACE?publishing_type=user_managed"
fi

echo ""
echo "Upload requested. Open https://central.sonatype.com/publishing to find the deployment and click Publish."
