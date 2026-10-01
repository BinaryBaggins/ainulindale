package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.domain.identity.CompositionId;
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
}
