package edu.cnm.deepdive.seesomethingabq.controller

/**
 * Keys used for navigation-result style refresh triggers back to [UserDashboardFragment].
 *
 * This mirrors the manager-side pattern (SavedStateHandle boolean flag) while keeping the
 * mechanism local to the user dashboard report list flow.
 */
object UserDashboardRefresh {

  const val USER_REPORTS_REFRESH_REQUIRED: String = "user_reports_refresh_required"

}

