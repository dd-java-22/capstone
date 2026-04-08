package edu.cnm.deepdive.seesomethingabq.service.proxy

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.cnm.deepdive.seesomethingabq.R
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant

@Module
@InstallIn(SingletonComponent::class)
/**
 * Hilt module providing networking dependencies (Gson, OkHttp, Retrofit).
 */
class ProxyModule {

  /**
   * Provides a configured [Gson] instance.
   *
   * @param adapter type adapter for [Instant] values.
   * @return Gson instance.
   */
  @Provides
  @Singleton
  fun provideGson(adapter: TypeAdapter<Instant>): Gson = GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .registerTypeAdapter(Instant::class.java, adapter)
    .create()

  /**
   * Provides an [OkHttpClient] with request/response logging.
   *
   * @param context application context used to read configuration resources.
   * @return configured HTTP client.
   */
  @Provides
  @Singleton
  fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
    val level = HttpLoggingInterceptor.Level.valueOf(context.getString(R.string.log_level).uppercase())

    val interceptor = HttpLoggingInterceptor().apply { setLevel(level) }

    return OkHttpClient.Builder().addInterceptor(interceptor).build()
  }

  /**
   * Provides the Retrofit API client for the server.
   *
   * @param context application context used to read configuration resources.
   * @param client HTTP client.
   * @param gson Gson instance.
   * @return web service interface implementation.
   */
  @Provides
  @Singleton
  fun provideSpeedometerWebService(
    @ApplicationContext context: Context,
    client: OkHttpClient,
    gson: Gson,
  ) : SeeSomethingWebService = Retrofit.Builder()
    .baseUrl(context.getString(R.string.base_url))
    .client(client)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()
    .create(SeeSomethingWebService::class.java)

}
