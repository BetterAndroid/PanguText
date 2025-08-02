# Android

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.pangutext/pangutext-android?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fpangutext%2Fpangutext-android%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-21-orange?logo=android&style=flat-square)

This is the core dependency for the Android platform. When using `PanguText` on Android, you need to include this module.

## Configure Dependency

You can add this module to your project using the following method.

### SweetDependency (Recommended)

Add dependency in your project's `SweetDependency` configuration file.

```yaml
libraries:
  com.highcapable.pangutext:
    pangutext-android:
      version: +
```

Configure dependency in your project `build.gradle.kts`.

```kotlin
implementation(com.highcapable.pangutext.pangutext.android)
```

### Version Catalog

Add dependency in your project's `gradle/libs.versions.toml`.

```toml
[versions]
pangutext-android = "<version>"

[libraries]
pangutext-android = { module = "com.highcapable.pangutext:pangutext-android", version.ref = "pangutext-android" }
```

Configure dependency in your project `build.gradle.kts`.

```kotlin
implementation(libs.pangutext.android)
```

Please change `<version>` to the version displayed at the top of this document.

### Traditional Method

Configure dependency in your project `build.gradle.kts`.

```kotlin
implementation("com.highcapable.pangutext:pangutext-android:<version>")
```

Please change `<version>` to the version displayed at the top of this document.

## Function Introduction

