package edu.cnm.deepdive.seesomethingabq.controller

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.cnm.deepdive.seesomethingabq.R
import edu.cnm.deepdive.seesomethingabq.model.dto.ReportImageDto
import edu.cnm.deepdive.seesomethingabq.viewmodel.ReportDetailImageEditViewModel.VisibleImageItem
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet

/**
 * Report detail/edit-only adapter that can display a combined list of existing server images and
 * newly staged local images. Remove controls are only active when [editing] is true.
 *
 * This adapter is UI-only; callers must not trigger server mutations from remove actions.
 */
class ReportDetailImageEditAdapter(
  private val activity: Activity,
  private val reportId: String,
  private val downloadToCache: (reportId: String, imageId: String, mimeType: String?) -> CompletableFuture<File>,
  private val onRemoveServerImage: (imageId: String) -> Unit,
  private val onRemoveLocalImage: (uri: Uri) -> Unit
) : RecyclerView.Adapter<ReportDetailImageEditAdapter.Holder>() {

  private enum class State {
    LOADING,
    SUCCESS,
    FAILURE
  }

  private val states: MutableMap<String, Pair<State, File?>> = ConcurrentHashMap()
  private val inFlight: MutableSet<String> = ConcurrentSkipListSet()

  var editing: Boolean = false
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  private var items: List<VisibleImageItem> = emptyList()

  /**
   * Replaces the current list of visible image items and refreshes the adapter.
   *
   * @param items combined list of server images and staged local images to display.
   */
  fun submitList(items: List<VisibleImageItem>) {
    this.items = items
    notifyDataSetChanged()
  }

  inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
    val thumbnail: ImageView = view.findViewById(R.id.image_thumbnail)
    val loading: ProgressBar = view.findViewById(R.id.image_loading)
    val remove: ImageButton = view.findViewById(R.id.remove_image_button)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_report_image_editable, parent, false)
    return Holder(view)
  }

  override fun onBindViewHolder(holder: Holder, position: Int) {
    val item = items[position]

    holder.remove.visibility = if (editing) View.VISIBLE else View.GONE
    holder.remove.isEnabled = editing

    when (item) {
      is VisibleImageItem.Local -> bindLocal(holder, item.uri)
      is VisibleImageItem.Server -> bindServer(holder, item.image)
    }
  }

  private fun bindLocal(holder: Holder, uri: Uri) {
    holder.loading.visibility = View.GONE
    Glide.with(holder.thumbnail)
      .load(uri)
      .placeholder(R.drawable.ic_image_placeholder)
      .error(R.drawable.ic_broken_image)
      .centerCrop()
      .into(holder.thumbnail)
    holder.remove.setOnClickListener {
      if (editing) {
        onRemoveLocalImage(uri)
      }
    }
  }

  private fun bindServer(holder: Holder, image: ReportImageDto) {
    val imageId = image.externalId
    val (state, file) = states[imageId] ?: (State.LOADING to null)

    when (state) {
      State.SUCCESS -> {
        holder.loading.visibility = View.GONE
        Glide.with(holder.thumbnail)
          .load(file)
          .placeholder(R.drawable.ic_image_placeholder)
          .error(R.drawable.ic_broken_image)
          .centerCrop()
          .into(holder.thumbnail)
      }

      State.FAILURE -> {
        holder.loading.visibility = View.GONE
        holder.thumbnail.setImageResource(R.drawable.ic_broken_image)
      }

      State.LOADING -> {
        holder.loading.visibility = View.VISIBLE
        holder.thumbnail.setImageResource(R.drawable.ic_image_placeholder)
      }
    }

    holder.remove.setOnClickListener {
      if (editing) {
        onRemoveServerImage(imageId)
      }
    }

    if (state == State.LOADING && inFlight.add(imageId)) {
      downloadToCache(reportId, imageId, image.mimeType)
        .thenAccept { cached ->
          states[imageId] = State.SUCCESS to cached
          activity.runOnUiThread { notifyImageChanged(imageId) }
        }
        .exceptionally { _ ->
          states[imageId] = State.FAILURE to null
          activity.runOnUiThread { notifyImageChanged(imageId) }
          null
        }
        .whenComplete { _, _ ->
          inFlight.remove(imageId)
        }
    }
  }

  private fun notifyImageChanged(imageId: String) {
    val index = items.indexOfFirst { it is VisibleImageItem.Server && it.image.externalId == imageId }
    if (index >= 0) {
      notifyItemChanged(index)
    }
  }

  override fun getItemCount() = items.size
}

