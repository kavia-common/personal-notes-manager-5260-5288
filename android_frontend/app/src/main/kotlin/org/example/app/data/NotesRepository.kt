package org.example.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

/**
 * PUBLIC_INTERFACE
 * Provides CRUD operations for Notes synchronized with the local SQLite database.
 */
class NotesRepository(context: Context) {

    private val dbHelper = NotesDatabaseHelper(context.applicationContext)

    // PUBLIC_INTERFACE
    fun getAllNotes(): List<Note> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            NotesDatabaseHelper.TABLE_NOTES,
            null,
            null,
            null,
            null,
            null,
            "${NotesDatabaseHelper.COL_UPDATED_AT} DESC"
        )
        return cursor.use { mapCursorToNotes(it) }
    }

    // PUBLIC_INTERFACE
    fun searchNotes(query: String): List<Note> {
        val db = dbHelper.readableDatabase
        val like = "%$query%"
        val cursor = db.query(
            NotesDatabaseHelper.TABLE_NOTES,
            null,
            "${NotesDatabaseHelper.COL_TITLE} LIKE ? OR ${NotesDatabaseHelper.COL_CONTENT} LIKE ?",
            arrayOf(like, like),
            null,
            null,
            "${NotesDatabaseHelper.COL_UPDATED_AT} DESC"
        )
        return cursor.use { mapCursorToNotes(it) }
    }

    // PUBLIC_INTERFACE
    fun getNoteById(id: Long): Note? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            NotesDatabaseHelper.TABLE_NOTES,
            null,
            "${NotesDatabaseHelper.COL_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return mapRow(it)
            }
        }
        return null
    }

    // PUBLIC_INTERFACE
    fun insertNote(note: Note): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotesDatabaseHelper.COL_TITLE, note.title)
            put(NotesDatabaseHelper.COL_CONTENT, note.content)
            put(NotesDatabaseHelper.COL_UPDATED_AT, note.updatedAt)
        }
        return db.insert(NotesDatabaseHelper.TABLE_NOTES, null, values)
    }

    // PUBLIC_INTERFACE
    fun updateNote(note: Note): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotesDatabaseHelper.COL_TITLE, note.title)
            put(NotesDatabaseHelper.COL_CONTENT, note.content)
            put(NotesDatabaseHelper.COL_UPDATED_AT, note.updatedAt)
        }
        val rows = db.update(
            NotesDatabaseHelper.TABLE_NOTES,
            values,
            "${NotesDatabaseHelper.COL_ID} = ?",
            arrayOf(note.id.toString())
        )
        return rows > 0
    }

    // PUBLIC_INTERFACE
    fun deleteNote(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        val rows = db.delete(
            NotesDatabaseHelper.TABLE_NOTES,
            "${NotesDatabaseHelper.COL_ID} = ?",
            arrayOf(id.toString())
        )
        return rows > 0
    }

    private fun mapCursorToNotes(cursor: Cursor): List<Note> {
        val list = mutableListOf<Note>()
        while (cursor.moveToNext()) {
            list.add(mapRow(cursor))
        }
        return list
    }

    private fun mapRow(cursor: Cursor): Note {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(NotesDatabaseHelper.COL_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(NotesDatabaseHelper.COL_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(NotesDatabaseHelper.COL_CONTENT))
        val updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(NotesDatabaseHelper.COL_UPDATED_AT))
        return Note(id = id, title = title, content = content, updatedAt = updatedAt)
    }
}
