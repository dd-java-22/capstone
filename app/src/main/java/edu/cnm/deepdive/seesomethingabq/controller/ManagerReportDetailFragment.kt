package edu.cnm.deepdive.seesomethingabq.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import dagger.hilt.android.AndroidEntryPoint
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentManagerReportDetailBinding
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueTypeViewModel

@AndroidEntryPoint
class ManagerReportDetailFragment : Fragment() {

  private var _binding: FragmentManagerReportDetailBinding? = null
  private val binding: FragmentManagerReportDetailBinding
    get() = _binding!!

  private val viewModel: IssueReportViewModel by viewModels()
  private val issueTypeViewModel: IssueTypeViewModel by viewModels()
  private val args: ManagerReportDetailFragmentArgs by navArgs()

  private var loadedReport: IssueReport? = null
  private val selectedIssueTypeTags: MutableSet<String> = linkedSetOf()
  private var availableIssueTypes: List<IssueType> = emptyList()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentManagerReportDetailBinding.inflate(inflater, container, false)

    binding.descriptionInput.isEnabled = false
    binding.descriptionInput.isFocusable = false
    binding.locationInput.isEnabled = false
    binding.locationInput.isFocusable = false

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
          selectedIssueTypeTags.clear()
          selectedIssueTypeTags.addAll(report.issueTypes)

          binding.descriptionInput.setText(report.description.orEmpty())
          binding.locationInput.setText(bestLocationText(report))
          populateIssueTypeChips()

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
      chip.setChipDrawable(
        ChipDrawable.createFromAttributes(
          requireContext(),
          null,
          0,
          com.google.android.material.R.style.Widget_Material3_Chip_Filter
        )
      )
      chip.text = tag
      chip.isCheckable = true
      chip.isChecked = selectedIssueTypeTags.contains(tag)
      chip.isEnabled = false
      chip.isClickable = false
      binding.issueTypeChipGroup.addView(chip)
    }
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }

}

