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
 * This file is created by fankes on 2026/5/30.
 */
package com.highcapable.pangutext.android.core

/**
 * Scan the text and find the character indexes that need to apply [PanguMarginSpan].
 *
 * This scanner only cares about direct spacing boundaries and does not perform any text-fixing behavior.
 */
internal object PanguScanner {

    private const val HASH_ROLE_OPENING = 1
    private const val HASH_ROLE_CLOSING = 2

    /**
     * Find the character indexes that need to add [PanguMarginSpan].
     * @param text text to scan.
     * @param excludePatterns the regular expression to exclude from scan.
     * @return [IntArray]
     */
    fun findSpacingIndexes(text: CharSequence, vararg excludePatterns: Regex): IntArray {
        if (text.length <= 1 || !text.containsAnyCjkAndSpacingCandidate()) return intArrayOf()

        val excludedIndexes = buildExcludedIndexes(text, excludePatterns)
        val hashRoles = buildHashRoles(text)
        val indexes = mutableListOf<Int>()

        (0 until text.lastIndex).forEach { index ->
            if (excludedIndexes[index] || excludedIndexes[index + 1]) return@forEach
            if (text.shouldAddSpacingAfter(index, hashRoles)) indexes += index
        }

        return indexes.toIntArray()
    }

    /**
     * Build the excluded character indexes from [excludePatterns].
     * @param text text to scan.
     * @param excludePatterns the regular expression to exclude from scan.
     * @return [BooleanArray]
     */
    private fun buildExcludedIndexes(text: CharSequence, excludePatterns: Array<out Regex>): BooleanArray {
        if (excludePatterns.isEmpty()) return BooleanArray(text.length)

        val excludedIndexes = BooleanArray(text.length)

        excludePatterns.forEach { pattern ->
            val matcher = pattern.toPattern().matcher(text)

            while (matcher.find())
                for (index in matcher.start() until matcher.end())
                    if (index in excludedIndexes.indices) excludedIndexes[index] = true
        }

        return excludedIndexes
    }

    /**
     * Build the hash roles for wrapped CJK tags like `中#话题#中`.
     * @param text text to scan.
     * @return [IntArray]
     */
    private fun buildHashRoles(text: CharSequence): IntArray {
        val roles = IntArray(text.length)

        (0 until text.lastIndex).forEach { index ->
            if (!text[index].isCjk() || text[index + 1] != '#') return@forEach

            var current = index + 2
            while (current < text.length && text[current].isCjk()) current++

            if (current <= index + 2 || current >= text.lastIndex || text[current] != '#' || !text[current + 1].isCjk()) return@forEach

            roles[index + 1] = HASH_ROLE_OPENING
            roles[current] = HASH_ROLE_CLOSING
        }

        return roles
    }

    /**
     * Check whether the current boundary needs spacing.
     * @receiver [CharSequence]
     * @param index the current character index.
     * @param hashRoles the hash roles.
     * @return [Boolean]
     */
    private fun CharSequence.shouldAddSpacingAfter(index: Int, hashRoles: IntArray): Boolean {
        val left = this[index]
        val right = this[index + 1]

        if (left == '%' && right.isAsciiLetter()) return true
        if (left == '…' && right.isCjk()) return true
        if (left.isQuote() && right.isCjk()) return true
        if (left == '\'' && right.isCjk()) return true
        if (left.isRightBracketForCjk() && right.isCjk()) return true
        if (left.isRightBracketForAnsi() && right.isAsciiLetterOrDigit()) return true
        if (left.isAsciiLetterOrDigit() && right.isOperator() && index + 2 < length && this[index + 2].isCjk()) return true
        if (left.isOperator() && right.isAsciiLetterOrDigit() && index > 0 && this[index - 1].isCjk()) return true
        if (left == '#' && right.isCjk() && index > 0 && !this[index - 1].isWhitespace()) return when (hashRoles[index]) {
            HASH_ROLE_OPENING -> false
            HASH_ROLE_CLOSING -> true
            else -> !this[index - 1].isCjk()
        }
        if (left.isAnsCjkChar() && right.isCjk()) return true
        if (left.isAsciiLetterOrDigit() && right.isLeftBracketForAnsi()) return true

        if (!left.isCjk()) return false

        if (right.isQuote()) return true
        if (right == '\'' && index + 2 < length && this[index + 2] != 's') return true
        if (right.isLeftBracketForCjk()) return true
        if (right == '#' && index + 2 < length && !this[index + 2].isWhitespace() && hashRoles[index + 1] != HASH_ROLE_CLOSING) return true

        return right.isCjkAnsChar()
    }

