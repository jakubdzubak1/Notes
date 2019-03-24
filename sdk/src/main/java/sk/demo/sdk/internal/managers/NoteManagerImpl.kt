package sk.demo.sdk.internal.managers

import android.os.HandlerThread
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import sk.demo.sdk.NoteManager
import sk.demo.sdk.internal.NotesApi
import sk.demo.sdk.model.note.Note

class NoteManagerImpl(api: NotesApi) : BaseManager(api), NoteManager {

    private val scheduler: Scheduler

    init {
        val handlerThread = HandlerThread("NoteManagerThread")
        handlerThread.start()
        scheduler = AndroidSchedulers.from(handlerThread.looper)
    }

    override fun getNote(id: Int, subscribeOn: Scheduler?): Single<Note> = api.getNote(id.toString()).subscribeOn(subscribeOn?: scheduler)

    override fun getNotes(subscribeOn: Scheduler?): Single<List<Note>> = api.getNotes().subscribeOn(subscribeOn?: scheduler)

    override fun createNote(note: Note, subscribeOn: Scheduler?): Single<Note> = api.createNote(note).subscribeOn(subscribeOn?: scheduler)

    override fun updateNote(id: Int, note: Note, subscribeOn: Scheduler?): Single<Note> = api.updateNote(id.toString(), note).subscribeOn(subscribeOn?: scheduler)

    override fun removeNote(id: Int, subscribeOn: Scheduler?): Completable = api.removeNote(id.toString()).subscribeOn(subscribeOn?: scheduler)
}