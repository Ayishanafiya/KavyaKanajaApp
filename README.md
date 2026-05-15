# Kavya-Kanaja

**Project Title 76: Android App Development using GenAI - Kavya-Kanaja (National Pride)**

Kavya-Kanaja is an offline-first Android application that helps students and young learners read, listen to, and understand Kannada poetry. The app presents a daily poem, tappable difficult-word meanings, simple Bhavartha explanations, poet biographies, login/profile settings, and AI-style voice recitation using Android TextToSpeech.

## Problem Statement

Karnataka has a rich literary history, but many students find classical Kannada and poetic vocabulary difficult to access. Because poems are often limited to textbooks, younger learners may miss the emotion, meaning, and cultural pride behind Kannada literature.

## Proposed Solution

Kavya-Kanaja works like a small digital poetry granary. It makes Kannada poems easier to discover and understand by combining:

- Daily poem discovery
- Clean reading interface
- Word meanings on tap
- Simple Bhavartha explanation
- Poet biographies
- Audio recitation through TextToSpeech
- Profile and theme customization

## Key Features

- **Login Page:** Demo login flow for students. Any username can be used with password `1234`.
- **Profile and Settings:** Displays learner username, allows logout, and lets users change app theme.
- **Theme Customization:** Includes Classic, Forest, and Royal reading themes. Theme choice is saved locally.
- **Poem of the Day:** Automatically changes the poem based on the current date.
- **Poem Library:** Includes 52 original Kannada sample poems stored locally.
- **Word Meanings:** Tapping difficult words opens a popup with meaning, modern Kannada explanation, and example usage.
- **Bhavartha:** Provides simple explanation of the poem's inner meaning.
- **Listen and Learn:** Uses MP3 playback if an audio file exists, otherwise uses Android TextToSpeech.
- **Poet's Corner:** Shows biographies and famous works of Kannada Jnanpith awardees.
- **Offline First:** The app can display poems without internet because data is stored in a local JSON file.

## Technology Stack

| Layer | Technology | Purpose |
|---|---|---|
| Language | Kotlin | Main Android development language |
| UI | Jetpack Compose | Builds screens, cards, navigation, dialogs, and forms |
| Design | Material Design 3 | Provides modern UI components |
| Data Storage | Local JSON in assets | Stores poems, meanings, Bhavartha, and poet data |
| JSON Parsing | Gson | Converts JSON into Kotlin data classes |
| Audio | MediaPlayer | Plays MP3 recitations when available |
| AI Voice | Android TextToSpeech | Generates voice recitation when MP3 is not available |
| Preferences | SharedPreferences | Saves selected theme locally |
| Build System | Gradle Kotlin DSL | Builds and manages dependencies |

## Setup Instructions

### Requirements

- Android Studio
- Android SDK installed through Android Studio
- Android emulator or physical Android phone
- Internet for first Gradle dependency sync

### Open the Project

1. Open Android Studio.
2. Select **File > Open**.
3. Open this project folder.
4. Wait for Gradle Sync to finish.
5. Select the `app` run configuration.
6. Select an emulator or connected Android device.
7. Click **Run**.

## Build Command

From the project root, run:

```powershell
.\gradlew :app:assembleDebug
```

To install on a running emulator/device:

```powershell
.\gradlew installDebug
```

## Demo Login

Use any username and this password:

```text
1234
```

Example:

```text
Username: student
Password: 1234
```

## Folder Structure

```text
.
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/
│       │   └── kavya_data.json
│       ├── java/com/example/kavyakanaja/
│       │   └── MainActivity.kt
│       └── res/
│           ├── drawable/
│           ├── mipmap-anydpi-v26/
│           └── values/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

## Important Files

| File | Purpose |
|---|---|
| `MainActivity.kt` | Main app code, screens, login, profile/settings, theme logic, poem display, word popup, audio logic |
| `kavya_data.json` | Local dataset containing 52 poems and poet biographies |
| `AndroidManifest.xml` | Defines app entry activity and app metadata |
| `app/build.gradle.kts` | App-level Gradle configuration and dependencies |
| `build.gradle.kts` | Project-level Gradle plugin configuration |
| `gradle.properties` | Gradle memory and AndroidX settings |

## System Architecture

```text
User
  |
  v
Jetpack Compose UI
  |
  v
MainActivity.kt
  |
  +-- Login and Profile Settings
  +-- Theme Selection with SharedPreferences
  +-- Load kavya_data.json from assets
  +-- Parse JSON using Gson
  +-- Poem of the Day date logic
  +-- Word Meaning popup
  +-- MediaPlayer or TextToSpeech audio
```

## Data Model

Each poem contains:

- `id`
- `title`
- `poet`
- `text`
- `bhavartha`
- `audioFile`
- `difficultWords`

Each difficult word contains:

- `word`
- `meaning`
- `modernKannada`
- `example`

## Current Status

Implemented:

- Android Studio project setup
- Jetpack Compose UI
- Login page
- Profile and Settings page
- Theme customization
- Daily poem logic
- 52 local poems
- Word meaning popup
- Bhavartha display
- Poet biographies
- TextToSpeech audio fallback
- Gradle debug build verified

## Future Improvements

- Add real recorded MP3 recitations
- Add quizzes after each poem
- Add favorites/bookmark feature
- Add search by poet, period, or keyword
- Add public-domain Kannada poem collection
- Add screenshots and demo video to GitHub README

## Submission Notes

Before uploading to GitHub:

- Keep the repository public.
- Commit source code, Gradle files, assets, and README.
- Do not commit generated folders like `build/`, `.gradle/`, `.kotlin/`, or `.idea/`.
- Do not commit large crash dump files like `*.hprof`.
- Test the build with `.\gradlew :app:assembleDebug`.
