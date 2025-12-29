package com.jerdiys.jobtracker.enums;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.EnumSet;
import java.util.Set;

public enum ApplicationStatus {
    APPLIED,
    REVIEWING,
    INTERVIEWED,
    OFFERED,
    REJECTED,
    HIRED;

    private Set<ApplicationStatus> allowedNext;

    public boolean canTransitionTo(ApplicationStatus nextStatus) {
        return allowedNext != null && allowedNext.contains(nextStatus);
    }

    static {
        APPLIED.allowedNext = EnumSet.of(REVIEWING);
        REVIEWING.allowedNext = EnumSet.of(INTERVIEWED, REJECTED);
        INTERVIEWED.allowedNext = EnumSet.of(OFFERED, REJECTED);
        OFFERED.allowedNext = EnumSet.of(HIRED, REJECTED);
        REJECTED.allowedNext = EnumSet.noneOf(ApplicationStatus.class);
        HIRED.allowedNext = EnumSet.noneOf(ApplicationStatus.class);
    }
}
