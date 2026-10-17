#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Structure .nbt generator for mirror_abyss (Minecraft 1.20.1, DataVersion 3465).

Writes Structure-Block-format NBT files used by Jigsaw structures:
  data/mirror_abyss/structures/{watchers_ruin,mirror_hallway,void_sanctum}.nbt

Format (named binary tag, big-endian):
  root Compound {
    size:        List[Int] [x, y, z]
    palette:     List[Compound {Name: str}]
    blocks:      List[Compound {pos: List[Int], state: Int, [nbt]}]
    entities:    List[Compound {pos: List[Double], blockPos: List[Int], nbt: Compound}]
    DataVersion: Int (3465 = 1.20.1)
  }
"""
import gzip
import os
import struct

# ----------------------- NBT primitives -----------------------

TAG_END = 0
TAG_BYTE = 1
TAG_SHORT = 2
TAG_INT = 3
TAG_LONG = 4
TAG_FLOAT = 5
TAG_DOUBLE = 6
TAG_BYTE_ARRAY = 7
TAG_STRING = 8
TAG_LIST = 9
TAG_COMPOUND = 10
TAG_INT_ARRAY = 11
TAG_LONG_ARRAY = 12

DATA_VERSION_1_20_1 = 3465


def _string(s):
    b = s.encode("utf-8")
    return struct.pack(">H", len(b)) + b


def _name(name):
    return _string(name) if name is not None else b""


def _tag(tag_type, name, payload):
    return bytes([tag_type]) + _name(name) + payload


def nbt_byte(name, v):
    return _tag(TAG_BYTE, name, struct.pack(">b", v))


def nbt_int(name, v):
    return _tag(TAG_INT, name, struct.pack(">i", v))


def nbt_long(name, v):
    return _tag(TAG_LONG, name, struct.pack(">q", v))


def nbt_float(name, v):
    return _tag(TAG_FLOAT, name, struct.pack(">f", v))


def nbt_double(name, v):
    return _tag(TAG_DOUBLE, name, struct.pack(">d", v))


def nbt_string(name, s):
    return _tag(TAG_STRING, name, _string(s))


def nbt_int_array(name, vals):
    return _tag(TAG_INT_ARRAY, name, struct.pack(">i", len(vals)) + b"".join(struct.pack(">i", v) for v in vals))


def nbt_list(name, items):
    """items: list of already-tagged (unnamed) elements."""
    if not items:
        return _tag(TAG_LIST, name, bytes([TAG_END]) + struct.pack(">i", 0))
    elem_type = items[0][0]
    payload = bytes([elem_type]) + struct.pack(">i", len(items)) + b"".join(i[1:] for i in items)
    return _tag(TAG_LIST, name, payload)


def nbt_compound(name, fields):
    return _tag(TAG_COMPOUND, name, b"".join(fields) + bytes([TAG_END]))


def nbt_root(fields):
    """Unnamed root compound. NbtIo.writeNamedTag writes the TAG_Compound byte
    (0x0A), an empty UTF name (0x0000), the fields, then TAG_End — this is the
    exact layout structure-block saves use (writeCompressed)."""
    return bytes([TAG_COMPOUND]) + struct.pack(">H", 0) + b"".join(fields) + bytes([TAG_END])


def int_list(name, vals):
    return nbt_list(name, [nbt_int(None, v) for v in vals])


def double_list(name, vals):
    return nbt_list(name, [nbt_double(None, v) for v in vals])


# ----------------------- Structure builders -----------------------

def block_entry(pos, state):
    x, y, z = pos
    return nbt_compound(None, [int_list("pos", [x, y, z]), nbt_int("state", state)])


def entity_entry(pos, block_pos, entity_fields):
    return nbt_compound(None, [
        double_list("pos", pos),
        int_list("blockPos", block_pos),
        nbt_compound("nbt", entity_fields),
    ])


def build_structure(size, palette, blocks, entities):
    palette_tags = [nbt_compound(None, [nbt_string("Name", s)]) for s in palette]
    block_tags = [block_entry(pos, state) for pos, state in blocks]
    entity_tags = entities or []
    return nbt_root([
        int_list("size", size),
        nbt_list("palette", palette_tags),
        nbt_list("blocks", block_tags),
        nbt_list("entities", entity_tags),
        nbt_int("DataVersion", DATA_VERSION_1_20_1),
    ])


def save(name, data):
    """Structure .nbt files are gzip-compressed (NbtIo.writeCompressed)."""
    path = os.path.join("src/main/resources/data/mirror_abyss/structures", name + ".nbt")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with gzip.open(path, "wb") as f:
        f.write(data)
    print("wrote", path, len(data), "bytes (gzip)")


# ----------------------- Layouts -----------------------

def watchers_ruin():
    """16x8x16 watcher ruin: brick walls, stone floor, central altar."""
    palette = [
        "mirror_abyss:cracked_stone",     # 0
        "mirror_abyss:watcher_brick",     # 1
        "mirror_abyss:watcher_altar",     # 2
        "mirror_abyss:mirror_shard_block",  # 3
    ]
    blocks = []

    def add(x, y, z, s):
        if 0 <= x < 16 and 0 <= z < 16:
            blocks.append(((x, y, z), s))

    # floor
    for x in range(16):
        for z in range(16):
            add(x, 0, z, 0)

    # outer walls (y=1..6) with a 2-wide gate on the north side
    for y in range(1, 7):
        for x in range(16):
            for z in (0, 15):
                add(x, y, z, 1)
            for z in range(16):
                add(0, y, z, 1)
                add(15, y, z, 1)
        # gate opening at z=0, x=7..8
        add(7, y, 0, None)
        add(8, y, 0, None)
        # gate posts glow
        if y <= 3:
            add(6, y, 0, 3)
            add(9, y, 0, 3)

    # interior: cross walls (y=1..2) + 4 pillars (y=1..5)
    for y in range(1, 3):
        for x in range(2, 14):
            add(x, y, 7, 1)
            add(x, y, 8, 1)
        for z in range(2, 14):
            add(7, y, z, 1)
            add(8, y, z, 1)
    for px, pz in ((3, 3), (12, 3), (3, 12), (12, 12)):
        for y in range(1, 6):
            add(px, y, pz, 1)

    # altar at the far end
    add(7, 1, 12, 2)
    add(8, 1, 12, 2)

    # shard inlays on the walls
    for y in (1, 2):
        add(7, y, 15, 3)
        add(8, y, 15, 3)
        add(15, y, 7, 3)
        add(15, y, 8, 3)

    return build_structure([16, 8, 16], palette,
                           [(p, s) for p, s in blocks if s is not None], [])


def mirror_hallway():
    """16x6x16 mirror corridor: shard-lined passage with reflective lamps."""
    palette = [
        "mirror_abyss:cracked_stone",        # 0
        "mirror_abyss:mirror_shard_block",   # 1
        "mirror_abyss:reflective_lamp",      # 2
        "mirror_abyss:watcher_brick",        # 3
    ]
    blocks = []

    def add(x, y, z, s):
        if 0 <= x < 16 and 0 <= z < 16:
            blocks.append(((x, y, z), s))

    # floor
    for x in range(16):
        for z in range(16):
            add(x, 0, z, 0)

    # corridor channel: z 6..9 floor is mirror
    for x in range(16):
        for z in range(6, 10):
            add(x, 1, z, 1)

    # corridor walls (y=1..5) at z=5 and z=10, mirror shards
    for y in range(1, 6):
        for x in range(16):
            add(x, y, 5, 1)
            add(x, y, 10, 1)

    # reflective lamps on the corridor walls
    for x in range(2, 15, 3):
        add(x, 2, 5, 2)
        add(x, 2, 10, 2)

    # end walls with brick + door frame
    for y in range(1, 6):
        for z in range(16):
            add(0, y, z, 3)
            add(15, y, z, 3)
        add(7, y, 0, None)
        add(8, y, 0, None)
        add(7, y, 15, None)
        add(8, y, 15, None)

    # side alcoves: shard blocks
    for x in (4, 11):
        for z in (2, 13):
            add(x, 1, z, 1)
            add(x, 2, z, 1)

    return build_structure([16, 6, 16], palette,
                           [(p, s) for p, s in blocks if s is not None], [])


def void_sanctum():
    """16x5x16 boss arena: brick walls, void floor center, stabilizers, boss."""
    palette = [
        "mirror_abyss:watcher_brick",       # 0
        "mirror_abyss:void_floor",          # 1
        "mirror_abyss:void_stabilizer",     # 2
        "mirror_abyss:cracked_stone",       # 3
    ]
    blocks = []

    def add(x, y, z, s):
        if 0 <= x < 16 and 0 <= z < 16:
            blocks.append(((x, y, z), s))

    # floor: brick ring, void center 6x6, stone corners
    for x in range(16):
        for z in range(16):
            s = 1 if 5 <= x <= 10 and 5 <= z <= 10 else (0 if x <= 2 or x >= 13 or z <= 2 or z >= 13 else 3)
            add(x, 0, z, s)

    # outer walls y=1..4
    for y in range(1, 5):
        for x in range(16):
            for z in (0, 15):
                add(x, y, z, 0)
            for z in range(16):
                add(0, y, z, 0)
                add(15, y, z, 0)
        # gate on the north
        add(7, y, 0, None)
        add(8, y, 0, None)

    # void stabilizers at the four corners (y=1)
    add(2, 1, 2, 2)
    add(13, 1, 2, 2)
    add(2, 1, 13, 2)
    add(13, 1, 13, 2)

    # boss entity at the arena center (structure-local coords)
    entities = [
        entity_entry(
            [8.0, 1.5, 8.0],
            [8, 1, 8],
            [
                nbt_string("id", "mirror_abyss:mirror_eater"),
                nbt_int_array("UUID", [0x11111111, 0x22222222, 0x33333333, 0x44444444]),
                nbt_byte("PersistenceRequired", 1),
            ],
        )
    ]

    return build_structure([16, 5, 16], palette,
                           [(p, s) for p, s in blocks if s is not None], entities)


def main():
    os.chdir(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
    save("watchers_ruin", watchers_ruin())
    save("mirror_hallway", mirror_hallway())
    save("void_sanctum", void_sanctum())


if __name__ == "__main__":
    main()
