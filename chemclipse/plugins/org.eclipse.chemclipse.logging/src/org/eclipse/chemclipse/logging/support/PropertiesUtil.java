/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - configure log level for org.apache.http
 *******************************************************************************/
package org.eclipse.chemclipse.logging.support;

import java.io.File;
import java.util.Properties;

public class PropertiesUtil {

	public static final String ROOT_LOGGER_KEY = "log4j.rootLogger";
	public static final String ROOT_LOGGER_VALUE = "DEBUG, ChemClipseConsoleAppender, ChemClipseFileAppender";
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
		properties.setProperty("log4j.appender.ChemClipseConsoleAppender", "org.apache.log4j.ConsoleAppender");
		properties.setProperty("log4j.appender.ChemClipseConsoleAppender.target", "System.out");
		properties.setProperty("log4j.appender.ChemClipseConsoleAppender.layout", "org.apache.log4j.PatternLayout");
		// configure lower log-level for certain libraries
		properties.setProperty("log4j.logger.org.apache.http", "INFO");
		// properties.setProperty("log4j.appender.ChemClipseConsoleAppender.layout.ConversionPattern",
		// "%d{ISO8601} %-5p [%t] %c{3}:%L %m%n");
		properties.setProperty("log4j.appender.ChemClipseConsoleAppender.layout.ConversionPattern", "%-5p %t %C{2}.%M (%F:%L) %m%n");
		/*
		 * Log to a file.
		 */
		properties.setProperty("log4j.appender.ChemClipseFileAppender", "org.apache.log4j.DailyRollingFileAppender");
		properties.setProperty("log4j.appender.ChemClipseFileAppender.datePattern", "'.'yyyy-MM-dd");
		StringBuilder builder = new StringBuilder();
		builder.append(getLogFilePath());
		builder.append(File.separator);
		builder.append("ChemClipse");
		builder.append(LOG_EXTENSION);
		properties.setProperty("log4j.appender.ChemClipseFileAppender.file", builder.toString());
		properties.setProperty("log4j.appender.ChemClipseFileAppender.layout", "org.apache.log4j.PatternLayout");
		properties.setProperty("log4j.appender.ChemClipseFileAppender.layout.ConversionPattern", "%d{ISO8601} %-5p [%t] %c: %m%n");
		/*
		 * Return the properties.
		 */
		return properties;
	}

	/**
	 * Returns the log file path and name. E.g.:
	 * "/home/user/.chemclipse/net.chemclipse.logging"
	 * 
	 * @return
	 */
	public static String getLogFilePath() {

		StringBuilder builder = new StringBuilder();
		builder.append(Settings.getSystemDirectory());
		builder.append(File.separator);
		builder.append(IDENTIFIER);
		return builder.toString();
	}
}
