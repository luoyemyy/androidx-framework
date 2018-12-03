package com.github.luoyemyy.framework.test.drawer

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.github.luoyemyy.ext.immerse
import com.github.luoyemyy.framework.test.R
import com.github.luoyemyy.framework.test.databinding.ActivityDrawerBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mBinding: ActivityDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_drawer)
        setSupportActionBar(mBinding.toolbar)

        mBinding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(this, mBinding.drawerLayout, mBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        mBinding.navView.setNavigationItemSelectedListener(this)

    }

    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
