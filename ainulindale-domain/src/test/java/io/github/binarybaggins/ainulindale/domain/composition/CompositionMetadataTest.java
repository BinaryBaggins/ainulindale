package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.ResultError;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
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
        assertRejected(result, CompositionErrors.INVALID_COMPOSITION_TITLE);
        assertTrue(composition.title().isEmpty());
        result = composition.setTitle("   ");
        assertRejected(result, CompositionErrors.INVALID_COMPOSITION_TITLE);
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
        assertRejected(result, CompositionErrors.INVALID_COMPOSITION_TITLE);
        assertEquals(Optional.of("Original"), composition.title());
    }

    // Part name tests

    @Test
    void testPartNameUnset() {
        Composition composition = Composition.create();
        composition.addPart();
        Result<Optional<String>> result = composition.partName(composition.parts().get(0).id());
        assertInstanceOf(Success.class, result);
        assertTrue(((Success<Optional<String>>) result).value().isEmpty());
    }

    @Test
    void testPartNameCreation() {
        Composition composition = Composition.create();
        composition.addPart();
        PartId partId = composition.parts().get(0).id();
        Result<Unit> setResult = composition.setPartName(partId, "Violin");
        assertInstanceOf(Success.class, setResult);
        Result<Optional<String>> getResult = composition.partName(partId);
        assertInstanceOf(Success.class, getResult);
        assertEquals(Optional.of("Violin"), ((Success<Optional<String>>) getResult).value());
    }

    @Test
    void testClearPartName() {
        Composition composition = Composition.create();
        composition.addPart();
        PartId partId = composition.parts().get(0).id();
        composition.setPartName(partId, "Violin");
        composition.clearPartName(partId);
        Result<Optional<String>> result = composition.partName(partId);
        assertTrue(((Success<Optional<String>>) result).value().isEmpty());
    }

    @Test
    void nullPartNameThrows() {
        Composition composition = Composition.create();
        composition.addPart();
        PartId partId = composition.parts().get(0).id();
        assertThrows(NullPointerException.class, () -> composition.setPartName(partId, null));
        Result<Optional<String>> result = composition.partName(partId);
        assertInstanceOf(Success.class, result);
    }

    @Test
    void emptyOrWhitespacePartNameReturnsError() {
        Composition composition = Composition.create();
        composition.addPart();
        PartId partId = composition.parts().get(0).id();
        Result<Unit> result = composition.setPartName(partId, "");
        assertRejected(result, CompositionErrors.INVALID_PART_NAME);
        Result<Optional<String>> getResult = composition.partName(partId);
        assertInstanceOf(Success.class, getResult);
        result = composition.setPartName(partId, "   ");
        assertRejected(result, CompositionErrors.INVALID_PART_NAME);
        getResult = composition.partName(partId);
        assertInstanceOf(Success.class, getResult);
    }

    @Test
    void partNameCanBeReplaced() {
        Composition composition = Composition.create();
        composition.addPart();
        PartId partId = composition.parts().get(0).id();
        composition.setPartName(partId, "Violin I");
        composition.setPartName(partId, "Violin II");
        Result<Optional<String>> result = composition.partName(partId);
        assertEquals(Optional.of("Violin II"), ((Success<Optional<String>>) result).value());
    }

    @Test
    void validPartNamePreservesWhitespace() {
        Composition composition = Composition.create();
        composition.addPart();
        PartId partId = composition.parts().get(0).id();
        composition.setPartName(partId, "  Violin  ");
        Result<Optional<String>> result = composition.partName(partId);
        assertEquals(Optional.of("  Violin  "), ((Success<Optional<String>>) result).value());
    }

    @Test
    void invalidPartNamePreservesExistingName() {
        Composition composition = Composition.create();
        composition.addPart();
        PartId partId = composition.parts().get(0).id();
        composition.setPartName(partId, "Original");
        Result<Unit> result = composition.setPartName(partId, "   ");
        assertRejected(result, CompositionErrors.INVALID_PART_NAME);
        Result<Optional<String>> getResult = composition.partName(partId);
        assertEquals(Optional.of("Original"), ((Success<Optional<String>>) getResult).value());
    }

    // Voice name tests

    @Test
    void testVoiceNameUnset() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().get(0).voices().get(0).id();
        Result<Optional<String>> result = composition.voiceName(voiceId);
        assertInstanceOf(Success.class, result);
        assertTrue(((Success<Optional<String>>) result).value().isEmpty());
    }

    @Test
    void testVoiceNameCreation() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().get(0).voices().get(0).id();
        Result<Unit> setResult = composition.setVoiceName(voiceId, "Soprano");
        assertInstanceOf(Success.class, setResult);
        Result<Optional<String>> getResult = composition.voiceName(voiceId);
        assertInstanceOf(Success.class, getResult);
        assertEquals(Optional.of("Soprano"), ((Success<Optional<String>>) getResult).value());
    }

    @Test
    void testClearVoiceName() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().get(0).voices().get(0).id();
        composition.setVoiceName(voiceId, "Soprano");
        composition.clearVoiceName(voiceId);
        Result<Optional<String>> result = composition.voiceName(voiceId);
        assertTrue(((Success<Optional<String>>) result).value().isEmpty());
    }

    @Test
    void nullVoiceNameThrows() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().get(0).voices().get(0).id();
        assertThrows(NullPointerException.class, () -> composition.setVoiceName(voiceId, null));
        Result<Optional<String>> result = composition.voiceName(voiceId);
        assertInstanceOf(Success.class, result);
    }

    @Test
    void emptyOrWhitespaceVoiceNameReturnsError() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().get(0).voices().get(0).id();
        Result<Unit> result = composition.setVoiceName(voiceId, "");
        assertRejected(result, CompositionErrors.INVALID_VOICE_NAME);
        Result<Optional<String>> getResult = composition.voiceName(voiceId);
        assertInstanceOf(Success.class, getResult);
        result = composition.setVoiceName(voiceId, "   ");
        assertRejected(result, CompositionErrors.INVALID_VOICE_NAME);
        getResult = composition.voiceName(voiceId);
        assertInstanceOf(Success.class, getResult);
    }

    @Test
    void voiceNameCanBeReplaced() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().get(0).voices().get(0).id();
        composition.setVoiceName(voiceId, "Soprano");
        composition.setVoiceName(voiceId, "Alto");
        Result<Optional<String>> result = composition.voiceName(voiceId);
        assertEquals(Optional.of("Alto"), ((Success<Optional<String>>) result).value());
    }

    @Test
    void validVoiceNamePreservesWhitespace() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().get(0).voices().get(0).id();
        composition.setVoiceName(voiceId, "  Soprano  ");
        Result<Optional<String>> result = composition.voiceName(voiceId);
        assertEquals(Optional.of("  Soprano  "), ((Success<Optional<String>>) result).value());
    }

    @Test
    void invalidVoiceNamePreservesExistingName() {
        Composition composition = Composition.create();
        composition.addPart();
        VoiceId voiceId = composition.parts().get(0).voices().get(0).id();
        composition.setVoiceName(voiceId, "Original");
        Result<Unit> result = composition.setVoiceName(voiceId, "   ");
        assertRejected(result, CompositionErrors.INVALID_VOICE_NAME);
        Result<Optional<String>> getResult = composition.voiceName(voiceId);
        assertEquals(Optional.of("Original"), ((Success<Optional<String>>) getResult).value());
    }

    private static void assertRejected(Result<?> result, ResultError expectedError) {
        Failure<?> failure = assertInstanceOf(Failure.class, result);
        assertEquals(expectedError, failure.error());
    }
}
