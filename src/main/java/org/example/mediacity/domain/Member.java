package org.example.mediacity.domain;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Member {
    private static final int SIGNIFICANT_LATE_RETURNS_BEFORE_SUSPENSION = 3;

    private final String id;
    private final String name;
    private final Map<Year, Integer> significantLateReturnsByYear = new HashMap<>();
    private boolean suspended;

    public Member(String id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void suspend() {
        suspended = true;
    }

    public void registerSignificantLateReturn(Year year) {
        int total = significantLateReturnsByYear.merge(year, 1, Integer::sum);
        if (total >= SIGNIFICANT_LATE_RETURNS_BEFORE_SUSPENSION) {
            suspend();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Member member)) {
            return false;
        }
        return id.equals(member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
