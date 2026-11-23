# Now Playing

Local audio player with a single screen: artwork, title, seek bar, transport
controls, queue and lyrics. No tabs, no library browser, no setup.

Open the app and it scans the device with MediaStore, then starts playing.

- Metadata and cover art read from the file tags.
- Lyrics from a `.lrc` file next to the track, or embedded in the tags (ID3
  USLT/TXXX, Vorbis comments, MP4 `©lyr`).
- Line-synced LRC scrolls with playback and can be tapped to seek. Enhanced
  LRC with word timings lights up word by word.
- Queue sheet, shuffle, repeat, background playback with a media notification.

## Build

JDK 17 and the Android SDK with platform 35. No local wrapper is committed, so
use a Gradle 8.11 install:

```
gradle assembleRelease
```

Release builds are signed from `SIGNING_STORE_FILE`, `SIGNING_STORE_PASSWORD`,
`SIGNING_KEY_ALIAS` and `SIGNING_KEY_PASSWORD`.

CI builds the APK on every push and publishes it when a `v*` tag is pushed.
