# EasyEntityRide

## Overview
**EasyEntityRide** is a client-side Minecraft Fabric mod for version 1.21. It allows players in Creative mode to easily pick up any entity and mount it onto a minecart.

This mod is useful for map making, testing, or just having fun moving mobs around without pushing them into minecarts manually.

## Features
- **Easy Selection**: Point at an entity and press `R` (Default) to select it.
- **Easy Mounting**: Point at a minecart and press `R` again to mount the selected entity.
- **Visual Feedback**: The selected entity emits particles (configurable) so you know what you're holding. No glowing effect is used to avoid NBT data pollution.
- **Smart Remount**: If you select an entity that is already riding something, the mod will automatically dismount it before placing it in the new minecart.
- **Clean Chat**: Suppresses unnecessary vanilla chat messages (e.g., "Dismounted entity") and provides clear, color-coded feedback.

## Usage
1. **Select an Entity**:
    - Aim at a mob or animal.
    - Press the `R` key.
    - Particles will appear above the entity to indicate selection.
2. **Mount to Minecart**:
    - Aim at a Minecart.
    - Press the `R` key again.
    - The entity will be teleported into the minecart.
3. **Deselect**:
    - Press `R` while looking at the selected entity again, OR
    - Sneak + `R` while looking at empty space (air) to clear selection.

## Configuration
You can change the selection particle effect in the config menu.
- **Open Config**: Install [Mod Menu](https://modrinth.com/mod/modmenu) and click the gear icon for EasyEntityRide.
- **Options**: Type a valid particle ID (e.g., `heart`, `flame`, `happy_villager`) in the text box.

## Installation
1. Install [Fabric Loader](https://fabricmc.net/).
2. Download `easy-entity-ride-mc1.21-x.x.x.jar`.
3. Place the .jar file in your `.minecraft/mods` folder.
4. Ensure `fabric-api` is also installed.

## License
MIT License
