package edu.cnm.deepdive.seesomethingabq.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(
  tableName = "issueType",
  indices = [
    Index(value = ["issue_type_tag"], unique = true)
  ]
)
data class IssueType(
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "issue_type_id")
  val id: Long = 0,

  @Expose
  @SerializedName("issueTypeTag")
  @ColumnInfo(name = "issue_type_tag", collate = ColumnInfo.NOCASE)
  val issueTypeTag: String,

  @Expose
  @SerializedName("issueTypeDescription")
  @ColumnInfo(name = "issue_type_description")
  val issueTypeDescription: String,
)

