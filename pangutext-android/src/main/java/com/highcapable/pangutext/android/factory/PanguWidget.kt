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
import androidx.core.view.doOnAttach
import com.highcapable.betterandroid.ui.extension.component.base.getBooleanOrNull
import com.highcapable.betterandroid.ui.extension.component.base.getFloatOrNull
import com.highcapable.betterandroid.ui.extension.component.base.getStringOrNull
import com.highcapable.betterandroid.ui.extension.component.base.obtainStyledAttributes
import com.highcapable.pangutext.android.PanguText
import com.highcapable.pangutext.android.PanguTextConfig
import com.highcapable.pangutext.android.R
import com.highcapable.pangutext.android.core.PanguTextView
import com.highcapable.pangutext.android.extension.injectPanguText
import com.highcapable.pangutext.android.extension.injectRealTimePanguText
import com.highcapable.pangutext.android.generated.PangutextAndroidProperties
import com.highcapable.yukireflection.factory.classOf
import com.highcapable.yukireflection.factory.constructor
import com.highcapable.yukireflection.factory.notExtends
import com.highcapable.yukireflection.factory.toClassOrNull
import com.highcapable.yukireflection.type.android.AttributeSetClass
import com.highcapable.yukireflection.type.android.ContextClass

/**
 * A widgets processor that automatically applies [PanguText] to the text content.
 */
internal object PanguWidget {

    /** The text regex split symbol. */
    private const val TEXT_REGEX_SPLITE_SYMBOL = "|@|"

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
            if (viewClass notExtends classOf<TextView>()) return null
            val twoParams = viewClass.constructor {
                param(ContextClass, AttributeSetClass)
            }.ignored().get()
            val onceParam = viewClass.constructor {
                param(ContextClass)
            }.ignored().get()
            // Catching when the attrs value initialization failed.
            runCatching { twoParams.newInstance<View>(context, attrs) }.onFailure {
                Log.w(PangutextAndroidProperties.PROJECT_NAME, "Failed to create instance of $viewClass using (Context, AttributeSet).", it)
            }.getOrNull()
                // Try to initialize with the default constructor again, otherwise return null.
                ?: runCatching { onceParam.newInstance<View>(context) }.onFailure { 
                    Log.w(PangutextAndroidProperties.PROJECT_NAME, "Failed to create instance of $viewClass, this process will be ignored.", it)
                }.getOrNull()
        }
        // Ignore if the instance is not a [TextView].
        if (instance !is TextView) return null
        var config = PanguText.globalConfig
        if (instance is PanguTextView) {
            val configCopy = config.copy()
            instance.configurePanguText(configCopy)
            config = configCopy
            if (!config.isEnabled) return instance
        } else instance.obtainStyledAttributes(attrs, R.styleable.PanguTextHelper) {
            val isEnabled = it.getBooleanOrNull(R.styleable.PanguTextHelper_panguText_enabled)
            val isProcessedSpanned = it.getBooleanOrNull(R.styleable.PanguTextHelper_panguText_processedSpanned)
            val isAutoRemeasureText = it.getBooleanOrNull(R.styleable.PanguTextHelper_panguText_autoRemeasureText)
            val cjkSpacingRatio = it.getFloatOrNull(R.styleable.PanguTextHelper_panguText_cjkSpacingRatio)
            val excludePatterns = it.getStringOrNull(R.styleable.PanguTextHelper_panguText_excludePatterns)
                ?.split(TEXT_REGEX_SPLITE_SYMBOL)?.mapNotNull { regex -> 
                    runCatching { regex.toRegex() }.onFailure { th ->
                        Log.e(PangutextAndroidProperties.PROJECT_NAME, "Invalid exclude pattern of $instance: $regex", th)
                    }.getOrNull()
                }?.toTypedArray() ?: emptyArray()
            if (isEnabled == false) return instance
            if (isProcessedSpanned != null || isAutoRemeasureText != null || cjkSpacingRatio != null || excludePatterns.isNotEmpty()) {
                val configCopy = config.copy()
                configCopy.isProcessedSpanned = isProcessedSpanned ?: config.isProcessedSpanned
                configCopy.isAutoRemeasureText = isAutoRemeasureText ?: config.isAutoRemeasureText
                configCopy.cjkSpacingRatio = cjkSpacingRatio ?: config.cjkSpacingRatio
                if (excludePatterns.isNotEmpty()) {
                    config.excludePatterns.clear()
                    config.excludePatterns.addAll(excludePatterns)
                }; config = configCopy
            }
        }
        when (instance.javaClass.name) {
            // Specialize those components because loading "hint" style after [doOnAttachRepeatable] causes problems.
            "com.google.android.material.textfield.TextInputEditText",
            "com.google.android.material.textfield.MaterialAutoCompleteTextView" -> {
                instance.injectPanguText(config = config)
                instance.doOnAttachRepeatable(config) { it.injectRealTimePanguText(injectHint = false, config) }
            }
            else -> instance.doOnAttachRepeatable(config) {
                it.injectRealTimePanguText(config = config)
            }
        }; return instance
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