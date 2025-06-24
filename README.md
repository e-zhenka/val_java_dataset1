# Vulnerability Java Dataset - SVACE Analysis Guide

This project contains Java code snippets with known vulnerabilities for testing SVACE SAST analysis.

## Project Structure

- `testcases1/` - First set of vulnerability examples (1-999)
- `testcases2/` - Second set of vulnerability examples (1000-1098)
- `src/main/java/com/` - Main application code
- `pom.xml` - Maven build configuration

## Running with SVACE

### Prerequisites
- Java 8 or higher
- Maven 3.6+
- SVACE analyzer

### Build Steps

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd vul_java_dataset
   ```

2. **Build the project:**
   ```bash
   ./build.sh
   # or manually:
   mvn clean compile test
   ```

3. **Run SVACE analysis:**
   ```bash
   svace analyze --config svace-config.xml
   ```

### SVACE Analysis

SVACE will automatically detect all CWE categories present in the vulnerability examples, including:

- CWE-78: OS Command Injection
- CWE-89: SQL Injection
- CWE-611: XML External Entity (XXE)
- CWE-22: Path Traversal
- CWE-502: Deserialization of Untrusted Data
- CWE-327: Use of a Broken or Risky Cryptographic Algorithm
- And many more...

### Analysis Coverage

SVACE will analyze:
- Static code patterns during compilation
- Runtime behavior during test execution
- All vulnerability types automatically
- Comprehensive CWE detection

## Notes for SVACE Users

- The project uses Maven for dependency management
- All vulnerability examples are in the testcases directories
- SVACE automatically detects all CWE categories - no manual configuration needed
- Build artifacts are in `target/` directory
- The test suite executes all vulnerability examples for comprehensive analysis
