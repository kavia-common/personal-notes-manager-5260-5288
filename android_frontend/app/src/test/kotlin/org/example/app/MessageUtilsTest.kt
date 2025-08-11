package org.example.app

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class MessageUtilsTest {
    @Test
    fun testGetMessage() {
        // Expect the exact string returned by MessageUtils.message()
        assertEquals("Hello     World!", MessageUtils.message())
    }
}
