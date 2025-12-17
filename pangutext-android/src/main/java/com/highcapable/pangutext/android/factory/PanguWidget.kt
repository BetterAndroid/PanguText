/*
 * PanguText - A typographic solution for the optimal alignment of CJK characters, English words, and half-width digits.
 * Copyright (C) 2019 HighCapable
 * https://github.com/BetterAndroid/PanguText
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is created by fankes on 2025/1/19.
 */
package com.highcapable.pangutext.android.factory

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import androidx.core.view.doOnAttach
import com.highcapable.betterandroid.ui.extension.component.base.getBooleanOrNull
import com.highcapable.betterandroid.ui.extension.component.base.getFloatOrNull
import com.highcapable.betterandroid.ui.extension.component.base.getStringOrNull
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.kavaref.extension.isNotSubclassOf
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.pangutext.android.PanguText
import com.highcapable.pangutext.android.PanguTextConfig
import com.highcapable.pangutext.android.R
import com.highcapable.pangutext.android.core.PanguTextView
import com.highcapable.pangutext.android.extension.injectPanguText
import com.highcapable.pangutext.android.extension.injectRealTimePanguText
import com.highcapable.pangutext.android.generated.PangutextAndroidProperties

/**
 * A widgets processor that automatically applies [PanguText] to the text content.
 */
internal object PanguWidget {

    /** The text regex split symbol. */
    private const val TEXT_REGEX_SPLIT_SYMBOL = "|@|"

    /**
     * Process the widget by the given name.
     * @param name the widget name.
     * @param view the current view.
     * @param context the context.
     * @param attrs the attributes.
     * @return [View] or null.
     */
    fun process(name: String, view: View?, context: Context, attrs: AttributeSet): View? {
        val instance = view ?: name.let {
            // There will be commonly used view class names in the XML layout, which is converted here.
            if (!it.contains(".")) "android.widget.$it" else it
        }.toClassOrNull()?.let { viewClass ->
            // Avoid creating unnecessary components for waste.
            if (viewClass isNotSubclassOf classOf<TextView>()) return null

            val twoParams = viewClass.resolve()
                .optional(silent = true)
                .firstConstructorOrNull { parameters(Context::class, AttributeSet::class) }
            val onceParam = viewClass.resolve()
                .optional(silent = true)
                .firstConstructorOrNull { parameters(Context::class) }

            // Catching when the attrs value initialization failed.
            runCatching { twoParams?.create(context, attrs) }.onFailure {
                Log.w(PangutextAndroidProperties.PROJECT_NAME, "Failed to create instance of $viewClass using (Context, AttributeSet).", it)
            }.getOrNull()
                // Try to initialize with the default constructor again, otherwise return null.
                ?: runCatching { onceParam?.create(context) }.onFailure {
                    Log.w(PangutextAndroidProperties.PROJECT_NAME, "Failed to create instance of $viewClass, this process will be ignored.", it)
                }.getOrNull()
        }

        // Ignore if the instance is not a [TextView].
        if (instance !is TextView) return null

        return startInjection(instance, attrs)
    }

    /**
     * Start the injection of [PanguText] to the given [TextView].
     * @param instance the instance of [TextView].
     * @param attrs the attributes.
     * @param config the configuration of [PanguText].
     * @return [TV]
     */
    inline fun <reified TV : TextView> startInjection(
        instance: TV,
        attrs: AttributeSet? = null,
        config: PanguTextConfig = PanguText.globalConfig
    ): TV {
        var sConfig = config

        if (instance is PanguTextView) {
            val configCopy = sConfig.copy()

            instance.configurePanguText(configCopy)
            sConfig = configCopy

            if (!sConfig.isEnabled) return instance
        } else instance.context.withStyledAttributes(attrs, R.styleable.PanguTextHelper) {
            val isEnabled = getBooleanOrNull(R.styleable.PanguTextHelper_panguText_enabled)
            val isProcessedSpanned = getBooleanOrNull(R.styleable.PanguTextHelper_panguText_processedSpanned)
            val isAutoRemeasureText = getBooleanOrNull(R.styleable.PanguTextHelper_panguText_autoRemeasureText)
            val cjkSpacingRatio = getFloatOrNull(R.styleable.PanguTextHelper_panguText_cjkSpacingRatio)

            val excludePatterns = getStringOrNull(R.styleable.PanguTextHelper_panguText_excludePatterns)
                ?.split(TEXT_REGEX_SPLIT_SYMBOL)?.mapNotNull { regex ->
                    runCatching { regex.toRegex() }.onFailure { th ->
                        Log.e(PangutextAndroidProperties.PROJECT_NAME, "Invalid exclude pattern of $instance: $regex", th)
                    }.getOrNull()
                }?.toTypedArray() ?: emptyArray()

            if (isEnabled == false) return instance

            if (isProcessedSpanned != null || isAutoRemeasureText != null || cjkSpacingRatio != null || excludePatterns.isNotEmpty()) {
                val configCopy = sConfig.copy()

                configCopy.isProcessedSpanned = isProcessedSpanned ?: sConfig.isProcessedSpanned
                configCopy.isAutoRemeasureText = isAutoRemeasureText ?: sConfig.isAutoRemeasureText
                configCopy.cjkSpacingRatio = cjkSpacingRatio ?: sConfig.cjkSpacingRatio

                if (excludePatterns.isNotEmpty()) {
                    sConfig.excludePatterns.clear()
                    sConfig.excludePatterns.addAll(excludePatterns)
                }

                sConfig = configCopy
            }
        }

        when (instance.javaClass.name) {
            // Specialize those components because loading "hint" style after [doOnAttachRepeatable] causes problems.
            "com.google.android.material.textfield.TextInputEditText",
            "com.google.android.material.textfield.MaterialAutoCompleteTextView" -> {
                instance.injectPanguText(config = sConfig)
                instance.doOnAttachRepeatable(sConfig) { it.injectRealTimePanguText(injectHint = false, sConfig) }
            }
            else -> instance.doOnAttachRepeatable(sConfig) {
                it.injectRealTimePanguText(config = sConfig)
            }
        }

        return instance
    }

    /** Copied from [View.doOnAttach]. */
    private inline fun <reified V : View> V.doOnAttachRepeatable(config: PanguTextConfig, crossinline action: (view: V) -> Unit) {
        if (!config.isEnabled) return

        if (isAttachedToWindow) action(this)
        addOnAttachStateChangeListener(
            object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(view: View) {
                    // Re-execute it every time to prevent layout re-creation problems
                    // similar to [RecyclerView.Adapter] or [BaseAdapter] after reuse.
                    if (config.isEnabled) action(view as V)
                }
                override fun onViewDetachedFromWindow(view: View) {}
            }
        )
    }
}