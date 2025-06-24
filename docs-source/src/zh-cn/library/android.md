# Android

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.pangutext/pangutext-android?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fpangutext%2Fpangutext-android%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-21-orange?logo=android&style=flat-square)

这是 Android 平台的核心依赖，在 Android 平台上使用 `PanguText` 时，你需要引入此模块。

## 配置依赖

你可以使用如下方式将此模块添加到你的项目中。

### SweetDependency (推荐)

在你的项目 `SweetDependency` 配置文件中添加依赖。

```yaml
libraries:
  com.highcapable.pangutext:
    pangutext-android:
      version: +
```

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(com.highcapable.pangutext.pangutext.android)
```

### Version Catalog

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

```toml
[versions]
pangutext-android = "<version>"

[libraries]
pangutext-android = { module = "com.highcapable.pangutext:pangutext-android", version.ref = "pangutext-android" }
```

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(libs.pangutext.android)
```

请将 `<version>` 修改为此文档顶部显示的版本。

### 传统方式

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation("com.highcapable.pangutext:pangutext-android:<version>")
```

请将 `<version>` 修改为此文档顶部显示的版本。

## 功能介绍

你可以 [点击这里](kdoc://pangutext-android) 查看 KDoc。

### 实现原理

`PanguText` 在 Android 平台有两种方案对文本进行格式化，一种为 `SpannableString` (不破坏原始文本长度)，另一种则是直接插入空白字符 (破坏原始文本长度)。

第一种方案为 `SpannableString`，它会在需要增加间距的字符的前一个字符后增加应用了间距的 `Span` 来实现文本在样式上的改变，而不实际改变字符串的内容，最后交由 `TextView` 层完成渲染 (或手动使用 `TextPaint` 基于 `Spanned` 做布局样式处理)，实现无侵入式为文本设置样式。

第一种方案同样支持直接处理已经应用了样式的文本 (`Spanned`)，例如通过 `Html.fromHtml` 创建的文本，**但是目前尚处于实验性阶段，可能仍然会出现非预期样式错误问题**，
你可以参考下方的 [个性化配置](#个性化配置) 选择禁用它。

动态应用 (注入) 功能主要针对 `EditText` 的输入状态，它会为 `EditText` 设置一个自定义的 `TextWatcher` 来监听输入状态，当输入状态发生变化时，从 `afterTextChanged` 中获取 `Editable` 并进行格式化。

第二种方案则是直接插入空白字符，它会直接在需要增加间距的字符后插入空白字符，这种方案会破坏原始文本的长度并且会改变文本内容自身，
但是可以不依赖于 `TextView` 层完成渲染，直接使用 `TextPaint` 绘制文本即可，适用于所有场景，**但不支持动态应用 (注入)**。

::: warning 尚未解决的问题

`PanguText` 可能会与 Material 组件 `TextInputEditText`、`MaterialAutoCompleteTextView` 与 `TextInputLayout` 结合时在 `setHint` 效果上产生冲突，
因为 `TextView` 不会在测量时计算文本中的 `Span`，在单行文本中此类问题尤为明显，暂时还没有解决方案，请谨慎配合此类组件使用。

受制于上述问题，通过 `View.measure` 方法计算包含了 `PanguText` 风格的 `TextView` 宽度时也可能会出现错误。

`PanguText` 目前不能处理 `Spanned` 文本中的下划线、删除线这种连续的字符，添加空白间距后线条会中断，
并且它可能会在一些特殊字符上发生样式错误或样式没有被正确应用，为了稳定性考虑请尽量不要对非常复杂的富文本启用 `PanguText` 或参考下方的 [个性化配置](#个性化配置) 设置 `excludePatterns`。

:::

### 集成到现有项目

将 `PanguText` 集成到你的当前项目中非常容易，你不需要改动过多代码，挑选以下你喜欢的方案进行，即可完成集成。

#### 注入布局装载器 (LayoutInflater)

`PanguText` 支持直接注入 `LayoutInflater.Factory2` 或为当前 `Activity` 创建 `LayoutInflater.Factory2` 实例以接管整个视图，
这是推荐的集成方案，这种方式不需要修改任何现有布局即可实现无侵入式快速集成。

> 示例如下

```kotlin
class MainActivity : AppCompactActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 在这里注入
        PanguTextFactory2.inject(this)
        setContentView(binding.root)
    }
}
```

::: tip

由于接管了 `LayoutInflater.Factory2`，所以包括类似 `ListView`、`RecyclerView` 的回收式布局也能被正确接管。

注入 `Activity` 中的 `LayoutInflater` 实例后，以下附属于当前 `Context` 的实例都会自动生效。

-  `Fragment`
-  `Dialog`
-  `PopupWindow`
-  `Toast` (在高版本系统中仅前台)

基于 `RemoteView` 的布局将无法生效，因为它们是远程对象，不会使用当前 `Context` 的 `LayoutInflater` 进行布局装载。

:::

如果你正在使用 `BetterAndroid` 中的 [ui-compoment → AppBindingActivity](https://betterandroid.github.io/BetterAndroid/KDoc/ui-component/ui-component/com.highcapable.betterandroid.ui.component.activity/-app-binding-activity)，你需要稍微改动当前代码。

> 示例如下

```kotlin
class MainActivity : AppBindingActivity<ActivityMainBinding>() {

