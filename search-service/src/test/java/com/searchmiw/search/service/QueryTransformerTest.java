package com.searchmiw.search.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class QueryTransformerTest {

    private final QueryTransformer queryTransformer = new QueryTransformer();

    @Test
    void testEmptyQuery() {
        assertEquals("", queryTransformer.transformQuery("", 1));
        assertEquals(null, queryTransformer.transformQuery(null, 1));
        assertEquals("  ", queryTransformer.transformQuery("  ", 1));
    }

    @ParameterizedTest
    @CsvSource({
        "'Albert Einstein biography', 'Albert Einstein biography', 1",
        "'Albert Einstein!@#$%^&*()', 'Albert Einstein', 1",
        "'The theory of relativity', 'theory relativity', 2",
        "'Albert Einstein physics', 'physics Albert Einstein', 3"
    })
    void testTransformQuery(String input, String expected, int attempt) {
        assertEquals(expected, queryTransformer.transformQuery(input, attempt));
    }

    @Test
    void testFirstAttemptRemovesSpecialChars() {
        // First attempt should remove special chars
        assertEquals("Hello World", queryTransformer.transformQuery("Hello-World!", 1));
        assertEquals("C Programming", queryTransformer.transformQuery("C++ Programming", 1));
        assertEquals("nodejs server", queryTransformer.transformQuery("node.js server", 1));
    }

    @Test
    void testSecondAttemptRemovesStopWords() {
        // Second attempt should remove common words
        assertEquals("theory relativity", queryTransformer.transformQuery("the theory of relativity", 2));
        assertEquals("quick brown fox jumps lazy dog", 
                    queryTransformer.transformQuery("the quick brown fox jumps over the lazy dog", 2));
    }

    @Test
    void testThirdAttemptReversesWords() {
        // Third attempt should reverse word order
        assertEquals("Einstein Albert", queryTransformer.transformQuery("Albert Einstein", 3));
        assertEquals("programming Java", queryTransformer.transformQuery("Java programming", 3));
    }
}
