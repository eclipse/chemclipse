/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.support;

import java.io.File;
import java.util.Properties;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public class PropertiesUtil {

	public static final String ROOT_LOGGER_KEY = "log4j.rootLogger";
	public static final String ROOT_LOGGER_VALUE = "DEBUG, OpenChromConsoleAppender, OpenChromFileAppender";
	public static final String LOG_EXTENSION = ".log";
	private static final String IDENTIFIER = "org.eclipse.chemclipse.logging";

	/**
	 * Returns the log4j properties.
	 * 
	 * @return {@link Properties}
	 */
	public static Properties getLog4jProperties() {

		Properties properties = new Properties();
		properties.setProperty(ROOT_LOGGER_KEY, ROOT_LOGGER_VALUE);
		/*
		 * Log to the console.
		 */
		properties.setProperty("log4j.appender.OpenChromConsoleAppender", "org.apache.log4j.ConsoleAppender");
		properties.setProperty("log4j.appender.OpenChromConsoleAppender.target", "System.out");
		properties.setProperty("log4j.appender.OpenChromConsoleAppender.layout", "org.apache.log4j.PatternLayout");
		properties.setProperty("log4j.appender.OpenChromConsoleAppender.layout.ConversionPattern", "%d{ISO8601} %-5p [%t] %c: %m%n");
		/*
		 * Log to a file.
		 */
		properties.setProperty("log4j.appender.OpenChromFileAppender", "org.apache.log4j.DailyRollingFileAppender");
		properties.setProperty("log4j.appender.OpenChromFileAppender.datePattern", "'.'yyyy-MM-dd");
		StringBuilder builder = new StringBuilder();
		builder.append(getLogFilePath());
		builder.append(File.separator);
		builder.append("OpenChrom");
		builder.append(LOG_EXTENSION);
		properties.setProperty("log4j.appender.OpenChromFileAppender.file", builder.toString());
		properties.setProperty("log4j.appender.OpenChromFileAppender.layout", "org.apache.log4j.PatternLayout");
		properties.setProperty("log4j.appender.OpenChromFileAppender.layout.ConversionPattern", "%d{ISO8601} %-5p [%t] %c: %m%n");
		/*
		 * Return the properties.
		 */
		return properties;
	}

	/**
	 * Returns the log file path and name.
	 * E.g.: "/home/user/.openchrom/net.openchrom.logging"
	 * 
	 * @return
	 */
	public static String getLogFilePath() {

		StringBuilder builder = new StringBuilder();
		builder.append(Settings.getSettingsDirectory());
		builder.append(File.separator);
		builder.append(IDENTIFIER);
		return builder.toString();
	}
}
