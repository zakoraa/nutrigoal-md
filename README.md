# How to Run an Android Studio Project

This document provides a step-by-step guide on running an Android project using Android Studio.

## Prerequisites
Before starting, ensure you have the following:

1. **Download and Install Android Studio**
   - Download Android Studio from the [official Android Studio website](https://developer.android.com/studio).
   - Follow the installation instructions based on your operating system.

2. **Installed JDK**
   - Make sure you have Java Development Kit (JDK) version 11 or higher installed.
   - Check your JDK version using the following command in the terminal or command prompt:
     ```bash
     java -version
     ```

3. **Android SDK and Emulator**
   - Android SDK is automatically installed with Android Studio. Ensure the SDK Tools are updated via **SDK Manager** in Android Studio.
   - Prepare an emulator or physical device to run the application.

4. **Android Project**
   - Ensure you have an Android project ready to run (in folder format).

---

## Steps to Run the Project

1. **Clone or Download the Project**
   If the project is on a GitHub repository, clone it using the following command:
   ```bash
   git clone https://github.com/Nutrigoal/Nutrigoal-MD
   ```
   Alternatively, download the project as a ZIP file and extract it locally.

2. **Open the Project in Android Studio**
   - Open Android Studio.
   - Click **File > Open** and select the folder containing the project.
   - Wait for Android Studio to finish syncing the dependencies (Gradle sync).

3. **Sync Gradle**
   - If prompted, click **Sync Now** at the top.
   - Ensure all dependencies are successfully downloaded. If errors occur, check the `build.gradle` file.

4. **Connect an Emulator or Physical Device**
   - For an emulator:
     - Open **AVD Manager** in Android Studio and start the configured emulator.
   - For a physical device:
     - Enable **USB Debugging** on your device through **Developer Options**.
     - Connect the device to your computer via USB.
     - Verify the device is recognized by running the following command:
       ```bash
       adb devices
       ```

5. **Run the Project**
   - Click the **Run** button (green triangle icon) in Android Studio.
   - Select the target device (emulator or physical device).
   - Wait for the application to be built and launched.

6. **Debugging (Optional)**
   - Use the **Logcat** tab in Android Studio to view application logs while running.
   - Add breakpoints for debugging code if needed.
