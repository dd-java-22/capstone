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

  sealed interface VisibleImageItem {
    data class Server(val image: ReportImageDto) : VisibleImageItem
    data class Local(val uri: Uri) : VisibleImageItem
  }

  data class State(
    val originalServerImages: List<ReportImageDto> = emptyList(),
    val serverImageIdsStagedForDeletion: Set<String> = emptySet(),
    val localUrisStagedForUpload: List<Uri> = emptyList(),
    val visibleItems: List<VisibleImageItem> = emptyList()
  )

  private val _state = MutableLiveData(State())
  val state: LiveData<State> = _state

  fun seedFromReport(images: List<ReportImageDto>) {
    val sorted = images.sortedBy { it.albumOrder }
    _state.value = State(
      originalServerImages = sorted,
      serverImageIdsStagedForDeletion = emptySet(),
      localUrisStagedForUpload = emptyList(),
      visibleItems = sorted.map { VisibleImageItem.Server(it) }
    )
  }

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

