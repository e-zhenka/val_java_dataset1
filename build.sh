#!/bin/bash

# Build script for SVACE analysis
echo "Building vulnerability dataset for SVACE analysis..."

# Clean previous builds
mvn clean

# Compile the project
echo "Compiling Java sources..."
mvn compile

# Run tests (this will execute vulnerability examples)
echo "Running vulnerability tests..."
mvn test

# Create executable jar
echo "Creating executable jar..."
mvn package

echo "Build completed successfully!"
echo "SVACE can now analyze the compiled classes and test execution."
echo "SVACE will automatically detect all CWE categories in the vulnerability examples." 