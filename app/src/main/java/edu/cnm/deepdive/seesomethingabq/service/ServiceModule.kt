package edu.cnm.deepdive.seesomethingabq.service

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ServiceModule {

  @Binds
  @Singleton
  fun bindUserService(implementation: UserServiceImpl): UserService

  @Binds
  @Singleton
  fun bindIssueTypeService(implementation: IssueTypeServiceImpl): IssueTypeService

  @Binds
  @Singleton
  fun bindCurrentLocationProvider(
    implementation: LocationManagerCurrentLocationProvider
  ): CurrentLocationProvider

  @Binds
  @Singleton
  fun bindLocationSearchProvider(
    implementation: GeocoderLocationSearchProvider
  ): LocationSearchProvider

}
