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
import com.highcapable.pangutext.android.generated.PangutextAndroidProperties
import com.highcapable.yukireflection.factory.classOf
import com.highcapable.yukireflection.factory.field
import java.util.regex.Matcher

/**
 * Replace the text content and preserve the original span style.
 * @see CharSequence.replace
 * @receiver [CharSequence]
 * @param regex the regular expression.
 * @param replacement the replacement text.
 * @param excludePatterns the regular expression to exclude from replacement, default is null.
 * @return [CharSequence]
 */
internal fun CharSequence.replaceAndPreserveSpans(regex: Regex, replacement: String, vararg excludePatterns: Regex) =
    runCatching {
        val builder = SpannableStringBuilder(this)
        val matcher = regex.toPattern().matcher(this)
        val excludeMatchers = excludePatterns.map { it.toPattern().matcher(this) }
        val excludeIndexs = mutableSetOf<Pair<Int, Int>>()
        excludeMatchers.forEach {
            while (it.find()) excludeIndexs.add(it.start() to it.end())
        }
        var offset = 0
        // Offset adjustment to account for changes in the text length after replacements.
        while (matcher.find()) {
            val start = matcher.start() + offset
            val end = matcher.end() + offset
            // Skip the replacement if the matched range is excluded.
            // The character range offset is adjusted by 1 to avoid the exclusion of the matched range.
            if (excludeIndexs.any { it.first <= start + 1 && it.second >= end - 1 }) continue
            // Perform the replacement.
            val replacementText = matcher.buildReplacementText(replacement)
            builder.replace(start, end, replacementText)
            // Adjust offset based on the length of the replacement.
            offset += replacementText.length - (end - start)
        }; builder
    }.onFailure {
        Log.w(PangutextAndroidProperties.PROJECT_NAME, "Failed to replace span text content.", it)
    }.getOrNull() ?: this

/**
 * Build the replacement text based on the matched groups.
 * @receiver [Matcher]
 * @param replacement the replacement text.
 * @return [String]
 */
private fun Matcher.buildReplacementText(replacement: String): String {
    val matcher = this
    var result = replacement
    // Check for group references (like $1, $2, ...).
    val pattern = "\\$(\\d+)".toRegex()
    result = pattern.replace(result) { matchResult ->
        val groupIndex = matchResult.groupValues[1].toInt()
        if (groupIndex <= matcher.groupCount())
            matcher.group(groupIndex) ?: ""
        else ""
    }
    // Check for named groups (like ${groupName}).
    val namedGroupPattern = "\\$\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}".toRegex()
    result = namedGroupPattern.replace(result) { matchResult ->
        val groupName = matchResult.groupValues[1]
        val groupIndex = matcher.getNamedGroupIndex(groupName)
        if (groupIndex >= 0)
            matcher.group(groupIndex) ?: ""
        else ""
    }; return result
}

/**
 * Helper method to find the group index for a named group.
 * @receiver [Matcher]
 * @param groupName the group name.
 * @return [Int]
 */
private fun Matcher.getNamedGroupIndex(groupName: String): Int {
    val namedGroups = classOf<Matcher>()
        .field { name = "namedGroups" }
        .ignored()
        .get(this)
        .cast<Map<String, Int>>()
    return namedGroups?.get(groupName) ?: -1
}