# Google Play icon

`icon-512.png` is the source of truth for both the Google Play listing and the
installed Android launcher icon. Upload this exact file in Play Console.

Requirements verified:

- 512 x 512, 32-bit RGBA PNG
- less than 1,024 KB
- no text, ranking, price, category, badge, or Google Play branding
- essential artwork remains inside the adaptive-icon 66/108 safe zone
- installed legacy density icons are generated directly from this file
- Android 8+ uses the same artwork through an adaptive icon, with a separate
  monochrome layer for themed icons

Generation method: built-in image generation, followed by deterministic
Lanczos resizing with FFmpeg.

Final generation prompt:

> Create a polished Android game icon for Untangle with five luminous metallic
> puzzle nodes, mostly cyan connections, one restrained coral problem line,
> and a lime success center on a full-bleed deep navy background. Keep all
> essential artwork within the central 61 percent safe zone. Premium stylized
> 3D geometry, readable at 48 px, no text, badges, borders, watermark, or
> pre-baked launcher mask.
