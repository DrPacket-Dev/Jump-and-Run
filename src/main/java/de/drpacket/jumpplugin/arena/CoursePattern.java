package de.drpacket.jumpplugin.arena;

import java.util.ArrayList;
import java.util.List;

public class CoursePattern {

    public record Step(int stepIndex, boolean isActiveTarget) {
    }

    public List<Step> createInitialSteps(int count) {
        List<Step> steps = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            steps.add(new Step(index, index == 1));
        }
        return steps;
    }

    public Step advance(Step current) {
        return new Step(current.stepIndex() + 1, true);
    }
}
