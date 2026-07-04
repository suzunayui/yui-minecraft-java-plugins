@echo off
cd /d "%~dp0"

if not exist "minecraft\plugins" mkdir "minecraft\plugins"

echo Clearing plugins folder...
del /Q "minecraft\plugins\*.jar" 2>nul

echo Copying plugins...
for /d %%d in (*) do (
    if exist "%%d\build\libs" (
        set "found="
        for /f "delims=" %%f in ('dir /b /o-d "%%d\build\libs\*.jar" 2^>nul') do (
            if not defined found (
                copy /Y "%%d\build\libs\%%f" "minecraft\plugins\" >nul
                echo   %%f
                set "found=1"
            )
        )
    )
)

if exist "plugins" (
    echo Copying additional plugins...
    for /f "delims=" %%f in ('dir /b "plugins\*.jar" 2^>nul') do (
        copy /Y "plugins\%%f" "minecraft\plugins\" >nul
        echo   %%f
    )
)

echo.
echo Done!
pause
