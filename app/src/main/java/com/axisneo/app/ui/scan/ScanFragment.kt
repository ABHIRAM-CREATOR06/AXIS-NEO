package com.axisneo.app.ui.scan

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.axisneo.app.R
import com.axisneo.app.adapter.IssueAdapter
import com.axisneo.app.data.Issue
import com.axisneo.app.data.IssueCategory
import com.axisneo.app.data.IssueType
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.tabs.TabLayout

/**
 * Fragment for the scan page
 */
class ScanFragment : Fragment() {

    private lateinit var urlInput: EditText
    private lateinit var scanButton: MaterialButton
    private lateinit var scanProgressCard: MaterialCardView
    private lateinit var scanLabel: TextView
    private lateinit var scanSubLabel: TextView
    private lateinit var scanProgressBar: LinearProgressIndicator
    private lateinit var stepText: TextView
    private lateinit var pctText: TextView
    private lateinit var emptyState: LinearLayout
    private lateinit var resultArea: LinearLayout
    private lateinit var scoreRing: CircularProgressIndicator
    private lateinit var scoreNumber: TextView
    private lateinit var scoreLabel: TextView
    private lateinit var scoreUrlLabel: TextView
    private lateinit var errorChip: TextView
    private lateinit var warningChip: TextView
    private lateinit var sumError: TextView
    private lateinit var sumWarning: TextView
    private lateinit var sumInfo: TextView
    private lateinit var sumPassed: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var issuesRecyclerView: RecyclerView
    private lateinit var issuesCountLabel: TextView
    private lateinit var chipAll: Chip
    private lateinit var chipImages: Chip
    private lateinit var chipContrast: Chip
    private lateinit var chipForms: Chip
    private lateinit var chipKeyboard: Chip
    private lateinit var chipStructure: Chip

    private lateinit var issueAdapter: IssueAdapter
    private var allIssues = mutableListOf<Issue>()
    private var currentTab = 0 // 0: All, 1: Errors, 2: Warnings, 3: Info
    private var currentCategory = IssueCategory.ALL
    private var isScanning = false

    private val scanSteps = listOf(
        ScanStep("Crawling page…", "Fetching DOM and resources"),
        ScanStep("Running image checks…", "Alt text, decorative detection"),
        ScanStep("Analysing color contrast…", "Checking 4.5:1 and 3:1 ratios"),
        ScanStep("Testing keyboard flow…", "Tab order and focus visibility"),
        ScanStep("Auditing ARIA & structure…", "Landmarks, roles, headings")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        initViews(view)
        setupRecyclerView()
        setupListeners()
    }

    private fun initViews(view: View) {
        urlInput = view.findViewById(R.id.urlInput)
        scanButton = view.findViewById(R.id.scanButton)
        scanProgressCard = view.findViewById(R.id.scanProgressCard)
        scanLabel = view.findViewById(R.id.scanLabel)
        scanSubLabel = view.findViewById(R.id.scanSubLabel)
        scanProgressBar = view.findViewById(R.id.scanProgressBar)
        stepText = view.findViewById(R.id.stepText)
        pctText = view.findViewById(R.id.pctText)
        emptyState = view.findViewById(R.id.emptyState)
        resultArea = view.findViewById(R.id.resultArea)
        scoreRing = view.findViewById(R.id.scoreRing)
        scoreNumber = view.findViewById(R.id.scoreNumber)
        scoreLabel = view.findViewById(R.id.scoreLabel)
        scoreUrlLabel = view.findViewById(R.id.scoreUrlLabel)
        errorChip = view.findViewById(R.id.errorChip)
        warningChip = view.findViewById(R.id.warningChip)
        sumError = view.findViewById(R.id.sumError)
        sumWarning = view.findViewById(R.id.sumWarning)
        sumInfo = view.findViewById(R.id.sumInfo)
        sumPassed = view.findViewById(R.id.sumPassed)
        tabLayout = view.findViewById(R.id.tabLayout)
        issuesRecyclerView = view.findViewById(R.id.issuesRecyclerView)
        issuesCountLabel = view.findViewById(R.id.issuesCountLabel)
        chipAll = view.findViewById(R.id.chipAll)
        chipImages = view.findViewById(R.id.chipImages)
        chipContrast = view.findViewById(R.id.chipContrast)
        chipForms = view.findViewById(R.id.chipForms)
        chipKeyboard = view.findViewById(R.id.chipKeyboard)
        chipStructure = view.findViewById(R.id.chipStructure)
    }

