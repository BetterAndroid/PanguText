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
 * This file is created by fankes on 2025/3/4.
 */
@file:Suppress("unused")

package com.highcapable.pangutext.android.factory

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.highcapable.betterandroid.ui.extension.view.walkThroughChildren
import com.highcapable.pangutext.android.PanguText
import com.highcapable.pangutext.android.PanguTextConfig

/**
 * Patcher for [PanguText].
 */
object PanguTextPatcher {

    /**
     * Patch [PanguText] to the view.
     * @param view the view or view group.
     * @param config the configuration of [PanguText].
     */
    @JvmOverloads
    @JvmStatic
    fun patch(view: View, config: PanguTextConfig = PanguText.globalConfig) {
        when (view) {
            is TextView -> PanguWidget.startInjection(view, config = config)
            is ViewGroup ->
                view.walkThroughChildren()
                    .filterIsInstance<TextView>()
                    .forEach { PanguWidget.startInjection(it, config = config) }
        }
    }
}