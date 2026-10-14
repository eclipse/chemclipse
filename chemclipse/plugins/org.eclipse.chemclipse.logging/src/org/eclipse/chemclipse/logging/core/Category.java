/*******************************************************************************
 * Copyright (c) 2008, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class Category {

	private static final LogLevel LEVEL = LogLevel.of(System.getProperty(LogLevel.D_ECLIPSE_LOG_LEVEL));

	private final ILog logger;

	public Category(final Class<?> clazz) {

		logger = ILog.of(clazz);
	}

	public void error(final String message) {

		logger.error(message);
	}

	public void error(Throwable t) {

		if(t != null) {
			logger.error(t.getMessage(), t);
		}
	}

	public void error(final String message, Throwable t) {

		logger.error(message, t);
	}

	public void info(Object object) {

		logger.info(object.toString());
	}

	public void info(final String message) {

		logger.info(message);
	}

	public void info(Throwable t) {

		if(t != null) {
			logger.info(t.getMessage(), t);
		}
	}

	public void info(final String message, Throwable t) {

		logger.info(message, t);
	}

	public void warn(final String message) {

		logger.warn(message);
	}

	public void warn(final String message, Throwable t) {

		logger.warn(message, t);
	}

	public void warn(Throwable t) {

		if(t != null) {
			logger.warn(t.getMessage(), t);
		}
	}

	/**
	 * Eclipse logs debug messages as IStatus.OK.
	 * They are only logged with -Declipse.log.level=DEBUG.
	 */
	public void debug(final String message) {

		debug(message, null);
	}

	public void debug(final String message, Throwable t) {

		if(isDebugEnabled()) {
			logger.log(new Status(IStatus.OK, logger.getBundle().getSymbolicName(), message, t));
		}
	}

	/**
	 * Eclipse has no trace severity, so trace messages are logged as IStatus.OK as well.
	 * They are only logged with -Declipse.log.level=TRACE.
	 */
	public void trace(final String message) {

		trace(message, null);
	}

	public void trace(final String message, Throwable t) {

		if(isTraceEnabled()) {
			logger.log(new Status(IStatus.OK, logger.getBundle().getSymbolicName(), message, t));
		}
	}

	public boolean isErrorEnabled() {

		return LEVEL.includes(LogLevel.ERROR);
	}

	public boolean isWarnEnabled() {

		return LEVEL.includes(LogLevel.WARN);
	}

	public boolean isInfoEnabled() {

		return LEVEL.includes(LogLevel.INFO);
	}

	public boolean isDebugEnabled() {

		return LEVEL.includes(LogLevel.DEBUG);
	}

	public boolean isTraceEnabled() {

		return LEVEL.includes(LogLevel.TRACE);
	}
}
