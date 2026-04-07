package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.Expose


// Written by Gemini
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