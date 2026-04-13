#!/bin/bash
# next-version.sh - Calculate next semantic version from conventional commits
# Outputs GITHUB_OUTPUT compatible format

set -euo pipefail

# Configuration
DEFAULT_VERSION="0.0.0"
TAG_PREFIX="v"

# Find the latest semver tag
get_latest_tag() {
    # List tags matching v*.*.* pattern, sorted by version
    git tag --list "${TAG_PREFIX}*.*.*" --sort=-version:refname 2>/dev/null | head -n 1 || true
}

# Parse version from tag (e.g., v1.2.3 -> 1.2.3)
parse_version_from_tag() {
    local tag="$1"
    echo "${tag#${TAG_PREFIX}}"
}

# Get commits since the given tag (or all commits if no tag)
# Outputs full commit messages (subject + body) for breaking change detection
get_commits_since_tag() {
    local tag="$1"
    if [ -z "$tag" ]; then
        git log --format="%B" HEAD
    else
        git log --format="%B" "${tag}..HEAD"
    fi
}

# Analyze commits and determine bump level
analyze_bump_level() {
    local commits="$1"
    local has_breaking=false
    local has_feat=false
    local has_fix=false

    while IFS= read -r commit; do
        [ -z "$commit" ] && continue

        # Check for breaking changes
        if echo "$commit" | grep -qE "^[a-z]+(\(.+\))?!:" || \
           echo "$commit" | grep -qiE "BREAKING CHANGE:"; then
            has_breaking=true
        fi

        # Check for feat
        if echo "$commit" | grep -qE "^feat(\(.+\))?:"; then
            has_feat=true
        fi

        # Check for fix or perf
        if echo "$commit" | grep -qE "^(fix|perf)(\(.+\))?:"; then
            has_fix=true
        fi
    done <<< "$commits"

    # Determine bump level
    if [ "$has_breaking" = true ]; then
        echo "major"
    elif [ "$has_feat" = true ]; then
        echo "minor"
    elif [ "$has_fix" = true ]; then
        echo "patch"
    else
        echo "none"
    fi
}

# Increment version based on bump level
increment_version() {
    local version="$1"
    local bump="$2"

    # Parse version components
    local major minor patch
    major=$(echo "$version" | cut -d. -f1)
    minor=$(echo "$version" | cut -d. -f2)
    patch=$(echo "$version" | cut -d. -f3)

    case "$bump" in
        major)
            major=$((major + 1))
            minor=0
            patch=0
            ;;
        minor)
            minor=$((minor + 1))
            patch=0
            ;;
        patch)
            patch=$((patch + 1))
            ;;
        *)
            # No bump
            ;;
    esac

    echo "${major}.${minor}.${patch}"
}

# Main logic
main() {
    # Get the latest tag
    local latest_tag
    latest_tag=$(get_latest_tag)

    # Determine base version
    local base_version
    if [ -z "$latest_tag" ]; then
        base_version="$DEFAULT_VERSION"
    else
        base_version=$(parse_version_from_tag "$latest_tag")
    fi

    # Get commits since latest tag
    local commits
    commits=$(get_commits_since_tag "$latest_tag")

    # Analyze bump level
    local bump_level
    bump_level=$(analyze_bump_level "$commits")

    # Calculate next version
    local next_version
    next_version=$(increment_version "$base_version" "$bump_level")

    # Determine if we should release
    local should_release="false"
    if [ "$bump_level" != "none" ]; then
        should_release="true"
    fi

    # Handle the case where no new commits exist after tag
    if [ -n "$latest_tag" ] && [ "$next_version" = "$base_version" ] && [ "$should_release" = "false" ]; then
        # Check if there are any commits at all since tag
        local commit_count
        commit_count=$(git rev-list --count "${latest_tag}..HEAD" 2>/dev/null || echo "0")
        if [ "$commit_count" -eq 0 ]; then
            echo "No new commits since ${latest_tag}" >&2
            should_release="false"
        fi
    fi

    # Output results
    local tag_name="${TAG_PREFIX}${next_version}"

    # GitHub Actions output format
    if [ -n "${GITHUB_OUTPUT:-}" ]; then
        echo "version=${next_version}" >> "$GITHUB_OUTPUT"
        echo "tag=${tag_name}" >> "$GITHUB_OUTPUT"
        echo "should_release=${should_release}" >> "$GITHUB_OUTPUT"
        echo "previous_tag=${latest_tag:-}" >> "$GITHUB_OUTPUT"
        echo "bump_level=${bump_level}" >> "$GITHUB_OUTPUT"
    fi

    # Also print to stdout for debugging
    echo "version=${next_version}"
    echo "tag=${tag_name}"
    echo "should_release=${should_release}"
    echo "previous_tag=${latest_tag:-}"
    echo "bump_level=${bump_level}"
}

main "$@"
