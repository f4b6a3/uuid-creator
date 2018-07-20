package com.github.f4b6a3.uuid.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class RandomnesTest {

	/**
	 * Execute ENT for simple randomness test.
	 * 
	 * Is necessary to have ENT installed in your system.
	 * 
	 * @param path
	 * @param random
	 */
	public static void runPseudoNumberSequenceTestProgram(String path, Random random) {
		
		try {
			try (DataOutputStream dos = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(new File(path))))) {
				byte[] data = new byte[100_000];

				for (long i = 0; i < 1000; i++) {
					random.nextBytes(data);
					dos.write(data);
				}

				String s;
				Runtime.getRuntime().exec(String.format("sync "));
				Process p = Runtime.getRuntime().exec(String.format("ent %s", path));
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
