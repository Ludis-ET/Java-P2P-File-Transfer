#!/bin/bash
echo "Compiling P2P File Transfer System..."

# Create directories if they don't exist
mkdir -p build/classes

# Set classpath - find the MySQL connector JAR automatically
MYSQL_JAR=$(find lib/ -name "mysql-connector-j-*.jar" | grep -v javadoc | head -n 1)
if [ -z "$MYSQL_JAR" ]; then
    echo "Error: MySQL Connector/J JAR not found in lib/ directory"
    echo "Please download the MySQL Connector/J driver (NOT javadoc) from:"
    echo "https://dev.mysql.com/downloads/connector/j/"
    exit 1
fi
CP="$MYSQL_JAR:build/classes"

# Compile Java files
javac -d build/classes -cp "$CP" \
    src/main/java/com/p2p/*.java \
    src/main/java/com/p2p/model/*.java \
    src/main/java/com/p2p/database/*.java \
    src/main/java/com/p2p/gui/*.java \
    src/main/java/com/p2p/network/*.java \
    src/main/java/com/p2p/utils/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "You can now run the application using ./run.sh"
    chmod +x run.sh
else
    echo "Compilation failed!"
    echo "Please check for errors above."
fi
