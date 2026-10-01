package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.ResultError;
import io.github.binarybaggins.ainulindale.domain.identity.CompositionId;
import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a composition which contains a unique identifier and a collection of parts.
 */
public final class Composition {

    private final CompositionId id;
    private final List<Part> parts;

    private Composition(CompositionId id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.parts = new ArrayList<>();
    }

    /**
     * Creates a new composition with a unique identifier.
     *
     * @return a new composition instance
     */
    public static Composition create() {
        return new Composition(CompositionId.create());
    }

    /**
     * Returns the unique identifier of this composition.
     *
     * @return the composition ID
     */
    public CompositionId id() {
        return id;
    }

    /**
     * Returns an unmodifiable view of the parts contained in this composition.
     *
     * @return an unmodifiable list of parts
     */
    public List<Part> parts() {
        return Collections.unmodifiableList(parts);
    }

    /**
     * Adds a new part to this composition and returns its unique identifier.
     *
     * @return the unique identifier of the newly added part
     */
    public PartId addPart() {
        Part part = Part.create();
        parts.add(part);
        return part.id();
    }

    /**
     * Adds a new voice to the specified part within this composition.
     *
     * @param partId the unique identifier of the part to which the voice will be added
     * @return a result containing the unique identifier of the newly added voice if successful,
     *         or an error if the part was not found
     */
    public Result<VoiceId> addVoice(PartId partId) {
        Objects.requireNonNull(partId, "partId must not be null");
        for (Part part : parts) {
            if (part.id().equals(partId)) {
                return Result.success(part.addVoice());
            }
        }
        return Result.failure(CompositionErrors.PART_NOT_FOUND);
    }
}

/**
 * Contains error definitions related to the Composition class.
 */
final class CompositionErrors {

    private CompositionErrors() {
        // Private constructor to prevent instantiation
    }

    /**
     * Error indicating that the specified part was not found within the composition.
     */
    static final ResultError PART_NOT_FOUND = new ResultError("Composition.PartNotFound");
}
