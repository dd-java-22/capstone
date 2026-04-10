package edu.cnm.deepdive.seesomethingabq.service

import java.io.File

/**
 * Helpers for naming/locating cached report images.
 *
 * Keep this stable so both user/manager screens can reuse cached files.
 */
object ReportImageCache {

  fun extensionFor(mimeType: String?): String =
    when (mimeType?.lowercase()) {
      "image/jpeg", "image/jpg" -> "jpg"
      "image/png" -> "png"
      "image/webp" -> "webp"
      else -> "img"
    }

  fun cacheDir(baseCacheDir: File): File =
    File(baseCacheDir, "report_images").apply { mkdirs() }

  fun cacheFile(baseCacheDir: File, reportId: String, imageId: String, mimeType: String?): File {
    val dir = cacheDir(baseCacheDir)
    val ext = extensionFor(mimeType)
    return File(dir, "report_${reportId}_image_${imageId}.$ext")
  }

}

