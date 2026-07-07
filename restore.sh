#!/bin/bash

SERVER_DIR="/home/ubuntu/minecraft"
BACKUP_ZIP="$SERVER_DIR/plugins/SimpleBackup/backups/2026-07-07_13-48.zip"
TEMP_DIR="/tmp/mc-restore-$$"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}=== Minecraft World Restore ===${NC}"

if [ ! -f "$BACKUP_ZIP" ]; then
    echo -e "${RED}Error: Backup file not found: $BACKUP_ZIP${NC}"
    exit 1
fi

mkdir -p "$TEMP_DIR"

echo -e "${YELLOW}[1/2] Extracting backup...${NC}"
if command -v unzip &>/dev/null; then
    unzip -q "$BACKUP_ZIP" -d "$TEMP_DIR"
elif command -v jar &>/dev/null; then
    cd "$TEMP_DIR" && jar xf "$BACKUP_ZIP" && cd - > /dev/null
else
    python3 -c "
import zipfile, sys
with zipfile.ZipFile('$BACKUP_ZIP', 'r') as z:
    z.extractall('$TEMP_DIR')
"
fi

echo -e "${YELLOW}[2/2] Overwriting worlds...${NC}"
BACKUP_ROOT="$TEMP_DIR/2026-07-07_13-48"

# Paper 1.21: world/dimensions/minecraft/overworld, world/dimensions/the_nether, world/dimensions/the_end
rm -rf "$SERVER_DIR/world"
mkdir -p "$SERVER_DIR/world/dimensions/minecraft"

cp -r "$BACKUP_ROOT/overworld" "$SERVER_DIR/world/dimensions/minecraft/overworld"
echo -e "  ${GREEN}Restored: overworld -> world/dimensions/minecraft/overworld/${NC}"

cp -r "$BACKUP_ROOT/the_nether" "$SERVER_DIR/world/dimensions/the_nether"
echo -e "  ${GREEN}Restored: the_nether -> world/dimensions/the_nether/${NC}"

cp -r "$BACKUP_ROOT/the_end" "$SERVER_DIR/world/dimensions/the_end"
echo -e "  ${GREEN}Restored: the_end -> world/dimensions/the_end/${NC}"

rm -rf "$TEMP_DIR"

echo -e "${GREEN}=== Done! ===${NC}"
