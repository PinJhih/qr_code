package com.example.qr_code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter : PagerAdapter() {
    override fun getCount(): Int {
        return 2
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return true
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) "產生QR Code" else "掃描QR Code"
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val view = if (position == 0)
            LayoutInflater.from(container.context).inflate(R.layout.item_generage, container, false)
        else
            LayoutInflater.from(container.context).inflate(R.layout.item_scan, container, false)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }
}