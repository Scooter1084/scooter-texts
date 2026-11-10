# Scooter Texts

<img alt="Scooter Texts icon" src="graphics/icon.svg" width="96" />

A personal SMS/MMS texting app for Android. It can be set as the phone's default messaging app.

## Download a test build

1. Open the **Actions** tab of this repo and pick **Build debug APK**.
2. Click **Run workflow** (or open the latest successful run).
3. When it finishes, download **scooter-texts-debug** under *Artifacts*, unzip it, and install the `.apk` on your phone.

The debug build installs as **Scooter Texts (debug)** with the app ID `com.scooter1084.texts.debug`, so it can sit next to any other messaging app.

## Building it yourself

Requires JDK 17 and the Android SDK.

```bash
./gradlew assembleFossDebug
```

The APK ends up in `app/build/outputs/apk/foss/debug/`.

## Credits and license

Scooter Texts is based on [Fossify Messages](https://github.com/FossifyOrg/Messages) and
[Fossify Commons](https://github.com/FossifyOrg/Commons) (copied into the `commons/` folder),
both licensed under the GNU General Public License v3.0.
Scooter Texts is also licensed under the [GPL-3.0](LICENSE).
