package com.github.f4b6a3.uuid.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
	
	private LogUtil() {
	}

	public static void log() {
		log("");
	}
	
	public static void log(Object object) {
		log(object.toString());
	}
	
	public static void log(String msg) {
		Logger.getAnonymousLogger().info(msg);
	}
	
	public static void err(String msg) {
		Logger.getAnonymousLogger().log(Level.SEVERE, msg);;
	}
}
