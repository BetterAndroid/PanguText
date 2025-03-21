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
 * This file is created by fankes on 2025/1/26.
 */
package com.highcapable.pangutext.android.core

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.highcapable.betterandroid.system.extension.tool.SystemVersion
import com.highcapable.pangutext.android.PanguText
import com.highcapable.pangutext.android.PanguTextConfig
import com.highcapable.pangutext.android.extension.injectRealTimePanguText
import com.highcapable.yukireflection.factory.current

/**
 * A [TextWatcher] that automatically applies [PanguText] to the text content.
 *
 * You don't need to create it manually, use [TextView.injectRealTimePanguText] instead.
 * @param base the base [TextView].
 * @param config the configuration of [PanguText].
 */
class PanguTextWatcher internal constructor(private val base: TextView, private val config: PanguTextConfig) : TextWatcher {

    /**
     * The text watchers of the base [TextView].
     * @return [ArrayList]<[TextWatcher]>.
     */
    private val textWatchers 
        get() = base.current(ignored = true).field { 
            name = "mListeners"
            superClass()
        }.cast<ArrayList<TextWatcher>>()

    /**
     * Whether to automatically re-measure the text width after processing.
     * @return [Boolean]
     */
    private val isAutoRemeasureText 
        get() = config.isAutoRemeasureText && base !is EditText && (base.maxLines == 1 ||
            SystemVersion.require(SystemVersion.Q, base.maxLines == 1) { base.isSingleLine })

    override fun afterTextChanged(editable: Editable?) {
        editable?.let { PanguText.format(base.resources, base.textSize, it, config) }
        if (!isAutoRemeasureText) return
        val currentWatchers = mutableListOf<TextWatcher>()
        textWatchers?.also {
            currentWatchers.addAll(it)
            // Avoid triggering events again during processing.
            it.clear()
        }
        // Reset the text to trigger remeasurement.
        base.text = editable
        // Re-add to continue listening to text changes.
        textWatchers?.addAll(currentWatchers)
        currentWatchers.clear()
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}