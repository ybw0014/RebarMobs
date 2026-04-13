#!/bin/bash
# test-next-version.sh - Test harness for next-version.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCRIPT="${SCRIPT_DIR}/next-version.sh"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counters
TESTS_RUN=0
TESTS_PASSED=0
TESTS_FAILED=0

# Create a temporary git repository for testing
setup_temp_repo() {
    local repo_dir
    repo_dir=$(mktemp -d)
    (
        cd "$repo_dir"
        git init --quiet
        git config user.name "Test User"
        git config user.email "test@example.com"
        git config commit.gpgsign false
        echo "initial" > file.txt
        git add file.txt
        git commit --quiet -m "chore: initial commit"
    )
    echo "$repo_dir"
}

# Clean up temporary repository
cleanup_temp_repo() {
    local repo_dir="$1"
    rm -rf "$repo_dir"
}

# Run a test case
run_test() {
    local test_name="$1"
    local expected_version="$2"
    local expected_release="$3"
    shift 3
    local commits=("$@")

    TESTS_RUN=$((TESTS_RUN + 1))

    local repo_dir
    repo_dir=$(setup_temp_repo)
    cd "$repo_dir"

    # Create commits
    for commit_msg in "${commits[@]}"; do
        echo "$commit_msg" >> file.txt
        git add file.txt
        git commit --quiet -m "$commit_msg" || true
    done

    # Run the script
    local output
    local version
    local should_release

    output=$($SCRIPT 2>/dev/null) || true
    version=$(echo "$output" | grep "^version=" | cut -d= -f2)
    should_release=$(echo "$output" | grep "^should_release=" | cut -d= -f2)

    cd - > /dev/null
    cleanup_temp_repo "$repo_dir"

    # Check results
    local pass=true
    if [ "$version" != "$expected_version" ]; then
        echo -e "${RED}✗ $test_name${NC}: expected version '$expected_version', got '$version'"
        pass=false
    fi

    if [ "$should_release" != "$expected_release" ]; then
        echo -e "${RED}✗ $test_name${NC}: expected should_release='$expected_release', got '$should_release'"
        pass=false
    fi

    if [ "$pass" = true ]; then
        echo -e "${GREEN}✓ $test_name${NC}"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
}

# Run tests with existing tag
run_test_with_tag() {
    local test_name="$1"
    local tag="$2"
    local expected_version="$3"
    local expected_release="$4"
    shift 4
    local commits=("$@")

    TESTS_RUN=$((TESTS_RUN + 1))

    local repo_dir
    repo_dir=$(setup_temp_repo)
    cd "$repo_dir"

    # Create initial commits and tag
    echo "before tag" >> file.txt
    git add file.txt
    git commit --quiet -m "chore: before tag"
    git tag "$tag"

    # Create commits after tag
    for commit_msg in "${commits[@]}"; do
        echo "$commit_msg" >> file.txt
        git add file.txt
        git commit --quiet -m "$commit_msg" || true
    done

    # Run the script
    local output
    local version
    local should_release

    output=$($SCRIPT 2>/dev/null) || true
    version=$(echo "$output" | grep "^version=" | cut -d= -f2)
    should_release=$(echo "$output" | grep "^should_release=" | cut -d= -f2)

    cd - > /dev/null
    cleanup_temp_repo "$repo_dir"

    # Check results
    local pass=true
    if [ "$version" != "$expected_version" ]; then
        echo -e "${RED}✗ $test_name${NC}: expected version '$expected_version', got '$version'"
        pass=false
    fi

    if [ "$should_release" != "$expected_release" ]; then
        echo -e "${RED}✗ $test_name${NC}: expected should_release='$expected_release', got '$should_release'"
        pass=false
    fi

    if [ "$pass" = true ]; then
        echo -e "${GREEN}✓ $test_name${NC}"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
}

echo "Running next-version.sh tests..."
echo "================================"

# Test 1: No tags + fix commit -> 0.0.1
run_test "No tags + fix:" "0.0.1" "true" "fix: correct typo"

# Test 2: No tags + feat commit -> 0.1.0
run_test "No tags + feat:" "0.1.0" "true" "feat: add new feature"

# Test 3: No tags + breaking change -> 1.0.0
run_test "No tags + breaking change" "1.0.0" "true" "feat!: breaking change"

# Test 4: No tags + chore only -> no release
run_test "No tags + only chore" "0.0.0" "false" "chore: cleanup"

# Test 5: No tags + scoped feat
run_test "No tags + scoped feat" "0.1.0" "true" "feat(parser): add ability"

# Test 6: No tags + scoped fix
run_test "No tags + scoped fix" "0.0.1" "true" "fix(parser): correct error"

# Test 7: No tags + breaking in body
run_test "No tags + BREAKING CHANGE in body" "1.0.0" "true" "feat: something

BREAKING CHANGE: api changed"

# Test 8: With existing tag v1.2.3 + fix -> 1.2.4
run_test_with_tag "v1.2.3 + fix:" "v1.2.3" "1.2.4" "true" "fix: bugfix"

# Test 9: With existing tag v1.2.3 + feat -> 1.3.0
run_test_with_tag "v1.2.3 + feat:" "v1.2.3" "1.3.0" "true" "feat: new feature"

# Test 10: With existing tag v1.2.3 + breaking -> 2.0.0
run_test_with_tag "v1.2.3 + breaking" "v1.2.3" "2.0.0" "true" "feat!: breaking"

# Test 11: With existing tag + only chore/docs -> no release
run_test_with_tag "v1.2.3 + only chore" "v1.2.3" "1.2.3" "false" "chore: cleanup" "docs: update readme"

# Test 12: With existing tag + perf -> patch
run_test_with_tag "v1.2.3 + perf:" "v1.2.3" "1.2.4" "true" "perf: improve speed"

# Test 13: Breaking change with scope
run_test_with_tag "v1.2.3 + scoped breaking" "v1.2.3" "2.0.0" "true" "feat(api)!: remove endpoint"

echo "================================"
echo -e "Results: ${TESTS_PASSED}/${TESTS_RUN} tests passed"

if [ $TESTS_FAILED -gt 0 ]; then
    echo -e "${RED}${TESTS_FAILED} test(s) failed${NC}"
    exit 1
else
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
fi
