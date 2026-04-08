package edu.cnm.deepdive.seesomethingabq.service.repository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import jakarta.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
/**
 * Hilt module binding repository interfaces to their implementations.
 */
public interface RepositoryModule {

  /**
   * Binds the Google auth repository implementation.
   *
   * @param impl implementation instance.
   * @return bound interface type.
   */
  @Binds
  @Singleton
  GoogleAuthRepository bindGoogleAuthRepository(GoogleAuthRepositoryImpl impl);

}
