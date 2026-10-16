# 深渊回响：终焉之镜 / Echoes of the Abyss: The Final Mirror

A Minecraft **Forge 1.20.1** mod (`mirror_abyss`) — mine mirror shards, enter the
**Mirror Abyss** dimension, survive its monsters, solve light puzzles, defeat the
**Mirror Eater** boss, and choose between sealing the void or fusing reality.

> Skeleton status: this repository is a complete **code / resource / texture
> skeleton** — every block, item, entity, dimension, advancement and recipe is
> registered and data-driven, with placeholder procedural 16x16 textures.
> Gameplay tuning, entity renderers/models, boss arena layout and structure
> templates still need final implementation (marked `TODO` in code).

---

## Tech Stack

| Item | Value |
| --- | --- |
| Minecraft | 1.20.1 |
| Loader | Forge `47.3.0` |
| Java | 17 |
| Mod ID | `mirror_abyss` |
| Mappings | official (Mojang) |
| Group | `com.example.mirror_abyss` |

## Features

- **New dimension `mirror_abyss`** — permanent twilight (fixed_time 12000),
  purple-gray sky, custom `NoiseBasedChunkGenerator` + custom `BiomeSource`,
  3 biomes (mirror_plains / cracked_badlands / void_ruins).
- **10 blocks** — cracked_stone, mirror_shard_block (reflection puzzle),
  void_stabilizer (block entity), watcher_altar (void_eye teleport),
  final_mirror (ending GUI), mirror_portal_frame (4x5 portal), void_floor
  (void damage), watcher_brick, reflective_lamp (light beam, light 12),
  abyss_ore (drops mirror_shard, deep overworld via Forge biome modifier).
- **4 mobs** — MirageWalker (neutral while you hold a shard and stand still),
  VoidDevourer (void_bolt + void_erosion effect), CrackSpider (climbs, spawns
  cracks), MirrorEater (3-phase boss, projectile reflect, teleport, interruptible
  "void annihilation" charge).
- **7 advancements** — data-driven JSONs + 2 custom criteria triggers
  (`light_refraction`, `final_decision`).
- **3 structures** — watchers_ruin / mirror_hallway / void_sanctum
  (procedural placeholder pieces, swap to Jigsaw + .nbt for final builds).
- **Bilingual** `zh_cn.json` + `en_us.json`, full loot tables + recipes.

## Project Layout

```
src/main/java/com/example/mirror_abyss/
  MirrorAbyss.java            # @Mod entry, all DeferredRegisters
  ModCreativeTabs.java
  block/   ModBlocks.java + 7 functional block classes
  item/    ModItems.java
  entity/  ModEntities.java, VoidBolt.java,
           Monster/{MirageWalker,VoidDevourer,CrackSpider}.java,
           boss/MirrorEater.java
  blockentity/ ModBlockEntities.java, VoidStabilizerBE.java, FinalMirrorBE.java
  gui/     ModMenuTypes.java, FinalMirrorMenu.java, FinalMirrorScreen.java
  effect/  ModEffects.java, VoidErosionEffect.java
  advancement/ ModAdvancements.java
  world/   ModDimensions.java, ModBiomes.java, ModStructures.java,
           MirrorAbyssBiomeSource.java, MirrorAbyssChunkGenerator.java,
           MirrorRuinsStructure.java

src/main/resources/
  META-INF/mods.toml, pack.mcmeta
  assets/mirror_abyss/
    blockstates/ models/block/ models/item/ lang/ textures/{block,item,gui}/
  data/mirror_abyss/
    advancements/ loot_tables/{blocks,entities}/ recipes/
    dimension/ dimension_type/ forge/biome_modifier/ tags/
    worldgen/{biome,noise_settings,configured_feature,placed_feature,structure,structure_set}/

tools/generate_textures.py    # regenerates all placeholder 16x16 PNGs
```

## Development

```bash
# 1. set up (downloads Forge toolchain)
./gradlew setup

# 2. run a dev client
./gradlew runClient

# 3. regenerate all datagen output (JSONs are hand-written for this skeleton)
./gradlew runData

# 4. build the jar
./gradlew build
# -> build/libs/mirror_abyss-1.0.0.jar
```

## Texture Pipeline

Placeholder 16x16 textures are generated procedurally (seeded, reproducible):

```bash
python3 tools/generate_textures.py
```

Upgrade to 32x32 by editing the `new_img(..., size=...)` defaults — all models
are resolution-independent.

## Known Skeleton Gaps (TODO)

- Entity renderers / models (client) — `EntityRenderersEvent` hooks are stubbed
  in `ModEntities#registerSpawnPlacements`; add `ModEntities#registerRenderers`
  + Geo/Java models.
- `MirrorRuinsStructure` uses a simple block-fill piece; replace with Jigsaw
  + Structure-Block-exported `.nbt` templates for the three structures.
- Boss arena anchor (`teleportToVoidSanctum`) uses a fixed coordinate; wire it
  to the generated `void_sanctum` structure bounding box.
- FinalMirror ending packet (`FinalMirrorScreen#onEndingChosen`) — add a
  `SimpleChannel` payload that writes the choice to `FinalMirrorBE` and fires
  the `final_decision` advancement.
- Dimension special effects (`ModDimensions#registerDimensionEffects`) — sky
  renderer / floating mirror-shard particles.

## License

MIT
