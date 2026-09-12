package io.github.binarybaggins.ainulindale.core.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ResultTest {

    @Test
    void successCreatesSuccessResult() {
        Result<String> result = Result.success("value");

        Success<?> success = assertInstanceOf(Success.class, result);
        assertEquals("value", success.value());
    }

    @Test
    void failureCreatesFailureResult() {
        ResultError error = new ResultError("Workspace.TrackNotFound");

        Result<String> result = Result.failure(error);

        Failure<?> failure = assertInstanceOf(Failure.class, result);
        assertSame(error, failure.error());
    }

    @Test
    void successConstructorRejectsNullValue() {
        assertThrows(NullPointerException.class, () -> new Success<>(null));
    }

    @Test
    void failureConstructorRejectsNullError() {
        assertThrows(NullPointerException.class, () -> new Failure<>(null));
    }

    @Test
    void successRejectsNullValue() {
        assertThrows(NullPointerException.class, () -> Result.success(null));
    }

    @Test
    void failureRejectsNullError() {
        assertThrows(NullPointerException.class, () -> Result.failure(null));
    }

    @Test
    void unitCanBeUsedAsSuccessValue() {
        Result<Unit> result = Result.success(Unit.INSTANCE);

        Success<?> success = assertInstanceOf(Success.class, result);
        assertEquals(Unit.INSTANCE, success.value());
    }
}
