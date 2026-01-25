# Publishing Guide

This guide explains how to publish the Composer library to GitHub Packages.

## Prerequisites

1. A GitHub account with access to the repository
2. A GitHub Personal Access Token (PAT) with `write:packages` permission

### Creating a GitHub Personal Access Token

1. Go to [GitHub Settings > Developer settings > Personal access tokens > Tokens (classic)](https://github.com/settings/tokens)
2. Click "Generate new token (classic)"
3. Give it a descriptive name (e.g., "Composer Library Publishing")
4. Select the `write:packages` scope
5. Click "Generate token"
6. **Copy the token immediately** - you won't be able to see it again!

## Configuration

### Step 1: Set Up Authentication

Add your GitHub credentials to `local.properties` (this file is already in `.gitignore`):

```properties
GITHUB_USER=debdutsaha
GITHUB_TOKEN=your_personal_access_token_here
VERSION_NAME=1.0.0
```

**Important:** Never commit `local.properties` to version control!

### Alternative: Environment Variables

You can also set these as environment variables:

```bash
export GITHUB_USER=debdutsaha
export GITHUB_TOKEN=your_personal_access_token_here
export VERSION_NAME=1.0.0
```

## Publishing

### Step 1: Update Version

Before publishing, update the version in `local.properties`:

```properties
VERSION_NAME=1.0.1
```

### Step 2: Build and Publish

**Option A: Using the Publishing Script (Recommended)**

Simply run:

```bash
./publish.sh
```

The script will:
- Verify your configuration
- Build the release variant
- Publish to GitHub Packages

**Option B: Manual Gradle Command**

Run the following Gradle command from the project root:

```bash
./gradlew :composer:publishReleasePublicationToGitHubPackagesRepository
```

Or on Windows:

```bash
gradlew.bat :composer:publishReleasePublicationToGitHubPackagesRepository
```

### Step 3: Verify Publication

1. Go to your GitHub repository: `https://github.com/debdutsaha/composerlibrary`
2. Click on "Packages" (on the right side of the repository page)
3. You should see your published package with the version you specified

## Version Management

- Use [Semantic Versioning](https://semver.org/): `MAJOR.MINOR.PATCH`
- Examples:
  - `1.0.0` - Initial release
  - `1.0.1` - Bug fix
  - `1.1.0` - New features (backward compatible)
  - `2.0.0` - Breaking changes

## Troubleshooting

### Authentication Failed

- Verify your `GITHUB_TOKEN` has `write:packages` permission
- Check that `GITHUB_USER` matches your GitHub username
- Ensure credentials are set in `local.properties` or as environment variables

### Publication Failed

- Make sure you've built the release variant: `./gradlew :composer:assembleRelease`
- Check that the version number is unique (GitHub Packages doesn't allow overwriting existing versions)
- Verify your repository name matches: `debdutsaha/composerlibrary`

### Package Not Found After Publishing

- It may take a few minutes for the package to appear in GitHub
- Check the "Packages" section of your repository
- Verify the package URL: `https://github.com/debdutsaha/composerlibrary/packages`

## CI/CD Integration

For automated publishing, you can use GitHub Actions. Create `.github/workflows/publish.yml`:

```yaml
name: Publish Package

on:
  release:
    types: [created]

jobs:
  publish:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      - name: Publish package
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          GITHUB_USER: debdutsaha
          VERSION_NAME: ${{ github.event.release.tag_name }}
        run: ./gradlew :composer:publishReleasePublicationToGitHubPackagesRepository
```

This will automatically publish when you create a new GitHub release.
