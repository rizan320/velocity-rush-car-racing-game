@echo off
setlocal enabledelayedexpansion
title Package Velocity Rush JAR
echo ========================================================
echo        PACKAGING VELOCITY RUSH INTO STANDALONE JAR
echo ========================================================
echo.

set "JAVAC_CMD="
set "JAR_CMD="

where javac >nul 2>nul
if %ERRORLEVEL% EQU 0 set "JAVAC_CMD=javac"
where jar >nul 2>nul
if %ERRORLEVEL% EQU 0 set "JAR_CMD=jar"

if not defined JAVAC_CMD (
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\javac.exe" (
            set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
            set "JAR_CMD=%JAVA_HOME%\bin\jar.exe"
        )
    )
)

if not defined JAVAC_CMD (
    for /d %%D in ("C:\Program Files\Java\jdk*" "C:\Program Files\Eclipse Adoptium\jdk*" "C:\Program Files\Microsoft\jdk*" "C:\Program Files\BellSoft\LibericaJDK*" "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk*") do (
        if exist "%%D\bin\javac.exe" (
            set "JAVAC_CMD=%%D\bin\javac.exe"
            set "JAR_CMD=%%D\bin\jar.exe"
            goto :FoundJava
        )
    )
)

:FoundJava
if not defined JAVAC_CMD (
    echo [ERROR] Java JDK was not found. Please install JDK or run from VS Code.
    pause
    exit /b 1
)

echo Java JDK detected at: "!JAVAC_CMD!"
echo.
if not exist "bin" mkdir bin
echo 1. Compiling all Java classes...
"!JAVAC_CMD!" -encoding UTF-8 -d bin src\com\velocityrush\*.java src\com\velocityrush\*\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo 2. Packaging into VelocityRush.jar...
"!JAR_CMD!" cfm VelocityRush.jar Manifest.txt -C bin com

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================================
    echo SUCCESS! VelocityRush.jar has been created!
    echo You can now double-click VelocityRush.jar to play anytime!
    echo ========================================================
) else (
    echo [ERROR] JAR packaging failed.
)
echo.
pause
