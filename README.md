# Civ-Craft

Civ-Craft is a lightweight Paper 26.2 plugin for living villages, towns, factions, diplomacy, and player-participating wars.

## Current foundation

- Paper 26.2 plugin scaffold using Java 25
- Persistent village storage in `plugins/CivCraft/villages.yml`
- Small starting villages with a 3x3 chunk claim footprint
- Village creation, listing, adoption, and nearest-village information commands
- Founding Stone item that creates a village when placed
- Basic claim protection for village land
- Lightweight population growth up to housing capacity
- Independent village affiliation state
- GitHub Actions build workflow

## Commands

```text
/civcraft village create <name>
/civcraft village give <player>
/civcraft village adopt <name>
/civcraft village info
/civcraft village list
/civcraft reload
```

## Build

Use Java 25 and run:

```powershell
.\gradlew.bat build
```

The plugin jar is created in `build/libs/`.
