# doistx-normalize

[![badge-version]](https://search.maven.org/search?q=g:com.doist.x%20a:normalize*)
![badge-android][badge-android]
![badge-jvm][badge-jvm]
![badge-js][badge-js]
![badge-ios][badge-ios]
![badge-ios][badge-watchos]
![badge-ios][badge-tvos]
![badge-macos][badge-macos]
![badge-windows][badge-windows]
![badge-linux][badge-linux]

Kotlin Multiplatform (KMP) library that adds support for normalization as described by [Unicode Standard Annex #15 - Unicode Normalization Forms](https://unicode.org/reports/tr15/), by extending the `String` class with a `normalize(Form)` method.

All normalization forms are supported:
- `Form.NFC`: Normalization Form C, canonical decomposition followed by canonical composition.
- `Form.NFD`: Normalization Form D, canonical decomposition.
- `Form.NFKC`: Normalization Form KC, compatibility decomposition followed by canonical composition.
- `Form.NFKD`: Normalization Form KD, compatibility decomposition.

## Usage

```kotlin
"Äffin".normalize(Form.NFC) // => "Äffin"
"Äffin".normalize(Form.NFD) // => "A\u0308ffin"
"Äffin".normalize(Form.NFKC) // => "Äffin"
"Äffin".normalize(Form.NFKD) // => "A\u0308ffin"

"Henry \u2163".normalize(Form.NFC) // => "Henry \u2163"
"Henry \u2163".normalize(Form.NFD) // => "Henry \u2163"
"Henry \u2163".normalize(Form.NFKC) // => "Henry IV"
"Henry \u2163".normalize(Form.NFKD) // => "Henry IV"
```

## Setup

```kotlin
repositories {
   mavenCentral()
}

kotlin {
   sourceSets {
      val commonMain by getting {
         dependencies {
            implementation("com.doist.x:normalize:1.0.5")
         }
      }
   }
}
```

## Development

Building this project can be tricky, as cross-compilation in KMP not widely supported. In this case:
- macOS and iOS targets must be built on macOS.
- Windows targets should be built on Windows (or a JDK under [Wine](https://www.winehq.org/)).
- Linux targets must be built on Linux due depending on `libunistring`.
- JVM/Android and JS targets can be cross-compiled.

The defaults can be adjusted using two [project properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties):
- `targets` is a string for which targets to build, test, or publish, depending on the task that runs.
   - `all` (default): All possible targets in the current host.
   - `native`: Native targets only (e.g., on macOS, that's macOS, iOS, watchOS and tvOS).
   - `common`: Common targets only (e.g., JVM, JS, Wasm).
   - `host`: Host OS only.
- `publishRootTarget` is a boolean that indicates whether the [`kotlinMultiplatform` root publication](https://kotlinlang.org/docs/mpp-publish-lib.html#structure-of-publications) is included when publishing enabled targets (can only be done once).

When targets are built, tested and published in CI/CD, the Apple host handles Apple-specific targets, the Windows host handles Windows, and Linux handles everything else.

## Release

To release a new version, ensure `CHANGELOG.md` is up-to-date, and push the corresponding tag (e.g., `v1.2.3`). GitHub Actions handles the rest.

## License

Released under the [MIT License](https://opensource.org/licenses/MIT).

Unicode's normalization test suite is subject to [this license](https://github.com/unicode-org/icu/blob/main/LICENSE).

[badge-version]: https://img.shields.io/maven-central/v/com.doist.x/normalize?style=flat
[badge-android]: https://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat
[badge-ios]: https://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat
[badge-js]: https://img.shields.io/badge/platform-js-F8DB5D.svg?style=flat
[badge-jvm]: https://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat
[badge-linux]: https://img.shields.io/badge/platform-linux-2D3F6C.svg?style=flat
[badge-windows]: https://img.shields.io/badge/platform-windows-4D76CD.svg?style=flat
[badge-macos]: https://img.shields.io/badge/platform-macos-111111.svg?style=flat
[badge-watchos]: https://img.shields.io/badge/platform-watchos-C0C0C0.svg?style=flat
[badge-tvos]: https://img.shields.io/badge/platform-tvos-808080.svg?style=flat
[badge-wasm]: httpss://img.shields.io/badge/platform-wasm-624FE8.svg?style=flat
