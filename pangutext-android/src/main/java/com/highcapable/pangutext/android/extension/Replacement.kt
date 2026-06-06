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
 * This file is created by fankes on 2025/1/16.
 */
@file:JvmName("ReplacementUtils")

package com.highcapable.pangutext.android.extension

import android.text.SpannableStringBuilder
import android.util.Log
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.pangutext.android.generated.PanguTextProperties
import java.util.regex.Matcher

/**
 * The shared exclusion state for regex replacement.
 * @property ranges the excluded ranges.
 */
internal class ExcludedRanges private constructor(private val ranges: MutableList<IntRange>) {

    companion object {

        /**
         * Build a new [ExcludedRanges] from the current text.
         * @param text the original text.
         * @param excludePatterns the regular expression to exclude from replacement.
         * @return [ExcludedRanges]
         */
        fun from(text: CharSequence, excludePatterns: Array<out Regex>): ExcludedRanges {
            if (excludePatterns.isEmpty()) return ExcludedRanges(mutableListOf())

            val ranges = mutableListOf<IntRange>()

            excludePatterns.forEach { pattern ->
                val matcher = pattern.toPattern().matcher(text)

                while (matcher.find())
                    if (matcher.start() < matcher.end()) ranges += matcher.start() until matcher.end()
            }

            ranges.sortBy { it.first }
            return ExcludedRanges(ranges)
        }
    }

    /**
     * Check whether the given range is excluded.
     * @param start the range start.
     * @param end the range end.
     * @return [Boolean]
     */
    fun contains(start: Int, end: Int) = ranges.any { it.first <= start + 1 && it.last + 1 >= end - 1 }

    /**
     * Shift all ranges after the current replacement.
     * @param position the replacement end position before shifting.
     * @param delta the changed length delta.
     */
    fun shiftAfter(position: Int, delta: Int) {
        if (delta == 0 || ranges.isEmpty()) return

        ranges.indices.forEach { index ->
            val range = ranges[index]
            if (range.first >= position) ranges[index] = (range.first + delta) until (range.last + 1 + delta)
        }
    }
}

/**
 * Replace the text content and preserve the original span style.
 * @see CharSequence.replace
 * @receiver [CharSequence]
 * @param regex the regular expression.
 * @param replacement the replacement text.
 * @param excludeState the shared exclusion state.
 * @return [CharSequence]
 */
internal fun CharSequence.replaceAndPreserveSpans(regex: Regex, replacement: String, excludeState: ExcludedRanges) = runCatching {
    val builder = SpannableStringBuilder(this)
    val matcher = regex.toPattern().matcher(this)
    var offset = 0

    // Offset adjustment to account for changes in the text length after replacements.
    while (matcher.find()) {
        val start = matcher.start() + offset
        val end = matcher.end() + offset

        // Skip the replacement if the matched range is excluded.
        // The character range offset is adjusted by 1 to avoid the exclusion of the matched range.
        if (excludeState.contains(start, end)) continue

        // Perform the replacement.
        val replacementText = matcher.buildReplacementText(replacement)

        builder.replace(start, end, replacementText)

        val delta = replacementText.length - (end - start)
        excludeState.shiftAfter(end, delta)

        // Adjust offset based on the length of the replacement.
        offset += delta
    }

    builder
}.onFailure {
    Log.w(PanguTextProperties.PROJECT_NAME, "Failed to replace span text content.", it)
}.getOrNull() ?: this

/**
 * Build the replacement text based on the matched groups.
 * @receiver [Matcher]
 * @param replacement the replacement text.
 * @return [String]
 */
private fun Matcher.buildReplacementText(replacement: String): String {
    val matcher = this
    if ('$' !in replacement) return replacement

    var result = replacement

    // Check for group references (like $1, $2, ...).
    if ('$' in result)
        result = numberGroupReferenceRegex.replace(result) { matchResult ->
            val groupIndex = matchResult.groupValues[1].toInt()

            if (groupIndex <= matcher.groupCount())
                matcher.group(groupIndex) ?: ""
            else ""
        }

    // Check for named groups (like ${groupName}).
    if ($$"${" in result)
        result = namedGroupReferenceRegex.replace(result) { matchResult ->
            val groupName = matchResult.groupValues[1]
            val groupIndex = matcher.getNamedGroupIndex(groupName)

            if (groupIndex >= 0)
                matcher.group(groupIndex) ?: ""
            else ""
        }

    return result
}

/**
 * Helper method to find the group index for a named group.
 * @receiver [Matcher]
 * @param groupName the group name.
 * @return [Int]
 */
private fun Matcher.getNamedGroupIndex(groupName: String): Int {
    val namedGroups = matcherNamedGroupsResolver?.copy()?.of(this)?.getQuietly<Map<String, Int>>()
    return namedGroups?.get(groupName) ?: -1
}

private val numberGroupReferenceRegex = "\\$(\\d+)".toRegex()
private val namedGroupReferenceRegex = "\\$\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}".toRegex()

private val matcherNamedGroupsResolver by lazy {
    Matcher::class.resolve()
        .optional(silent = true)
        .firstFieldOrNull {
            name = "namedGroups"
        }
}