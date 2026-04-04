package edu.cnm.deepdive.seesomethingabq.model.dto


// Written by Gemini
data class PaginatedResponse<T>(
    val content: List<T>,          // This is your list of Summary objects
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int,               // Current page number
    val last: Boolean,
    val first: Boolean
)