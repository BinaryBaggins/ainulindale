package io.github.binarybaggins.ainulindale.core.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ResultErrorTest {

    @Test
    void preservesCode() {
        ResultError error = new ResultError("Workspace.TrackNotFound");

        assertEquals("Workspace.TrackNotFound", error.code());
    }

    @Test
    void rejectsNullCode() {
        assertThrows(NullPointerException.class, () -> new ResultError(null));
    }

    @Test
    void rejectsEmptyCode() {
        assertThrows(IllegalArgumentException.class, () -> new ResultError(""));
    }

    @Test
    void rejectsBlankCode() {
        assertThrows(IllegalArgumentException.class, () -> new ResultError("   "));
    }
}
