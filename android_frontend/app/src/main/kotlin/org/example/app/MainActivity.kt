package org.example.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.example.app.data.Note
import org.example.app.data.NotesRepository
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var repository: NotesRepository
    private lateinit var adapter: NotesAdapter
    private val ioExecutor = Executors.newSingleThreadExecutor()

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = NotesRepository(this)

        toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.notesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NotesAdapter(
            onClick = { note -> openEditor(note.id) },
            onLongClick = { note -> confirmDelete(note) }
        )
        recyclerView.adapter = adapter

        searchView = findViewById(R.id.searchView)
        setupSearch()

        fab = findViewById(R.id.fabAddNote)
        fab.setOnClickListener { openEditor(null) }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadNotes(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadNotes(newText.orEmpty())
                return true
            }
        })
    }

    private fun loadNotes(query: String = "") {
        ioExecutor.execute {
            val notes: List<Note> = if (query.isBlank()) {
                repository.getAllNotes()
            } else {
                repository.searchNotes(query)
            }
            runOnUiThread {
                adapter.submitList(notes)
            }
        }
    }

    private fun openEditor(noteId: Long?) {
        val intent = Intent(this, EditorActivity::class.java)
        if (noteId != null) {
            intent.putExtra(EditorActivity.EXTRA_NOTE_ID, noteId)
        }
        startActivity(intent)
    }

    private fun confirmDelete(note: Note) {
        // Simple delete on long-press with a toast. In a full app, show AlertDialog for confirmation.
        ioExecutor.execute {
            val deleted = repository.deleteNote(note.id)
            runOnUiThread {
                if (deleted) {
                    Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show()
                    loadNotes(searchView.query?.toString().orEmpty())
                } else {
                    Toast.makeText(this, getString(R.string.error_deleting), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
