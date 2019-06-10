package me.nunum.whereami.framework.domain;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Executable implements
        Callable<Boolean>,
        Comparable<Executable> {

    private final Integer priority;

    private static AtomicInteger instanceCounter = new AtomicInteger(0);

    public Executable() {
        priority = instanceCounter.incrementAndGet();
    }

    @Override
    public int compareTo(Executable o) {
        return this.priority.compareTo(o.priority);
    }
}
