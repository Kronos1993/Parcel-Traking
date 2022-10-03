package com.kronos.parcel.traking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kronos.core.util.AndroidPermissionUtil
import com.kronos.core.util.NavigateUtil
import com.kronos.myparceltraking.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var grantedAll = false
    private var grantedFullStorage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()
    }

    private fun checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            checkFullStorageAccess()
        } else {
            grantedAll =
                AndroidPermissionUtil.validatePermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) &&
                        AndroidPermissionUtil.validatePermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
            if (!grantedAll) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
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
                .setPositiveButton(R.string.ok) { dialogInterface, i ->
                    NavigateUtil.startActivityForResult(
                        this,
                        Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,
                        150
                    )
                    dialogInterface.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
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

    fun init(){
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}