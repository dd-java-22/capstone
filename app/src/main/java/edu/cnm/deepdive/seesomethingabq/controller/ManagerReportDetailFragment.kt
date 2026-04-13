package edu.cnm.deepdive.seesomethingabq.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import dagger.hilt.android.AndroidEntryPoint
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentManagerReportDetailBinding
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportStatusUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportTypesUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.viewmodel.AcceptedStateViewModel
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueTypeViewModel
import java.util.concurrent.CompletableFuture

/**
 * Fragment for manager-only review and updates of an issue report.
 *
 * This screen allows a manager to update report status and issue-type tags, and then persists those
 * changes via the backing ViewModels.
 */
@AndroidEntryPoint
class ManagerReportDetailFragment : Fragment() {

  companion object {
    private const val MANAGER_ISSUE_REPORTS_REFRESH_REQUIRED =
      "manager_issue_reports_refresh_required"
  }

  private var _binding: FragmentManagerReportDetailBinding? = null
  private val binding: FragmentManagerReportDetailBinding
    get() = _binding!!

  private val viewModel: IssueReportViewModel by viewModels()
  private val issueTypeViewModel: IssueTypeViewModel by viewModels()
  private val acceptedStateViewModel: AcceptedStateViewModel by viewModels()
  private val args: ManagerReportDetailFragmentArgs by navArgs()

  private var loadedReport: IssueReport? = null
  private var originalReport: IssueReport? = null
  private val selectedIssueTypeTags: MutableSet<String> = linkedSetOf()
  private val originalIssueTypeTags: MutableSet<String> = linkedSetOf()
  private var availableIssueTypes: List<IssueType> = emptyList()
  private var acceptedStateTag: String = "Unknown"
  private var originalAcceptedStateTag: String = "Unknown"
  private var editing: Boolean = false
  private var mutationInProgress: Boolean = false

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
    binding.acceptedStateValue.setOnItemClickListener { _, _, position, _ ->
      if (!editing) {
        return@setOnItemClickListener
      }
      val value = binding.acceptedStateValue.adapter?.getItem(position) as? String
      acceptedStateTag = value ?: acceptedStateTag
    }

    binding.editButton.setOnClickListener { setEditing(true) }
    binding.saveButton.setOnClickListener { save() }
    binding.cancelButton.setOnClickListener { cancelEdits() }

    setEditing(false)

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

