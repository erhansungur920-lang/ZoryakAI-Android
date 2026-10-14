ZORYAK AI 9.0 ANDROID PROJECT (SOURCE, NOT AN APK)

This is an Android Studio project source. It is not a compiled APK because Android SDK/build tools are not installed in the environment that created this package.

HOW TO BUILD APK ON WINDOWS
1. Install Android Studio from https://developer.android.com/studio
2. Extract this ZIP.
3. In Android Studio choose Open, then select the extracted ZoryakAI_Android_9 folder.
4. Let Gradle sync and install Android SDK Platform 35 if prompted.
5. Choose Build > Build Bundle(s) / APK(s) > Build APK(s).
6. When complete, click Locate. The debug APK will be under app/build/outputs/apk/debug/app-debug.apk.

IMPORTANT BACKEND NOTE
- This app packages the Zoryak 9.0 interface and microphone/camera permission bridge.
- It does not package Ollama or the Qwen model. For real AI replies on phones while the PC is off, you must deploy the Python API and model to an internet-accessible server.
- After opening the app, use the Sunucu button (or first-run prompt) to enter the HTTPS base URL of that API.
- The API must support /api/status and /api/chat, and must allow requests from the app's origin (CORS). The existing Windows server binds to 127.0.0.1:8012 and is not internet-accessible as-is.
- Keep secret API keys off the client app. Do not expose Supabase service_role keys.
- Voice recognition support varies by Android WebView/device. Camera/microphone require runtime permission.
