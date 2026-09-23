# Civ-Craft

Civ-Craft is a lightweight Paper 26.2 plugin for living villages, towns, factions, diplomacy, and player-participating wars.

## Current foundation

- Paper 26.2 plugin scaffold using Java 25
- Persistent village storage in `plugins/CivCraft/villages.yml`
- Small starting villages with a 3x3 chunk claim footprint
- Village creation, listing, and nearest-village information commands
- GitHub Actions build workflow

## Commands

```text
/civcraft village create <name>
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
