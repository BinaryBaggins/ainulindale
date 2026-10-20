package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CompositionTest {

    @Test
    void createGeneratesCompositionId() {
        Composition composition = Composition.create();
        assertNotNull(composition.id());
    }
}
