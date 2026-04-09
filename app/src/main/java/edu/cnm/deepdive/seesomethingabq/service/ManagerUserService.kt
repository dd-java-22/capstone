package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import androidx.paging.Pager
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary
import java.util.concurrent.CompletableFuture
import java.util.UUID

/**
 * Service for retrieving manager-visible user data from the server API.
 */
interface ManagerUserService {

  /**
   * Retrieves a single user profile summary by external ID for manager views.
   *
   * @param activity activity used for authentication flows.
   * @param externalId user external ID (UUID).
   * @return future completing with the user profile summary.
   */
  fun getManagerUser(
    activity: Activity,
    externalId: UUID
  ): CompletableFuture<UserProfileSummary>

  /**
   * Retrieves a single paginated page of user profile summaries for manager views.
   *
   * @param activity activity used for authentication flows.
   * @param page zero-based page number.
   * @param size page size.
   * @return future completing with a paginated response.
   */
  fun getManagerUsersPage(
    activity: Activity,
    page: Int = 0,
    size: Int = 10
  ): CompletableFuture<PaginatedResponse<UserProfileSummary>>

  /**
   * Creates a paging [Pager] for manager-visible user profiles.
   *
   * @param activity activity used for authentication flows.
   * @return pager producing [UserProfileSummary] items.
   */
  fun getManagerUsersPager(activity: Activity): Pager<Int, UserProfileSummary>

  /**
   * Sets (grants/revokes) manager authorization status for a user.
   *
   * @param activity activity used for authentication flows.
   * @param externalId user external ID (UUID).
   * @param isManager desired manager status.
   * @return future completing with the updated user profile summary.
   */
  fun setManagerStatus(
    activity: Activity,
    externalId: UUID,
    isManager: Boolean
  ): CompletableFuture<UserProfileSummary>

  /**
   * Sets enabled/active status for a user.
   *
   * @param activity activity used for authentication flows.
   * @param externalId user external ID (UUID).
   * @param isEnabled desired enabled status.
   * @return future completing with the updated user profile summary.
   */
  fun setEnabledStatus(
    activity: Activity,
    externalId: UUID,
    isEnabled: Boolean
  ): CompletableFuture<UserProfileSummary>

}

