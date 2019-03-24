package sk.demo.notes.model

import sk.demo.sdk.model.note.Note

data class AppNote (
    val note: Note,
    val position: Int
)