    private fun setupRecyclerView() {
        issueAdapter = IssueAdapter { issue ->
            // Handle issue click - expand/collapse is handled in adapter
        }
        issuesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = issueAdapter
        }
    }

    private fun setupListeners() {
        scanButton.setOnClickListener {
            startScan()
        }

        urlInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                startScan()
                true
            } else false
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                filterIssues()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Filter chips
        chipAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentCategory = IssueCategory.ALL
                filterIssues()
            }
        }
        chipImages.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentCategory = IssueCategory.IMAGES
                filterIssues()
            }
        }
        chipContrast.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentCategory = IssueCategory.CONTRAST
                filterIssues()
            }
        }
        chipForms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentCategory = IssueCategory.FORMS
                filterIssues()
            }
        }
        chipKeyboard.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentCategory = IssueCategory.KEYBOARD
                filterIssues()
            }
        }
        chipStructure.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentCategory = IssueCategory.STRUCTURE
                filterIssues()
            }
        }
    }

    private fun startScan() {
        val url = urlInput.text.toString().trim()
        if (url.isEmpty()) return
        if (isScanning) return

        isScanning = true

        // Hide empty state and result, show progress
        emptyState.isVisible = false
        resultArea.isVisible = false
        scanProgressCard.isVisible = true
        scanProgressBar.progress = 0

        var step = 0
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                if (step >= scanSteps.size) {
                    // Scan complete
                    showResults(url)
                    isScanning = false
                    return
                }

                val currentStep = scanSteps[step]
                scanLabel.text = currentStep.label
                scanSubLabel.text = currentStep.sub

                val progress = ((step + 1) * 100) / scanSteps.size
                scanProgressBar.progress = progress

                stepText.text = getString(R.string.step_format, step + 1, scanSteps.size)
                pctText.text = getString(R.string.percentage_format, progress)

                step++
                handler.postDelayed(this, 700)
            }
        }

        handler.post(runnable)
    }

    private fun showResults(url: String) {
        scanProgressCard.isVisible = false
        resultArea.isVisible = true

        // Set URL (extract hostname)
        try {
            val hostname = java.net.URI(url).host ?: url
            scoreUrlLabel.text = hostname
        } catch (e: Exception) {
            scoreUrlLabel.text = url
        }

        // Sample issues (same as in HTML)
        allIssues.clear()
        allIssues.addAll(getSampleIssues())

        // Calculate score
        val errors = allIssues.count { it.type == IssueType.ERROR }
        val warnings = allIssues.count { it.type == IssueType.WARNING }
        val infos = allIssues.count { it.type == IssueType.INFO }
        val passed = 38

        val score = maxOf(0, 100 - errors * 12 - warnings * 4 - infos * 1)
        val grade = when {
            score >= 90 -> "Excellent"
            score >= 70 -> "Good"
            score >= 50 -> "Needs Work"
            else -> "Poor"
        }

        // Update UI
        scoreRing.progress = score
        scoreNumber.text = score.toString()
        scoreLabel.text = getString(R.string.score_format, grade, score)

        errorChip.text = getString(R.string.error_count, errors, if (errors != 1) "s" else "")
        warningChip.text = getString(R.string.warning_count, warnings, if (warnings != 1) "s" else "")

        sumError.text = errors.toString()
        sumWarning.text = warnings.toString()
        sumInfo.text = infos.toString()
        sumPassed.text = passed.toString()

        filterIssues()
    }

    private fun filterIssues() {
        var filtered = allIssues.toList()

        // Filter by tab
        filtered = when (currentTab) {
            1 -> filtered.filter { it.type == IssueType.ERROR }
            2 -> filtered.filter { it.type == IssueType.WARNING }
            3 -> filtered.filter { it.type == IssueType.INFO }
            else -> filtered
        }

        // Filter by category
        if (currentCategory != IssueCategory.ALL) {
            filtered = filtered.filter { it.category == currentCategory }
        }

        // Update count label
        issuesCountLabel.text = getString(
            R.string.issues_count,
            filtered.size,
            if (filtered.size != 1) "s" else ""
        )

        // Update adapter
        issueAdapter.submitList(filtered)
    }

    private fun getSampleIssues(): List<Issue> {
        return listOf(
            Issue(
                id = 1,
                type = IssueType.ERROR,
                category = IssueCategory.IMAGES,
                rule = "WCAG 1.1.1 (Level A)",
                title = "Image missing alt attribute",
                description = "3 <img> elements have no alt attribute, making them inaccessible to screen readers.",
                code = "<img src=\"hero.png\" class=\"hero-img\">\n<!-- Missing: alt=\"\" or alt=\"description\" -->",
                fix = "Add a descriptive alt attribute to every meaningful image. For decorative images, use alt=\"\" to hide them from assistive tech.",
                wcag = listOf("1.1.1", "2.1.1")
            ),
            Issue(
                id = 2,
                type = IssueType.ERROR,
                category = IssueCategory.CONTRAST,
                rule = "WCAG 1.4.3 (Level AA)",
                title = "Insufficient color contrast",
                description = "Body text has a contrast ratio of 2.3:1 against the background. Minimum required is 4.5:1.",
                code = "color: #aaaaaa;\nbackground: #f5f5f5;\n/* Ratio: 2.3:1 — FAIL (min 4.5:1) */",
                fix = "Darken the text color to at least #767676 on a white background, achieving a 4.54:1 ratio.",
                wcag = listOf("1.4.3", "1.4.6")
            ),
            Issue(
                id = 3,
                type = IssueType.ERROR,
                category = IssueCategory.FORMS,
                rule = "WCAG 1.3.1 (Level A)",
                title = "Form input has no associated label",
                description = "2 <input> elements are not programmatically associated with a visible label.",
                code = "<input type=\"email\" placeholder=\"Email address\">\n<!-- Missing: <label for=\"...\"> or aria-label -->",
                fix = "Add a <label for=\"inputId\"> element or use aria-label / aria-labelledby on the input element.",
                wcag = listOf("1.3.1", "4.1.2")
            ),
            Issue(
                id = 4,
                type = IssueType.WARNING,
                category = IssueCategory.KEYBOARD,
                rule = "WCAG 2.4.7 (Level AA)",
                title = "Focus indicator not visible",
                description = "Interactive elements lose their browser focus ring via outline:none without a custom replacement.",
                code = "a, button {\n  outline: none; /* Removes focus ring */\n}",
                fix = "Replace outline:none with a custom focus style. Use :focus-visible to show it only for keyboard users.",
                wcag = listOf("2.4.7", "2.4.11")
            ),
            Issue(
                id = 5,
                type = IssueType.WARNING,
                category = IssueCategory.STRUCTURE,
                rule = "WCAG 1.3.1 (Level A)",
                title = "Heading hierarchy skipped",
                description = "Page jumps from <h1> to <h3> with no <h2>, breaking logical document outline.",
                code = "<h1>Page Title</h1>\n<!-- Missing <h2> -->\n<h3>Sub-section</h3>",
                fix = "Ensure headings follow a sequential, logical hierarchy (h1 → h2 → h3). Don't skip levels.",
                wcag = listOf("1.3.1")
            ),
            Issue(
                id = 6,
                type = IssueType.WARNING,
                category = IssueCategory.STRUCTURE,
                rule = "WCAG 4.1.2 (Level A)",
                title = "Missing landmark regions",
                description = "Page has no <main>, <nav>, or <header> landmarks. Screen reader users can't jump between sections.",
                code = "<div class=\"content\">\n  <!-- Should be: <main> -->\n</div>",
                fix = "Add ARIA landmark roles or HTML5 semantic elements: <main>, <nav>, <header>, <footer>, <aside>.",
                wcag = listOf("1.3.6", "4.1.2")
            ),
            Issue(
                id = 7,
                type = IssueType.INFO,
                category = IssueCategory.IMAGES,
                rule = "WCAG 1.4.5 (Level AA)",
                title = "Images of text detected",
                description = "2 images appear to contain text. Prefer real text with CSS styling for scalability.",
                code = "<img src=\"banner-text.png\" alt=\"Sale 50% Off\">",
                fix = "Use real text styled with CSS. If images of text are necessary, ensure the alt text matches exactly.",
                wcag = listOf("1.4.5")
            )
        )
    }

    data class ScanStep(val label: String, val sub: String)
}