package edu.cnm.deepdive.seesomethingabq.controller.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary

/**
 * Paging adapter for manager-visible users.
 *
 * Uses a built-in Android row layout to avoid introducing new UI resources in this pass.
 */
class ManagerUserAdapter :
  PagingDataAdapter<UserProfileSummary, ManagerUserAdapter.ViewHolder>(DIFF_CALLBACK) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(android.R.layout.simple_list_item_2, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    getItem(position)?.let { holder.bind(it) }
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val text1: TextView = itemView.findViewById(android.R.id.text1)
    private val text2: TextView = itemView.findViewById(android.R.id.text2)

    fun bind(user: UserProfileSummary) {
      text1.text = user.displayName
      text2.text = user.email
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

