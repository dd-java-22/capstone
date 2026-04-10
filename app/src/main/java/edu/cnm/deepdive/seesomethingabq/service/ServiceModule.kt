package edu.cnm.deepdive.seesomethingabq.service

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
/**
 * Hilt module binding app service interfaces to their implementations.
 */
interface ServiceModule {

  /**
   * Binds the user service implementation.
   *
   * @param implementation implementation instance.
   * @return bound interface type.
   */
  @Binds
  @Singleton
  fun bindUserService(implementation: UserServiceImpl): UserService

  /**
   * Binds the issue type service implementation.
   *
   * @param implementation implementation instance.
   * @return bound interface type.
   */
  @Binds
  @Singleton
  fun bindIssueTypeService(implementation: IssueTypeServiceImpl): IssueTypeService

  /**
   * Binds the issue report service implementation.
   *
   * @param implementation implementation instance.
   * @return bound interface type.
   */
  @Binds
  @Singleton
  fun bindIssueReportService(implementation: IssueReportServiceImpl): IssueReportService

  /**
   * Binds the manager user service implementation.
   *
   * @param implementation implementation instance.
   * @return bound interface type.
   */
  @Binds
  @Singleton
  fun bindManagerUserService(implementation: ManagerUserServiceImpl): ManagerUserService

  /**
   * Binds the accepted state service implementation.
   *
   * @param implementation implementation instance.
   * @return bound interface type.
   */
  @Binds
  @Singleton
  fun bindAcceptedStateService(implementation: AcceptedStateServiceImpl): AcceptedStateService

}
