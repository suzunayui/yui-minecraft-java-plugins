@echo off
cd /d "%~dp0"

call gradlew.bat build
if %errorlevel% neq 0 (
    pause
    exit /b 1
)

echo.
echo Build successful!

for %%f in (build\libs\SimpleBackup-*.jar) do (
    set "JAR_FILE=%%f"
    set "JAR_NAME=%%~nxf"
)

echo Output: %JAR_FILE%

if not exist "..\minecraft\plugins" mkdir "..\minecraft\plugins"
copy /Y "%JAR_FILE%" "..\minecraft\plugins\"
if %errorlevel% neq 0 (
    echo Failed to copy jar file.
    pause
    exit /b 1
)

echo.
echo Copied to minecraft\plugins\%JAR_NAME%
pause
