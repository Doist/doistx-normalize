# doistx-normalize

![badge-version][badge-version]
![badge-android][badge-android]
![badge-jvm][badge-jvm]
![badge-js][badge-js]
![badge-ios][badge-ios]
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
            implementation("com.doist.x:normalize:1.0.0")
         }
      }
   }
}
```

## Development

Building KMP projects can be tricky, as cross-compilation is not widely supported. In this case:
- macOS and iOS targets must be built on macOS.
- Windows targets should be built on Windows.
  - Alternatively, IntelliJ IDEA 2021.1 + AdoptOpenJDK 8u292-b10 have been shown to work under Wine 6.7.
- Linux targets can be cross-compiled, but this is disabled due to `libunistring` not being pre-installed on macOS/Windows.
  - To enable, comment the respective line in `buildSrc/src/main/kotlin/disable-cross-compile.gradle.kts`.
- JVM/Android and JS targets can be cross-compiled.

CI/CD tests, builds, and publishes macOS and iOS targets on macOS, Windows targets on Windows, and everything else on Linux.

## Releasing

To release a new version, simply push the corresponding tag (e.g., `v1.0.1`). GitHub Actions handles the rest.

## License

```
MIT License

Copyright (c) 2021 Doist

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

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
