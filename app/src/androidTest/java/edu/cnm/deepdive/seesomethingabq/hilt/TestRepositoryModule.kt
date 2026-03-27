package edu.cnm.deepdive.seesomethingabq.hilt

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import edu.cnm.deepdive.seesomethingabq.service.repository.GoogleAuthRepository
import edu.cnm.deepdive.seesomethingabq.service.repository.FakeGoogleAuthRepository
import edu.cnm.deepdive.seesomethingabq.service.repository.RepositoryModule
import jakarta.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class TestRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGoogleAuthRepository(impl: FakeGoogleAuthRepository): GoogleAuthRepository

}
