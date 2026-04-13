package edu.cnm.deepdive.seesomethingabq.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.cnm.deepdive.seesomethingabq.model.dto.ReportImageDto

/**
 * Holds edit-session state for report images on the user report detail/edit screen.
 *
 * This is UI-only staging state; it must not trigger any server mutations.
 */
class ReportDetailImageEditViewModel : ViewModel() {

  /**
   * Union type representing an image that should be shown in the report image grid.
   */
  sealed interface VisibleImageItem {
    /** Image currently stored on the server for the report. */
    data class Server(val image: ReportImageDto) : VisibleImageItem

    /** Image selected on-device and staged for upload during the current edit session. */
    data class Local(val uri: Uri) : VisibleImageItem
  }

  /**
   * Immutable snapshot of the current edit-session state.
   *
   * @property originalServerImages images originally loaded from the server, in display order.
   * @property serverImageIdsStagedForDeletion server image IDs the user has marked for deletion.
   * @property localUrisStagedForUpload local image URIs the user has selected for upload.
   * @property visibleItems combined list used by the UI to render server + staged local items.
   */
  data class State(
    val originalServerImages: List<ReportImageDto> = emptyList(),
    val serverImageIdsStagedForDeletion: Set<String> = emptySet(),
    val localUrisStagedForUpload: List<Uri> = emptyList(),
    val visibleItems: List<VisibleImageItem> = emptyList()
  )

  private val _state = MutableLiveData(State())

  /** Current image edit-session state for the report detail UI. */
  val state: LiveData<State> = _state

  /**
   * Initializes the edit-session state using the server-provided image list.
   *
   * @param images images currently associated with the report on the server.
   */
  fun seedFromReport(images: List<ReportImageDto>) {
    val sorted = images.sortedBy { it.albumOrder }
    _state.value = State(
      originalServerImages = sorted,
      serverImageIdsStagedForDeletion = emptySet(),
      localUrisStagedForUpload = emptyList(),
      visibleItems = sorted.map { VisibleImageItem.Server(it) }
    )
  }

  /**
   * Marks a server image for removal and updates the computed visible list.
   *
   * @param imageId external ID of the server image to delete.
   */
  fun stageRemoveServerImage(imageId: String) {
    val current = _state.value ?: State()
    val updatedDeletionIds = current.serverImageIdsStagedForDeletion + imageId
    _state.value = current.copy(
      serverImageIdsStagedForDeletion = updatedDeletionIds,
      visibleItems = buildVisibleItems(
        current.originalServerImages,
        updatedDeletionIds,
        current.localUrisStagedForUpload
      )
    )
  }

  /**
   * Adds one or more local image URIs to the staged upload list.
   *
   * Duplicate URIs are removed (based on {@link Uri#equals} semantics).
   *
   * @param uris local image URIs selected by the user.
   */
  fun stageAddLocalUris(uris: List<Uri>) {
    if (uris.isEmpty()) {
      return
    }
    val current = _state.value ?: State()
    val updatedLocalUris = (current.localUrisStagedForUpload + uris)
      .distinct() // de-dupe by Uri equality
    _state.value = current.copy(
      localUrisStagedForUpload = updatedLocalUris,
      visibleItems = buildVisibleItems(
        current.originalServerImages,
        current.serverImageIdsStagedForDeletion,
        updatedLocalUris
      )
    )
  }

  /**
   * Removes a previously staged local image URI.
   *
   * @param uri local image URI to remove from staged uploads.
   */
  fun stageRemoveLocalUri(uri: Uri) {
    val current = _state.value ?: State()
    if (!current.localUrisStagedForUpload.contains(uri)) {
      return
    }
    val updatedLocalUris = current.localUrisStagedForUpload.filter { it != uri }
    _state.value = current.copy(
      localUrisStagedForUpload = updatedLocalUris,
      visibleItems = buildVisibleItems(
        current.originalServerImages,
        current.serverImageIdsStagedForDeletion,
        updatedLocalUris
      )
    )
  }

  /**
   * Resets the edit-session to the original server state, clearing any staged deletions/uploads.
   */
  fun resetToOriginal() {
    val current = _state.value ?: State()
    _state.value = current.copy(
      serverImageIdsStagedForDeletion = emptySet(),
      localUrisStagedForUpload = emptyList(),
      visibleItems = current.originalServerImages.map { VisibleImageItem.Server(it) }
    )
  }

  private fun buildVisibleItems(
    originalServerImages: List<ReportImageDto>,
    stagedForDeletion: Set<String>,
    stagedLocalUris: List<Uri>
  ): List<VisibleImageItem> {
    val serverVisible = originalServerImages
      .filter { !stagedForDeletion.contains(it.externalId) }
      .map { VisibleImageItem.Server(it) }
    val localVisible = stagedLocalUris.map { VisibleImageItem.Local(it) }
    return serverVisible + localVisible
  }
}

