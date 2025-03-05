# Changelog

> The version update history of `PanguText` is recorded here.

::: danger

We will only maintain the latest API version, if you are using an outdate API version, you voluntarily renounce any possibility of maintenance.

:::

::: warning

To avoid translation time consumption, Changelog will use **Google Translation** from **Chinese** to **English**, please refer to the original text for actual reference.

Time zone of version release date: **UTC+8**

:::

## pangutext-android

### 1.0.2 | 2025.03.05 &ensp;<Badge type="tip" text="latest" vertical="middle" />

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