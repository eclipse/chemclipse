/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings;

public class OperatingSystemUtils {

	/*
	 * Tab
	 */
	public static final String TAB = "\t";
	/*
	 * Unix: \n Windows: \r\n Mac OSX: \r
	 */
	private static final String END_OF_LINE_LINUX = "\n";
	private static final String END_OF_LINE_UNIX = "\n";
	private static final String END_OF_LINE_WINDOWS = "\r\n";
	private static final String END_OF_LINE_MAC = "\r";
	private static final String END_OF_LINE_DEFAULT = "\r\n";

	/**
	 * Use static methods only.
	 */
	private OperatingSystemUtils() {
	}

	public static String getLineDelimiter() {

		if(isWindows()) {
			return END_OF_LINE_WINDOWS;
		} else if(isMac()) {
			return END_OF_LINE_MAC;
		} else if(isLinux()) {
			return END_OF_LINE_LINUX;
		} else if(isUnix()) {
			return END_OF_LINE_UNIX;
		} else {
			return END_OF_LINE_DEFAULT;
		}
	}

	public static boolean isWindows() {

		return (getOperatingSystem().indexOf("win") >= 0);
	}

	public static boolean isLinux() {

		return (getOperatingSystem().indexOf("linux") >= 0);
	}

	public static boolean isMac() {

		return (getOperatingSystem().indexOf("mac") >= 0);
	}

	public static boolean isUnix() {

		return (getOperatingSystem().indexOf("unix") >= 0);
	}

	private static String getOperatingSystem() {

		return System.getProperty("os.name").toLowerCase();
	}
}
