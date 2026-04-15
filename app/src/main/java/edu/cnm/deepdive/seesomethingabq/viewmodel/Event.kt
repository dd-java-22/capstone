package edu.cnm.deepdive.seesomethingabq.viewmodel

/**
 * Wrapper for data exposed via LiveData that represents a one-time event.
 *
 * This prevents events (e.g., toasts, navigation) from being re-consumed when
 * an observer is re-attached after a configuration change or fragment recreation.
 */
class Event<T>(private val content: T) {

  private var hasBeenHandled: Boolean = false

  fun getContentIfNotHandled(): T? =
    if (hasBeenHandled) {
      null
    } else {
      hasBeenHandled = true
      content
    }

  fun peekContent(): T = content
}

