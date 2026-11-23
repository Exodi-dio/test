# Now playing screen — Figma reference

Source: **Apple music player screen (Community)**, file key `SAOSlPz2ZyADGlI5TrCQv1`.

Two frames, both 375 × 812 (iPhone 13 mini grid), same layout, different backdrop treatment.

| variant | node | file |
| --- | --- | --- |
| Warm | `2:5` | [warm](figma-frame-warm.png) |
| Dim | `5:189` | [dim](figma-frame-dim.png) |

## Layout

Positions are on the 375 grid, origin top-left of the frame.

| element | node | x | y | w | h |
| --- | --- | --- | --- | --- | --- |
| backdrop (blurred album art) | `3:107` / `5:191` | 0 | 0 | 375 | 812 |
| drag handle | `3:158` | 167 | 52 | 40 | 0 |
| header row | `3:113` | 20 | 73 | 335 | 59 |
| album thumb | `3:15` | 20 | 73 | 59 | 59 |
| track title | `3:108` | 45 | 81 | 218 | 21 |
| artist | `3:109` | 45 | 106 | 218 | 19 |
| overflow button | `3:111` | 327 | 89 | 28 | 28 |
| lyrics block | `3:154` | 20 | 132 | 335 | 394 |
| lyrics scrim | `3:155` | 20 | 132 | 335 | 394 |
| progress line | `3:145` | 20 | 526 | 335 | 0 |
| elapsed / remaining | `3:143`, `3:144` | 20 / 323 | 538 | 23 / 32 | 15 |
| transport | `3:38` | 59 | 580 | 256 | 48 |
| volume row | `3:120` | 62 | 651 | 250 | 16 |
| bottom row | `3:116` | 91 | 708 | 191 | 26 |

## Type

| role | family | weight | size | line height | tracking | colour |
| --- | --- | --- | --- | --- | --- | --- |
| track title | SF Pro | 590 | 18 | 21.48 | -0.165 | white |
| artist | SF Pro | 400 | 16 | 19.09 | -0.165 | white |
| lyrics | SF Pro Display | 700 | 31 | 38.44 | 0 | white |
| time labels | SF Pro Display | 500 | 12 | 14.88 | 0 | white |

SF Pro cannot be redistributed. Inter is the closest open stand-in: SemiBold for the
title, Bold for lyrics, Medium for the time labels.

## Colour and effects

- Backdrop: album art scaled to fill, then **background blur 36**.
  Warm has no tint. Dim adds a **50 % black** layer over the blurred art.
- Lyrics scrim: vertical linear gradient, colour `#D9D9D9`,
  alpha `0.10 @ 0`, `1 @ 0.125`, `1 @ 0.87`, `0 @ 1`.
- The second lyric line carries an extra **layer blur 2** on top of the scrim.
- Glass chips: **background blur 28–30** behind the small icons.
- Album thumb: 59 × 59, corner radius 8, drop shadow radius 10 at 25 % black plus
  radius 9 at 45 % black.
- Bottom quote chip: 27 × 26, corner radius 4, background blur 30.

## Icons

Phosphor shapes, filled white, exported as SVG and ported as vectors.

| icon | node | size |
| --- | --- | --- |
| rewind | `3:24` | 40 × 40 |
| pause | `3:22` | 48 × 48 |
| fast forward | `3:26` | 40 × 40 |
| speaker low | `3:28` | 16 × 16 |
| speaker high | `3:30` | 16 × 16 |
| speaker | `3:36` | 24 × 24 |
| queue | `3:32` | 24 × 24 |
| overflow dots | `3:34` | 20 × 20 in a 28 × 28 circle |
| quote chip | `5:180` | 27 × 26, radius 4 |

## Sliders

- Progress: 335 wide at y 526, played portion 52. Times under it, elapsed left,
  remaining right as `- 2:34`.
- Volume: 202 wide at x 86, played portion 168, speaker glyphs at both ends.
