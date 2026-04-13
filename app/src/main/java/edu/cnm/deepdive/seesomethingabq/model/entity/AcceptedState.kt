package edu.cnm.deepdive.seesomethingabq.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Room entity and API DTO representing an accepted-state/status option for issue reports.
 *
 * @property acceptedStateId local Room primary key.
 * @property statusTag short tag identifier (used in API payloads and UI).
 * @property statusTagDescription human-readable description of the status tag.
 */
@Entity(tableName = "acceptedState")
data class AcceptedState(
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "accepted_state_id")
  val acceptedStateId: Long = 0,
  @Expose
  @SerializedName("statusTag")
  @ColumnInfo(name = "status_tag")
  val statusTag: String = "",
  @Expose
  @SerializedName("statusTagDescription")
  @ColumnInfo(name = "status_tag_description")
  val statusTagDescription: String = ""
)

