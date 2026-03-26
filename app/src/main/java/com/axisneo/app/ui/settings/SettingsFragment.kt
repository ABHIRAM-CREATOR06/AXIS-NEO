package com.axisneo.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.axisneo.app.R
import com.google.android.material.materialswitch.MaterialSwitch

/**
 * Fragment for the settings page
 */
class SettingsFragment : Fragment() {

    private lateinit var switchContrast: MaterialSwitch
    private lateinit var switchAltText: MaterialSwitch
    private lateinit var switchKeyboard: MaterialSwitch
    private lateinit var switchDarkTheme: MaterialSwitch
    private lateinit var switchPdf: MaterialSwitch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
    }

    private fun initViews(view: View) {
        switchContrast = view.findViewById(R.id.switchContrast)
        switchAltText = view.findViewById(R.id.switchAltText)
        switchKeyboard = view.findViewById(R.id.switchKeyboard)
        switchDarkTheme = view.findViewById(R.id.switchDarkTheme)
        switchPdf = view.findViewById(R.id.switchPdf)
    }

    private fun setupListeners() {
        switchContrast.setOnCheckedChangeListener { _, isChecked ->
            // Handle contrast check toggle
        }

        switchAltText.setOnCheckedChangeListener { _, isChecked ->
            // Handle alt text audit toggle
        }

        switchKeyboard.setOnCheckedChangeListener { _, isChecked ->
            // Handle keyboard navigation toggle
        }

        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            // Handle dark theme toggle - would need to switch AppCompatDelegate
        }

        switchPdf.setOnCheckedChangeListener { _, isChecked ->
            // Handle PDF export toggle
        }
    }
}