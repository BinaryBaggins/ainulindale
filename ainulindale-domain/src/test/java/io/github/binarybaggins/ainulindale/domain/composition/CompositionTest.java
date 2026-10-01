package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import java.util.List;
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
        assertTrue(composition.parts().isEmpty());
    }

    @Test
    void partsCollectionIsUnmodifiable() {
        Composition composition = Composition.create();
        assertThrows(UnsupportedOperationException.class, () -> composition.parts().clear());
    }

    @Test
    void addPartAddsPartAndReturnsCreatedPartId() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        assertEquals(1, composition.parts().size());
        assertEquals(partId, composition.parts().getFirst().id());
    }

    @Test
    void addedPartsPreservesPartOrder() {
        Composition composition = Composition.create();
        PartId firstPartId = composition.addPart();
        PartId secondPartId = composition.addPart();
        assertEquals(firstPartId, composition.parts().get(0).id());
        assertEquals(secondPartId, composition.parts().get(1).id());
    }
}
