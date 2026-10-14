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
package org.eclipse.chemclipse.logging.slf4j;

import org.eclipse.chemclipse.logging.core.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;

/**
 * Forwards SLF4J log events to the ChemClipse logger, which decides
 * by -Declipse.log.level which levels are logged.
 */
public class ChemClipseLoggerAdapter extends LegacyAbstractLogger {

	private static final long serialVersionUID = 3215894760917643028L;
	private static final Logger logger = Logger.getLogger(ChemClipseLoggerAdapter.class);

	public ChemClipseLoggerAdapter(String name) {

		this.name = name;
	}

	@Override
	public boolean isTraceEnabled() {

		return logger.isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {

		return logger.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {

		return logger.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {

		return logger.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {

		return logger.isErrorEnabled();
	}

	@Override
	protected String getFullyQualifiedCallerName() {

		return null;
	}

	@Override
	protected void handleNormalizedLoggingCall(Level level, Marker marker, String messagePattern, Object[] arguments, Throwable throwable) {

		String message = name + ": " + MessageFormatter.basicArrayFormat(messagePattern, arguments);
		switch(level) {
			case ERROR -> logger.error(message, throwable);
			case WARN -> logger.warn(message, throwable);
			case INFO -> logger.info(message, throwable);
			case DEBUG -> logger.debug(message, throwable);
			case TRACE -> logger.trace(message, throwable);
		}
	}
}
