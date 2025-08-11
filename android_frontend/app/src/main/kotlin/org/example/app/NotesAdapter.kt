package org.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.example.app.data.Note
import java.text.DateFormat
import java.util.Date

/**
 * RecyclerView Adapter for displaying notes in the list.
 */
class NotesAdapter(
    private val onClick: (Note) -> Unit,
    private val onLongClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val items = mutableListOf<Note>()

    // PUBLIC_INTERFACE
    fun submitList(data: List<Note>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(v)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(items[position], onClick, onLongClick)
    }

    override fun getItemCount(): Int = items.size

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.noteTitle)
        private val contentView: TextView = itemView.findViewById(R.id.noteContent)
        private val dateView: TextView = itemView.findViewById(R.id.noteDate)

        fun bind(note: Note, onClick: (Note) -> Unit, onLongClick: (Note) -> Unit) {
            titleView.text = if (note.title.isNotBlank()) note.title else itemView.context.getString(R.string.untitled)
            val snippet = if (note.content.length > 100) note.content.substring(0, 100) + "…" else note.content
            contentView.text = snippet
            dateView.text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(Date(note.updatedAt))

            itemView.setOnClickListener { onClick(note) }
            itemView.setOnLongClickListener {
                onLongClick(note)
                true
            }
        }
    }
}
