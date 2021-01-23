package com.hzh.demo.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.hzh.base.fragment.BaseFragment
import com.hzh.demo.databinding.FragmentRectBinding
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 * Create by hzh on 2021/01/23.
 */
class RectFragment : BaseFragment<FragmentRectBinding>() {

    companion object {

        fun newInstance(): RectFragment = RectFragment()
    }

    private var timer: Timer? = null

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRectBinding {
        return FragmentRectBinding.inflate(inflater, container, false)
    }

    override fun initView() {

    }

    override fun initListener() {

    }

    override fun onResume() {
        super.onResume()
        timer = fixedRateTimer(period = 1500) {
            mBinding.run {
                pb1.setProgress((Math.random() * 50).toInt())

                pb2.setSecondaryProgress(25)
                pb2.setProgress((Math.random() * 100).toInt(), true)

                pb3.setProgress((Math.random() * 100).toInt(), true)

                pb4.setProgress((Math.random() * 100).toInt(), true)

                pb5.setProgress((Math.random() * 100).toInt(), true)

                pb6.setProgress((Math.random() * 100).toInt(), true)

                pb7.setProgress((Math.random() * 100).toInt(), true)

                pb8.setProgress((Math.random() * 100).toInt(), true)
            }
        }
    }

    override fun onPause() {
        timer?.cancel()
        super.onPause()
    }
}