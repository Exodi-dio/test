# Now playing screen — Figma reference

Source: **Apple Music Redesign (Community)**, file key `KpKE1p45nLdf1ahrCzgXDh`.

Three mobile frames, all on a 402 × 874 grid, all sharing the same player block.

| frame | node | file |
| --- | --- | --- |
| player big | `3224:4136` | [big](figma-player-big.png) |
| player lyrics | `3224:4305` | [lyrics](figma-player-lyrics.png) |
| player queue | `3226:4431` | [queue](figma-player-queue.png) |

## Backdrop

- Screen gradient: `#2878B7` → `#4FAFF0` at 77 %. This is the blurred album art
  wash, so the hue has to come from the cover, not a fixed colour.
- Album art sits behind everything at `642 × 600`, offset `-135, 0`, **layer blur 86.4**,
  with its own gradient fade from `rgba(78,147,198,0)` to `rgba(78,147,197,1)`.
- Liquid glass sheet over the art: fill `rgba(33,30,30,0.5)`, **inner shadow 4 at 15 % white**,
  **drop shadow 8 at 25 % black**, radius 16.

## Player block (shared by all three frames)

`Player` frame: x 0, y 528 (big) / y 616 (lyrics, queue), 402 wide.

| element | node | x | y | w | h |
| --- | --- | --- | --- | --- | --- |
| title (big only) | `3224:4302` | 176 | 557 | 49 | 16 |
| artist (big only) | `3224:4303` | 140 | 588 | 123 | 16 |
| quote button | `3224:4269` | 28 | 644 | 50 | 50 |
| centre pill | `3224:4278` | 122 | 644 | 158 | 50 |
| queue button | `3224:4291` | 324 | 644 | 50 | 50 |
| progress track | `5071:44634` | 31 | 734 | 340 | 4 |
| elapsed / remaining | `5071:44638` / `5071:44640` | 31 / 344 | 748 | 28 / 27 | 14 |
| transport row | `5071:44642` | 38 | 794 | 326 | 32 |

### Buttons

Glass recipe, repeated for every pill and circle:

- fill `rgba(255,255,255,0.18)` over a `rgba(48,48,48,0.18)` tint
- inner shadow 4 at 20 % white
- drop shadow 5 at 10 % black
- radius 20 for pills, 25 for the 50 px circles
- the quote button on the lyrics screen is the pressed state: fill `rgba(255,255,255,0.45)`

### Progress

- track 340 × 4, radius 2, fill black at 5 %
- played portion 118 × 4, fill white at 50 %
- times 12 / 14.32, tracking -0.24, white at 25 %
- Dolby Atmos lockup centred between the times, 85 × 14, 60 % opacity

### Transport

32 tall row: airplay, previous, play, next, shuffle. Symbols are white at 50 %
opacity, play is the filled 32 × 24 triangle, shuffle comes from the
`Play Options Icons` set (`4839:37532`).

## Type

| role | family | weight | size | line height | tracking | colour |
| --- | --- | --- | --- | --- | --- | --- |
| lyrics | SF Pro | 700 | 32 | 56 | 0 | white, gradient |
| track title | SF Pro | 590 | 20 | 16 | -0.4 | white 90 % |
| artist | SF Pro | 510 | 18 | 16 | -0.36 | white 50 % |
| mini pill title | SF Pro | 510 | 16 | 16 | -0.32 | white 90 % |
| mini pill artist | SF Pro | 510 | 16 | 16 | -0.32 | white 50 % |
| queue section label | Inter | 600 | 14 | 16.94 | 0 | white 50 % |
| playlist name | Inter | 600 | 16 | 19.36 | 0 | white 90 % |
| "Playing from" | SF Pro | 510 | 14 | 16.71 | -0.28 | white 50 % |
| time labels | SF Pro | 590 | 12 | 14.32 | -0.24 | white 25 % |
| status bar | SF Pro | 590 | 17 | 22 | 0 | white |

SF Pro cannot be redistributed. Inter is already used in the file for the
section labels and playlist names, and is the closest open stand-in for the
rest, so Inter ships with the app.

## Lyrics screen

- Lyrics column `3224:4422` at x 46, y 86, 310 wide, **layer blur 20**.
- Lyrics text `3224:4424`: SF Pro 700, 32 / 56, fill is a vertical gradient:
  white at 0, white 30 % at 17.9 %, white 30 % at 70.7 %, transparent at 100 %.
- Focused word `3224:4425` ("calling?") is a separate sharp layer at 97,142 with
  **layer blur 5** on top of the blurred column.
- So the screen is three layers: a blurred copy of the whole column, a sharp
  copy of the line being sung, and a sharper copy of the word inside it.

### Karaoke

- The blurred column scrolls with the song and drifts out of focus away from the
  active line.
- The active line is sharp white, the word currently being sung is sharp white on
  top of it.
- Everything below fades through the gradient and dissolves into the blur.
- Tapping a line seeks to it, same as the reference app.

## Queue screen

- Content column `3226:4548` at x 21, y 76, 360 wide, **layer blur 10**.
- "Playing from" card `3226:4549` at y 76: 34 × 34 artwork, radius 6, label
  "Playing from" 14 / 50 %, name in Inter 600 16 / 90 %, waveform glyph right at
  60 %.
- Section label "Playing Next:" at y 146.
- Rows from y 183, 58 apart: thumbnail, title, artist, overflow dots on the right.
- The same glass player block and transport sit underneath, so the queue scrolls
  above a fixed player.

## Icons

SF Symbols shapes, re-cut as vectors for Android.

| icon | where |
| --- | --- |
| quote bubble | left glass button, all screens |
| star | centre pill, player big |
| music note | centre pill, player big |
| ellipsis | centre pill, player big |
| list | right glass button, all screens |
| waveform | "Playing from" card |
| airplay | transport row |
| backward / play / forward | transport row |
| shuffle | transport row, from `4839:37532` |
| ellipsis | queue rows |
