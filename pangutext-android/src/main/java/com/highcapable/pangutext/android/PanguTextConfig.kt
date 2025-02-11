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
 * This file is created by fankes on 2025/2/6.
 */
package com.highcapable.pangutext.android

import android.text.Spanned
import android.widget.TextView
import java.io.Serializable

/**
 * The [PanguText] configuration.
 */
class PanguTextConfig internal constructor() : Serializable {

    private companion object {

        /**
         * The default CJK spacing ratio, adjusted to 7f.
         * This ratio is considered to be the most comfortable size for reading after a series of comparisons.
         */
        private const val DEFAULT_CJK_SPACING_RATIO = 7f
    }

    /**
     * Enable the [PanguText].
     *
     * This is a global switch that can be used to enable or disable the [PanguText] processor.
     */
    var isEnabled = true

    /**
     * Processed [Spanned] text (experimental).
     *
     * - Note: This feature is in experimental stage and may not be fully supported,
     *   if the text is not processed correctly, please disable this feature.
     */
    var isProcessedSpanned = true

    /**
     * Whether to automatically re-measure the text width after processing.
     *
     * - Note: [PanguText] after injecting text and changing the text,
     *   the width of [TextView] will not be calculated automatically.
     *   At this time, this feature will call [TextView.setText] to re-execute the measurements,
     *   which can fix every time in some dynamic layouts (such as `RecyclerView`) changes in text width,
     *   but may cause performance issues, you can choose to disable this feature.
     *   To prevent unnecessary performance overhead, this feature only takes effect on [TextView] with `maxLines` set to 1 or `singleLine`.
     */
    var isAutoRemeasureText = true

    /**
     * The regular expression for text content that needs to be excluded.
     * [PanguText] processing will be skipped after matching these texts.
     *
     * Usage:
     *
     * ```kotlin
     * val config: PanguTextConfig
     * // Exclude all URLs.
     * config.excludePatterns.add("https?://\\S+".toRegex())
     * // Exclude emoji symbol placeholder like "[doge]".
     * config.excludePatterns.add("\\[.*?]".toRegex())
     * ```
     */
    val excludePatterns = mutableSetOf<Regex>()

    /**
     * The CJK spacing ratio, default is [DEFAULT_CJK_SPACING_RATIO].
     *
     * The larger the value, the smaller the spacing, and cannot be less than 0.1f.
     *
     * It is recommended to adjust with caution, it will only affect the spacing of CJK characters.
     */
    var cjkSpacingRatio = DEFAULT_CJK_SPACING_RATIO

    /**
     * Copy the current configuration.
     * @param body the configuration body.
     * @return [PanguTextConfig]
     */
    @JvmOverloads
    fun copy(body: PanguTextConfig.() -> Unit = {}) = PanguTextConfig().also {
        it.isEnabled = this.isEnabled
        it.isProcessedSpanned = this.isProcessedSpanned
        it.isAutoRemeasureText = this.isAutoRemeasureText
        it.excludePatterns.addAll(this.excludePatterns)
        it.cjkSpacingRatio = this.cjkSpacingRatio
        it.body()
    }
}