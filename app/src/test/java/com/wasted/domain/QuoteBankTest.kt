package com.wasted.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Test

class QuoteBankTest {
    @Test fun `quotes list is not empty`() {
        assertFalse(QuoteBank.quotes.isEmpty())
    }

    @Test fun `todaysQuote is not blank`() {
        assertFalse(QuoteBank.todaysQuote.isBlank())
    }

    @Test fun `todaysQuote returns same value when called twice`() {
        assertEquals(QuoteBank.todaysQuote, QuoteBank.todaysQuote)
    }

    @Test fun `day index wraps within quotes size`() {
        for (day in 0..400) {
            assertNotNull(QuoteBank.quotes[day % QuoteBank.quotes.size])
        }
    }
}
