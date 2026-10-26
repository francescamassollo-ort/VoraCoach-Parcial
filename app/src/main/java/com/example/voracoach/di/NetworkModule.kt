package com.example.voracoach.di

import com.example.voracoach.BuildConfig
import com.example.voracoach.data.remote.WorkoutApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Módulo de Hilt para la red. Un @Module le explica a Hilt cómo construir objetos que
// no puede crear solo (Retrofit y OkHttp se arman con builders, no con un constructor @Inject).
// Hilt genera a partir de esto el código de Dagger que arma el grafo de dependencias.
//
// @InstallIn(SingletonComponent): estos objetos viven mientras viva la app.
// @Singleton: se crean una sola vez y se reutilizan (crear un Retrofit por pedido sería caro).
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // En debug muestra cada request y respuesta en Logcat (filtrar por "okhttp").
        // En release no registra nada, para no exponer datos en los logs.
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            // Sin conexión, que el error llegue en segundos y no deje la pantalla cargando.
            // 15 s de lectura alcanzan para probar la demora del mock (X-Mock-Delay-Ms hasta 10 s).
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Hilt ya sabe construir Retrofit (función de arriba), así que lo recibe como parámetro.
    // retrofit.create genera la implementación de la interfaz WorkoutApi.
    @Provides
    @Singleton
    fun provideWorkoutApi(retrofit: Retrofit): WorkoutApi =
        retrofit.create(WorkoutApi::class.java)
}
