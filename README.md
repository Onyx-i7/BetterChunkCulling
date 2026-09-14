# Better Chunk Culling

Better Chunk Culling is a performance mod for Minecraft 1.12.2 designed to significantly increase FPS, especially on low-end hardware and processors with integrated graphics

## How it works

Minecraft's default system processes chunk sections in rigid rectangular blocks. Better Chunk Culling replaces this behavior by implementing a 3D ellipsoidal culling algorithm, skipping 16x16x16 block sections that fall outside the player's natural line of sight.

To keep CPU usage to a minimum, it uses an optimized squared elliptical distance calculation without performing heavy square root operations:

Distance ^2 = (dx)^2 + (dy * k)^2 + (dz)^2

Note: Because non-visible chunks are aggressively culled to maximize frame rates, in some cases minor lighting glitches or delays in block rendering updates may occur, so I recommend using the [Alfheim Lighting Engine](https://www.curseforge.com/minecraft/mc-mods/alfheim-lighting-engine) mod

---

## Benchmark results

Tests conducted on legacy low-end hardware (Intel Pentium E2220 CPU @ 2.76GHz with Intel GMA 3100 / G31 integrated graphics):

* Without mod (MixinBooter only): 16 FPS | 75/2704 Chunk sections rendered
* With Better Chunk Culling: 27 FPS | 42/1936 Chunk sections rendered
* Performance gain: +68% FPS increase and approximately 45% reduction in GPU load

tesppp

Left: Without mod + MixinBooter | Right: Without mod + MixinBooter + Better Chunk Culling (Same seed and coordinates)

**Notice**: This may not apply to all hardware configurations, but it worked in my case

---

## Bug reports

If you find any issues or visual bugs, please report them on the [GitHub Issue Tracker](https://github.com/Onyx-i7/BetterChunkCulling/issues)
