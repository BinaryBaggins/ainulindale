package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ActionIdTest {

    @Test
    void persistentIdsAreUnique() {
        Set<String> ids = Arrays.stream(ActionId.values()).map(ActionId::persistentId).collect(Collectors.toSet());

        assertEquals(ActionId.values().length, ids.size());
    }
}
