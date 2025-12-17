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
 * This file is created by fankes on 2025/1/12.
 */
@file:Suppress("unused")
@file:JvmName("PanguTextUtils")

package com.highcapable.pangutext.android.extension

import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import com.highcapable.betterandroid.ui.extension.view.getTag
import com.highcapable.pangutext.android.PanguText
import com.highcapable.pangutext.android.PanguTextConfig
import com.highcapable.pangutext.android.R
import com.highcapable.pangutext.android.core.PanguTextWatcher
import com.highcapable.pangutext.android.core.TextViewDelegate.Companion.delegate

/**
 * Create a new instance of [PanguTextConfig].
 * @see PanguTextConfig
 * @param copyFromGlobal whether to copy the [PanguText.globalConfig], default is true.
 * @param body the configuration body.
 * @return [PanguTextConfig]
 */
@JvmOverloads
fun PanguTextConfig(copyFromGlobal: Boolean = true, body: PanguTextConfig.() -> Unit) = 
    if (copyFromGlobal) PanguText.globalConfig.copy(body) else PanguTextConfig().apply(body)

/**
 * Inject [PanguText] to the current text content once.
 * @see TextView.setTextWithPangu
 * @see TextView.setHintWithPangu
 * @see PanguText.format
 * @receiver [TextView]
 * @param injectHint whether to apply [TextView.setHint], default is true.
 * @param config the configuration of [PanguText].
 */
@JvmOverloads
fun TextView.injectPanguText(injectHint: Boolean = true, config: PanguTextConfig = PanguText.globalConfig) {
    if (!config.isEnabled) return
    
    this.delegate.withoutTextWatchers {
        setTextWithPangu(this.text, config)
        if (injectHint) setHintWithPangu(this.hint, config)
    }
}

/**
 * Inject [PanguText] to the current text content in real time.
 *
 * - Note: Repeated inject will be ignored unless the current [TextView] is detached.
 * @see TextView.setTextWithPangu
 * @see TextView.setHintWithPangu
 * @see PanguText.format
 * @receiver [TextView]
 * @param injectHint whether to apply [TextView.setHint], default is true.
 * @param config the configuration of [PanguText].
 */
@JvmOverloads
fun TextView.injectRealTimePanguText(injectHint: Boolean = true, config: PanguTextConfig = PanguText.globalConfig) {
    if (!config.isEnabled) return

    val observerKey = R.id.tag_inject_real_time_pangu_text
    val isRepeated = getTag<Boolean>(observerKey) == true

    // It will no longer be executed if it exceeds one time.
    if (isRepeated) return

    injectPanguText(injectHint, config)

    var currentHint = this.hint
    val textWatcher = PanguTextWatcher(base = this, config)
    val listener = ViewTreeObserver.OnGlobalLayoutListener {
        val self = this@injectRealTimePanguText
        if (self.hint != currentHint)
            self.setHintWithPangu(self.hint, config)

        @Suppress("AssignedValueIsNeverRead")
        currentHint = self.hint
    }

    setTag(observerKey, true)
    doOnAttach {
        addTextChangedListener(textWatcher)

        // Add a global layout listener to monitor the hint text changes.
        if (injectHint) viewTreeObserver?.addOnGlobalLayoutListener(listener)

        doOnDetach {
            removeTextChangedListener(textWatcher)

            // Remove the global layout listener when the view is detached.
            if (injectHint) viewTreeObserver?.removeOnGlobalLayoutListener(listener)
            setTag(observerKey, false)
        }
    }
}

/**
 * Use [PanguText.format] to format the text content.
 * @see PanguText.format
 * @receiver [TextView]
 * @param text the text content.
 * @param config the configuration of [PanguText].
 */
@JvmOverloads
fun TextView.setTextWithPangu(text: CharSequence?, config: PanguTextConfig = PanguText.globalConfig) {
    if (!config.isEnabled) return

    this.text = text?.let {
        PanguText.format(resources, textSize, it, config)
    }
}

/**
 * Use [PanguText.format] to format the hint text content.
 * @see PanguText.format
 * @receiver [TextView]
 * @param text the text content.
 * @param config the configuration of [PanguText].
 */
@JvmOverloads
fun TextView.setHintWithPangu(text: CharSequence?, config: PanguTextConfig = PanguText.globalConfig) {
    if (!config.isEnabled) return

    this.hint = text?.let {
        PanguText.format(resources, textSize, it, config)
    }
}