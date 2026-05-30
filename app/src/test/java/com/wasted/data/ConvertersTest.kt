package com.wasted.data

import com.wasted.data.db.Converters
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {
    private val c = Converters()

    @Test fun `map round-trips through json`() {
        val map = mapOf("com.instagram.android" to 3600, "com.twitter.android" to 1200)
        assertEquals(map, c.toMap(c.fromMap(map)))
    }

    @Test fun `empty map round-trips`() {
        val map = emptyMap<String, Int>()
        assertEquals(map, c.toMap(c.fromMap(map)))
    }

    @Test fun `list round-trips through string`() {
        val list = (0 until 24).map { it * 100 }
        assertEquals(list, c.toList(c.fromList(list)))
    }

    @Test fun `empty list round-trips`() {
        assertEquals(emptyList<Int>(), c.toList(c.fromList(emptyList())))
    }
}
