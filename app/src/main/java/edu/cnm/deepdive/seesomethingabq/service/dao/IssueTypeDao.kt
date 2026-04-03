package edu.cnm.deepdive.seesomethingabq.service.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType

@Dao
interface IssueTypeDao {

  @Insert
  suspend fun insert(issueType: IssueType): Long

  @Insert
  suspend fun insert(issueTypes: List<IssueType>): List<Long>

  @Query("DELETE FROM issueType")
  suspend fun deleteAll()

  @Query("SELECT * FROM issueType ORDER BY issue_type_tag ASC")
  fun getAll(): LiveData<List<IssueType>>

  @Transaction
  suspend fun replaceAll(issueTypes: List<IssueType>) {
    deleteAll()
    insert(issueTypes)
  }

}
