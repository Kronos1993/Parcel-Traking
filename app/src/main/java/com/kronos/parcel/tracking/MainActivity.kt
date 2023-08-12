package com.kronos.parcel.tracking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kronos.core.extensions.binding.activityBinding
import com.kronos.core.util.startActivityForResult
import com.kronos.core.util.validatePermission
import com.kronos.parcel.tracking.databinding.ActivityMainBinding
import com.kronos.parcel.tracking.ui.history.HistoryViewModel
import com.kronos.parcel.tracking.ui.home.HomeViewModel
import com.kronos.parcel.tracking.ui.parcel_details.ParcelDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by activityBinding<ActivityMainBinding>(R.layout.activity_main)
    private val viewModel by viewModels<MainActivityViewModel>()
    private val viewModelHistory by viewModels<HistoryViewModel>()
    private val viewModelHome by viewModels<HomeViewModel>()
    private val viewModelParcelDetail by viewModels<ParcelDetailsViewModel>()

    private var grantedAll = false
    private var grantedFullStorage = false
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            lifecycleOwner = this@MainActivity
            setContentView(root)
            checkPermission()
        }
    }

    private fun checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            checkFullStorageAccess()
        } else {
            grantedAll =
                validatePermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) && validatePermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) && validatePermission(
                            this,
                            Manifest.permission.CAMERA
                )
            if (!grantedAll) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ),
                    1
                )
            } else {
                init()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkFullStorageAccess() {
        grantedFullStorage = Environment.isExternalStorageManager()

        if (!grantedFullStorage) {
            MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.permission_dialog_message))
                .setTitle(getString(R.string.permission_dialog_title))
                .setPositiveButton(R.string.ok) { dialogInterface, _ ->
                    startActivityForResult(
                        this,
                        Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,
                        150
                    )
                    dialogInterface.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    finish()
                }
                .create()
                .show()
        } else {
            init()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var grantedPermissions = true
        for (grantResult in grantResults) {
            grantedPermissions =
                grantedPermissions and (grantResult == PackageManager.PERMISSION_GRANTED)
        }
        if (grantedPermissions) {
            init()
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 150) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                checkFullStorageAccess()
            }
        }
    }

    private fun init() {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_history,
                R.id.navigation_notifications,
                R.id.navigation_user,
                R.id.navigation_about,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        observeViewModel()
        observeNavigation(navController)
        viewModel.getEventCount()
        viewModelHome.refreshParcels()
        needsNavigate()
    }

    private fun needsNavigate() {
        var argument = intent.extras
        if(argument!=null){
            if (argument.get("go_to")!=null)
                when (argument.getInt("go_to")){
                    R.id.navigation_notifications->{
                        findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_notifications)
                    }
                }
        }
    }

    private fun observeNavigation(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_notifications -> {
                    viewModel.setAllEventRead()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.eventCount.observe(this, ::handleEventCount)
        viewModelHistory.state.observe(this, ::handleHistoryState)
        viewModelHome.state.observe(this, ::handleHomeState)
        viewModelParcelDetail.state.observe(this, ::handleParcelDetailState)
    }

    private fun handleParcelDetailState(state: MainState?) {
        when (state) {
            MainState.NewEvent -> {
                viewModel.getEventCount()
            }
        }
    }

    private fun handleHistoryState(state: MainState?) {
        when (state) {
            MainState.NewEvent -> {
                viewModel.getEventCount()
            }
        }
    }

    private fun handleHomeState(state: MainState?) {
        when (state) {
            MainState.NewEvent -> {
                viewModel.getEventCount()
            }
        }
    }

    private fun handleEventCount(i: Int) {
        val navBar = binding.navView
        if (i > 0)
            navBar.getOrCreateBadge(R.id.navigation_notifications).number = i
        else
            navBar.removeBadge(R.id.navigation_notifications)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment_activity_main)
        return (navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (val id: Int = item.itemId) {
            R.id.navigation_settings -> findNavController(R.id.nav_host_fragment_activity_main).navigate(
                id
            )
        }
        return super.onOptionsItemSelected(item)
    }
}