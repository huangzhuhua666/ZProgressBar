package com.hzh.demo

import com.example.hzh.base.activity.BaseActivity
import com.example.hzh.base.viewpager.SimplePageAdapter
import com.hzh.demo.databinding.ActivityMainBinding
import com.hzh.demo.fragment.CircleFragment
import com.hzh.demo.fragment.RectFragment
import com.hzh.demo.util.ViewPager2Delegate

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun createViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val list = listOf(
            RectFragment.newInstance(),
            CircleFragment.newInstance(),
        )

        val adapter = SimplePageAdapter(
            supportFragmentManager,
            lifecycle,
            list.size
        ) { list[it] }
        mBinding.run {
            vpContent.run {
                this.adapter = adapter
                offscreenPageLimit = 1
            }

            ViewPager2Delegate.install(vpContent, indicator) // 关联ViewPager2和TabLayout
        }
    }

    override fun initData() {

    }

    override fun initListener() {

    }
}