package edu.cnm.deepdive.seesomethingabq.service.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
/**
 * Hilt module providing the app's Room database.
 */
class DatabaseModule {

  /**
   * Provides the [SeeSomethingDatabase] singleton.
   *
   * @param context application context.
   * @return Room database instance.
   */
  @Provides
  @Singleton
  fun provideSeeSomethingDatabase(@ApplicationContext context: Context): SeeSomethingDatabase =
    Room.databaseBuilder(context, SeeSomethingDatabase::class.java, SeeSomethingDatabase.NAME)
      .addMigrations(SeeSomethingDatabase.MIGRATION_1_2, SeeSomethingDatabase.MIGRATION_2_3)
      .build()

}
