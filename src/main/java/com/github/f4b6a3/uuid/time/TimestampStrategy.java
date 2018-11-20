package com.github.f4b6a3.uuid.time;

/**
 * This interface exists to allow implementations other than the default, that
 * has a has a accuracy of milliseconds. If someone prefers to use a timestamp
 * with real 100-nanosecond resolution, it's necessary to implement this
 * interface and pass it to the corresponding time-based factory.
 */
public interface TimestampStrategy {

	long getCurrentTimestamp();
}
