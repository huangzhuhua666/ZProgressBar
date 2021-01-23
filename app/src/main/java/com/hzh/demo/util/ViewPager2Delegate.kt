package com.hzh.demo.util

import androidx.viewpager2.widget.ViewPager2
import com.angcyo.tablayout.DslTabLayout
import com.angcyo.tablayout.ViewPagerDelegate
import kotlin.math.absoluteValue

/**
 * Create by hzh on 2020/5/25.
 */
class ViewPager2Delegate(
    private val viewPager: ViewPager2,
    private val dslTabLayout: DslTabLayout?
) : ViewPager2.OnPageChangeCallback(), ViewPagerDelegate {

    companion object {
        fun install(viewPager: ViewPager2, dslTabLayout: DslTabLayout?) {
            ViewPager2Delegate(viewPager, dslTabLayout)
        }
    }

    init {
        viewPager.registerOnPageChangeCallback(this)
        dslTabLayout?.setupViewPager(this)
    }

    override fun onGetCurrentItem(): Int {
        return viewPager.currentItem
    }

    override fun onSetCurrentItem(fromIndex: Int, toIndex: Int) {
        viewPager.setCurrentItem(toIndex, (toIndex - fromIndex).absoluteValue <= 1)
    }

    override fun onPageScrollStateChanged(state: Int) {
        dslTabLayout?.onPageScrollStateChanged(state)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        dslTabLayout?.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        dslTabLayout?.onPageSelected(position)
    }
}