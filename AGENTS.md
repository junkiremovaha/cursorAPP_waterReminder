# AGENTS.md

## Cursor Cloud specific instructions

### Repository layout

- `main` is a stub (empty `README.md` only).
- The HydroMinder Android app lives on `cursor/hydrominder-android-app-715e` (Kotlin, Jetpack Compose, Gradle). Check out that branch before building or running anything.

### Android SDK (one-time per VM)

The Android SDK is expected at `/home/ubuntu/android-sdk` with `ANDROID_HOME` set (see `~/.bashrc` on configured VMs). Required packages include `platform-tools`, `platforms;android-37.0`, `platforms;android-36`, and a recent `build-tools` (e.g. `36.0.0`). Accept licenses with `yes | sdkmanager --licenses`.

Create `local.properties` (gitignored) so Gradle can find the SDK:

```properties
sdk.dir=/home/ubuntu/android-sdk
```

### Build, lint, and test

From the repo root on the app branch (see `README.md`):

| Task | Command |
|------|---------|
| Debug APK | `./gradlew assembleDebug` |
| Lint | `./gradlew lint` |
| Unit tests | `./gradlew test` (currently no unit test sources) |

APK output: `app/build/outputs/apk/debug/app-debug.apk`

### Running on a device or emulator

- **AVD / emulator**: Requires `/dev/kvm` (hardware acceleration). Cloud Agent VMs typically do not expose KVM; the standard `emulator` will fail with “x86_64 emulation currently requires hardware acceleration”.
- **Physical device**: `adb install -r app/build/outputs/apk/debug/app-debug.apk`, then launch `com.hydrominder.app/.MainActivity`.
- **Redroid / Docker Android**: May need `binder`/`ashmem` kernel modules; not reliable in nested cloud VMs.

For interactive UI verification, use a machine with KVM or a physical device.

### Services

Single Android client app; no backend or database. No long-running dev server—only Gradle builds and optional emulator/adb.
