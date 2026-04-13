package edu.cnm.deepdive.seesomethingabq.service.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState

/**
 * Room DAO for persisting and querying accepted-state reference data.
 */
@Dao
interface AcceptedStateDao {

  /**
   * Inserts an accepted state row.
   *
   * @param acceptedState entity to insert.
   * @return generated row ID.
   */
  @Insert
  suspend fun insert(acceptedState: AcceptedState): Long

  /**
   * Inserts multiple accepted state rows.
   *
   * @param acceptedStates entities to insert.
   * @return generated row IDs.
   */
  @Insert
  suspend fun insert(acceptedStates: List<AcceptedState>): List<Long>

  /**
   * Deletes all accepted state rows.
   */
  @Query("DELETE FROM acceptedState")
  suspend fun deleteAll()

  /**
   * Returns all accepted states, ordered by status tag.
   *
   * @return live data stream of accepted states.
   */
  @Query("SELECT * FROM acceptedState ORDER BY status_tag ASC")
  fun getAll(): LiveData<List<AcceptedState>>

  /**
   * Replaces all accepted states in a single transaction.
   *
   * @param acceptedStates replacement list.
   */
  @Transaction
  suspend fun replaceAll(acceptedStates: List<AcceptedState>) {
    deleteAll()
    insert(acceptedStates)
  }

}

