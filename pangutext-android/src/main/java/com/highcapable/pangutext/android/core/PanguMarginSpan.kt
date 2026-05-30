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
import android.graphics.Color
import android.graphics.Paint
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.MetricAffectingSpan
import android.text.style.ReplacementSpan
import androidx.annotation.Px
import androidx.core.graphics.withTranslation
import androidx.core.text.getSpans
import com.highcapable.betterandroid.ui.extension.component.base.toDp
import com.highcapable.betterandroid.ui.extension.component.base.toPx
import kotlin.math.round

/**
 * Pangu span with margin.
 * @param margin the margin size (px).
 */
internal class PanguMarginSpan(@field:Px val margin: Int) : ReplacementSpan() {

    companion object {

        private const val DECORATION_SPACER = " "

        /**
         * Create a new instance of [PanguMarginSpan].
         * @param resources the current resources.
         * @param textSize the text size (px).
         * @param ratio the CJK spacing ratio.
         * @return [PanguMarginSpan]
         */
        fun create(resources: Resources, @Px textSize: Float, ratio: Float) =
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
        (paint.buildStyledPaint(text, start, end, includeMeasureState = true).measureText(text, start, end) + margin).toInt()

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val workingPaint = paint.buildStyledPaint(text, start, end, includeMeasureState = false)
        val textWidth = text?.let { workingPaint.measureText(it, start, end) } ?: 0f

        if (workingPaint is TextPaint && workingPaint.bgColor != Color.TRANSPARENT) {
            val originalColor = workingPaint.color
            workingPaint.color = workingPaint.bgColor
            canvas.drawRect(x, top.toFloat(), x + textWidth + margin, bottom.toFloat(), workingPaint)
            workingPaint.color = originalColor
        }

        text?.let { canvas.drawText(it, start, end, x, y.toFloat(), workingPaint) }
        drawDecorationSpacer(canvas, x + textWidth, y.toFloat(), workingPaint)
    }

    /**
     * Build a styled paint for the current range.
     * @receiver [Paint]
     * @param text the current text.
     * @param start the range start.
     * @param end the range end.
     * @param includeMeasureState whether to include measure-affecting styles.
     * @return [Paint]
     */
    private fun Paint.buildStyledPaint(text: CharSequence?, start: Int, end: Int, includeMeasureState: Boolean): Paint {
        val workingPaint = if (this is TextPaint) TextPaint(this) else Paint(this)
        if (text !is Spanned || workingPaint !is TextPaint) return workingPaint

        text.getSpans<Any>(start, end).forEach { span ->
            if (span === this@PanguMarginSpan || span is ReplacementSpan) return@forEach

            if (includeMeasureState && span is MetricAffectingSpan) span.updateMeasureState(workingPaint)
            if (span is CharacterStyle) span.updateDrawState(workingPaint)
        }

        return workingPaint
    }

    /**
     * Draw the decoration spacer in the reserved margin.
     *
     * The scaled space keeps underline and strikethrough continuous without changing text content.
     * @param canvas the current canvas.
     * @param x the drawing start x.
     * @param y the drawing baseline y.
     * @param paint the styled paint.
     */
    private fun drawDecorationSpacer(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        if (margin <= 0 || paint !is TextPaint || (!paint.isUnderlineText && !paint.isStrikeThruText)) return

        val spaceWidth = paint.measureText(DECORATION_SPACER)
        if (spaceWidth <= 0f) return

        canvas.withTranslation(x, 0f) {
            scale(margin / spaceWidth, 1f)
            drawText(DECORATION_SPACER, 0f, y, paint)
        }
    }
}