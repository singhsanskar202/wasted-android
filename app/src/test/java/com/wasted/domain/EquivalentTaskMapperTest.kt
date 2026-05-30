package com.wasted.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EquivalentTaskMapperTest {
    @Test fun `under threshold returns null`() {
        assertNull(EquivalentTaskMapper.equivalent(800))
    }

    @Test fun `exactly at threshold returns that entry`() {
        val eq = EquivalentTaskMapper.equivalent(900)
        assertEquals("a 15-min meditation", eq?.description)
    }

    @Test fun `above highest threshold returns last entry`() {
        val eq = EquivalentTaskMapper.equivalent(100_000)
        assertEquals("a half-day hike", eq?.description)
    }

    @Test fun `mid-range returns correct bucket`() {
        val eq = EquivalentTaskMapper.equivalent(4000)
        assertEquals("a book chapter (25 pages)", eq?.description)
    }
}
