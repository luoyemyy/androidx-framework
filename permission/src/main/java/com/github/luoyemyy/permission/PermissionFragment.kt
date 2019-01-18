package com.github.luoyemyy.permission

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.luoyemyy.bus.Bus

internal class PermissionFragment : Fragment() {

    companion object {

        fun startPermissionFragment(fragmentManager: FragmentManager, event: String, requestPermission: Array<String>) {
            val permissionFragment = PermissionFragment().apply {
                arguments = Bundle().apply {
                    putString(PermissionHelper.PERMISSION_EVENT, event)
                    putStringArray(PermissionHelper.PERMISSIONS, requestPermission)
                }
            }
            fragmentManager.beginTransaction().add(permissionFragment, null).commit()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val permissions = arguments?.getStringArray(PermissionHelper.PERMISSIONS)
        if (permissions != null) {
            requestPermissions(permissions, 1)
        } else {
            closeRequest()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            arguments?.getString(PermissionHelper.PERMISSION_EVENT)?.apply {
                val deniedList = mutableListOf<String>()
                grantResults.forEachIndexed { index, i ->
                    if (i == PackageManager.PERMISSION_DENIED) {
                        deniedList.add(permissions[index])
                    }
                }
                Bus.post(this, extra = Bundle().apply {
                    putStringArray(PermissionHelper.PERMISSIONS, deniedList.toTypedArray())
                })
            }
        }
        closeRequest()
    }

    private fun closeRequest() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }
}