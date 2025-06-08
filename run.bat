@echo off
echo Starting P2P File Transfer System...

REM Set classpath - find MySQL connector JAR
for %%f in (lib\mysql-connector-j-*.jar) do (
    if not "%%~nf" == "*javadoc*" (
        set CP=%%f;build\classes
        goto found
    )
)
echo Error: MySQL Connector/J JAR not found in lib\ directory
echo Please download the MySQL Connector/J driver (NOT javadoc) from:
echo https://dev.mysql.com/downloads/connector/j/
pause
exit /b 1
:found

REM Run the application
java -cp %CP% com.p2p.Main

pause
