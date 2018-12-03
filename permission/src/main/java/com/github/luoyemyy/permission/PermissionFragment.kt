package com.github.luoyemyy.permission

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders

internal class PermissionFragment : Fragment() {

    companion object {

        const val REQUEST_PERMISSION = "requestPermission"

        fun startPermissionFragment(fragmentManager: FragmentManager, requestPermission: Array<String>) {
            val permissionFragment = PermissionFragment().apply {
                arguments = Bundle().apply {
                    putStringArray(REQUEST_PERMISSION, requestPermission)
                }
            }
            fragmentManager.beginTransaction().add(permissionFragment, null).commit()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val permissions = arguments?.getStringArray(REQUEST_PERMISSION)
        if (permissions != null) {
            requestPermissions(permissions, PermissionHelper.REQUEST_CODE)
        } else {
            closeRequest()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.REQUEST_CODE) {
            val deniedList = mutableListOf<String>()
            grantResults.forEachIndexed { index, i ->
                if (i == PackageManager.PERMISSION_DENIED) {
                    deniedList.add(permissions[index])
                }
            }
            ViewModelProviders.of(requireActivity()).get(PermissionPresenter::class.java).postValue(deniedList.toTypedArray())
        }
        closeRequest()
    }

    private fun closeRequest() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }
}