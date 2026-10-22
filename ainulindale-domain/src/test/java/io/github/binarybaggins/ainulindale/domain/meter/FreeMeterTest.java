package io.github.binarybaggins.ainulindale.domain.meter;

import static org.junit.jupiter.api.Assertions.*;

import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import java.util.List;
import org.junit.jupiter.api.Test;

class FreeMeterTest {

    @Test
    void isMeter() {
        Meter meter = FreeMeter.INSTANCE;

        assertSame(FreeMeter.INSTANCE, meter);
    }

    @Test
    void hasExactlyOneEnumConstant() {
        assertArrayEquals(new FreeMeter[] { FreeMeter.INSTANCE }, FreeMeter.values());
    }

    @Test
    void isDistinctFromStructuredMeter() {
        Meter free = FreeMeter.INSTANCE;
        Meter structured = new StructuredMeter(List.of(new MeterComponent(4, MusicalDuration.of(1, 4))));

        assertNotEquals(free, structured);
    }
}
