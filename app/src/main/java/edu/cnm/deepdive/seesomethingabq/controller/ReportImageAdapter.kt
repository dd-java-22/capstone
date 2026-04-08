package edu.cnm.deepdive.seesomethingabq.controller

import android.app.Activity
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import edu.cnm.deepdive.seesomethingabq.R
import edu.cnm.deepdive.seesomethingabq.model.dto.ReportImageDto
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel

class ReportImageAdapter(
    private val activity: Activity,
    private val images: List<ReportImageDto>,
    private val viewModel: IssueReportViewModel,
    private val reportId: String
) : RecyclerView.Adapter<ReportImageAdapter.Holder>() {

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.image_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_image, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val image = images[position]

        // Thumbnail placeholder (optional)
        holder.thumbnail.setImageResource(R.drawable.add_photo_alternate_24px)

        holder.thumbnail.setOnClickListener {
            viewModel.downloadImage(activity, reportId, image.externalId)
                .thenAccept { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    activity.runOnUiThread {
                        ImageViewerDialog(activity, bitmap).show()
                    }
                }
        }
    }

    override fun getItemCount() = images.size
}
