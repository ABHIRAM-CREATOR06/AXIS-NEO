package com.axisneo.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.axisneo.app.databinding.ActivityMainBinding
import com.axisneo.app.ui.history.HistoryFragment
import com.axisneo.app.ui.scan.ScanFragment
import com.axisneo.app.ui.settings.SettingsFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout

/**
 * Main activity that hosts the scan, history, and settings fragments
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragmentTag = "scan"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBottomNavigation()

        // Show default fragment
        if (savedInstanceState == null) {
            navigateTo("scan")
        }
    }

    private fun navigateTo(tag: String) {
        val menuItemId = when (tag) {
            "scan" -> R.id.nav_scan
            "history" -> R.id.nav_history
            "settings" -> R.id.nav_settings
            else -> R.id.nav_scan
        }
        if (binding.bottomNav.selectedItemId != menuItemId) {
            binding.bottomNav.selectedItemId = menuItemId
        } else {
            showFragment(tag)
        }
    }

    private fun setupToolbar() {
        binding.btnBack.visibility = View.GONE
        
        binding.btnInfo.setOnClickListener {
            showInfoDialog()
        }

        binding.btnSettings.setOnClickListener {
            navigateTo("settings")
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_scan -> {
                    showFragment("scan")
                    true
                }
                R.id.nav_history -> {
                    showFragment("history")
                    true
                }
                R.id.nav_settings -> {
                    showFragment("settings")
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(tag: String) {
        currentFragmentTag = tag
        
        val fragment: Fragment = when (tag) {
            "scan" -> ScanFragment()
            "history" -> HistoryFragment()
            "settings" -> SettingsFragment()
            else -> ScanFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment, tag)
            .commit()

        // Update title based on current fragment
        updateToolbarForFragment(tag)
    }

    private fun updateToolbarForFragment(tag: String) {
        when (tag) {
            "scan" -> {
                binding.btnBack.visibility = View.GONE
                binding.btnSettings.visibility = View.VISIBLE
            }
            "history" -> {
                binding.btnBack.visibility = View.VISIBLE
                binding.btnBack.setOnClickListener { navigateTo("scan") }
                binding.btnSettings.visibility = View.GONE
            }
            "settings" -> {
                binding.btnBack.visibility = View.VISIBLE
                binding.btnBack.setOnClickListener { navigateTo("scan") }
                binding.btnSettings.visibility = View.GONE
            }
        }
    }

    private fun showInfoDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.about_dialog_title))
            .setMessage(getString(R.string.about_dialog_body))
            .setPositiveButton(R.string.got_it) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}