package edu.cnm.deepdive.seesomethingabq.util

import com.google.gson.TypeAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Instant

@Module
@InstallIn(SingletonComponent::class)
interface UtilModule {

  @Binds
  fun bindInstantTypeAdapter(adapter: InstantTypeAdapter): TypeAdapter<Instant>

}