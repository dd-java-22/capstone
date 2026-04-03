package edu.cnm.deepdive.seesomethingabq.service.dao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cnm.deepdive.seesomethingabq.service.database.SeeSomethingDatabase
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

  @Provides
  @Singleton
  fun provideUserDao(database: SeeSomethingDatabase) = database.getUserDao()

  @Provides
  @Singleton
  fun provideIssueTypeDao(database: SeeSomethingDatabase) = database.getIssueTypeDao()

}
