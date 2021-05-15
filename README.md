# doistx.normalize

![badge][badge-android]
![badge][badge-jvm]
![badge][badge-js]
![badge][badge-ios]
![badge][badge-mac]
![badge][badge-windows]
![badge][badge-linux]
![badge-version][badge-version]

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

TBD

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

[badge-android]: http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat
[badge-ios]: http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat
[badge-js]: http://img.shields.io/badge/platform-js-F8DB5D.svg?style=flat
[badge-jvm]: http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat
[badge-linux]: http://img.shields.io/badge/platform-linux-2D3F6C.svg?style=flat
[badge-windows]: http://img.shields.io/badge/platform-windows-4D76CD.svg?style=flat
[badge-mac]: http://img.shields.io/badge/platform-macos-111111.svg?style=flat
[badge-watchos]: http://img.shields.io/badge/platform-watchos-C0C0C0.svg?style=flat
[badge-tvos]: http://img.shields.io/badge/platform-tvos-808080.svg?style=flat
[badge-wasm]: https://img.shields.io/badge/platform-wasm-624FE8.svg?style=flat
[badge-version]: https://img.shields.io/maven-central/v/com.doist.x.normalize/doistx-normalize?style=flat
