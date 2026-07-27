package de.drpacket.jumpplugin;

import de.drpacket.jumpplugin.arena.CoursePattern;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoursePatternTest {

    @Test
    void initialPatternCreatesThreeBlocksAndAdvancesCleanly() {
        CoursePattern pattern = new CoursePattern();

        var steps = pattern.createInitialSteps(3);

        assertEquals(3, steps.size());
        assertEquals(0, steps.get(0).stepIndex());
        assertEquals(1, steps.get(1).stepIndex());
        assertEquals(2, steps.get(2).stepIndex());
        assertTrue(steps.get(1).isActiveTarget());

        var advanced = pattern.advance(steps.get(1));
        assertEquals(1, advanced.stepIndex());
        assertTrue(advanced.isActiveTarget());
    }
}
