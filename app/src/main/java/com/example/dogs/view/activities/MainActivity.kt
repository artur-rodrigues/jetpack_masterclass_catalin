package com.example.dogs.view.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.dogs.R
import com.example.dogs.utils.PERMISSION_SEND_SMS
import com.example.dogs.view.fragments.DetailFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pega o controller de navegação e seta ele no ActionBar para aparecer a flecha de voltar
        navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Seta o comando de voltar no navController quando clicado na flecha do ActionBar
        return NavigationUI.navigateUp(navController, null)
    }

    fun checkSmsPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
            PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat
                    .shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                AlertDialog.Builder(this)
                    .setTitle("Send SMS permission")
                    .setMessage("This app requires access to send an SMS.")
                    .setPositiveButton("Ask me") {_, _ ->
                        requestSmsPermission()
//                        dialog.dismiss()
                    }.setNegativeButton("No") {_, _ ->
                        notifyDetailFragment(false)
//                        dialog.dismiss()
                    }.show()
            } else {
                requestSmsPermission()
            }
        } else {
            notifyDetailFragment(true)
        }
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SEND_SMS),
            PERMISSION_SEND_SMS)
    }

    private fun notifyDetailFragment(permissionGranted: Boolean) {
        val activeFragment = fragment.childFragmentManager.primaryNavigationFragment
        if(activeFragment is DetailFragment) {
            activeFragment.onPermissionResult(permissionGranted)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PERMISSION_SEND_SMS -> {
                notifyDetailFragment(
                grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                )
            }
        }
    }
}
