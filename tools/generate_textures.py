#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Procedural 16x16 texture generator for mirror_abyss (Minecraft Forge 1.20.1).

Each function paints a 16x16 RGBA array with numpy, then saves PNGs to
src/main/resources/assets/mirror_abyss/textures/{block,item,gui}.
All designs follow the texture design table in the mod design doc.
"""
import os
import numpy as np
from PIL import Image

RNG = np.random.default_rng(20241016)

BLOCK_DIR = "src/main/resources/assets/mirror_abyss/textures/block"
ITEM_DIR = "src/main/resources/assets/mirror_abyss/textures/item"
GUI_DIR = "src/main/resources/assets/mirror_abyss/textures/gui"



def _c(color, alpha=255):
    """Normalize a 3/4-tuple color to RGBA with clamped channels."""
    if len(color) >= 4:
        alpha = color[3]
    return (max(0, min(255, int(color[0]))), max(0, min(255, int(color[1]))),
            max(0, min(255, int(color[2]))), max(0, min(255, alpha)))

def new_img(base=(0, 0, 0, 255), size=16):
    arr = np.zeros((size, size, 4), dtype=np.uint8)
    arr[:, :] = base
    return arr


def save(arr, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    Image.fromarray(arr, "RGBA").save(path)


def speckle(arr, colors, count, alpha=255):
    """Random pixels of the given colors."""
    h, w = arr.shape[:2]
    xs = RNG.integers(0, w, count)
    ys = RNG.integers(0, h, count)
    for x, y in zip(xs, ys):
        c = colors[RNG.integers(0, len(colors))]
        arr[y, x] = _c(c, alpha)
    return arr


def crack_lines(arr, color=(122, 60, 255, 255), count=3):
    """Random jagged crack lines. Accepts a single color or a list of colors."""
    h, w = arr.shape[:2]
    palette = color if isinstance(color, (list, tuple)) and color and isinstance(color[0], (list, tuple)) else [color]
    for _ in range(count):
        x = RNG.integers(0, w)
        y = RNG.integers(0, h)
        c = palette[RNG.integers(0, len(palette))]
        for _ in range(RNG.integers(4, 8)):
            if 0 <= x < w and 0 <= y < h:
                arr[y, x] = _c(c)
            x += RNG.integers(-1, 2)
            y += RNG.integers(0, 2)
    return arr


def triangles(arr, colors):
    """Triangular mirror shard pattern."""
    h, w = arr.shape[:2]
    for ty in range(0, h, 4):
        for tx in range(0, w, 4):
            c = colors[RNG.integers(0, len(colors))]
            c4 = _c(c)
            # upper-left triangle
            for dy in range(4):
                for dx in range(4):
                    if dx <= dy:
                        arr[ty + dy, tx + dx] = c4
    return arr


def vertical_gradient(arr, top, bottom):
    h = arr.shape[0]
    for y in range(h):
        t = y / max(1, h - 1)
        arr[y, :, 0] = max(0, min(255, int(top[0] + (bottom[0] - top[0]) * t)))
        arr[y, :, 1] = max(0, min(255, int(top[1] + (bottom[1] - top[1]) * t)))
        arr[y, :, 2] = max(0, min(255, int(top[2] + (bottom[2] - top[2]) * t)))
    return arr


def ring(arr, center, radius, color, thickness=1):
    cx, cy = center
    for y in range(arr.shape[0]):
        for x in range(arr.shape[1]):
            d = np.sqrt((x - cx) ** 2 + (y - cy) ** 2)
            if radius - thickness <= d <= radius + thickness:
                arr[y, x] = _c(color)
    return arr


def fill_circle(arr, center, radius, color):
    cx, cy = center
    for y in range(arr.shape[0]):
        for x in range(arr.shape[1]):
            if (x - cx) ** 2 + (y - cy) ** 2 <= radius ** 2:
                arr[y, x] = _c(color)
    return arr


# ============================================================
# BLOCK TEXTURES
# ============================================================

def cracked_stone():
    a = new_img((43, 43, 51, 255))
    a = speckle(a, [(38, 38, 46), (52, 52, 62), (30, 30, 38)], 90)
    a = crack_lines(a, (122, 60, 255, 255), 4)
    a = crack_lines(a, (90, 40, 190, 255), 3)
    # glowing crack dots
    a = speckle(a, [(150, 90, 255, 255)], 12)
    return a


def mirror_shard_base(brightness=1.0, purple=1.0):
    silver = (int(201 * brightness), int(214 * brightness), int(229 * brightness))
    violet = (int(143 * purple), int(123 * purple), int(255 * purple))
    a = new_img((30, 34, 46, 255))
    a = triangles(a, [silver, violet, (110, 120, 150)])
    a = speckle(a, [(235, 240, 250, 255)], 10)
    return a


def mirror_shard_block():
    return mirror_shard_base()


def mirror_shard_block_top():
    return mirror_shard_base(1.05, 0.9)


def mirror_shard_block_side():
    return mirror_shard_base(0.9, 1.0)


def mirror_shard_block_reflecting():
    return mirror_shard_base(1.3, 1.1)


def void_stabilizer():
    # central spherical core
    a = new_img((28, 28, 34, 255))
    a = fill_circle(a, (8, 8), 6, (40, 90, 160, 255))
    a = fill_circle(a, (8, 8), 4, (78, 168, 255, 255))
    a = fill_circle(a, (7, 7), 2, (200, 235, 255, 255))
    a = ring(a, (8, 8), 6, (20, 20, 28), 1)
    return a


def void_stabilizer_top():
    a = new_img((28, 28, 34, 255))
    a = ring(a, (8, 8), 6, (78, 168, 255, 255), 1)
    a = fill_circle(a, (8, 8), 2, (120, 220, 255, 255))  # indicator light
    return a


def void_stabilizer_side():
    a = new_img((28, 28, 34, 255))
    a = ring(a, (8, 8), 5, (70, 80, 100), 1)
    # pulse runes
    for i in range(8):
        x = (i * 2 + 1) % 16
        a[x, 8] = (78, 168, 255, 255)
    return a


def void_stabilizer_bottom():
    a = new_img((28, 28, 34, 255))
    a = ring(a, (8, 8), 5, (50, 55, 65), 1)
    return a


def watcher_altar_top():
    a = new_img((107, 88, 54, 255))
    a = speckle(a, [(90, 74, 46), (120, 100, 62)], 40)
    # eye rune in the center
    a = fill_circle(a, (8, 8), 4, (159, 216, 255, 255))
    a = fill_circle(a, (8, 8), 2, (30, 40, 60, 255))  # pupil
    a = crack_lines(a, (159, 216, 255, 255), 2)
    return a


def watcher_altar_top_activated():
    a = watcher_altar_top()
    a = speckle(a, [(220, 240, 255, 255)], 25)  # glowing runes
    return a


def watcher_altar_side():
    a = new_img((107, 88, 54, 255))
    a = speckle(a, [(90, 74, 46)], 50)
    a = crack_lines(a, (159, 216, 255, 255), 2)
    a = crack_lines(a, (60, 60, 70), 2)
    return a


def final_mirror():
    a = new_img((5, 5, 7, 255))
    # iridescent mirror surface
    a = vertical_gradient(a, (20, 18, 40), (60, 40, 90))
    # void pupil in center
    a = fill_circle(a, (8, 8), 5, (10, 8, 18, 255))
    a = fill_circle(a, (8, 8), 2, (180, 140, 255, 255))
    a = ring(a, (8, 8), 5, (179, 136, 255, 255), 1)
    a = ring(a, (8, 8), 7, (122, 252, 255, 255), 1)
    return a


def final_mirror_top():
    a = new_img((10, 10, 14, 255))
    a = speckle(a, [(179, 136, 255), (122, 252, 255)], 8)
    return a


def final_mirror_side():
    a = new_img((24, 22, 30, 255))
    a = speckle(a, [(40, 38, 50), (60, 56, 72)], 40)
    a = crack_lines(a, [(179, 136, 255, 255)], 2)
    return a


def mirror_portal_frame():
    a = new_img((42, 22, 64, 255))
    a = speckle(a, [(60, 40, 90)], 40)
    # bright mirror inner edge
    a = ring(a, (8, 8), 6, (217, 230, 255, 255), 1)
    a = fill_circle(a, (8, 8), 5, (70, 60, 110, 255))
    a = fill_circle(a, (8, 8), 2, (217, 230, 255, 255))
    return a


def mirror_portal_frame_activated():
    a = mirror_portal_frame()
    a = speckle(a, [(220, 235, 255, 255)], 30)
    return a


def void_floor():
    a = new_img((10, 10, 18, 255))
    a = crack_lines(a, (90, 43, 214, 255), 4)
    a = speckle(a, [(50, 30, 90), (120, 80, 255)], 25)
    # bubbling edges
    for i in range(6):
        x = RNG.integers(0, 16)
        y = RNG.integers(0, 16)
        a[y, x] = (150, 110, 255, 255)
    return a


def watcher_brick():
    a = new_img((59, 74, 90, 255))
    # mortar lines
    a[0, :] = (40, 50, 62, 255)
    a[8, :] = (40, 50, 62, 255)
    a[:, 0] = (40, 50, 62, 255)
    a[:, 8] = (40, 50, 62, 255)
    # gold glyphs
    a[2, 2] = (202, 164, 90, 255)
    a[3, 3] = (202, 164, 90, 255)
    a[10, 11] = (202, 164, 90, 255)
    a[11, 10] = (202, 164, 90, 255)
    a = speckle(a, [(70, 85, 102)], 30)
    return a


def reflective_lamp():
    a = new_img((90, 92, 100, 255))
    a = crack_lines(a, (60, 62, 70), 2)
    a = ring(a, (8, 8), 4, (200, 210, 220), 1)
    return a


def reflective_lamp_top():
    a = new_img((255, 244, 214, 255))
    a = fill_circle(a, (8, 8), 5, (255, 252, 240, 255))  # lens
    a = ring(a, (8, 8), 6, (180, 190, 200), 1)
    a = speckle(a, [(255, 255, 255)], 8)
    return a


def reflective_lamp_off():
    a = reflective_lamp()
    a = ring(a, (8, 8), 4, (70, 74, 80), 1)
    return a


def reflective_lamp_top_off():
    a = new_img((120, 118, 108, 255))
    a = fill_circle(a, (8, 8), 5, (140, 140, 132, 255))
    a = ring(a, (8, 8), 6, (90, 92, 96), 1)
    return a


def abyss_ore():
    a = new_img((30, 30, 36, 255))
    a = speckle(a, [(20, 20, 26), (40, 40, 48)], 80)
    # embedded glowing mirror chips (like eyes in the vein)
    for _ in range(4):
        x = RNG.integers(2, 14)
        y = RNG.integers(2, 14)
        a[y, x] = (120, 220, 255, 255)
        a[y + 1, x] = (90, 170, 220, 255)
    a = crack_lines(a, (90, 170, 220, 255), 2)
    return a


# ============================================================
# ITEM TEXTURES
# ============================================================

def item_mirror_shard():
    a = np.zeros((16, 16, 4), dtype=np.uint8)
    # triangle mirror shard
    for y in range(16):
        for x in range(16):
            if x <= y and x + y <= 15:
                a[y, x] = (201, 214, 229, 255)
            elif x + y <= 15 and x > y:
                a[y, x] = (143, 123, 255, 255)
    a[8:, :8] = (0, 0, 0, 0)
    a[8:, 8:] = (0, 0, 0, 0)
    a[8, :] = (0, 0, 0, 0)
    a[:8, 8:] = (0, 0, 0, 0)
    a = speckle(a, [(235, 240, 250, 255)], 6)
    return a


def item_void_essence():
    a = np.zeros((16, 16, 4), dtype=np.uint8)
    a = fill_circle(a, (8, 9), 6, (40, 20, 60, 255))
    a = fill_circle(a, (8, 9), 4, (90, 43, 160, 255))
    a = fill_circle(a, (7, 8), 2, (180, 140, 255, 255))
    return a


def item_void_eye():
    a = np.zeros((16, 16, 4), dtype=np.uint8)
    # almond eye shape
    for y in range(16):
        for x in range(16):
            dx = (x - 8) / 6.0
            dy = (y - 8) / 4.5
            if dx * dx + dy * dy <= 1:
                a[y, x] = (30, 30, 40, 255)
            if dx * dx + (dy * 0.5) ** 2 <= 0.18:
                a[y, x] = (200, 140, 255, 255)  # iris glow
    a = fill_circle(a, (8, 8), 2, (10, 10, 20, 255))  # pupil
    a = speckle(a, [(220, 180, 255, 255)], 4)
    return a


def item_watcher_relic():
    a = np.zeros((16, 16, 4), dtype=np.uint8)
    a = ring(a, (8, 8), 6, (107, 88, 54, 255), 2)
    a = fill_circle(a, (8, 8), 3, (159, 216, 255, 255))
    a = fill_circle(a, (8, 8), 1, (30, 40, 60, 255))
    return a


def item_final_mirror_item():
    a = np.zeros((16, 16, 4), dtype=np.uint8)
    a = ring(a, (8, 8), 7, (24, 22, 30, 255), 1)
    a = ring(a, (8, 8), 5, (179, 136, 255, 255), 1)
    a = fill_circle(a, (8, 8), 4, (20, 18, 40, 255))
    a = fill_circle(a, (8, 8), 1, (122, 252, 255, 255))
    return a


def item_cracked_silk():
    a = np.zeros((16, 16, 4), dtype=np.uint8)
    for i in range(3):
        x = 2 + i * 4
        for y in range(3, 13):
            a[y, x] = (150, 140, 160, 255)
        a[3 + i, x - 1] = (150, 140, 160, 255)
    return a


def item_void_core():
    a = np.zeros((16, 16, 4), dtype=np.uint8)
    a = fill_circle(a, (8, 8), 5, (20, 16, 34, 255))
    a = fill_circle(a, (8, 8), 3, (90, 43, 160, 255))
    a = fill_circle(a, (8, 8), 1, (220, 200, 255, 255))
    a = ring(a, (8, 8), 6, (179, 136, 255, 255), 1)
    return a


# ============================================================
# GUI
# ============================================================

def gui_final_mirror():
    a = np.zeros((110, 176, 4), dtype=np.uint8)
    a[:] = (18, 16, 30, 255)
    # mirror frame
    a[:2, :] = (90, 60, 120, 255)
    a[-2:, :] = (90, 60, 120, 255)
    a[:, :2] = (90, 60, 120, 255)
    a[:, -2:] = (90, 60, 120, 255)
    # glowing line under title
    a[26, 30:146] = (179, 136, 255, 255)
    return a


def main():
    # __file__ may be relative ("tools/generate_textures.py"); resolve to the
    # project root reliably in both cases.
    base = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
    os.chdir(base)

    blocks = {
        "cracked_stone": cracked_stone,
        "mirror_shard_block": mirror_shard_block,
        "mirror_shard_block_top": mirror_shard_block_top,
        "mirror_shard_block_side": mirror_shard_block_side,
        "mirror_shard_block_reflecting": mirror_shard_block_reflecting,
        "void_stabilizer": void_stabilizer,
        "void_stabilizer_top": void_stabilizer_top,
        "void_stabilizer_side": void_stabilizer_side,
        "void_stabilizer_bottom": void_stabilizer_bottom,
        "watcher_altar_top": watcher_altar_top,
        "watcher_altar_top_activated": watcher_altar_top_activated,
        "watcher_altar_side": watcher_altar_side,
        "final_mirror": final_mirror,
        "final_mirror_top": final_mirror_top,
        "final_mirror_side": final_mirror_side,
        "mirror_portal_frame": mirror_portal_frame,
        "mirror_portal_frame_activated": mirror_portal_frame_activated,
        "void_floor": void_floor,
        "watcher_brick": watcher_brick,
        "reflective_lamp": reflective_lamp,
        "reflective_lamp_top": reflective_lamp_top,
        "reflective_lamp_off": reflective_lamp_off,
        "reflective_lamp_top_off": reflective_lamp_top_off,
        "abyss_ore": abyss_ore,
    }
    items = {
        "mirror_shard": item_mirror_shard,
        "void_essence": item_void_essence,
        "void_eye": item_void_eye,
        "watcher_relic": item_watcher_relic,
        "final_mirror_item": item_final_mirror_item,
        "cracked_silk": item_cracked_silk,
        "void_core": item_void_core,
    }

    for name, fn in blocks.items():
        save(fn(), os.path.join(BLOCK_DIR, name + ".png"))
    for name, fn in items.items():
        save(fn(), os.path.join(ITEM_DIR, name + ".png"))
    save(gui_final_mirror(), os.path.join(GUI_DIR, "final_mirror.png"))

    print("Generated", len(blocks), "block textures,", len(items), "item textures, 1 gui texture.")


if __name__ == "__main__":
    main()
