package com.google.firebase.codelab.awesomedrawingquiz.ui.game;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ClueGeneratorTest {

    @Test
    public void generate() {
        assertEquals("c**", ClueGenerator.generate("car", 1));
        assertEquals("ca*", ClueGenerator.generate("car", 2));

        // don't disclose all characters in the word:
        // leave at least one character undisclosed
        assertEquals("ca*", ClueGenerator.generate("car", 3));
    }
}
