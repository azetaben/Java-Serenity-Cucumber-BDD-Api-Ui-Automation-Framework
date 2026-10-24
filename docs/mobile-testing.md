# Appium mobile testing

The framework supports Android and iOS mobile browsers and native-app sessions through Serenity's Appium driver. Appium Java Client 11.0.0 is pinned explicitly and Selenium stays at 4.51.0. This combination follows [Appium's client compatibility matrix](https://github.com/appium/java-client#compatibility-matrix) and Serenity 5.4.1's dependency versions.

## Server and devices

Install and run Appium separately from Maven. For this Java client generation, use Appium 3 and compatible drivers:

```shell
npm install -g appium@3
appium driver install uiautomator2@5
# On the Mac hosting iOS automation:
appium driver install xcuitest@10
appium --address 127.0.0.1 --port 4723
```

UiAutomator2 5+ and XCUITest 10+ require Appium 3. Appium Java Client 11 requires Appium server 3 or newer. See the official [UiAutomator2 requirements](https://appium.github.io/appium-uiautomator2-driver/latest/getting-started/) and [XCUITest requirements](https://github.com/appium/appium-xcuitest-driver/blob/master/docs/getting-started/system-requirements.md).

Android needs the Android SDK/platform tools, configured SDK environment, a running emulator or USB-debugging-enabled real device, and Chrome for mobile web runs. Verify the device ID with `adb devices`. The Appium host must provide a Chromedriver compatible with the device's Chrome version; configure `-Dappium.chromedriverExecutable` when needed.

iOS automation runs on an Appium host with macOS, Xcode and an available simulator or provisioned device. A Windows Maven client can connect to that Mac's Appium endpoint. Select the simulator/device UDID and a platform version installed on that host. Real-device signing and WebDriverAgent setup are external prerequisites.

The default endpoint is `http://127.0.0.1:4723/`, without the legacy `/wd/hub` path. If the server uses another base path, set the full URL. See [Appium server setup](https://appium.io/docs/en/3.0/quickstart/install/).

## Profiles and configuration

| Serenity environment | Platform | Target | Automation |
| --- | --- | --- | --- |
| `android-web` | Android | Chrome | UiAutomator2 |
| `ios-web` | iOS | Safari | XCUITest |
| `android-native` | Android | App or installed package/activity | UiAutomator2 |
| `ios-native` | iOS | App or installed bundle ID | XCUITest |

All profiles use Serenity's `webdriver.driver=provided` with the `appium` driver source, automatic desktop driver downloads are disabled, and desktop vendor options are excluded. Serenity owns session creation, screenshots and cleanup; do not create another driver in step definitions. Both mobile suites run sequentially to avoid sharing a device between scenarios.

| Environment variable | Alternative Maven property | Purpose |
| --- | --- | --- |
| `APPIUM_SERVER_URL` | `appium.hub` | Appium endpoint |
| `APPIUM_UDID` | `appium.udid` | Required device/simulator ID |
| `APPIUM_DEVICE_NAME` | `appium.deviceName` | Device display name |
| `APPIUM_PLATFORM_VERSION` | `appium.platformVersion` | Device OS version |
| `APPIUM_APP` | `appium.app` | App path/URL accessible to the Appium server |
| `APPIUM_APP_PACKAGE` | `appium.appPackage` | Installed Android package |
| `APPIUM_APP_ACTIVITY` | `appium.appActivity` | Installed Android launch activity |
| `APPIUM_BUNDLE_ID` | `appium.bundleId` | Installed iOS app bundle ID |

Maven does not load `.env` files automatically. Export variables in the shell or supply properties. Device IDs and app targets are validated before session startup. Native runs require either `APPIUM_APP`, an Android package plus activity, or an iOS bundle ID. App install paths belong to the Appium server filesystem, not necessarily the Maven machine.

## Run mobile browser tests

```powershell
$env:APPIUM_UDID = 'emulator-5554'
mvn clean verify -Pmobile-web "-Denvironment=android-web"
```

For iOS, set your actual simulator/device ID and Mac server endpoint:

```powershell
$env:APPIUM_UDID = 'your-ios-udid'
$env:APPIUM_SERVER_URL = 'http://your-mac:4723/'
mvn clean verify -Pmobile-web "-Denvironment=ios-web"
```

The mobile web scenario reuses the existing SauceDemo page objects, login steps and assertions. Native Windows Password Manager handling is skipped for Appium drivers. Use the deployment URL override `-Dwebdriver.base.url=...` when appropriate. Mobile layout differences may require device-specific locators for future workflows.

## Run native-app session smoke tests

```powershell
$env:APPIUM_UDID = 'emulator-5554'
$env:APPIUM_APP = '/path/on/appium-host/application.apk'
mvn clean verify -Pmobile-native "-Denvironment=android-native"
```

For iOS, use `-Denvironment=ios-native` and an `.app`/`.ipa` appropriate to the simulator/device, or use an installed app's `APPIUM_BUNDLE_ID` instead of `APPIUM_APP`. The native smoke scenario starts the configured app and verifies an active Appium session; it does not assert business screens for an unspecified app.

Add native screen objects under `saucedemo.com.Appium` using `AppiumBy.accessibilityId(...)`, Android resource IDs, or iOS predicates. Access the managed session with:

```java
AppiumDriver mobile = MobileSupport.appiumDriver(getDriver());
mobile.findElement(AppiumBy.accessibilityId("your-accessibility-id"));
```

## Build isolation and CI

Plain `mvn test` runs unit tests without Appium. Plain `mvn clean verify` continues selecting the desktop/API `Cucumber*TestSuite` classes. `-Pmobile-web` selects `MobileWebTestSuite`; `-Pmobile-native` selects `MobileNativeTestSuite`. Choose one mobile Maven profile per run. Mobile feature folders are separate from desktop features, so existing CI provider runs do not acquire mobile sessions.

Mobile runs generate the same Serenity and Failsafe artifacts as desktop runs. Existing Docker images, Compose providers and Jenkins stages do not provision SDKs, Appium or devices; run mobile profiles on a dedicated device-capable agent with a reachable Appium server. Keep Appium device runs sequential unless each worker has an isolated UDID, server port and driver-specific ports.

## Validation

`AppiumDriverSource` normalizes legacy Serenity properties with typed UiAutomator2/XCUITest options so the server receives W3C `appium:` capabilities. `AppiumIntegrationTest` validates all four profile capabilities and sends real Java-client W3C session requests to a local protocol stub. It also checks missing-device/app errors. These checks verify client wiring, not emulator/device compatibility. Real mobile runs require the external prerequisites above.
