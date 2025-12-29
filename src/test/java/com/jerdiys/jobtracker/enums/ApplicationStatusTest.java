package com.jerdiys.jobtracker.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationStatusTest {

    @Test
    void testValidTransitions() {
        assertTrue(ApplicationStatus.APPLIED.canTransitionTo(ApplicationStatus.REVIEWING));
        assertTrue(ApplicationStatus.REVIEWING.canTransitionTo(ApplicationStatus.INTERVIEWED));
        assertTrue(ApplicationStatus.REVIEWING.canTransitionTo(ApplicationStatus.REJECTED));
        assertTrue(ApplicationStatus.INTERVIEWED.canTransitionTo(ApplicationStatus.OFFERED));
        assertTrue(ApplicationStatus.INTERVIEWED.canTransitionTo(ApplicationStatus.REJECTED));
        assertTrue(ApplicationStatus.OFFERED.canTransitionTo(ApplicationStatus.HIRED));
        assertTrue(ApplicationStatus.OFFERED.canTransitionTo(ApplicationStatus.REJECTED));
    }

    @Test
    void testInvalidTransitions() {
        assertFalse(ApplicationStatus.APPLIED.canTransitionTo(ApplicationStatus.INTERVIEWED));
        assertFalse(ApplicationStatus.REVIEWING.canTransitionTo(ApplicationStatus.OFFERED));
        assertFalse(ApplicationStatus.REVIEWING.canTransitionTo(ApplicationStatus.APPLIED));
        assertFalse(ApplicationStatus.INTERVIEWED.canTransitionTo(ApplicationStatus.HIRED));
        assertFalse(ApplicationStatus.OFFERED.canTransitionTo(ApplicationStatus.APPLIED));
        assertFalse(ApplicationStatus.REJECTED.canTransitionTo(ApplicationStatus.APPLIED));
        assertFalse(ApplicationStatus.HIRED.canTransitionTo(ApplicationStatus.REJECTED));
    }

}