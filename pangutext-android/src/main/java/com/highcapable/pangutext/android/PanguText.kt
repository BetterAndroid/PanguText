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
@file:Suppress("MemberVisibilityCanBePrivate")

package com.highcapable.pangutext.android

import android.content.res.Resources
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.CharacterStyle
import android.widget.TextView
import androidx.annotation.Px
import com.highcapable.kavaref.extension.classOf
import com.highcapable.pangutext.android.core.PanguMarginSpan
import com.highcapable.pangutext.android.core.PanguPatterns
import com.highcapable.pangutext.android.extension.injectPanguText
import com.highcapable.pangutext.android.extension.injectRealTimePanguText
import com.highcapable.pangutext.android.extension.setHintWithPangu
import com.highcapable.pangutext.android.extension.setTextWithPangu

/**
 * The library core of Pangu text processor.
 *
 * Bigger thanks for [this](https://github.com/vinta/pangu.java) project.
 * @see PanguPatterns
 */
object PanguText {

    /**
     * This is a placeholder character for replacing the content of the regular expression,
     * with no actual meaning.
     */
    private const val PH = '\u001C'

    /**
     * The global configuration of [PanguText].
     */
    val globalConfig = PanguTextConfig()

    /**
     * Use [PanguText] to format specified text.
     *
     * [PanguText] will automatically set [PanguMarginSpan] for some characters in
     * the text to achieve white space typesetting effect without actually inserting
     * any characters or changing the length of the original text.
     *
     * This function will insert a style for the current given [text] without actually changing the string position in the text.
     * If the current [text] is of type [Spannable], it will return the original unmodified object,
     * otherwise it will return the wrapped object [SpannableString] after.
     *
     * - Note: Processed [Spanned] text is in experimental stage and may not be fully supported,
     *   if the text is not processed correctly, please disable [PanguTextConfig.isProcessedSpanned].
     * @see PanguTextConfig.isProcessedSpanned
     * @see PanguTextConfig.cjkSpacingRatio
     * @see TextView.injectPanguText
     * @see TextView.injectRealTimePanguText
     * @see TextView.setTextWithPangu
     * @see TextView.setHintWithPangu
     * @param resources the current resources.
     * @param textSize the text size (px).
     * @param text text to be formatted.
     * @param config the configuration of [PanguText].
     * @return [CharSequence]
     */
    @JvmOverloads
    @JvmStatic
    fun format(resources: Resources, @Px textSize: Float, text: CharSequence, config: PanguTextConfig = globalConfig): CharSequence {
        if (!config.isEnabled) return text
        if (text.isBlank()) return text
        val formatted = format(text, PH, config)
        return text.applySpans(formatted, resources, textSize, config)
    }

    /**
     * Use [PanguText] to format the current text content.
     *
     * Using this function will add extra [whiteSpace] as character spacing to the text,
     * changing the length of the original text.
     *
     * - Note: Processed [Spanned] text is in experimental stage and may not be fully supported,
     *   if the text is not processed correctly, please disable [PanguTextConfig.isProcessedSpanned].
     * @see PanguTextConfig.isProcessedSpanned
     * @param text text to be formatted.
     * @param whiteSpace the spacing character, default is 'U+200A'.
     * @param config the configuration of [PanguText].
     * @return [CharSequence]
     */
    @JvmOverloads
    @JvmStatic
    fun format(text: CharSequence, whiteSpace: Char = ' ', config: PanguTextConfig = globalConfig): CharSequence {
        if (!config.isEnabled) return text
        // In any case, always perform a cleanup operation before accepting text.
        val processed = text.clearSpans()
        val patterns = config.excludePatterns.toTypedArray()
        return if ((config.isProcessedSpanned || text !is Spanned) && text.isNotBlank() && text.length > 1)
            PanguPatterns.matchAndReplace(processed, whiteSpace, *patterns)
        else processed
    }

    /**
     * Apply the [PanguMarginSpan] to the text.
     * @receiver [CharSequence]
     * @param formatted the formatted text.
     * @param resources the current resources.
     * @param textSize the text size (px).
     * @param config the configuration of [PanguText].
     * @param whiteSpace the spacing character, default is [PH].
     * @return [CharSequence]
     */
    private fun CharSequence.applySpans(
        formatted: CharSequence,
        resources: Resources,
        @Px textSize: Float,
        config: PanguTextConfig = globalConfig,
        whiteSpace: Char = PH
    ): CharSequence {
        val builder = SpannableStringBuilder(formatted)
        formatted.forEachIndexed { index, c ->
            // Add spacing to the previous character.
            if (c == whiteSpace && index in 0..formatted.lastIndex) {
                val span = PanguMarginSpan.Placeholder()
                builder.setSpan(span, index - 1, index, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
        // Delete the placeholder character.
        for (i in (builder.length - 1) downTo 0) {
            if (builder[i] == whiteSpace) builder.delete(i, i + 1)
        }
        // Find the [PanguMarginSpan.Placeholder] subscript in [builder] and use [PanguMarginSpan] to set it to [original].
        val builderSpans = builder.getSpans(0, builder.length, classOf<PanguMarginSpan.Placeholder>())
        val spannable = if (this !is Spannable) SpannableString(this) else this
        // Add new [PanguMarginSpan].
        builderSpans.forEach {
            val start = builder.getSpanStart(it)
            val end = builder.getSpanEnd(it)
            val span = PanguMarginSpan.create(resources, textSize, config.cjkSpacingRatio)
            spannable.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }; builder.clear()
        return spannable
    }

    /**
     * Clear the [PanguMarginSpan] from the text.
     *
     * Workaround for the issue that the [PanguMarginSpan] repeatedly sets
     * the same range causes performance degradation.
     * @receiver [CharSequence]
     * @return [CharSequence]
     */
    private fun CharSequence.clearSpans(): CharSequence {
        if (this !is Spannable || isBlank() || !hasSpan<PanguMarginSpan>()) return this
        getSpans(0, length, classOf<PanguMarginSpan>()).forEach { span ->
            val start = getSpanStart(span)
            val end = getSpanEnd(span) 
            // Clear the [PanguMarginSpan].
            if (start < length && end > 0) removeSpan(span)
        }; return this
    }

    /**
     * Check if the text contains a specific span [T].
     * @receiver [CharSequence]
     * @return [Boolean]
     */
    private inline fun <reified T : CharacterStyle> CharSequence.hasSpan(): Boolean {
        val spannable = this as? Spanned ?: return false
        val spans = spannable.getSpans(0, spannable.length, classOf<T>())
        return spans.isNotEmpty()
    }
}