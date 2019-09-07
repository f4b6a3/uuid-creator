package com.github.f4b6a3.demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.github.f4b6a3.uuid.util.LogUtil;

public class SimpleBenchmark {

	private SimpleBenchmark() {
	}

	/**
	 * This method estimates the average running time for a method in
	 * nanoseconds.
	 */
	public static long run(Object obj, Class<?> clazz, String methodName, long rounds) {

		long elapsedSum = 0;
		long elapsedAvg = 0;

		long extraSum = 0;

		long beforeTime = 0;
		long afterTime = 0;

		long min = 1_000_000;
		long max = min + rounds;

		try {

			Method method = null;

			if (obj != null) {
				method = obj.getClass().getMethod(methodName);
			} else {
				method = clazz.getDeclaredMethod(methodName);
			}

			for (int i = 0; i < max; i++) {
				beforeTime = System.nanoTime();
				if (i > min) {
					extraSum += (beforeTime - afterTime);
				}
				method.invoke(obj);
				afterTime = System.nanoTime();
				if (i > min) {
					elapsedSum += (afterTime - beforeTime);
				}
			}

			elapsedAvg = (elapsedSum - extraSum) / (rounds - 1);

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LogUtil.err(e.toString());
		}

		return elapsedAvg;
	}
}