    acceptedStateViewModel.acceptedStates.observe(viewLifecycleOwner) { acceptedStates ->
      val binding = _binding ?: return@observe
      if (acceptedStates != null) {
        val tags = acceptedStates.map { it.statusTag }
        binding.acceptedStateValue.setAdapter(
          ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, tags)
        )
        // Keep showing the currently selected value after adapters swap.
        binding.acceptedStateValue.setText(acceptedStateTag, false)
      }
    }
    acceptedStateViewModel.refresh(requireActivity())
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
          originalIssueTypeTags.clear()
          originalIssueTypeTags.addAll(report.issueTypes)
          acceptedStateTag = report.acceptedState ?: "Unknown"
          originalAcceptedStateTag = acceptedStateTag
          setEditing(false)

          binding.descriptionInput.setText(report.description.orEmpty())
          binding.acceptedStateValue.setText(acceptedStateTag, false)
          binding.locationInput.setText(bestLocationText(report))
          populateIssueTypeChips()

          val images = (report.reportImages ?: emptyList())
            .sortedBy { it.albumOrder }

          val adapter = ReportImageThumbnailAdapter(
            requireActivity(),
            report.externalId,
            images
          ) { reportId, imageId, mimeType ->
            viewModel.downloadImageToCache(requireActivity(), reportId, imageId, mimeType)
          }

          if (images.isEmpty()) {
            binding.imageList.visibility = View.GONE
            binding.noImagesPlaceholder.visibility = View.VISIBLE
          } else {
            binding.noImagesPlaceholder.visibility = View.GONE
            binding.imageList.visibility = View.VISIBLE
            binding.imageList.layoutManager = GridLayoutManager(requireContext(), 3)
            binding.imageList.adapter = adapter
          }
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

  private fun setEditing(editing: Boolean) {
    this.editing = editing

    val binding = _binding ?: return

    binding.acceptedStateLayout.isEnabled = editing
    binding.acceptedStateValue.isEnabled = editing
    binding.acceptedStateValue.isFocusable = editing
    binding.acceptedStateValue.isClickable = editing

    populateIssueTypeChips()

    binding.saveButton.visibility = if (editing) View.VISIBLE else View.GONE
    binding.cancelButton.visibility = if (editing) View.VISIBLE else View.GONE
    binding.editButton.visibility = if (editing) View.GONE else View.VISIBLE
  }

  private fun cancelEdits() {
    if (mutationInProgress) {
      return
    }
    val binding = _binding ?: return
    acceptedStateTag = originalAcceptedStateTag
    binding.acceptedStateValue.setText(acceptedStateTag, false)
    selectedIssueTypeTags.clear()
    selectedIssueTypeTags.addAll(originalIssueTypeTags)
    populateIssueTypeChips()
    setEditing(false)
  }

  private fun save() {
    if (mutationInProgress) {
      return
    }
    val original = originalReport ?: return
    val binding = _binding ?: return

    val originalTags = originalIssueTypeTags.toSet()
    val currentTags = selectedIssueTypeTags.toSet()

    val statusChanged = acceptedStateTag != (original.acceptedState ?: "Unknown")
    val tagsChanged = originalTags != currentTags

    if (!statusChanged && !tagsChanged) {
      setEditing(false)
      return
    }

    mutationInProgress = true
    binding.editButton.isEnabled = false
    binding.saveButton.isEnabled = false
    binding.cancelButton.isEnabled = false

    var chain: CompletableFuture<Void?> = CompletableFuture.completedFuture(null)
    if (statusChanged) {
      chain = chain.thenCompose {
        viewModel.updateManagerReportStatus(
          requireActivity(),
          original.externalId,
          IssueReportStatusUpdateRequest(statusTag = acceptedStateTag)
        )
      }
    }
    if (tagsChanged) {
      chain = chain.thenCompose {
        viewModel.replaceManagerReportIssueTypes(
          requireActivity(),
          original.externalId,
          IssueReportTypesUpdateRequest(issueTypeTags = currentTags.toList())
        )
      }
    }

    chain
      .thenCompose {
        viewModel.getReport(requireActivity(), original.externalId)
      }
      .thenAccept { reloaded ->
        requireActivity().runOnUiThread {
          val binding = _binding ?: return@runOnUiThread
          loadedReport = reloaded
          originalReport = reloaded
          originalIssueTypeTags.clear()
          originalIssueTypeTags.addAll(reloaded.issueTypes)
          selectedIssueTypeTags.clear()
          selectedIssueTypeTags.addAll(reloaded.issueTypes)
          acceptedStateTag = reloaded.acceptedState ?: "Unknown"
          originalAcceptedStateTag = acceptedStateTag
          binding.acceptedStateValue.setText(acceptedStateTag, false)
          populateIssueTypeChips()
          setEditing(false)
          Snackbar.make(binding.root, "Saved", Snackbar.LENGTH_SHORT).show()
          markManagerIssueReportsRefreshRequired()
          mutationInProgress = false
          Navigation.findNavController(requireView()).popBackStack()
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
          mutationInProgress = false
          binding.saveButton.isEnabled = true
          binding.cancelButton.isEnabled = true
        }
        null
      }
  }

  private fun markManagerIssueReportsRefreshRequired() {
    val navController: NavController = Navigation.findNavController(requireView())
    val previousEntry: NavBackStackEntry? = navController.previousBackStackEntry
    previousEntry?.savedStateHandle?.set(MANAGER_ISSUE_REPORTS_REFRESH_REQUIRED, true)
  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }

}
