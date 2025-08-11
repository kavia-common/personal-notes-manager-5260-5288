package org.example.app.data

/**
 * PUBLIC_INTERFACE
 * Represents a single note in the application.
 */
data class Note(
    val id: Long = 0,
    var title: String = "",
    var content: String = "",
    var updatedAt: Long = System.currentTimeMillis()
)
