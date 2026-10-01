package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CompositionTest {

    @Test
    void createGeneratesCompositionId() {
        Composition composition = Composition.create();
        assertNotNull(composition.id());
    }

    @Test
    void newCompositionHasNoParts() {
        Composition composition = Composition.create();
        assert composition.parts().isEmpty();
    }

    @Test
    void partsCollectionIsUnmodifiable() {
        Composition composition = Composition.create();
        assertThrows(UnsupportedOperationException.class, () -> composition.parts().clear());
    }
}
