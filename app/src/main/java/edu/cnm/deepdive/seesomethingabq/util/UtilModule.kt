package edu.cnm.deepdive.seesomethingabq.util

import com.google.gson.TypeAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Instant

@Module
@InstallIn(SingletonComponent::class)
/**
 * Hilt module binding utility types.
 */
interface UtilModule {

  /**
   * Binds the [InstantTypeAdapter] as a Gson [TypeAdapter] for [Instant].
   *
   * @param adapter adapter instance.
   * @return bound type adapter.
   */
  @Binds
  fun bindInstantTypeAdapter(adapter: InstantTypeAdapter): TypeAdapter<Instant>

}
