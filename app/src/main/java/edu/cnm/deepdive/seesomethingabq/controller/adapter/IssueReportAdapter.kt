package edu.cnm.deepdive.seesomethingabq.controller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import edu.cnm.deepdive.seesomethingabq.databinding.ItemIssueReportBinding
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Paging adapter for rendering [IssueReportSummary] items in a RecyclerView.
 *
 * @param onClick invoked when the user taps a report row.
 */
class IssueReportAdapter(
  private val onClick: (IssueReportSummary) -> Unit = {}
) :
  PagingDataAdapter<IssueReportSummary, IssueReportAdapter.ViewHolder>(DIFF_CALLBACK) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemIssueReportBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
    return ViewHolder(binding, onClick)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = getItem(position)
    item?.let { holder.bind(it) }
  }

  /**
   * ViewHolder for an issue report summary row.
   */
  class ViewHolder(
    private val binding: ItemIssueReportBinding,
    private val onClick: (IssueReportSummary) -> Unit
  ) :
    RecyclerView.ViewHolder(binding.root) {

    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
      .withZone(ZoneId.systemDefault())

    /**
     * Binds the provided report summary to the row views.
     *
     * @param issueReport report summary to display.
     */
    fun bind(issueReport: IssueReportSummary) {
      binding.description.text = issueReport.description
      binding.acceptedState.text = issueReport.acceptedState
      binding.timeFirstReported.text = dateTimeFormatter.format(issueReport.timeFirstReported)
      binding.timeLastModified.text = dateTimeFormatter.format(issueReport.timeLastModified)
      binding.root.setOnClickListener { onClick(issueReport) }
    }
  }

  companion object {
    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<IssueReportSummary>() {
      override fun areItemsTheSame(
        oldItem: IssueReportSummary,
        newItem: IssueReportSummary
      ): Boolean {
        return oldItem.externalId == newItem.externalId
      }

      override fun areContentsTheSame(
        oldItem: IssueReportSummary,
        newItem: IssueReportSummary
      ): Boolean {
        return oldItem == newItem
      }
    }
  }
}
