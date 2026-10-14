/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.core;

public enum LogLevel {

	ERROR, //
	WARN, //
	INFO, //
	DEBUG, //
	TRACE;

	/*
	 * The level is defined as a parameter when starting the application.
	 * -Declipse.log.level=ERROR|WARNING|INFO|DEBUG|TRACE|ALL
	 */
	public static final String D_ECLIPSE_LOG_LEVEL = "eclipse.log.level";

	/**
	 * Equinox writes every severity to the .log file unless the level is ERROR, WARNING or INFO.
	 * Debug and trace messages would flood it, so they need to be requested explicitly.
	 */
	public static LogLevel of(String value) {

		if(value == null) {
			return INFO;
		}
		return switch(value) {
			case "ERROR" -> ERROR;
			case "WARNING" -> WARN;
			case "DEBUG" -> DEBUG;
			case "TRACE", "ALL" -> TRACE;
			default -> INFO;
		};
	}

	/**
	 * Returns true if messages of the given level are logged at this level.
	 */
	public boolean includes(LogLevel level) {

		return level.ordinal() <= ordinal();
	}
}
