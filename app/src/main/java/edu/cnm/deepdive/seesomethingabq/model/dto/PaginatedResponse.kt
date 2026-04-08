package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.Expose


/**
 * Generic DTO representing a paginated response returned by the server API.
 *
 * @param T element type.
 * @property content current page of content items.
 * @property totalElements total number of matching elements.
 * @property totalPages total number of pages.
 * @property size requested/returned page size.
 * @property number zero-based page number.
 * @property last whether this page is the last page.
 * @property first whether this page is the first page.
 */
data class PaginatedResponse<T>(
    @Expose
    val content: List<T>,          // This is your list of Summary objects
    @Expose
    val totalElements: Long,
    @Expose
    val totalPages: Int,
    @Expose
    val size: Int,
    @Expose
    val number: Int,               // Current page number
    @Expose
    val last: Boolean,
    @Expose
    val first: Boolean
)
