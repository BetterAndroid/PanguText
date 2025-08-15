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
 * This file is created by fankes on 2025/8/15.
 */
package com.highcapable.pangutext.android.core

import android.text.TextWatcher
import android.widget.TextView
import com.highcapable.kavaref.KavaRef.Companion.resolve

/**
 * A delegate for [TextView] to manage its text watchers.
 * @param instance the [TextView] instance.
 */
internal class TextViewDelegate private constructor(private val instance: TextView) {

    companion object {

        private val mListeners by lazy {
            TextView::class.resolve()
                .optional(silent = true)
                .firstFieldOrNull { name = "mListeners" }
        }

        /**
         * Create the [TextViewDelegate] for the given [TextView] instance.
         * @return [TextViewDelegate]
         */
        val TextView.delegate get() = TextViewDelegate(this)
    }

    /**
     * The text watchers of the [TextView].
     * @return [ArrayList]<[TextWatcher]>.
     */
    private val textWatchers
        get() = mListeners?.copy()?.of(instance)?.getQuietly<ArrayList<TextWatcher>>()

    /**
     * Execute the given action without triggering text watchers.
     * @param action the action to execute without triggering text watchers.
     */
    inline fun withoutTextWatchers(action: () -> Unit) {
        val currentWatchers = mutableListOf<TextWatcher>()
        textWatchers?.also {
            currentWatchers.addAll(it)
            // Avoid triggering events again during processing.
            it.clear()
        }

        // Run action.
        action()
        // Re-add to continue listening to text changes.
        textWatchers?.addAll(currentWatchers)

        currentWatchers.clear()
    }
}