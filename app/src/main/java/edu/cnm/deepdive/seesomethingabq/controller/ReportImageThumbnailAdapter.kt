package edu.cnm.deepdive.seesomethingabq.controller

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.cnm.deepdive.seesomethingabq.R
import edu.cnm.deepdive.seesomethingabq.model.dto.ReportImageDto
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet

/**
 * Shared, read-only thumbnail adapter for report-attached images.
 *
 * The [downloadToCache] function should perform an authenticated download and return the cached file.
 */
class ReportImageThumbnailAdapter(
  private val activity: Activity,
  private val reportId: String,
  private val images: List<ReportImageDto>,
  private val downloadToCache: (reportId: String, imageId: String, mimeType: String?) -> CompletableFuture<File>
) : RecyclerView.Adapter<ReportImageThumbnailAdapter.Holder>() {

  private enum class State {
    LOADING,
    SUCCESS,
    FAILURE
  }

  private val states: MutableMap<String, Pair<State, File?>> = ConcurrentHashMap()
  private val inFlight: MutableSet<String> = ConcurrentSkipListSet()

  private fun notifyImageChanged(imageId: String) {
    val index = images.indexOfFirst { it.externalId == imageId }
    if (index >= 0) {
      notifyItemChanged(index)
    }
  }

  inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
    val thumbnail: ImageView = view.findViewById(R.id.image_thumbnail)
    val loading: ProgressBar = view.findViewById(R.id.image_loading)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_report_image, parent, false)
    return Holder(view)
  }

  override fun onBindViewHolder(holder: Holder, position: Int) {
    val image = images[position]

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

    // Kick off download once per imageId (best-effort; no retries in this pass).
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

  override fun getItemCount() = images.size

}

