package edu.cnm.deepdive.seesomethingabq.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentReportDetailBinding
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueTypeViewModel
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable

@AndroidEntryPoint
class ReportDetailFragment : Fragment() {

    private var _binding: FragmentReportDetailBinding? = null
    private val binding: FragmentReportDetailBinding
        get() = _binding!!
    private val viewModel: IssueReportViewModel by viewModels()
    private val issueTypeViewModel: IssueTypeViewModel by viewModels()
    private val args: ReportDetailFragmentArgs by navArgs()

    private var loadedReport: IssueReport? = null
    private var originalReport: IssueReport? = null
    private var editing: Boolean = false
    private val selectedIssueTypeTags: MutableSet<String> = linkedSetOf()
    private var availableIssueTypes: List<IssueType> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportDetailBinding.inflate(inflater, container, false)

        binding.editButton.setOnClickListener {
            setEditing(true)
        }

        binding.saveButton.setOnClickListener {
            save()
        }

        binding.cancelButton.setOnClickListener {
            cancelEdits()
        }

        // Location/images are not editable in PR1; keep controls disabled.
        binding.locationInput.isEnabled = false
        binding.useCurrentLocationButton.isEnabled = false
        binding.takePhotoButton.isEnabled = false
        binding.attachGalleryImageButton.isEnabled = false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        issueTypeViewModel.issueTypes.observe(viewLifecycleOwner) { issueTypes ->
            if (issueTypes != null) {
                availableIssueTypes = issueTypes
                populateIssueTypeChips()
            }
        }
        issueTypeViewModel.refresh(requireActivity())
    }

    override fun onResume() {
        super.onResume()

        val reportId = args.reportId

        viewModel.getReport(requireActivity(), reportId)
            .thenAccept { report ->
                requireActivity().runOnUiThread {
                    val binding = _binding ?: return@runOnUiThread
                    loadedReport = report
                    originalReport = report
                    selectedIssueTypeTags.clear()
                    selectedIssueTypeTags.addAll(report.issueTypes)
                    setEditing(false)

                    // Populate form fields.
                    binding.descriptionInput.setText(report.description.orEmpty())
                    binding.locationInput.setText(bestLocationText(report))
                    populateIssueTypeChips()

                    // Setup existing images display (read-only).
                    val adapter = ReportImageAdapter(
                        requireActivity(),
                        report.reportImages ?: emptyList(),
                        viewModel,
                        report.externalId
                    )

                    binding.imageList.layoutManager = GridLayoutManager(requireContext(), 3)
                    binding.imageList.adapter = adapter
                }
            }
    }

    private fun setEditing(editing: Boolean) {
        this.editing = editing

        binding.descriptionInput.isEnabled = editing
        binding.descriptionInput.isFocusable = editing
        binding.descriptionInput.isFocusableInTouchMode = editing

        // Chips are always visible; enable/disable selection by mode.
        populateIssueTypeChips()

        binding.saveButton.visibility = if (editing) View.VISIBLE else View.GONE
        binding.cancelButton.visibility = if (editing) View.VISIBLE else View.GONE
        binding.editButton.visibility = if (editing) View.GONE else View.VISIBLE
    }

    private fun cancelEdits() {
        val original = originalReport ?: return
        val binding = _binding ?: return
        binding.descriptionInput.setText(original.description ?: "")
        selectedIssueTypeTags.clear()
        selectedIssueTypeTags.addAll(original.issueTypes)
        populateIssueTypeChips()
        setEditing(false)
    }

    private fun save() {
        val current = loadedReport ?: return

        val description = binding.descriptionInput.text?.toString()?.trim().orEmpty()
        val issueTypes = selectedIssueTypeTags.toList()

        val request = IssueReportRequest(
            textDescription = description,
            latitude = current.latitude,
            longitude = current.longitude,
            // Location is read-only in PR1; don't mutate it (and don't overwrite streetCoordinate with a display string).
            streetCoordinate = current.streetCoordinate,
            locationDescription = current.locationDescription,
            issueTypes = issueTypes
        )

        // Keep user on screen and keep their edits if the save fails.
        viewModel.updateReport(requireActivity(), current.externalId, request)
            .thenAccept { saved ->
                requireActivity().runOnUiThread {
                    val binding = _binding ?: return@runOnUiThread
                    loadedReport = saved
                    originalReport = saved
                    binding.descriptionInput.setText(saved.description.orEmpty())
                    binding.locationInput.setText(bestLocationText(saved))
                    selectedIssueTypeTags.clear()
                    selectedIssueTypeTags.addAll(saved.issueTypes)
                    populateIssueTypeChips()
                    setEditing(false)
                    Snackbar.make(binding.root, "Saved", Snackbar.LENGTH_SHORT).show()
                    findNavController().previousBackStackEntry?.savedStateHandle
                        ?.set(UserDashboardRefresh.USER_REPORTS_REFRESH_REQUIRED, true)
                }
            }
            .exceptionally { thrown ->
                requireActivity().runOnUiThread {
                    val binding = _binding ?: return@runOnUiThread
                    Snackbar.make(
                        binding.root,
                        thrown?.message ?: "Save failed",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                null
            }
    }

    private fun bestLocationText(report: IssueReport): String {
        val description = report.locationDescription?.trim()
        if (!description.isNullOrEmpty()) {
            return description
        }
        return "${report.latitude}, ${report.longitude}"
    }

    private fun populateIssueTypeChips() {
        val binding = _binding ?: return
        binding.issueTypeChipGroup.removeAllViews()
        for (issueType in availableIssueTypes) {
            val tag = issueType.issueTypeTag
            val chip = Chip(requireContext())
            chip.setChipDrawable(ChipDrawable.createFromAttributes(
                requireContext(),
                null,
                0,
                com.google.android.material.R.style.Widget_Material3_Chip_Filter
            ))
            chip.text = tag
            chip.isCheckable = true
            chip.isChecked = selectedIssueTypeTags.contains(tag)
            chip.isEnabled = editing
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (!editing) {
                    return@setOnCheckedChangeListener
                }
                if (isChecked) {
                    selectedIssueTypeTags.add(tag)
                } else {
                    selectedIssueTypeTags.remove(tag)
                }
            }
            binding.issueTypeChipGroup.addView(chip)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
