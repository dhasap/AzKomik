# GitHub Actions Workflows

This directory contains all GitHub Actions workflows for the AzKomik project.

## 📋 Workflow Overview

### 🔄 Continuous Integration

#### `android-test.yml`
- **Triggers**: PR to main/develop, push to main/develop
- **Jobs**:
  - Code Quality (Ktlint, Detekt)
  - Unit Tests (Debug & Release)
  - Android Lint
  - Build Verification
  - Dependency Check
- **Status**: Required for PR merge

#### `android-debug-build.yml`
- **Triggers**: PR to main/develop, push to main/develop
- **Jobs**:
  - Lint & Static Analysis
  - Unit Tests
  - Build Debug APK
  - Create Debug Release (main branch only)
- **Artifacts**: Debug APK with version info
- **Retention**: 30 days

### 🚀 Release Workflows

#### `android-release-build.yml`
- **Triggers**: Tag push (v*), manual dispatch
- **Requirements**: All secrets must be configured
- **Jobs**:
  - Validate Release (secrets, version tag)
  - Quality Gate (tests, lint)
  - Build Release APK & AAB
  - Verify APK signature
  - Generate checksums
  - Create GitHub Release with changelog
- **Artifacts**: Signed APK, AAB, checksums
- **Retention**: 90 days

### 🌙 Nightly Builds

#### `android-nightly.yml`
- **Triggers**: Daily at 2 AM UTC, manual dispatch
- **Branch**: develop
- **Jobs**:
  - Fast build & test
  - Create/Update nightly release
- **Release**: Tag `nightly` (prerelease)
- **Retention**: 7 days

### 📝 Pull Request Features

#### `pr-preview.yml`
- **Triggers**: PR open/sync to main/develop
- **Features**:
  - Build PR preview APK
  - Auto-comment on PR with download link
  - APK size analysis
  - Add `preview-ready` label

### 📦 Dependency Management

#### `dependency-updates.yml`
- **Triggers**: Weekly (Monday 6 AM), manual
- **Jobs**:
  - Check for dependency updates
  - Create PR with updates
  - Security audit

#### `dependabot.yml`
- **Schedule**: Weekly
- **Scope**: Gradle dependencies, GitHub Actions
- **Limit**: 10 open PRs

## 🔐 Required Secrets

For release builds, configure these secrets in Repository Settings:

| Secret | Description | How to Generate |
|--------|-------------|-----------------|
| `RELEASE_KEYSTORE` | Base64 encoded keystore | `base64 -i release.keystore \| tr -d '\n'` |
| `RELEASE_KEYSTORE_PASSWORD` | Keystore password | - |
| `RELEASE_KEY_ALIAS` | Key alias | - |
| `RELEASE_KEY_PASSWORD` | Key password | - |
| `GITHUB_TOKEN` | Auto-generated | Already available |

### Setting up Secrets

1. Go to Repository Settings → Secrets and Variables → Actions
2. Click "New repository secret"
3. Add each secret from the table above

## 🏷️ Version Tagging

### Debug Builds
- Format: `debug-v{version}-{build}`
- Example: `debug-v2.0.100-45`
- Auto-generated on every push to main

### Release Builds
- Format: `v{major}.{minor}.{patch}[-{prerelease}]`
- Examples:
  - `v1.0.0` - Stable release
  - `v1.0.0-beta` - Beta prerelease
  - `v1.0.0-rc1` - Release candidate

### Nightly Builds
- Format: `2.0.{date}-nightly-{short_sha}`
- Example: `2.0.20240226-nightly-a1b2c3d`
- Tag: `nightly` (always updated)

## 📊 Workflow Status Badges

Add to README.md:

```markdown
![CI](https://github.com/USERNAME/azkomik-v2/actions/workflows/android-test.yml/badge.svg)
![Build](https://github.com/USERNAME/azkomik-v2/actions/workflows/android-debug-build.yml/badge.svg)
![Release](https://github.com/USERNAME/azkomik-v2/actions/workflows/android-release-build.yml/badge.svg)
```

## 🚀 Quick Reference

### Manual Build Triggers

1. **Debug Build**: Go to Actions → Android Debug Build → Run workflow
2. **Release Build**: Push a tag `v*` or run workflow manually
3. **Nightly Build**: Runs automatically or trigger manually

### Build Matrix

| Type | Signing | Tests | Lint | Release |
|------|---------|-------|------|---------|
| Debug | No | Yes | Yes | Auto (main) |
| Release | Yes | Yes | Yes | Manual |
| Nightly | No | Yes | No | Yes (nightly tag) |
| PR | No | Yes | Yes | No |

## 🛠️ Troubleshooting

### Common Issues

1. **Build fails with "Keystore not found"**
   - Check that all `RELEASE_*` secrets are configured
   - Verify keystore is base64 encoded correctly

2. **Out of disk space**
   - Reduce artifact retention days
   - Add `clean` step before build

3. **Gradle daemon issues**
   - Use `--no-daemon` flag (already configured)
   - Adjust `GRADLE_OPTS` memory settings

4. **Cache misses**
   - Check `cache-read-only` setting for PRs
   - Verify `key` format in cache step

## 📝 Notes

- All workflows use Ubuntu Latest runner
- JDK 17 (Temurin distribution) is used
- Android SDK 34 is pre-installed
- Gradle wrapper is used for consistency
- Caching is enabled for faster builds
