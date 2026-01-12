# Changelog

All notable changes are documented in this file. The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

## [1.3.3] - 2026-01-12

### Changed

- Publish all targets from Linux

## [1.3.2] - 2026-01-12

### Changed

- Build and publish mingw target on Windows

## [1.3.1] - 2026-01-12

### Fixed

- Preserve NUL characters in native normalization
- Free libunistring output to avoid memory leaks

## [1.3.0] - 2025-12-13

### Added

- Build watchosDeviceArm64 target

## [1.2.0] - 2025-03-20

### Added

- Support for Wasm - thanks @zsmb13!

## [1.1.1] - 2024-07-16

### Fixed

- Ensure root publication is aware of all targets

## [1.1.0] - 2024-07-16

> [!IMPORTANT]
> Version 1.1.0 and higher target Kotlin 2.0 are incompatible with Kotlin 1.9.
> Stick to version 1.0.5 if you still target Kotlin 1.9.

### Added

- Support for Kotlin 2.0

## [1.0.5] - 2023-04-22

### Fixed

- Fix normalization being truncated on Windows in some cases

## [1.0.4] - 2021-12-13

### Added

- Support for macOS ARM 64-bit
- Support for targeting Apple simulators on macOS ARM 64-bit

## [1.0.3] - 2021-07-22

### Fixed

- Apple and Windows targets not being published

## [1.0.2] - 2021-07-22

### Added

- Support for watchOS ARM 32-bit, including the Apple Watch Series 3, still supported by watchOS 7
- Support for Linux ARM 64-bit, including the Raspberry Pi 4

## [1.0.1] - 2021-05-18

### Added

- Support for watchOS and tvOS (64-bit)

## [1.0.0] - 2021-05-17

### Added

- Initial release with support for JVM, Android, JS, iOS, macOS, Windows, Linux