   override fun onPrepareContentView(savedInstanceState: Bundle?): LayoutInflater {
       val inflater = super.onPrepareContentView(savedInstanceState)
       // 在这里注入
       PanguTextFactory2.inject(inflater)
       return inflater
   }

   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       // Your code here.
   }
}
```

如果你的应用程序没有使用 `AppCompatActivity` 也没有使用 `ViewBinding`，没有关系，你依然可以使用最初的方案进行。

> 示例如下

```kotlin
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 在这里注入
        PanguTextFactory2.inject(this)
        setContentView(R.layout.activity_main)
    }
}
```

::: tip

`PanguTextFactory2` 除了可以配合 `Activity` 使用，它还支持注入到任何现有的 `LayoutInflater` 实例中，但请在 `LayoutInflater` 实例被用于装载布局前进行注入，否则将无法生效。

:::

#### 使用修补工具

你可以使用 `PanguTextPatcher` 修补现有的 `View` 或 `ViewGroup` 实例。

修补整个根布局，`PanguTextPatcher` 会自动修补根布局下的所有 `TextView` 或继承于其的组件。

> 示例如下

```kotlin
// 假设你有一个根布局
val root: ViewGroup
// 修补根布局
PanguTextPatcher.patch(root)
```

修补单个 `View`，类型为 `TextView` 或继承于 `TextView` 的组件。

> 示例如下

```kotlin
// 假设这就是你的 TextView
val textView: TextView
// 修补单个 View
PanguTextPatcher.patch(textView)
```

::: warning

在 `RecyclerView`、`ListView`、`ViewPager` 等回收式布局中使用 `PanguTextPatcher` 时，你需要在 `onCreateViewHolder` 或 `onBindViewHolder` 中获取到 `itemView` 后进行修补，否则不会生效。

:::

#### 手动注入或格式化文本

`PanguText` 同样支持手动注入，你可以在需要的 `TextView` 或 `EditText` 上手动进行注入。

> 示例如下

```kotlin
// 假设这就是你的 TextView
val textView: TextView
// 假设这就是你的 EditText
val editText: EditText
// 注入到现有文本
textView.injectPanguText()
editText.injectPanguText()
// 可以选择是否同时注入 Hint (默认是)
textView.injectPanguText(injectHint = false)
editText.injectPanguText(injectHint = false)
// 动态注入，重新调用 setText 也会自动生效
textView.injectRealTimePanguText()
// 动态注入主要针对于 EditText 的输入状态
editText.injectRealTimePanguText()
// 同样可以选择是否同时注入 Hint (默认是)
textView.injectRealTimePanguText(injectHint = false)
editText.injectRealTimePanguText(injectHint = false)
```

`PanguText` 还对 `TextView` 的 `setText` 方法进行了扩展，你可以使用如下方式直接设置带有 `PanugText` 样式的文本。

> 示例如下

```kotlin
// 假设这就是你的 TextView
val textView: TextView
// 设置带有 PanguText 样式的文本
textView.setTextWithPangu("Xiaoming今年16岁")
// 设置带有 PanguText 样式的 Hint
textView.setHintWithPangu("输入Xiaoming的年龄")
```

你还可以使用 `PanguText.format` 方法直接格式化文本。

> 示例如下

```kotlin
// 假设这就是你的 TextView
val textView: TextView
// 使用 SpannableString 方案格式化文本
// 需要传入当前 TextView 的 Resources 以及字体大小
// 如果传入的文本自身为 Spannable 类型，则不会创建新的 SpannableString，而是返回原始对象
val text = PanguText.format(textView.resources, textView.textSize, "Xiaoming今年16岁")
// 设置文本
textView.text = text
// 直接使用空白字符以插入破坏的方式格式化文本
// 这个方案会为文本增加额外的空白字符 " " (HSP)
// 下方的结果会输出字符串 "Xiaoming 今年 16 岁"
// 你也可以在方法末位自定义要使用的空白字符
val text = PanguText.format("Xiaoming今年16岁")
// 设置文本
textView.text = text
```

::: tip

`injectPanguText`、`injectRealTimePanguText`、`setTextWithPangu`、`setHintWithPangu`、`PanguText.format` 方法支持 `config` 参数，你可以参考下方的 [个性化配置](#个性化配置)。

:::

#### 自定义 View

`PanguText` 还可以配合自定义 `View` 进行使用，你可以将你的 `View` 继承到 `AppCompatTextView` 并重写 `setText` 方法。

> 示例如下

```kotlin
class MyTextView(context: Context, attrs: AttributeSet? = null) : AppCompatTextView(context, attrs) {

