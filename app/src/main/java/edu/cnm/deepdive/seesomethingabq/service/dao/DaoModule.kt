package edu.cnm.deepdive.seesomethingabq.service.dao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cnm.deepdive.seesomethingabq.service.database.SeeSomethingDatabase
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
/**
 * Hilt module providing Room DAOs from [SeeSomethingDatabase].
 */
class DaoModule {

  /**
   * Provides the [UserDao] instance.
   *
   * @param database Room database.
   * @return user DAO.
   */
  @Provides
  @Singleton
  fun provideUserDao(database: SeeSomethingDatabase) = database.getUserDao()

  /**
   * Provides the [IssueTypeDao] instance.
   *
   * @param database Room database.
   * @return issue type DAO.
   */
  @Provides
  @Singleton
  fun provideIssueTypeDao(database: SeeSomethingDatabase) = database.getIssueTypeDao()

  /**
   * Provides the [AcceptedStateDao] instance.
   *
   * @param database Room database.
   * @return accepted state DAO.
   */
  @Provides
  @Singleton
  fun provideAcceptedStateDao(database: SeeSomethingDatabase) = database.getAcceptedStateDao()

}
