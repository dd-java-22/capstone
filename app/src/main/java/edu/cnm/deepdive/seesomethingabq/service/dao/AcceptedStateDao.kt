package edu.cnm.deepdive.seesomethingabq.service.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState

@Dao
interface AcceptedStateDao {

  @Insert
  suspend fun insert(acceptedState: AcceptedState): Long

  @Insert
  suspend fun insert(acceptedStates: List<AcceptedState>): List<Long>

  @Query("DELETE FROM acceptedState")
  suspend fun deleteAll()

  @Query("SELECT * FROM acceptedState ORDER BY status_tag ASC")
  fun getAll(): LiveData<List<AcceptedState>>

  @Transaction
  suspend fun replaceAll(acceptedStates: List<AcceptedState>) {
    deleteAll()
    insert(acceptedStates)
  }

}

