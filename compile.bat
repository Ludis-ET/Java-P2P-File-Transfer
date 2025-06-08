@echo off
echo Compiling P2P File Transfer System...

REM Create directories if they don't exist
if not exist "build" mkdir build
if not exist "build\classes" mkdir build\classes

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

REM Compile Java files
javac -d build\classes -cp %CP% src\main\java\com\p2p\*.java src\main\java\com\p2p\model\*.java src\main\java\com\p2p\database\*.java src\main\java\com\p2p\gui\*.java src\main\java\com\p2p\network\*.java src\main\java\com\p2p\utils\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo You can now run the application using run.bat
) else (
    echo Compilation failed!
    echo Please check for errors above.
)

pause
