package edu.cnm.deepdive.seesomethingabq.controller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import edu.cnm.deepdive.seesomethingabq.databinding.ItemManagerUserBinding
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary
import java.util.function.Consumer

/**
 * Paging adapter for manager-visible users.
 */
class ManagerUserAdapter(
  private val onItemClick: Consumer<UserProfileSummary>
) :
  PagingDataAdapter<UserProfileSummary, ManagerUserAdapter.ViewHolder>(DIFF_CALLBACK) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = ItemManagerUserBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
    return ViewHolder(binding, onItemClick)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    getItem(position)?.let { holder.bind(it) }
  }

  /**
   * ViewHolder for a manager-visible user summary row.
   */
  class ViewHolder(
    private val binding: ItemManagerUserBinding,
    private val onItemClick: Consumer<UserProfileSummary>
  ) : RecyclerView.ViewHolder(binding.root) {

    /**
     * Binds the provided user summary to the row views.
     *
     * @param user user summary to display.
     */
    fun bind(user: UserProfileSummary) {
      binding.displayName.text = user.displayName.ifBlank { "(No name)" }
      binding.email.text = user.email.ifBlank { "(No email)" }
      binding.status.text = statusText(user.manager, user.userEnabled)
      binding.root.setOnClickListener { onItemClick.accept(user) }
    }

    private fun statusText(isManager: Boolean, enabled: Boolean): String {
      val managerText = if (isManager) "Manager" else "Not manager"
      val enabledText = if (enabled) "Active" else "Disabled"
      return "$managerText \u2022 $enabledText"
    }
  }

  companion object {
    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserProfileSummary>() {
      override fun areItemsTheSame(oldItem: UserProfileSummary, newItem: UserProfileSummary) =
        oldItem.externalId == newItem.externalId

      override fun areContentsTheSame(oldItem: UserProfileSummary, newItem: UserProfileSummary) =
        oldItem == newItem
    }
  }

}
