package sk.demo.sdk

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import sk.demo.sdk.model.note.Note

interface NoteManager {

    fun getNote(id: Int, subscribeOn: Scheduler? = null): Single<Note>

    fun getNotes(subscribeOn: Scheduler? = null): Single<List<Note>>

    fun createNote(note: Note, subscribeOn: Scheduler? = null): Single<Note>

    fun updateNote(id: Int, note: Note, subscribeOn: Scheduler? = null): Single<Note>

    fun removeNote(id: Int, subscribeOn: Scheduler? = null): Completable

}