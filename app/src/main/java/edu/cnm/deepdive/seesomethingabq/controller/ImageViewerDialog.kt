package edu.cnm.deepdive.seesomethingabq.controller

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import edu.cnm.deepdive.seesomethingabq.R

class ImageViewerDialog(
    context: Context,
    private val bitmap: Bitmap
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_image_viewer)

        val imageView = findViewById<ImageView>(R.id.fullscreen_image)
        imageView.setImageBitmap(bitmap)
    }
}

