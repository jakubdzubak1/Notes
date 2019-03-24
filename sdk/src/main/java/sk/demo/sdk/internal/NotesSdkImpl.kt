package sk.demo.sdk.internal

import android.content.Context
import sk.demo.sdk.NoteManager
import sk.demo.sdk.NotesSdk
import sk.demo.sdk.internal.backend.Environment
import sk.demo.sdk.internal.managers.NoteManagerImpl

internal class NotesSdkImpl(
    context: Context,
    environment: Environment
): NotesSdk {

    private val communication: Communication = Communication(NotesContext(context.applicationContext, environment))
    private val noteManager: NoteManager by lazy { NoteManagerImpl(communication.api) }

    override fun notes(): NoteManager = noteManager
}