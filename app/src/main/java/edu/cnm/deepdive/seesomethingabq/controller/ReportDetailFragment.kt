package edu.cnm.deepdive.seesomethingabq.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentReportDetailBinding
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel

class ReportDetailFragment : Fragment() {

    private lateinit var binding: FragmentReportDetailBinding
    private val viewModel: IssueReportViewModel by viewModels()
    private val args: ReportDetailFragmentArgs by navArgs()

    private var loadedReport: IssueReport? = null
    private var editing: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportDetailBinding.inflate(inflater, container, false)

        binding.editButton.setOnClickListener {
            setEditing(true)
        }

        binding.saveButton.setOnClickListener {
            save()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val reportId = args.reportId

        viewModel.getReport(requireActivity(), reportId)
            .thenAccept { report ->
                requireActivity().runOnUiThread {
                    loadedReport = report
                    setEditing(false)

                    // Populate text fields
                    binding.reportDescription.setText(report.description ?: "")
                    binding.reportIssueTypes.setText(report.issueTypes.joinToString(", "))

                    // Setup RecyclerView
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
        binding.reportDescription.isEnabled = editing
        binding.reportDescription.isFocusable = editing
        binding.reportDescription.isFocusableInTouchMode = editing

        binding.reportIssueTypes.isEnabled = editing
        binding.reportIssueTypes.isFocusable = editing
        binding.reportIssueTypes.isFocusableInTouchMode = editing

        binding.saveButton.visibility = if (editing) View.VISIBLE else View.GONE
        binding.editButton.visibility = if (editing) View.GONE else View.VISIBLE
    }

    private fun save() {
        val current = loadedReport ?: return

        val description = binding.reportDescription.text?.toString()?.trim().orEmpty()
        val issueTypes = binding.reportIssueTypes.text
            ?.toString()
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        val request = IssueReportRequest(
            textDescription = description,
            latitude = current.latitude,
            longitude = current.longitude,
            streetCoordinate = null, // Not currently part of IssueReport DTO; preserve server value by omission.
            locationDescription = current.locationDescription,
            issueTypes = issueTypes
        )

        // Keep user on screen and keep their edits if the save fails.
        viewModel.updateReport(requireActivity(), current.externalId, request)
            .thenAccept { saved ->
                requireActivity().runOnUiThread {
                    loadedReport = saved
                    binding.reportDescription.setText(saved.description ?: "")
                    binding.reportIssueTypes.setText(saved.issueTypes.joinToString(", "))
                    setEditing(false)
                    Snackbar.make(binding.root, "Saved", Snackbar.LENGTH_SHORT).show()
                }
            }
            .exceptionally { thrown ->
                requireActivity().runOnUiThread {
                    Snackbar.make(
                        binding.root,
                        thrown?.message ?: "Save failed",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                null
            }
    }
}
