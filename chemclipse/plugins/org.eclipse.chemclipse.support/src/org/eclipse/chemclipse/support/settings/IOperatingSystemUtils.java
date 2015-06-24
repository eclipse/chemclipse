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
package org.eclipse.chemclipse.support.settings;

public interface IOperatingSystemUtils {

	/*
	 * Unix: \n Windows: \r\n Mac OSX: \r
	 */
	String END_OF_LINE_LINUX = "\n";
	String END_OF_LINE_UNIX = "\n";
	String END_OF_LINE_WINDOWS = "\r\n";
	String END_OF_LINE_MAC = "\r";
	String END_OF_LINE_DEFAULT = "\r\n";
	/*
	 * Tab
	 */
	String TAB = "\t";

	String getLineDelimiter();

	boolean isWindows();

	boolean isLinux();

	boolean isUnix();

	boolean isMac();
}
