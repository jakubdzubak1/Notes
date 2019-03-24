package sk.demo.notes.model

import sk.demo.sdk.model.note.Note

data class AdapterNote(
    val note: Note,
    val isSelected: Boolean
)