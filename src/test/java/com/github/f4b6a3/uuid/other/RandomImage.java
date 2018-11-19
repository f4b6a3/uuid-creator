package com.github.f4b6a3.uuid.other;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class RandomImage {

	public static void createRandomImageFile(String path, Random random, int width, int height) {

		int w = 1280;
		int h = 640;

		if (width > 0) {
			w = width;
		}
		if (height > 0) {
			h = height;
		}

		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		File f = null;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int a = 255; // alpha
				int r = (int) (random.nextInt() >>> 56); // red
				int g = (int) (random.nextInt() >>> 56); // green
				int b = (int) (random.nextInt() >>> 56); // blue
				
				int p = (a << 24) | (r << 16) | (g << 8) | b; // pixel

				img.setRGB(x, y, p);
			}
		}
		try {
			f = new File(path);
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		System.out.println(String.format("Created file '%s'", path));
	}
}