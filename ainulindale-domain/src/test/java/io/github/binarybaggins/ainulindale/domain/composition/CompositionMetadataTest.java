package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.ResultError;
import io.github.binarybaggins.ainulindale.core.result.Success;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CompositionMetadataTest {

    @Test
    void testTitleUnset() {
        Composition composition = Composition.create();
        assertTrue(composition.title().isEmpty());
    }

    @Test
    void testTitleCreation() {
        Composition composition = Composition.create();
        Result<?> result = composition.setTitle("Symphony No. 5");
        assertInstanceOf(Success.class, result);
        assertEquals(Optional.of("Symphony No. 5"), composition.title());
    }

    @Test
    void testClearTitle() {
        Composition composition = Composition.create();
        composition.setTitle("Symphony No. 5");
        composition.clearTitle();
        assertTrue(composition.title().isEmpty());
    }

    @Test
    void nullTitleThrows() {
        Composition composition = Composition.create();
        assertThrows(NullPointerException.class, () -> composition.setTitle(null));
        assertTrue(composition.title().isEmpty());
    }

    @Test
    void emptyOrWhitespaceTitleReturnsError() {
        Composition composition = Composition.create();
        Result<?> result = composition.setTitle("");
        assertRejected(result, CompositionErrors.INVALID_TITLE);
        assertTrue(composition.title().isEmpty());
        result = composition.setTitle("   ");
        assertRejected(result, CompositionErrors.INVALID_TITLE);
        assertTrue(composition.title().isEmpty());
    }

    @Test
    void titleCanBeReplaced() {
        Composition composition = Composition.create();
        composition.setTitle("Original");
        composition.setTitle("Revised");
        assertEquals(Optional.of("Revised"), composition.title());
    }

    @Test
    void validTitlePreservesWhitespace() {
        Composition composition = Composition.create();
        composition.setTitle("  Symphony No. 5  ");
        assertEquals(Optional.of("  Symphony No. 5  "), composition.title());
    }

    @Test
    void invalidTitlePreservesExistingTitle() {
        Composition composition = Composition.create();
        composition.setTitle("Original");
        Result<?> result = composition.setTitle("   ");
        assertRejected(result, CompositionErrors.INVALID_TITLE);
        assertEquals(Optional.of("Original"), composition.title());
    }

    private static void assertRejected(Result<?> result, ResultError expectedError) {
        Failure<?> failure = assertInstanceOf(Failure.class, result);
        assertEquals(expectedError, failure.error());
    }
}
