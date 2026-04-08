package edu.cnm.deepdive.seesomethingabq.service.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType

@Dao
/**
 * Room DAO for storing and retrieving [IssueType] records.
 */
interface IssueTypeDao {

  /**
   * Inserts a single issue type.
   *
   * @param issueType issue type to insert.
   * @return row ID of the inserted record.
   */
  @Insert
  suspend fun insert(issueType: IssueType): Long

  /**
   * Inserts multiple issue types.
   *
   * @param issueTypes issue types to insert.
   * @return list of row IDs of inserted records.
   */
  @Insert
  suspend fun insert(issueTypes: List<IssueType>): List<Long>

  /**
   * Deletes all issue types.
   */
  @Query("DELETE FROM issueType")
  suspend fun deleteAll()

  /**
   * Returns all issue types ordered by tag.
   *
   * @return live data stream of issue types.
   */
  @Query("SELECT * FROM issueType ORDER BY issue_type_tag ASC")
  fun getAll(): LiveData<List<IssueType>>

  /**
   * Replaces all stored issue types with the provided list as a single transaction.
   *
   * @param issueTypes replacement issue types.
   */
  @Transaction
  suspend fun replaceAll(issueTypes: List<IssueType>) {
    deleteAll()
    insert(issueTypes)
  }

}
