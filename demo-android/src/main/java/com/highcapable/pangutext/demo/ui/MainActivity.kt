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
package com.highcapable.pangutext.demo.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.core.text.HtmlCompat
import com.highcapable.betterandroid.ui.component.insets.factory.handleOnWindowInsetsChanged
import com.highcapable.betterandroid.ui.component.insets.factory.setInsetsPadding
import com.highcapable.betterandroid.ui.extension.component.startActivity
import com.highcapable.pangutext.demo.databinding.ActivityMainBinding
import com.highcapable.pangutext.demo.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val demoText = HtmlCompat.fromHtml(
        "今天下午，我去了一家新开的咖啡店，店里环境非常舒适，感觉很cozy。" +
            "我点了一杯latte，坐在窗边，透过玻璃看着街上的人来人往。店员还<b>特别热情</b>，" +
            "给我推荐了一款很<font color='#639F70'><b>特别的</b></font>chocolate cake，味道真是不错！<br/>" +
            "我发现现在很多人都<b>喜欢在咖啡店里工作</b>，几乎每桌都有laptop。我的旁边有一位女士，正在忙着处理emails。" +
            "我想，这样的环境真是适合集中精力工作。<br/>" +
            "总的来说，今天的体验很不错，下次还想再来尝试其他的<font color='#5C80BC'>drinks</font>和<font color='#9C528B'>desserts</font>。<br/>" +
            "<span style='background-color: #E9EDDE'>混<b>合wo</b>rd样式</span>测试。<br/>" +
            "You can<a href='https://github.com/BetterAndroid/PanguText'>点击这里访问项目地址</a>。",
        HtmlCompat.FROM_HTML_MODE_LEGACY
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.root.handleOnWindowInsetsChanged(animated = true) { linearLayout, insetsWrapper ->
            linearLayout.setInsetsPadding(insetsWrapper.safeDrawing)
        }

        listOf(
            binding.textViewPanguText,
            binding.textViewPanguTextCjkSpacingRatio,
            binding.textViewNoPanguText
        ).forEach {
            it.movementMethod = LinkMovementMethod.getInstance()
            it.text = demoText
        }
        binding.buttonJumpList.setOnClickListener {
            startActivity<ListActivity>()
        }
    }
}