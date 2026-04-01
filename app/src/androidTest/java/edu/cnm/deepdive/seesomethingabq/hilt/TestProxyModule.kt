package edu.cnm.deepdive.seesomethingabq.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import edu.cnm.deepdive.seesomethingabq.service.proxy.ProxyModule
import edu.cnm.deepdive.seesomethingabq.service.proxy.SeeSomethingWebService
import edu.cnm.deepdive.seesomethingabq.service.repository.FakeSeeSomethingWebService
import jakarta.inject.Singleton

@Module
@TestInstallIn(
  components = [SingletonComponent::class],
  replaces = [ProxyModule::class]
)
object TestProxyModule {

  @Provides
  @Singleton
  fun provideSeeSomethingWebService(impl: FakeSeeSomethingWebService): SeeSomethingWebService = impl
}