You can view the KDoc [click here](kdoc://pangutext-android).

### Implementation Principle

`PanguText` provides two methods for text formatting on the Android platform: `SpannableString` (does not alter the original text length) and direct insertion of whitespace characters (alters the original text length).

The first method, `SpannableString`, adds a `Span` with spacing to the character before the one that needs spacing, changing the text style without altering the string content. The rendering is done by the `TextView` layer (or manually using `TextPaint` based on `Spanned` for layout styling), achieving non-intrusive text styling.

This method also supports processing already styled text (`Spanned`), such as text created via `Html.fromHtml`.

**However, it is currently experimental and may still have unexpected style errors**. You can refer to the [Personalized Configuration](#personalized-configuration) section below to disable it.

The dynamic application (injection) feature mainly targets the input state of `EditText`. It sets a custom `TextWatcher` for `EditText` to monitor input changes and formats the text from `afterTextChanged`.

The second method directly inserts whitespace characters after the characters that need spacing. This method alters the original text length and content but does not rely on the `TextView` layer for rendering. It uses `TextPaint` to draw the text directly, suitable for all scenarios, **but does not support dynamic application (injection)**.

::: warning Unresolved Issues

`PanguText` may conflict with Material components like `TextInputEditText`, `MaterialAutoCompleteTextView`, and `TextInputLayout` when using `setHint`, as `TextView` does not account for `Span` during measurement. This issue is particularly noticeable in single-line text, and there is no solution yet. Use these components cautiously.

Due to the above issue, calculating the width of a `TextView` with `PanguText` style using the `View.measure` method may also result in errors.

`PanguText` currently cannot handle continuous characters like underlines or strikethroughs in `Spanned` text, as the lines will break after adding spacing. It may also cause style errors or fail to apply styles correctly to some special characters. For stability, avoid enabling `PanguText` for very complex rich text or refer to the [Personalized Configuration](#personalized-configuration) section to set `excludePatterns`.

:::

### Integrate into Existing Projects

Integrating `PanguText` into your current project is very easy. You don't need to change much code. Choose your preferred method below to complete the integration.

#### Inject to LayoutInflater

`PanguText` supports direct injection of `LayoutInflater.Factory2` or creating a `LayoutInflater.Factory2` instance for the current `Activity` to take over the entire view layout inflation. This is the recommended integration method, as it allows for non-intrusive and quick integration without modifying any existing layouts.

> The following example

```kotlin
class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inject here.
        PanguTextFactory2.inject(this)
        setContentView(binding.root)
    }
}
```

::: tip

Since `LayoutInflater.Factory2` is taken over, recycled layouts like `ListView` and `RecyclerView` can also be correctly handled.

After injecting the `LayoutInflater` instance in the `Activity`, the following instances attached to the current `Context` will automatically take effect:

- `Fragment`
- `Dialog`
- `PopupWindow`
- `Toast` (foreground only in higher system versions)

Layouts based on `RemoteView` will not take effect because they are remote objects and do not use the current `Context`'s `LayoutInflater` for layout inflation.

:::

If you are using [ui-component → AppBindingActivity](https://betterandroid.github.io/BetterAndroid/KDoc/ui-component/ui-component/com.highcapable.betterandroid.ui.component.activity/-app-binding-activity) in `BetterAndroid`, you need to slightly modify the current code.

> The following example

```kotlin
class MainActivity : AppBindingActivity<ActivityMainBinding>() {

    override fun onPrepareContentView(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onPrepareContentView(savedInstanceState)
        // Inject here.
        PanguTextFactory2.inject(inflater)
        return inflater
    }
  
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Your code here.
    }
}
```

If your application does not use `AppCompatActivity` or `ViewBinding`, don't worry, you can still use the original method.

> The following example

```kotlin
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inject here.
        PanguTextFactory2.inject(this)
        setContentView(R.layout.activity_main)
    }
}
```

::: tip

`PanguTextFactory2` can be used not only with `Activity` but also injected into any existing `LayoutInflater` instance.
However, please inject it before the `LayoutInflater` instance is used to inflate the layout, otherwise it will not take effect.

:::

#### Using the Patching Tool

You can use `PanguTextPatcher` to patch existing `View` or `ViewGroup` instances.

Patch the entire root layout, and `PanguTextPatcher` will automatically patch all `TextView` instances or their subclasses under the root layout.

> The following example

```kotlin
// Assume you have a root layout.
val root: ViewGroup
// Patch the root layout.
PanguTextPatcher.patch(root)
```

Patch a single `View`, which is of type `TextView` or a subclass of `TextView`.

> The following example

```kotlin
// Assume this is your TextView.
val textView: TextView
// Patch a single View.
PanguTextPatcher.patch(textView)
```

::: warning

When using `PanguTextPatcher` in recycled layouts such as `RecyclerView`, `ListView`, or `ViewPager`, you need to patch the `itemView` in `onCreateViewHolder` or `onBindViewHolder`,
otherwise it will not take effect.

:::

#### Manual Injection or Text Formatting

`PanguText` also supports manual injection, allowing you to inject it into the desired `TextView` or `EditText`.

> The following example

```kotlin
// Assume this is your TextView.
val textView: TextView
// Assume this is your EditText.
val editText: EditText
// Inject into existing text.
textView.injectPanguText()
editText.injectPanguText()
// Optionally choose whether to inject Hint (default is true).
textView.injectPanguText(injectHint = false)
editText.injectPanguText(injectHint = false)
// Dynamic injection, re-calling setText will automatically take effect.
textView.injectRealTimePanguText()
// Dynamic injection mainly targets the input state of EditText.
editText.injectRealTimePanguText()
// Optionally choose whether to inject Hint (default is true).
textView.injectRealTimePanguText(injectHint = false)
editText.injectRealTimePanguText(injectHint = false)
```

`PanguText` also extends the `setText` method of `TextView`, allowing you to directly set text with `PanguText` style.

> The following example

```kotlin
// Assume this is your TextView.
val textView: TextView
// Set text with PanguText style.
textView.setTextWithPangu("Xiaoming今年16岁")
// Set Hint with PanguText style.
textView.setHintWithPangu("输入Xiaoming的年龄")
```

You can also use the `PanguText.format` method to directly format text.

> The following example

```kotlin
// Assume this is your TextView.
val textView: TextView
// Format text using SpannableString method.
// Requires passing the current TextView's Resources and text size.
// If the input text is already Spannable,
// it will return the original object without creating a new SpannableString.
val text = PanguText.format(textView.resources, textView.textSize, "Xiaoming今年16岁")
// Set text.
textView.text = text
// Directly format text using whitespace characters for insertion.
// This method adds extra whitespace characters " " (HSP) to the text.
// The result below will output the string "Xiaoming 今年 16 岁".
// You can also customize the whitespace character at the end of the method.
val text = PanguText.format("Xiaoming今年16岁")
// Set text.
textView.text = text
```

::: tip

The `injectPanguText`, `injectRealTimePanguText`, `setTextWithPangu`, `setHintWithPangu`, and `PanguText.format` methods support the `config` parameter.
You can refer to the [Personalized Configuration](#personalized-configuration) section below.

:::

#### Custom View

`PanguText` can also be used with custom `View`. You can extend your `View` to `AppCompatTextView` and override the `setText` method.

> The following example

```kotlin
class MyTextView(context: Context, attrs: AttributeSet? = null) : AppCompatTextView(context, attrs) {

    override fun setText(text: CharSequence?, type: BufferType?) {
        // Manually inject here.
        val panguText = text?.let { PanguText.format(resources, textSize, it) }
        super.setText(panguText, type)
    }
}
```

::: warning

After injecting `PanguText` into `TextView`, if you use `android:singleLine="true"` in XML layout or `TextView.setSingleLine(true)` in code along with `android:ellipsize="..."`,
this method of setting single-line text may cause unresolvable `OBJ` characters (truncated by ellipsis) to appear when the text exceeds the screen width, because `TextView` does not account for `Span` during measurement, leading to incorrect text width calculation.

The solution is to use `android:maxLines="1"` in XML layout or `TextView.setMaxLines(1)` in code instead.

> The following example

```xml
<TextView
    android:id="@+id/text"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="这是一段很长很长长长长长长长长长长长长还有English混入的的文本"
    android:maxLines="1"
    android:ellipsize="end" />
```

:::

### Personalized Configuration

`PanguText` supports personalized configuration. You can use the global static instance `PanguText.globalConfig` to get the global configuration or configure it individually.

> The following example

```kotlin
// Get global configuration.
val config = PanguText.globalConfig
// Enable or disable the feature.
config.isEnabled = true
// Process Spanned text.
// Processing Spanned text is enabled by default, but this feature is experimental.
// If issues occur, you can disable it. When disabled, Spanned text will return the original text.
config.isProcessedSpanned = true
// Whether to automatically re-measure the text width after processing.
// Note: After [PanguText] injects text and changes the text,
// the width of [TextView] will not be calculated automatically.
// At this time, this feature will call [TextView.setText] to re-execute the measurements,
// which can fix issues in some dynamic layouts (such as `RecyclerView`) where text width changes each time,
// but may cause performance issues. You can choose to disable this feature.
// To prevent unnecessary performance overhead,
// this feature only takes effect on [TextView] with `maxLines` set to 1 or `singleLine`.
config.isAutoRemeasureText = true
// Set patterns to exclude during formatting using regular expressions.
// For example, exclude all URLs.
config.excludePatterns.add("https?://\\S+".toRegex())
// For example, exclude emoji placeholders like "[doge]".
// If you use [ImageSpan] to display emoji images, you can choose to exclude these placeholders.
config.excludePatterns.add("\\[.*?]".toRegex())
// Set the spacing ratio for CJK characters.
// This determines the final layout effect.
// It is recommended to keep the default ratio and adjust it according to personal preference.
config.cjkSpacingRatio = 7f
```

::: warning

If you integrated using the [Inject to LayoutInflater](#inject-to-layoutinflater) method, configure `PanguText.globalConfig` before executing `PanguTextFactory2.inject(...)`, otherwise the configuration will not take effect.

:::

You can also pass the `config` parameter for personalized configuration when manually injecting or formatting text.

> The following example

```kotlin
// Assume this is your TextView.
val textView: TextView
// Create a new configuration.
// You can set [copyFromGlobal] to false to not copy from the global configuration.
val config = PanguTextConfig(copyFromGlobal = false) {
    excludePatterns.add("https?://\\S+".toRegex())
    excludePatterns.add("\\[.*?]".toRegex())
    cjkSpacingRatio = 7f
}
// You can also copy and create a new configuration from any configuration.
val config2 = config.copy {
    excludePatterns.clear()
    excludePatterns.add("https?://\\S+".toRegex())
    excludePatterns.add("\\[.*?]".toRegex())
    cjkSpacingRatio = 7f
}
// Manually inject and configure.
textView.injectPanguText(config = config2)
```

If you integrated using the [Inject to LayoutInflater](#inject-to-layoutinflater) method, you can use the following attributes in the XML layout declaration of `TextView`, `EditText`, or their subclasses for personalized configuration.

- `panguText_enabled` corresponds to `PanguTextConfig.isEnabled`
- `panguText_processedSpanned` corresponds to `PanguTextConfig.isProcessedSpanned`
- `panguText_autoRemeasureText` corresponds to `PanguTextConfig.isAutoRemeasureText`
- `panguText_excludePatterns` corresponds to `PanguTextConfig.excludePatterns`, string array, multiple patterns separated by `|@|`
- `panguText_cjkSpacingRatio` corresponds to `PanguTextConfig.cjkSpacingRatio`

> The following example

```xml
<TextView
    android:id="@+id/text"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Xiaoming今年16岁"
    app:panguText_enabled="true"
    app:panguText_processedSpanned="true"
    app:panguText_autoRemeasureText="true"
    app:panguText_excludePatterns="https?://\\S+;\\[.*?]|@|\\[.*?]"
    app:panguText_cjkSpacingRatio="7.0" />
```

::: warning

Due to issues with Android Studio, the above attributes may not have auto-completion hints. Please complete them manually.

Don't forget to add the declaration `xmlns:app="http://schemas.android.com/apk/res-auto"`.

:::

In custom `View`, you can extend your `View` to implement the `PanguTextView` interface to achieve the same functionality.
This feature is also effective for the [Using the Patching Tool](#using-the-patching-tool) method.

> The following example

```kotlin
class MyTextView(context: Context, attrs: AttributeSet? = null) : AppCompatTextView(context, attrs),
    PanguTextView {

    override fun configurePanguText(config: PanguTextConfig) {
        // Configure your [PanguTextConfig].
    }
}
```

::: warning

The `PanguTextView` interface takes precedence over attributes used directly in the XML layout. If you use both methods for configuration, the `PanguTextView` interface configuration will override the XML layout configuration.

Individual configurations will override global configurations, and options not configured will follow the global configuration.

:::