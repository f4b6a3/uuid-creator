package com.github.small.uuid;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class UUIDClock extends Clock
{
    private final Clock clock;

    private Instant initialInstant;
    private long initialNanoseconds;
    private long lastNanoseconds;

    public UUIDClock()
    {
        this(Clock.systemUTC());
    }

    public UUIDClock(final Clock clock)
    {
        this.clock = clock;
        reset();
    }
    
    @Override
    public ZoneId getZone()
    {
        return clock.getZone();
    }

    @Override
    public Instant instant()
    {
        return initialInstant.plusNanos(getSystemNanos() - initialNanoseconds);
    }
    
    @Override
    public Clock withZone(final ZoneId zone)
    {
        return new UUIDClock(clock.withZone(zone));
    }

    protected long getSystemNanos()
    {
    	long currentNanoseconds = System.nanoTime();

    	if (currentNanoseconds > lastNanoseconds) {
    		lastNanoseconds = currentNanoseconds;
    	} else {
    		reset(); // You can't go back!
    	}
    	
    	return currentNanoseconds;
    }
    
    protected void reset() {
        initialInstant = clock.instant();
        initialNanoseconds = System.nanoTime();
        lastNanoseconds = initialNanoseconds;
    }
}