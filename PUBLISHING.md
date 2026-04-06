# Publishing Guide

This guide explains how to publish the Composer library to **Maven Central** via the Sonatype Central Portal.

## Prerequisites

1. A Sonatype Central Portal account at [central.sonatype.com](https://central.sonatype.com)
2. The namespace `io.github.debdutsaha` verified on the Central Portal
3. A GPG key pair for signing artifacts

---

## One-Time Setup

### 1. Register on Sonatype Central Portal

1. Sign up at [central.sonatype.com](https://central.sonatype.com)
2. Navigate to **Account > Namespaces** and add `io.github.debdutsaha`
3. Verify the namespace (you'll be asked to create a temporary public repo)
4. Generate a **User Token** under **Account > Access User Token** — this gives you a username/password pair for publishing

### 2. Generate a GPG Key

```bash
# Generate a new key (use RSA 4096)
gpg --gen-key

# List keys and note your key ID
gpg --list-secret-keys --keyid-format LONG

# Export the private key in ASCII armor
gpg --export-secret-keys --armor YOUR_KEY_ID > signing-key.asc

# Publish your public key to a key server (required by Maven Central)
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 3. Configure Local Credentials

Add the following to your `local.properties` (never commit this file):

```properties
mavenCentralUsername=your-central-portal-user-token-username
mavenCentralPassword=your-central-portal-user-token-password
signingInMemoryKey=-----BEGIN PGP PRIVATE KEY BLOCK-----\n...\n-----END PGP PRIVATE KEY BLOCK-----
signingInMemoryKeyPassword=your-gpg-passphrase
```

> `signingInMemoryKey` is the full content of your exported `signing-key.asc`, with literal `\n` replacing each newline.

### 4. Configure GitHub Actions Secrets

For automated CI/CD publishing, add these four secrets to your GitHub repository
(**Settings → Secrets and variables → Actions**):

| Secret | Value |
|--------|-------|
| `MAVEN_CENTRAL_USERNAME` | Central Portal user token username |
| `MAVEN_CENTRAL_PASSWORD` | Central Portal user token password |
| `SIGNING_KEY` | Full GPG private key (ASCII armor, newlines as `\n`) |
| `SIGNING_KEY_PASSWORD` | GPG key passphrase |

---

## Publishing

### Version Management

Update `VERSION_NAME` in `gradle.properties` before each release:

```properties
VERSION_NAME=1.0.1
```

Follow [Semantic Versioning](https://semver.org/): `MAJOR.MINOR.PATCH`

### Option A: Using the Publishing Script

```bash
./publish.sh
```

The script builds a release, runs tests, checks binary API compatibility, and publishes all three artifacts atomically.

### Option B: Manual Gradle Commands

```bash
./gradlew \
  :composer:publishAndReleaseToMavenCentral \
  :composer-compose:publishAndReleaseToMavenCentral \
  :composer-bom:publishAndReleaseToMavenCentral \
  --no-configuration-cache
```

### Option C: Automated via GitHub Actions

Publishing is triggered automatically when you create a **GitHub Release**:

1. Bump `VERSION_NAME` in `gradle.properties` and commit
2. Go to **GitHub → Releases → Draft a new release**
3. Create a tag (e.g., `v1.0.1`) and publish the release
4. The `publish.yml` workflow runs: tests → `apiCheck` → publish all three artifacts

---

## Verification

After publishing, verify your artifacts:

1. Log in to [central.sonatype.com](https://central.sonatype.com) and check **Deployments**
2. Once released, search Maven Central: `https://central.sonatype.com/artifact/io.github.debdutsaha/composer`
3. Consumers can add the dependency immediately after the deployment is **Released** (not just uploaded)

---

## Troubleshooting

### Signing Failed
- Ensure `signingInMemoryKey` contains the full key including header/footer lines
- Check that newlines are escaped as `\n` in the properties value
- Verify `signingInMemoryKeyPassword` matches the key's passphrase

### Authentication Failed
- Regenerate a **User Token** from the Central Portal (not your account password)
- Confirm the token username/password are copied exactly — no leading/trailing spaces

### Namespace Not Verified
- The `io.github.debdutsaha` namespace must be verified on the Central Portal
- Verification requires a public GitHub repo named after the verification code provided

### Version Already Exists
- Maven Central does not allow overwriting published versions
- Bump `VERSION_NAME` and re-publish

### `apiCheck` Failed
Run `./gradlew :composer:apiDump :composer-compose:apiDump` to update the API files, review the diff, then commit the updated `.api` files before re-publishing.
