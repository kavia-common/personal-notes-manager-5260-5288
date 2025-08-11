package org.example.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import org.example.app.data.Note
import org.example.app.data.NotesRepository
import java.util.concurrent.Executors

/**
 * PUBLIC_INTERFACE
 * EditorActivity allows users to create a new note or edit an existing one.
 * - If EXTRA_NOTE_ID is provided via intent, the note is loaded and fields are pre-populated.
 * - The save icon in the top app bar persists the note.
 */
class EditorActivity : AppCompatActivity() {

    companion object {
        // PUBLIC_INTERFACE
        const val EXTRA_NOTE_ID = "extra_note_id"
    }

    private lateinit var repository: NotesRepository
    private val ioExecutor = Executors.newSingleThreadExecutor()

    private var noteId: Long? = null

    private lateinit var titleField: EditText
    private lateinit var contentField: EditText
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        repository = NotesRepository(this)

        toolbar = findViewById(R.id.editorTopAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleField = findViewById(R.id.inputTitle)
        contentField = findViewById(R.id.inputContent)

        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L).takeIf { it > 0 }

        if (noteId != null) {
            loadNote(noteId!!)
            supportActionBar?.title = getString(R.string.edit_note)
        } else {
            supportActionBar?.title = getString(R.string.new_note)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadNote(id: Long) {
        ioExecutor.execute {
            val note = repository.getNoteById(id)
            runOnUiThread {
                if (note != null) {
                    titleField.setText(note.title)
                    contentField.setText(note.content)
                } else {
                    Toast.makeText(this, getString(R.string.error_loading), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveNote() {
        val title = titleField.text?.toString()?.trim().orEmpty()
        val content = contentField.text?.toString()?.trim().orEmpty()

        if (title.isBlank() && content.isBlank()) {
            Toast.makeText(this, getString(R.string.empty_note_warning), Toast.LENGTH_SHORT).show()
            return
        }

        ioExecutor.execute {
            val now = System.currentTimeMillis()
            val success = if (noteId == null) {
                // Insert
                val id = repository.insertNote(Note(title = title, content = content, updatedAt = now))
                id > 0
            } else {
                // Update
                repository.updateNote(Note(id = noteId!!, title = title, content = content, updatedAt = now))
            }
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, getString(R.string.note_saved), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.error_saving), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
