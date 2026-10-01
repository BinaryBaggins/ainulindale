package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
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
    void addPartPreservesPartOrder() {
        Composition composition = Composition.create();
        PartId firstPartId = composition.addPart();
        PartId secondPartId = composition.addPart();
        assertEquals(firstPartId, composition.parts().get(0).id());
        assertEquals(secondPartId, composition.parts().get(1).id());
    }

    @Test
    void addVoiceAddsVoiceAndReturnsCreatedVoiceId() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        Result<VoiceId> result = composition.addVoice(partId);
        Success<?> success = assertInstanceOf(Success.class, result);
        Part part = composition.parts().getFirst();
        assertEquals(2, part.voices().size());
        assertEquals(success.value(), part.voices().get(1).id());
    }

    @Test
    void addVoicePreservesVoiceOrder() {
        Composition composition = Composition.create();
        PartId partId = composition.addPart();
        VoiceId firstVoiceId = composition.parts().getFirst().voices().getFirst().id();
        Success<?> second = assertInstanceOf(Success.class, composition.addVoice(partId));
        Success<?> third = assertInstanceOf(Success.class, composition.addVoice(partId));
        assertEquals(
            List.of(firstVoiceId, second.value(), third.value()),
            composition.parts().getFirst().voices().stream().map(Voice::id).toList()
        );
    }

    @Test
    void addVoiceForUnknownPartReturnsFailure() {
        Composition composition = Composition.create();
        PartId unknownPartId = PartId.create();
        Result<VoiceId> result = composition.addVoice(unknownPartId);
        Failure<?> failure = assertInstanceOf(Failure.class, result);
        assertEquals(CompositionErrors.PART_NOT_FOUND, failure.error());
    }

    @Test
    void addVoiceRejectsNullPartId() {
        Composition composition = Composition.create();
        assertThrows(NullPointerException.class, () -> composition.addVoice(null));
    }
}
