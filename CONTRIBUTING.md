# Contributing to Composer Library

First off, thank you for considering contributing to Composer Library! It's people like you that make Composer Library such a great tool.

## Code of Conduct

This project adheres to a Code of Conduct that all contributors are expected to follow. Please be respectful and constructive in all interactions.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the issue list as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

- **Clear and descriptive title**
- **Steps to reproduce the issue**
- **Expected behavior**
- **Actual behavior**
- **Screenshots** (if applicable)
- **Environment details** (Android version, library version, etc.)
- **Code samples** that demonstrate the issue

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

- **Clear and descriptive title**
- **Detailed description of the proposed enhancement**
- **Use case**: Why is this enhancement useful?
- **Possible implementation** (if you have ideas)

### Pull Requests

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Make your changes
4. Add tests if applicable
5. Ensure all tests pass
6. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
7. Push to the branch (`git push origin feature/AmazingFeature`)
8. Open a Pull Request

#### Pull Request Guidelines

- **Keep it focused**: One feature or bug fix per PR
- **Write clear commit messages**: Follow conventional commits format
- **Add tests**: New features should include tests
- **Update documentation**: If you're adding features, update the README
- **Follow code style**: Match the existing code style in the project

## Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/12345debdut/composer.git
   cd composer
   ```

2. Open the project in Android Studio

3. Make your changes

4. Run tests:
   ```bash
   ./gradlew test
   ```

5. Build the library:
   ```bash
   ./gradlew :composer:assembleRelease
   ```

## Coding Standards

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions focused and small
- Write unit tests for new features

## Commit Message Format

We follow a loose form of [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

Example:
```
feat(store): add support for batch state updates

This allows multiple state updates to be applied atomically,
improving performance for complex state transitions.
```

## Questions?

If you have questions, feel free to:
- Open an issue with the `question` label
- Contact the maintainer: debdut.saha.1@gmail.com

Thank you for contributing! 🎉
