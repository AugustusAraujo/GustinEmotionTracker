# AGENTS.md

Kotlin multi-module Android app: mood tracker with two native apps — `:wear` (Wear OS, fast input) and `:mobile` (Android, storage + CSV export) — synced via the Wearable Data Layer API. No backend. UI text is pt-BR, hardcoded in composables (not string resources). Do not add comments to code.

## Commands

```bash
./gradlew :core:assembleDebug :mobile:assembleDebug :wear:assembleDebug   # build all
./gradlew :mobile:testDebugUnitTest :wear:testDebugUnitTest               # unit tests
./gradlew :wear:assembleDebug                                             # watch app only
```

No lint/typecheck/format tasks are configured. Verify work by compiling all three modules.

## Module boundaries

- `:core` — shared, UI-free. `MoodEntry` (Room `@Entity`), `Quadrant` enum (label + ARGB color), `EmotionCatalog` (5 emotions per quadrant), `MoodEntrySerializer` (⇄ `DataMap`). Exposes Room runtime + play-services-wearable as `api`. Room compiler (KSP) lives only in `:mobile`; the watch never persists locally — `DataClient.putDataItem` queues offline and delivers on reconnect.
- `:mobile` — Room DB (`mood_tracker.db`), `MoodListenerService` (manifest intent-filter `DATA_CHANGED` on pathPrefix `/mood_entry`), history screen, CSV export via `FileProvider` (authority `${applicationId}.fileprovider`, cache `exports/`) + `ACTION_SEND`. Reminder settings (DataStore) are pushed to the watch via a `/prompt_config` DataItem.
- `:wear` — 3-screen linear flow via a `sealed interface Screen` in `MainActivity` (no navigation library). `MoodSender` uses path `/mood_entry/<timestamp>` — the timestamp suffix is mandatory: DataItems with identical paths overwrite each other. `setUrgent()` is set. Reminders: `PromptConfigListenerService` (`/prompt_config`) → `PromptScheduler` (inexact `setAndAllowWhileIdle`, no exact-alarm permission) → `PromptReceiver` notification; `BootReceiver` reschedules after reboot. `nextTriggerMillis` is a pure function with unit tests in `:wear`.

Data flow: wear tap → `MoodSender` → Data Layer → `MoodListenerService` → Room → `StateFlow` updates history. Config flow: mobile settings → DataStore + `/prompt_config` DataItem → wear listener → alarms → notification → app opens on quadrant screen.

## Toolchain quirks (verified, cost hours)

- **AGP 9.1.1 has built-in Kotlin.** Do NOT apply `org.jetbrains.kotlin.android` — the build fails with an explicit error. The compose compiler plugin (`org.jetbrains.kotlin.plugin.compose`) IS still required for `:mobile` and `:wear`.
- **KSP versioning is decoupled from Kotlin** (KSP 2.3.13 pairs with Kotlin 2.4.21 — not the old `<kotlin>-<ksp>` scheme).
- **compileSdk 37** everywhere (required by Compose BOM 2026.09.00 / lifecycle 2.11); targetSdk stays 36, minSdk 30 (targets Galaxy S25 Ultra + Galaxy Watch 8). Missing SDK platforms auto-download via `android.builder.sdkDownload=true` in `gradle.properties`; keep that line.
- **Both apps MUST share the same `applicationId`** (`com.example.gustinemotiontracker`) — the Data Layer API only delivers DataItems to the companion app with an identical package name; `namespace` differs per module. Watch app must be uninstalled before reinstalling whenever the applicationId changes.
- Wear Compose 1.7: `ScalingLazyColumnDefaults.AnchorType` / `AutoCenteringParams` no longer exist — use plain defaults. `material-icons-core` is pinned to 1.7.8 (icons left the newer BOMs).
- Wear serializer: `DataMap.getString` returns `String?` — `requireNotNull` is used in `MoodEntrySerializer`.

## Run configuration

`.idea/runConfigurations/mobile.xml` and `wear.xml` are the shared run configs behind the IDE dropdown (mobile = phone, wear = watch). `.idea/workspace.xml` may be deleted safely; it regenerates. The old `:app` module no longer exists — it was renamed to `:mobile`.
