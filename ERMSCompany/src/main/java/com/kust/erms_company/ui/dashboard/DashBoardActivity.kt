package com.kust.erms_company.ui.dashboard

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.kust.erms_company.R
import com.kust.erms_company.databinding.ActivityDashboardBinding
import com.kust.erms_company.utils.NetworkChangeListener
import com.kust.erms_company.utils.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashBoardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDashboardBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var navController: LiveData<NavController>? = null

    private val networkChangeListener = NetworkChangeListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            setUpBottomNav()
        }
    }

    private fun setUpBottomNav() {
        val graphIds = listOf(
            R.navigation.home_nav_graph,
            R.navigation.profile_nav_graph,
            R.navigation.setting_nav_graph
        )

        val controller = binding.bottomNavigationView.setupWithNavController(
            graphIds,
            supportFragmentManager,
            R.id.dashboard_nav_host,
            intent
        )

        controller.observe(this) {
            appBarConfiguration = AppBarConfiguration(it.graph)
            binding.toolbar.setupWithNavController(it, appBarConfiguration)
        }
        navController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.value?.navigateUp()!! || super.onSupportNavigateUp()
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        setUpBottomNav()
    }

    override fun onStart() {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeListener, intentFilter)
        super.onStart()
    }

    override fun onStop() {
        unregisterReceiver(networkChangeListener)
        super.onStop()
    }
}