@echo off
cd /d "%~dp0"

if not exist "minecraft\plugins" mkdir "minecraft\plugins"

echo Clearing plugins folder...
del /Q "minecraft\plugins\*.jar" 2>nul

echo Copying CompactFarms...
for /f "delims=" %%f in ('dir /b CompactFarms\build\libs\CompactFarms-*.jar 2^>nul') do (
    copy /Y "CompactFarms\build\libs\%%f" "minecraft\plugins\" >nul
    echo   %%f
)

echo Copying ContainerSearch...
for /f "delims=" %%f in ('dir /b ContainerSearch\build\libs\ContainerSearch-*.jar 2^>nul') do (
    copy /Y "ContainerSearch\build\libs\%%f" "minecraft\plugins\" >nul
    echo   %%f
)

echo Copying PositionHUD...
for /f "delims=" %%f in ('dir /b PositionHUD\build\libs\PositionHUD-*.jar 2^>nul') do (
    copy /Y "PositionHUD\build\libs\%%f" "minecraft\plugins\" >nul
    echo   %%f
)

echo Copying QualityofLifeRecipes...
for /f "delims=" %%f in ('dir /b QualityofLifeRecipes\build\libs\QualityofLifeRecipes-*.jar 2^>nul') do (
    copy /Y "QualityofLifeRecipes\build\libs\%%f" "minecraft\plugins\" >nul
    echo   %%f
)

echo Copying SimpleBackup...
for /f "delims=" %%f in ('dir /b SimpleBackup\build\libs\SimpleBackup-*.jar 2^>nul') do (
    copy /Y "SimpleBackup\build\libs\%%f" "minecraft\plugins\" >nul
    echo   %%f
)

echo.
echo Done!
pause
