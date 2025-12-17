# 更新日志

> 这里记录了 `PanguText` 的版本更新历史。

::: danger

我们只会对最新的 API 版本进行维护，若你正在使用过时的 API 版本则代表你自愿放弃一切维护的可能性。

:::

## pangutext-android

### 1.0.5 | 2025.12.17 &ensp;<Badge type="tip" text="最新" vertical="middle" />

- 适配 Kotlin 2.2+
- 适配 `BetterAndroid` 新特性

### 1.0.4 | 2025.08.16 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 在执行 `injectPanguText` 时排除 `TextView` 自身设置的 `TextWatcher` 防止重复触发 `doOnTextChanged`

### 1.0.3 | 2025.08.03 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 将 Java 反射相关行为由 [YukiReflection](https://github.com/HighCapable/YukiReflection) 迁移至 [KavaRef](https://github.com/HighCapable/KavaRef)
- 其它已知问题修复

### 1.0.2 | 2025.03.05 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- `PanguTextFactory2` 在注入时新增异常捕获，避免在 `View` 自身初始化过程中断整个处理过程
- 移除重复注入的警告日志，现在重复注入 `PanguText` 将无任何作用产生
- 新增 `PanguTextPatcher`，可以使用新的方案注入 `PanguText`

### 1.0.1 | 2025.02.11 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 修复注入 `PanguText` 后 `TextView` 可能导致测量宽度不正确的问题
- `PanguTextConfig` 新增 `isAutoRemeasureText`，用于控制是否自动重新测量文本宽度 (作用于 `TextView` 单行文本)

### 1.0.0 | 2025.02.10 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 首个版本提交至 Maven

## pangutext-compose

暂未发布。