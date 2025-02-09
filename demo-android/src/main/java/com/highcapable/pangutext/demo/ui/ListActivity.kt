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
 * This file is created by fankes on 2025/2/9.
 */
package com.highcapable.pangutext.demo.ui

import android.graphics.Color
import android.os.Bundle
import com.highcapable.betterandroid.ui.component.adapter.factory.bindAdapter
import com.highcapable.betterandroid.ui.extension.view.textColor
import com.highcapable.pangutext.demo.databinding.ActivityListBinding
import com.highcapable.pangutext.demo.databinding.AdapterListBinding
import com.highcapable.pangutext.demo.ui.base.BaseActivity

class ListActivity : BaseActivity<ActivityListBinding>() {
    
    private val listData = List(100) { "这是第${it}条Data演示" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.recyclerView.bindAdapter<String> { 
            onBindData { listData }
            onBindViews<AdapterListBinding> { binding, text, _ -> 
                binding.text.text = text
                binding.text.textColor = Color.rgb(
                    (0..255).random(),
                    (0..255).random(),
                    (0..255).random()
                )
            }
        }
    }
}