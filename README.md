# Hermes Android

Native Android client for [Hermes Agent](https://github.com/NousResearch/hermes-agent) — run Hermes locally on your device via Termux, or connect to a remote Hermes Gateway API Server.

## Features

- **Dual Mode Operation**
  - **Local Mode**: Run Hermes directly on your Android device via [Termux](https://termux.dev/)
  - **Remote Mode**: Connect to any Hermes Gateway instance via OpenAI-compatible API

- **Full Chat Experience**
  - Streaming responses (SSE) from Hermes Gateway
  - Session management (create, rename, delete, archive)
  - Message history with persistence
  - Code blocks with syntax highlighting
  - Markdown rendering

- **Termux Integration**
  - One-tap Hermes installation in Termux
  - Background Hermes Gateway process management
  - Real-time output log viewer
  - Automatic startup on app launch (optional)

- **Remote Connection**
  - OpenAI-compatible `/v1/chat/completions` endpoint
  - Responses API (`/v1/responses`) support
  - Server capabilities discovery
  - Model listing
  - Connection testing with latency measurement

- **Modern Android Stack**
  - Kotlin + Jetpack Compose (Material 3)
  - Hilt Dependency Injection
  - Room Database for local persistence
  - DataStore for preferences
  - Retrofit + OkHttp with SSE support
  - Edge-to-edge display

## Screenshots

*Coming soon*

## Requirements

- Android 8.0+ (API 26)
- For Local Mode: [Termux](https://github.com/termux/termux-app) installed from F-Droid or GitHub
- For Remote Mode: Hermes Gateway running with API Server enabled

## Quick Start

### Remote Mode (Easiest)

1. Install the app
2. Go to Settings → Connection Mode → **Remote**
3. Enter your Hermes Gateway URL (e.g., `https://hermes.example.com`)
4. Enter your API Key (from `API_SERVER_KEY` in Hermes `.env`)
5. Tap **Test Connection**
6. Start chatting!

### Local Mode (Advanced)

1. Install [Termux](https://f-droid.org/packages/com.termux/) from F-Droid
2. Install [Termux:API](https://f-droid.org/packages/com.termux.api/) for enhanced features
3. Open Termux and run:
   ```bash
   pkg update && pkg install -y python git
   pip install hermes-agent
   ```
4. In the app, go to Settings → Connection Mode → **Local**
5. Tap **Install Hermes in Termux** (automates step 3)
6. Tap **Start Hermes** to launch the gateway
7. Start chatting!

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Hermes Android App                      │
├──────────────────┬──────────────────────────────────────────┤
│   Remote Mode    │            Local Mode                     │
├──────────────────┼──────────────────────────────────────────┤
│  Retrofit/OkHttp │           TermuxManager                   │
│  SSE Streaming   │   ┌─────────────────────────────────┐    │
│  /v1/chat/*      │   │  Termux Process (hermes gateway)│    │
│  /v1/responses   │   │  API Server on localhost:8642   │    │
│  /v1/models      │   └─────────────────────────────────┘    │
│  /v1/capabilities│                                            │
└──────────────────┴──────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Shared Layer                              │
│  Room Database (Sessions, Chats, RemoteConfigs)             │
│  DataStore Preferences (Mode, URLs, Keys, Theme)            │
│  Repository Pattern + UseCases                              │
│  Hilt Dependency Injection                                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  Jetpack Compose UI (Material 3)                            │
│  ViewModels + StateFlow                                     │
│  Navigation Compose                                         │
└─────────────────────────────────────────────────────────────┘
```

## Building

```bash
# Clone
git clone https://github.com/jondmarien/hermes-android.git
cd hermes-android

# Open in Android Studio Arctic Fox+ or build via CLI
./gradlew assembleDebug

# Install on device
./gradlew installDebug
```

## Configuration

### Remote Server Setup

On your Hermes Gateway host, enable the API Server in `~/.hermes/.env`:

```bash
API_SERVER_ENABLED=true
API_SERVER_KEY=your-secure-random-key
API_SERVER_CORS_ORIGINS=http://localhost:3000,https://yourdomain.com
```

Then start the gateway:

```bash
hermes gateway
```

The API will be available at `http://host:8642/v1`

### Local Termux Setup

The app can auto-install Hermes in Termux, or manually:

```bash
# In Termux
pkg update && pkg install -y python git openssl
pip install --upgrade pip
pip install hermes-agent

# Run gateway with API server
hermes gateway
```

## Project Structure

```
hermes-android/
├── app/
│   ├── src/main/java/com/hermes/android/
│   │   ├── data/
│   │   │   ├── local/
│   │   │   │   ├── db/           # Room: entities, DAOs, database
│   │   │   │   ├── preferences/  # DataStore: HermesPreferences
│   │   │   │   └── termux/       # TermuxManager, TermuxConnectionService
│   │   │   ├── remote/           # RemoteHermesRepositoryImpl
│   │   │   │   └── api/          # Retrofit services
│   │   │   └── repository/       # Repository implementations
│   │   ├── domain/
│   │   │   ├── model/            # Session, Chat, RemoteConfig, TermuxStatus
│   │   │   ├── repository/       # Repository interfaces
│   │   │   └── usecase/          # UseCases (chat, session, remote, termux)
│   │   ├── presentation/
│   │   │   ├── ui/               # Compose screens & components
│   │   │   │   ├── chat/         # ChatScreen, MessageBubble, InputBar
│   │   │   │   ├── sessions/     # SessionListScreen
│   │   │   │   ├── settings/     # SettingsScreen
│   │   │   │   └── local/        # TermuxSetupScreen, TermuxStatusWidget
│   │   │   └── viewmodel/        # ViewModels
│   │   └── di/                   # Hilt modules
│   └── src/main/res/
│       ├── values/               # strings, colors, themes
│       └── xml/                  # backup rules, data extraction
├── gradle/
│   └── libs.versions.toml        # Version catalog
├── build.gradle.kts
├── settings.gradle.kts
└── proguard-rules.pro
```

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 1.9.23 |
| Build | Gradle 8.5 (KTS) |
| UI | Jetpack Compose, Material 3 |
| DI | Hilt 2.50 |
| Database | Room 2.6.1 |
| Preferences | DataStore 1.1.1 |
| Networking | Retrofit 2.11, OkHttp 4.12, OkHttp-SSE |
| Serialization | Gson / Kotlinx Serialization |
| Coroutines | Kotlinx Coroutines 1.8 |
| Date/Time | Kotlinx Datetime 0.4 |
| Images | Coil 2.5 |
| Testing | JUnit 5, MockK, Turbine, Robolectric, Compose UI Test |

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Commit Convention

Follows [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` — New feature
- `fix:` — Bug fix
- `refactor:` — Code restructuring
- `docs:` — Documentation
- `chore:` — Maintenance
- `test:` — Tests
- `style:` — Formatting

## License

MIT License — see [LICENSE](LICENSE) for details.

## Related Projects

- [Hermes Agent](https://github.com/NousResearch/hermes-agent) — The core agent
- [Hermes Gateway](https://hermes-agent.nousresearch.com/docs/user-guide/messaging/) — Messaging platform gateway
- [Termux](https://termux.dev/) — Linux environment for Android
- [Termux:API](https://github.com/termux/termux-api) — Android API access for Termux

## Support

- 📖 [Hermes Agent Documentation](https://hermes-agent.nousresearch.com/docs/)
- 🐛 [Issue Tracker](https://github.com/jondmarien/hermes-android/issues)
- 💬 [Discussions](https://github.com/jondmarien/hermes-android/discussions)

---

Built with ❤️ for the Hermes Agent community