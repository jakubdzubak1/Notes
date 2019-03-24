package sk.demo.sdk.internal

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import sk.demo.sdk.model.note.Note

const val NOTES = "notes/"

interface NotesApi {

    @GET("$NOTES{id}")
    fun getNote(@Path("id") id: String): Single<Note>

    @GET("notes")
    fun getNotes(): Single<List<Note>>

    @POST("notes")
    fun createNote(@Body note: Note): Single<Note>

    @PUT("$NOTES{id}")
    fun updateNote(@Path("id") id: String, @Body note: Note): Single<Note>

    @DELETE("$NOTES{id}")
    fun removeNote(@Path("id") id: String): Completable

}