    /**
     * Check whether the text contains any CJK and spacing candidate.
     * @receiver [CharSequence]
     * @return [Boolean]
     */
    private fun CharSequence.containsAnyCjkAndSpacingCandidate(): Boolean {
        var hasCjk = false
        var hasSpacingCandidate = false

        forEach {
            if (!hasCjk && it.isCjk()) hasCjk = true
            if (!hasSpacingCandidate && it.isSpacingCandidate()) hasSpacingCandidate = true
            if (hasCjk && hasSpacingCandidate) return true
        }

        return false
    }

    /**
     * Check whether the current character is a spacing candidate.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isSpacingCandidate() =
        isCjkAnsChar() || isAnsCjkChar() || isQuote() || this == '\'' || this == '#' || this == '…' ||
            this == '@' || isLeftBracketForCjk() || isRightBracketForCjk() || isLeftBracketForAnsi() || isRightBracketForAnsi()

    /**
     * Check whether the current character is CJK.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isCjk(): Boolean {
        val code = code
        return code in 0x2E80..0x2EFF ||
            code in 0x2F00..0x2FDF ||
            code in 0x3040..0x309F ||
            code in 0x30A0..0x30FA ||
            code in 0x30FC..0x30FF ||
            code in 0x3100..0x312F ||
            code in 0x3200..0x32FF ||
            code in 0x3400..0x4DBF ||
            code in 0x4E00..0x9FFF ||
            code in 0xF900..0xFAFF
    }

    /**
     * Check whether the current character matches the original CJK-ANS right-side candidate.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isCjkAnsChar() =
        isAsciiLetterOrDigit() || isGreek() || isLatinSupplement() || isFraction() || isDingbat() ||
            this == '@' || this == '$' || this == '%' || this == '^' || this == '&' || this == '*' ||
            this == '-' || this == '+' || this == '\\' || this == '=' || this == '|' || this == '/'

    /**
     * Check whether the current character matches the original ANS-CJK left-side candidate.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isAnsCjkChar() =
        isAsciiLetterOrDigit() || isGreek() || isLatinSupplement() || isFraction() || isDingbat() ||
            this == '~' || this == '$' || this == '%' || this == '^' || this == '&' || this == '*' ||
            this == '-' || this == '+' || this == '\\' || this == '=' || this == '|' || this == '/' ||
            this == '!' || this == ';' || this == ':' || this == ',' || this == '.' || this == '?'

    /**
     * Check whether the current character is an ASCII letter.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isAsciiLetter() = this in 'a'..'z' || this in 'A'..'Z'

    /**
     * Check whether the current character is an ASCII letter or digit.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isAsciiLetterOrDigit() = isAsciiLetter() || this in '0'..'9'

    /**
     * Check whether the current character is an ASCII operator.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isOperator() = this == '+' || this == '-' || this == '*' ||
        this == '/' || this == '=' || this == '&' || this == '|' || this == '<' || this == '>'

    /**
     * Check whether the current character is a Greek letter.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isGreek() = code in 0x0370..0x03FF

    /**
     * Check whether the current character is a Latin supplement symbol.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isLatinSupplement() = code in 0x00A1..0x00FF

    /**
     * Check whether the current character is a fraction symbol.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isFraction() = code in 0x2150..0x218F

    /**
     * Check whether the current character is a dingbat symbol.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isDingbat() = code in 0x2700..0x27BF

    /**
     * Check whether the current character is a quote.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isQuote() = this == '`' || this == '"' || this == '\u05F4'

    /**
     * Check whether the current character is a left bracket for CJK boundaries.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isLeftBracketForCjk() = this == '(' || this == '[' || this == '{' || this == '<' || this == '\u201C'

    /**
     * Check whether the current character is a right bracket for CJK boundaries.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isRightBracketForCjk() = this == ')' || this == ']' || this == '}' || this == '>' || this == '\u201D'

    /**
     * Check whether the current character is a left bracket for ANSI boundaries.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isLeftBracketForAnsi() = this == '(' || this == '[' || this == '{'

    /**
     * Check whether the current character is a right bracket for ANSI boundaries.
     * @receiver [Char]
     * @return [Boolean]
     */
    private fun Char.isRightBracketForAnsi() = this == ')' || this == ']' || this == '}'
}