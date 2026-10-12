@echo off
setlocal enabledelayedexpansion
title Velocity Rush - Java Racing Game
echo ========================================================
echo        VELOCITY RUSH - JAVA ARCADE RACING GAME
echo ========================================================
echo.

:: 1. Search for javac and java
set "JAVAC_CMD="
set "JAVA_CMD="

:: Check PATH first
where javac >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    set "JAVAC_CMD=javac"
)
where java >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    set "JAVA_CMD=java"
)

:: If not found in PATH, check JAVA_HOME
if not defined JAVAC_CMD (
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\javac.exe" (
            set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
            set "JAVA_CMD=%JAVA_HOME%\bin\javaw.exe"
        )
    )
)

:: If still not found, scan common Windows JDK installation directories
if not defined JAVAC_CMD (
    for /d %%D in ("C:\Program Files\Java\jdk*" "C:\Program Files\Eclipse Adoptium\jdk*" "C:\Program Files\Microsoft\jdk*" "C:\Program Files\BellSoft\LibericaJDK*" "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk*") do (
        if exist "%%D\bin\javac.exe" (
            set "JAVAC_CMD=%%D\bin\javac.exe"
            set "JAVA_CMD=%%D\bin\javaw.exe"
            goto :FoundJava
        )
    )
)

:FoundJava
if not defined JAVAC_CMD (
    echo [ERROR] Java JDK compiler (javac) was not found in standard paths.
    echo.
    echo To fix this:
    echo 1. In VS Code, you can run the game directly by opening Main.java and clicking 'Run'.
    echo 2. Or install a JDK from: https://adoptium.net (free OpenJDK) and select 'Add to PATH'.
    echo.
    pause
    exit /b 1
)

echo Java JDK detected at: "!JAVAC_CMD!"
echo.
echo Compiling game source files...
if not exist "bin" mkdir bin
"!JAVAC_CMD!" -encoding UTF-8 -d bin src\com\velocityrush\*.java src\com\velocityrush\*\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Starting Velocity Rush...
start "" "!JAVA_CMD!" -cp bin com.velocityrush.Main
exit
