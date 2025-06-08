#!/bin/bash
echo "Starting P2P File Transfer System..."

# Set classpath - find the MySQL connector JAR automatically
MYSQL_JAR=$(find lib/ -name "mysql-connector-j-*.jar" | grep -v javadoc | head -n 1)
if [ -z "$MYSQL_JAR" ]; then
    echo "Error: MySQL Connector/J JAR not found in lib/ directory"
    echo "Please download the MySQL Connector/J driver (NOT javadoc) from:"
    echo "https://dev.mysql.com/downloads/connector/j/"
    exit 1
fi
CP="$MYSQL_JAR:build/classes"

# Run the application
java -cp "$CP" com.p2p.Main
