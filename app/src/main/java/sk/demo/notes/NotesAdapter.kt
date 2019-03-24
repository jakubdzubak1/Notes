package sk.demo.notes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_note.view.*
import sk.demo.notes.model.AppNote
import sk.demo.sdk.model.note.Note

class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view)

class NotesAdapter(
    var data: MutableList<Note>,
    private val onLongPressAction: ((AppNote) -> Unit),
    private val onClickAction: ((AppNote) -> Unit)
) : RecyclerView.Adapter<NoteViewHolder>() {

    var selected: Int? = null
    val selectedNote: AppNote?
        get() = selected?.let { AppNote(data[it], it) }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): NoteViewHolder {
        return NoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.itemView.isSelected = position == selected
        holder.itemView.title.text = data[position].title
        holder.itemView.setOnLongClickListener {
            onLongPressAction.invoke(AppNote(data[position], position))
            true
        }
        holder.itemView.setOnClickListener {
            setSelected(position)
            onClickAction.invoke(AppNote(data[position], position))
        }
    }

    private fun setSelected(position: Int){
        if (selected != position) {
            selected?.let { selected -> notifyItemChanged(selected) }
            selected = position
            notifyItemChanged(position)
        } else {
            val toUnselect = selected
            selected = null
            toUnselect?.let { toUnselect -> notifyItemChanged(toUnselect) }
        }
    }

}