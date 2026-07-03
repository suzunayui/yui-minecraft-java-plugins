@echo off
cd /d "%~dp0"

if not exist "minecraft\plugins" mkdir "minecraft\plugins"

echo Clearing plugins folder...
del /Q "minecraft\plugins\*.jar" 2>nul

echo Copying plugins...
for /d %%d in (*) do (
    if exist "%%d\build\libs" (
        for /f "delims=" %%f in ('dir /b "%%d\build\libs\*.jar" 2^>nul') do (
            copy /Y "%%d\build\libs\%%f" "minecraft\plugins\" >nul
            echo   %%f
        )
    )
)

echo.
echo Done!
pause
