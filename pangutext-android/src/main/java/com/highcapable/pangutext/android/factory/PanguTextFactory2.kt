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
 * This file is created by fankes on 2025/1/19.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.pangutext.android.factory

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.highcapable.betterandroid.ui.extension.view.layoutInflater
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.pangutext.android.generated.PangutextAndroidProperties

/**
 * Pangu text factory 2 for [LayoutInflater.Factory2].
 * @param base the base factory.
 */
class PanguTextFactory2 private constructor(private val base: LayoutInflater.Factory2?) : LayoutInflater.Factory2 {

    companion object {

        /**
         * Inject [PanguTextFactory2] to the current [LayoutInflater] of [context].
         * 
         * Simple Usage:
         * 
         * ```kotlin
         * class MainActivity : AppCompactActivity() {
         * 
         *     val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
         * 
         *     override fun onCreate(savedInstanceState: Bundle?) {
         *         super.onCreate(savedInstanceState)
         *         // Inject here.
         *         PanguTextFactory2.inject(this)
         *         setContentView(binding.root)
         *     }
         * }
         * ```
         *
         * Traditional Usage:
         *
         * ```kotlin
         * class MainActivity : Activity() {
         *
         *     override fun onCreate(savedInstanceState: Bundle?) {
         *         super.onCreate(savedInstanceState)
         *         // Inject here.
         *         PanguTextFactory2.inject(this)
         *         setContentView(R.layout.activity_main)
         *     }
         * }
         * ```
         *
         * Usage with BetterAndroid's AppBindingActivity:
         *
         * ```kotlin
         * class MainActivity : AppBindingActivity<ActivityMainBinding>() {
         *
         *    override fun onPrepareContentView(savedInstanceState: Bundle?): LayoutInflater {
         *        val inflater = super.onPrepareContentView(savedInstanceState)
         *        // Inject here.
         *        PanguTextFactory2.inject(inflater)
         *        return inflater
         *    }
         *
         *    override fun onCreate(savedInstanceState: Bundle?) {
         *        super.onCreate(savedInstanceState)
         *        // Your code here.
         *    }
         * }
         * ```
         * @param context the current context.
         */
        @JvmStatic
        fun inject(context: Context) = inject(context.layoutInflater)

        /**
         * Inject [PanguTextFactory2] to the current [LayoutInflater].
         * @see inject
         * @param inflater the current inflater.
         */
        @JvmStatic
        fun inject(inflater: LayoutInflater) {
            val original = inflater.factory2
            if (original is PanguTextFactory2) return run {
                Log.w(PangutextAndroidProperties.PROJECT_NAME, "PanguTextFactory2 was already injected.")
            }
            val replacement = PanguTextFactory2(original)
            if (original != null)
                inflater.resolve().optional(silent = true).firstFieldOrNull {
                    name = "mFactory2"
                    superclass()
                }?.setQuietly(replacement) ?: Log.e(PangutextAndroidProperties.PROJECT_NAME, "LayoutInflater.mFactory2 not found.")
            else inflater.factory2 = replacement
        }
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet) =
        base?.onCreateView(parent, name, context, attrs).let {
            PanguWidget.process(name, it, context, attrs) ?: it
        }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet) =
        base?.onCreateView(name, context, attrs).let {
            PanguWidget.process(name, it, context, attrs) ?: it
        }
}