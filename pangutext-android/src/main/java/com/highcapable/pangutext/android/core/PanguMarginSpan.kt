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
 * This file is created by fankes on 2025/1/14.
 */
package com.highcapable.pangutext.android.core

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Spanned
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ReplacementSpan
import androidx.annotation.Px
import androidx.core.text.getSpans
import com.highcapable.betterandroid.ui.extension.component.base.toDp
import com.highcapable.betterandroid.ui.extension.component.base.toPx
import kotlin.math.round

/**
 * Pangu span with margin.
 * @param margin the margin size (px).
 */
internal class PanguMarginSpan(@Px val margin: Int) : ReplacementSpan() {

    internal companion object {

        /**
         * Create a new instance of [PanguMarginSpan].
         * @param resources the current resources.
         * @param textSize the text size (px).
         * @param ratio the CJK spacing ratio.
         * @return [PanguMarginSpan]
         */
        internal fun create(resources: Resources, @Px textSize: Float, ratio: Float) =
            PanguMarginSpan(getSpanMargin(resources, textSize, ratio))

        /**
         * Get the margin size (px).
         * @param resources the current resources.
         * @param textSize the text size (px).
         * @param ratio the CJK spacing ratio.
         * @return [Int]
         */
        private fun getSpanMargin(resources: Resources, @Px textSize: Float, ratio: Float) =
            round(textSize.toDp(resources) / ratio.coerceAtLeast(0.1f)).toInt().toPx(resources)
    }

    override fun getContentDescription() = "PanguMarginSpan"

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?) =
        (paint.measureText(text, start, end) + margin).toInt()

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        if (text is Spanned) text.getSpans<Any>(start, end).forEach { span ->
            when {
                span is BackgroundColorSpan -> {
                    // Get background color.
                    val color = span.backgroundColor
                    val originalColor = paint.color
                    // Save the current [paint] color.
                    paint.color = color
                    // Get the width of the text.
                    val textWidth = paint.measureText(text, start, end)
                    // Draw background rectangle.
                    canvas.drawRect(x, top.toFloat(), x + textWidth + margin, bottom.toFloat(), paint)
                    // Restore original color.
                    paint.color = originalColor
                }
                span is CharacterStyle && paint is TextPaint -> span.updateDrawState(paint)
            }
        }; text?.let { canvas.drawText(it, start, end, x, y.toFloat(), paint) }
    }

    /**
     * A placeholder span.
     */
    class Placeholder : ReplacementSpan() {
        override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?) = 0
        override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {}
    }
}