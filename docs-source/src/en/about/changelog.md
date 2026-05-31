# Changelog

> The version update history of `PanguText` is recorded here.

::: danger

We will only maintain the latest API version. If you are using an outdated API version, you voluntarily renounce any possibility of maintenance.

:::

::: warning

To avoid translation time consumption, the Changelog will use **Google Translation** from **Chinese** to **English**. Please refer to the original text for actual reference.

Time zone of version release date: **UTC+8**

:::

## pangutext-android

### 1.0.6 | 2026.05.31 &ensp;<Badge type="tip" text="latest" vertical="middle" />

- `PanguText.format(resources, textSize, ...)` is adjusted to directly scan character boundaries and apply `PanguMarginSpan`, no longer reusing the string replacement pipeline, expected to be 10 times faster.
  `PanguText.format(text, ...)` continues to keep regex replacement and text-content corrections, and the responsibilities of the two implementations are now separated
- Fixed the handling of `excludePatterns` in `SpannableString` solution, it is now only used as a scan exclusion mask and no longer mixed into text-content correction logic
- Fixed spacing recognition in `SpannableString` solution for boundaries such as `#topic#`, operators, brackets, and quotes
- `SpannableString` solution is no longer described as an experimental feature, `Spanned` text is supported for direct processing by default, and can still be skipped through `isProcessedSpanned` when needed
- Fixed continuous decoration rendering when `SpannableString` solution processes `Spanned` text, underlines, strikethroughs, and background colors can now stay continuous with `PanguMarginSpan`

### 1.0.5 | 2025.12.17 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- Adapted to Kotlin 2.2+
- Adapted to new features of `BetterAndroid`

### 1.0.4 | 2025.08.16 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- Exclude `TextView`'s own `TextWatcher` set during `injectPanguText` execution to prevent repeated triggering of `doOnTextChanged`

### 1.0.3 | 2025.08.03 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- Migrate Java reflection-related behavior from [YukiReflection](https://github.com/HighCapable/YukiReflection) to [KavaRef](https://github.com/HighCapable/KavaRef)
- Other known issues fixed

### 1.0.2 | 2025.03.05 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- Added exception handling in `PanguTextFactory2` during injection to avoid interrupting the entire process during `View` initialization
- Removed duplicate injection warning logs, now duplicate injections of `PanguText` will have no effect
- Added `PanguTextPatcher`, which allows injecting `PanguText` using a new method

### 1.0.1 | 2025.02.11 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- Fixed an issue where injecting `PanguText` could cause incorrect width measurement in `TextView`
- Added `isAutoRemeasureText` to `PanguTextConfig` to control whether to automatically remeasure text width (applies to single-line text in `TextView`)

### 1.0.0 | 2025.02.10 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- The first version is submitted to Maven

## pangutext-compose

Not yet released.