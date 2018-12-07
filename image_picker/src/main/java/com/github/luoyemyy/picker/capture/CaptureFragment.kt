package com.github.luoyemyy.picker.capture

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.luoyemyy.bus.Bus
import com.github.luoyemyy.ext.toJsonString
import com.github.luoyemyy.mvp.getPresenter
import com.github.luoyemyy.picker.ImagePicker
import com.github.luoyemyy.picker.crop.CropActivity

internal class CaptureFragment : Fragment() {

    companion object {

        private const val FULL_TAG = "com.github.luoyemyy.picker.capture.CaptureFragment"

        fun start(fragmentManager: FragmentManager) {
            val fragment = fragmentManager.findFragmentByTag(FULL_TAG)
            if (fragment != null) {
                fragment.getPresenter<CapturePresenter>().capture(fragment)
            } else {
                fragmentManager.beginTransaction().add(CaptureFragment(), FULL_TAG).commit()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getPresenter<CapturePresenter>().capture(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == CapturePresenter.CAPTURE_REQUEST_CODE) {
            val images = getPresenter<CapturePresenter>().captureResult(requireContext()).toJsonString()
            when (ImagePicker.option.cropType) {
                0 -> {
                    Bus.post(ImagePicker.PICKER_RESULT, stringValue = images)
                }
                1, 2 -> {
                    startActivity(Intent(requireContext(), CropActivity::class.java).putExtra("images", images))
                }
            }
        }
    }
}