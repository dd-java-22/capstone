package edu.cnm.deepdive.seesomethingabq.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentReportDetailBinding
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel

class ReportDetailFragment : Fragment() {

    private lateinit var binding: FragmentReportDetailBinding
    private val viewModel: IssueReportViewModel by viewModels()
    private val args: ReportDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val reportId = args.reportId

        viewModel.getReport(requireActivity(), reportId)
            .thenAccept { report ->
                requireActivity().runOnUiThread {

                    // Populate text fields
                    binding.reportDescription.text = report.description
                    binding.reportIssueTypes.text = report.issueTypes.joinToString(", ")

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
}
