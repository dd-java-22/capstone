package edu.cnm.deepdive.seesomethingabq.service

import java.io.File

/**
 * Helpers for naming/locating cached report images.
 *
 * Keep this stable so both user/manager screens can reuse cached files.
 */
object ReportImageCache {

  /**
   * Returns a file extension appropriate for the provided image MIME type.
   *
   * @param mimeType image MIME type (e.g., {@code image/jpeg}); may be {@code null}.
   * @return lowercase extension without leading dot.
   */
  fun extensionFor(mimeType: String?): String =
    when (mimeType?.lowercase()) {
      "image/jpeg", "image/jpg" -> "jpg"
      "image/png" -> "png"
      "image/webp" -> "webp"
      else -> "img"
    }

  /**
   * Returns the directory used for cached report images, creating it if necessary.
   *
   * @param baseCacheDir app cache directory (typically {@code context.cacheDir}).
   * @return cache directory for report images.
   */
  fun cacheDir(baseCacheDir: File): File =
    File(baseCacheDir, "report_images").apply { mkdirs() }

  /**
   * Returns the cache file location for a specific report image.
   *
   * This does not validate that the file exists.
   *
   * @param baseCacheDir app cache directory (typically {@code context.cacheDir}).
   * @param reportId external report identifier.
   * @param imageId external image identifier.
   * @param mimeType image MIME type (used to select an extension); may be {@code null}.
   * @return cache file path for the requested image.
   */
  fun cacheFile(baseCacheDir: File, reportId: String, imageId: String, mimeType: String?): File {
    val dir = cacheDir(baseCacheDir)
    val ext = extensionFor(mimeType)
    return File(dir, "report_${reportId}_image_${imageId}.$ext")
  }

}

