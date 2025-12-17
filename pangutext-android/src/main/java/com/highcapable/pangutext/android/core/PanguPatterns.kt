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
 * This file is created by fankes on 2025/1/20.
 */
@file:Suppress("RegExpRedundantEscape", "RegExpSimplifiable", "CanConvertToMultiDollarString", "CanUnescapeDollarLiteral")

package com.highcapable.pangutext.android.core

import com.highcapable.pangutext.android.PanguText
import com.highcapable.pangutext.android.extension.replaceAndPreserveSpans

/**
 * The regular expression patterns for [PanguText].
 *
 * Some schemes are copied from [Pangu.java](https://github.com/vinta/pangu.java/blob/master/src/main/java/ws/vinta/pangu/Pangu.java),
 * and some modifications have been made to adapt to the Android environment.
 */
internal object PanguPatterns {

    private const val CJK = "\u2e80-\u2eff\u2f00-\u2fdf\u3040-\u309f\u30a0-\u30fa\u30fc-" +
        "\u30ff\u3100-\u312f\u3200-\u32ff\u3400-\u4dbf\u4e00-\u9fff\uf900-\ufaff"

    private val ANY_CJK = "[$CJK]".toRegex()

    private val DOTS_CJK = "([\\.]{2,}|\\u2026)([$CJK])".toRegex()

    private val FIX_CJK_COLON_ANS = "([$CJK])\\:([A-Z0-9\\(\\)])".toRegex()
    private val CJK_QUOTE = "([$CJK])([\\`\"\\u05f4])".toRegex()

    private val QUOTE_CJK = "([\\`\"\\u05f4])([$CJK])".toRegex()
    private val FIX_QUOTE_ANY_QUOTE = "([`\"\\u05f4]+)[ ]*(.+?)[ ]*([`\"\\u05f4]+)".toRegex()
    private val CJK_SINGLE_QUOTE_BUT_POSSESSIVE = "([$CJK])('[^s])".toRegex()

    private val SINGLE_QUOTE_CJK = "(')([$CJK])".toRegex()
    private val HASH_ANS_CJK_HASH = "([$CJK])(#)([$CJK]+)(#)([$CJK])".toRegex()

    private val CJK_HASH = "([$CJK])(#([^ ]))".toRegex()
    private val HASH_CJK = "(([^ ])#)([$CJK])".toRegex()
    private val CJK_OPERATOR_ANS = "([$CJK])([\\+\\-\\*\\/=&\\|<>])([A-Za-z0-9])".toRegex()

    private val ANS_OPERATOR_CJK = "([A-Za-z0-9])([\\+\\-\\*\\/=&\\|<>])([$CJK])".toRegex()
    private val FIX_SLASH_AS = "([/]) ([a-z\\-\\_\\./]+)".toRegex()

    private val FIX_SLASH_AS_SLASH = "([/\\.])([A-Za-z\\-\\_\\./]+) ([/])".toRegex()
    private val CJK_LEFT_BRACKET = "([$CJK])([\\(\\[\\{<>\\u201c])".toRegex()

    private val RIGHT_BRACKET_CJK = "([\\)\\]\\}>\\u201d])([$CJK])".toRegex()
    private val FIX_LEFT_BRACKET_ANY_RIGHT_BRACKET = "([\\(\\[\\{<\\u201c]+)[ ]*(.+?)[ ]*([\\)\\]\\}>\u201d]+)".toRegex()
    private val AN_LEFT_BRACKET = "([A-Za-z0-9])([\\(\\[\\{])".toRegex()

    private val RIGHT_BRACKET_AN = "([\\)\\]\\}])([A-Za-z0-9])".toRegex()
    private val CJK_ANS = ("([$CJK])([A-Za-z\\u0370-\\u03ff0-9@\$%\\^&\\*\\-\\+\\\\=\\|" +
        "/\\u00a1-\\u00ff\\u2150-\\u218f\\u2700—\\u27bf])").toRegex()

    private val ANS_CJK = ("([A-Za-z\\u0370-\\u03ff0-9~\\\$%\\^&\\*\\-\\+\\\\=\\|" +
        "/!;:,\\.\\?\\u00a1-\\u00ff\\u2150-\\u218f\\u2700—\\u27bf])([$CJK])").toRegex()
    private val S_A = "(%)([A-Za-z])".toRegex()

    /**
     * Match and replace the text with the given regular expression.
     * @param text text to be formatted.
     * @param whiteSpace the spacing character.
     * @param excludePatterns the regular expression to exclude from replacement.
     * @return [CharSequence]
     */
    internal fun matchAndReplace(text: CharSequence, whiteSpace: Char, vararg excludePatterns: Regex) =
        if (ANY_CJK.containsMatchIn(text))
            text.replaceAndPreserveSpans(DOTS_CJK, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(FIX_CJK_COLON_ANS, "$1:$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(CJK_QUOTE, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(QUOTE_CJK, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(FIX_QUOTE_ANY_QUOTE, "$1$2$3", *excludePatterns)
                .replaceAndPreserveSpans(CJK_SINGLE_QUOTE_BUT_POSSESSIVE, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(SINGLE_QUOTE_CJK, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(HASH_ANS_CJK_HASH, "$1$whiteSpace$2$3$4$whiteSpace$5", *excludePatterns)
                .replaceAndPreserveSpans(CJK_HASH, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(HASH_CJK, "$1$whiteSpace$3", *excludePatterns)
                .replaceAndPreserveSpans(CJK_OPERATOR_ANS, "$1$whiteSpace$2$whiteSpace$3", *excludePatterns)
                .replaceAndPreserveSpans(ANS_OPERATOR_CJK, "$1$whiteSpace$2$whiteSpace$3", *excludePatterns)
                .replaceAndPreserveSpans(FIX_SLASH_AS, "$1$2", *excludePatterns)
                .replaceAndPreserveSpans(FIX_SLASH_AS_SLASH, "$1$2$3", *excludePatterns)
                .replaceAndPreserveSpans(CJK_LEFT_BRACKET, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(RIGHT_BRACKET_CJK, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(FIX_LEFT_BRACKET_ANY_RIGHT_BRACKET, "$1$2$3", *excludePatterns)
                .replaceAndPreserveSpans(AN_LEFT_BRACKET, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(RIGHT_BRACKET_AN, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(CJK_ANS, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(ANS_CJK, "$1$whiteSpace$2", *excludePatterns)
                .replaceAndPreserveSpans(S_A, "$1$whiteSpace$2", *excludePatterns)
        else text
}