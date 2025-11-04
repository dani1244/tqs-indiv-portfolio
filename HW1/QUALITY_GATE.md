# Quality Gate Configuration

## Overview

This project implements a comprehensive Quality Gate system in the CI/CD pipeline to ensure code quality standards are maintained before merging changes.

## Quality Gate Components

### 1. JaCoCo Code Coverage

**Configuration**: `pom.xml`

The project uses JaCoCo Maven Plugin to measure code coverage with the following enforcement rules:

```xml
<limit>
    <counter>LINE</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.40</minimum>  <!-- 40% line coverage -->
</limit>
<limit>
    <counter>BRANCH</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.25</minimum>  <!-- 25% branch coverage -->
</limit>
```

**Excluded from Coverage:**
- DTOs (`**/dto/**`)
- Model/Entity classes (`**/model/**`)
- Configuration classes (`**/config/**`)
- Exception classes (`**/exception/**`)
- Main application class

### 2. GitHub Actions CI Workflow

**File**: `.github/workflows/ci.yml`

The CI workflow automatically runs on:
- Push to `master`, `main`, or `develop` branches
- Pull requests targeting these branches

**Workflow Steps:**
1. **Checkout Code** - Fetch repository code
2. **Setup JDK 17** - Configure Java environment
3. **Build** - Compile the project
4. **Run Unit Tests** - Execute all unit tests
5. **Run Integration Tests** - Execute integration tests
6. **Generate Coverage Report** - Create JaCoCo report
7. **Verify Coverage** - **QUALITY GATE** - Build fails if coverage is below threshold
8. **Upload Artifacts** - Store test and coverage reports
9. **Publish Test Results** - Display test results in PR
10. **Comment on PR** - Add coverage report comment to pull requests

### 3. Quality Gate Enforcement

The quality gate **BLOCKS** merging if:
- Any unit or integration tests fail
- Line coverage < 40%
- Branch coverage < 25%
- Build compilation fails

The quality gate **ALLOWS** merging if:
- All tests pass
- Line coverage ≥ 40%
- Branch coverage ≥ 25%
- Build succeeds

## Current Coverage Status

**Current Metrics** (as of last build):
- Line Coverage: **41%** 
- Branch Coverage: **26%** 

## How to Check Coverage Locally

### Run tests with coverage:
```bash
mvn clean test jacoco:report
```

### View coverage report:
Open `target/site/jacoco/index.html` in browser

### Verify quality gate:
```bash
mvn clean verify
```

This command will:
1. Run all tests (unit + integration)
2. Generate coverage report
3. Check coverage thresholds
4. **FAIL** if thresholds not met

## Production Recommendations

For production-grade projects, consider increasing thresholds:

```xml
<minimum>0.80</minimum>  <!-- 80% line coverage -->
<minimum>0.70</minimum>  <!-- 70% branch coverage -->
```

## Improving Coverage

To improve test coverage:

1. **Controllers**: Add tests for all endpoints
   - Currently missing: Employee endpoints, WorkList endpoints

2. **Services**: Test all business logic paths
   - Add tests for AuthService
   - Add tests for PermissionService
   - Add tests for WorkListService
   - Add tests for EmployeeService

3. **Edge Cases**: Test error scenarios
   - Invalid inputs
   - Boundary conditions
   - Exception handling

4. **Integration Tests**: Cover end-to-end flows
   - Authentication flow
   - Permission checking
   - WorkList assignment

## GitHub Actions Artifacts

After each CI run, the following artifacts are available:
- **coverage-reports** - JaCoCo HTML reports
- **surefire-reports** - Unit test results
- **failsafe-reports** - Integration test results

## Troubleshooting

### Build fails locally but passes in CI
- Ensure you're using JDK 17
- Run `mvn clean` before testing
- Check for cached test results

### Coverage drops unexpectedly
- New code added without tests
- Tests were removed
- Code refactored without updating tests

### Quality gate too strict
- Adjust thresholds in `pom.xml`
- Add more exclusions if needed
- Improve test coverage

## Benefits of Quality Gate

1. **Prevents Regressions** - Catches bugs before merge
2. **Enforces Testing** - Developers must write tests
3. **Maintains Quality** - Code quality doesn't degrade over time
4. **Visibility** - Team sees coverage trends
5. **Automated** - No manual intervention needed
6. **Fast Feedback** - Results in minutes

## Implementation Details

**Technology Stack:**
- Maven 3.9+
- JaCoCo 0.8.11
- GitHub Actions
- JUnit 5
- Spring Boot Test

**Report Format:**
- XML (for SonarQube integration)
- HTML (for human reading)
- CSV (for data analysis)
