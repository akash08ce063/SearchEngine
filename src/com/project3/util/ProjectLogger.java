package com.project3.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.project3.core.Configuration;

public class ProjectLogger {
	public static Logger logger = Logger.getLogger(ProjectLogger.class.getName());
	static{
		try{
		FileHandler handler = new FileHandler(Configuration.logDirectory);
		handler.setFormatter(new SimpleFormatter());
		logger.addHandler(handler);
		logger.addHandler(new ConsoleHandler());
		logger.setLevel(Level.INFO);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
