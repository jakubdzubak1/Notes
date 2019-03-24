package sk.demo.sdk.internal

import android.util.Log
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import sk.demo.sdk.BuildConfig
import java.util.concurrent.TimeUnit

abstract class AbsCommunication<API_INTERFACE>(notesContext: NotesContext, apiClass: Class<API_INTERFACE>) {

    private val objectMapper: ObjectMapper = ObjectMapper()
    private val client: OkHttpClient

    val api: API_INTERFACE

    init {
        objectMapper.findAndRegisterModules()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        val clientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor { Log.d("OkHttp", it) }
            logging.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(logging)
        }
        clientBuilder.connectTimeout(1, TimeUnit.MINUTES)
        clientBuilder.readTimeout(1, TimeUnit.MINUTES)
        clientBuilder.writeTimeout(1, TimeUnit.MINUTES)
        client = clientBuilder.build()

        val retrofitBuilder = Retrofit.Builder()
        retrofitBuilder.baseUrl(notesContext.environment.getUrl())
        retrofitBuilder.client(client)
        retrofitBuilder.addConverterFactory(JacksonConverterFactory.create(objectMapper))
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        retrofitBuilder.validateEagerly(true)
        val retrofit = retrofitBuilder.build()
        api = retrofit.create<API_INTERFACE>(apiClass)
    }

}

class Communication(notesContext: NotesContext) : AbsCommunication<NotesApi>(notesContext, NotesApi::class.java)