    override fun setText(text: CharSequence?, type: BufferType?) {
        // 在这里手动进行注入
        val panguText = text?.let { PanguText.format(resources, textSize, it) }
        super.setText(panguText, type)
    }
}
```

::: warning

`TextView` 在注入 `PanguText` 后，如果你在 XML 布局中使用了 `android:singleLine="true"` 或在代码中使用了 `TextView.setSingleLine(true)` 并且配合 `android:elipsize="..."`，
那么这种方式设置单行文本可能会造成文本超出屏幕后其中会中显示出无法解析的 `OBJ` 字符 (被省略号截断)，因为 `TextView` 不会在测量时计算文本中的 `Span`，这会导致文本宽度计算错误。
解决方案为在 XML 布局中使用 `android:maxLines="1"` 或在代码中使用 `TextView.setMaxLines(1)` 来代替。

> 示例如下

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

### 个性化配置

`PanguText` 支持个性化配置，你可以使用全局静态实例 `PanguText.globalConfig` 获取全局配置，或单独进行配置。

> 示例如下

```kotlin
// 获取全局配置
val config = PanguText.globalConfig
// 开关，禁用将使所有功能失效
config.isEnabled = true
// 处理 Spanned 文本
// Spanned 文本处理默认启用，但此功能尚处于实验性阶段，
// 如果发生问题你可以选择禁用，禁用后遇到 Spanned 文本将返回原始文本
config.isProcessedSpanned = true
// 是否要在处理后自动重新测量文本宽度
// 注意：[PanguText] 注入文本并更改文本后，[TextView] 的宽度将不会自动计算
// 目前，此功能将调用 [TextView.setText] 重新执行测量结果，
// 该测量可以在某些动态布局 (例如 `RecyclerView`) 中每次修复文本宽度，
// 但可能会导致性能问题，你可以选择禁用此功能
// 为了防止不必要的性能开销，此功能仅在 `maxlines` 设置为 1 或 `singleLine` 的 [TextView] 上生效
config.isAutoRemeasureText = true
// 设置在格式化过程中以正则形式定义需要排除的内容
// 例如排除全部 URL
config.excludePatterns.add("https?://\\S+".toRegex())
// 例如排除类似 "[doge]" 的 emoji 占位符，
// 如果你需要使用 [ImageSpan] 显示 emoji 图片，你可以选择排除这些占位符
config.excludePatterns.add("\\[.*?]".toRegex())
// 设置 CJK 空白占位间距比例
// 这会决定最终的排版效果，建议保持默认比例，然后再以此跟随个人喜好进行调整
config.cjkSpacingRatio = 7f
```

::: warning

如果你使用了 [注入布局装载器](#注入布局装载器-layoutinflater) 的方案进行集成，请在 `PanguTextFactory2.inject(...)` 执行前配置 `PanguText.globalConfig`，否则配置将无法生效。

:::

你还可以在手动注入或格式化文本时传入 `config` 参数以进行个性化配置。

> 示例如下

```kotlin
// 假设这就是你的 TextView
val textView: TextView
// 创建一个新配置
// 你可以设置 [copyFromGlobal] 为 false 来不从全局配置中复制配置
val config = PanguTextConfig(copyFromGlobal = false) {
    excludePatterns.add("https?://\\S+".toRegex())
    excludePatterns.add("\\[.*?]".toRegex())
    cjkSpacingRatio = 7f
}
// 你还可以从任意一个配置中复制并创建新配置
val config2 = config.copy {
    excludePatterns.clear()
    excludePatterns.add("https?://\\S+".toRegex())
    excludePatterns.add("\\[.*?]".toRegex())
    cjkSpacingRatio = 7f
}
// 手动注入并配置
textView.injectPanguText(config = config2)
```

如果你使用了 [注入布局装载器](#注入布局装载器-layoutinflater) 的方案进行集成，你可以在 `TextView`、`EditText` 或继承于它们的 XML 布局声明中使用以下属性来进行个性化配置。

- `panguText_enabled` 对应 `PanguTextConfig.isEnabled`
- `panguText_processedSpanned` 对应 `PanguTextConfig.isProcessedSpanned`
- `panguText_autoRemeasureText` 对应 `PanguTextConfig.isAutoRemeasureText`
- `panguText_excludePatterns` 对应 `PanguTextConfig.excludePatterns`，字符串数组，多个使用 `|@|` 分隔
- `panguText_cjkSpacingRatio` 对应 `PanguTextConfig.cjkSpacingRatio`

> 示例如下

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

由于 Android Studio 的问题，上述属性可能不会有补全提示，请自行补全。

不要忘记加入声明 `xmlns:app="http://schemas.android.com/apk/res-auto"`。

:::

在自定义 `View` 中，你可以将你的 `View` 继承于 `PanguTextView` 接口以同样实现上述功能，此功能对 [使用修补工具](#使用修补工具) 方案同样有效。

> 示例如下

```kotlin
class MyTextView(context: Context, attrs: AttributeSet? = null) : AppCompatTextView(context, attrs),
    PanguTextView {

    override fun configurePanguText(config: PanguTextConfig) {
        // 配置你的 [PanguTextConfig]
    }
}
```

::: warning

`PanguTextView` 接口的优先级将高于直接在 XML 布局中使用的属性，如果你同时使用了这两种方式进行配置，`PanguTextView` 接口的配置将覆盖 XML 布局中的配置。

单独配置将覆盖全局配置，未配置的选项将跟随全局配置。

:::