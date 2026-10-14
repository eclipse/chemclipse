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

import org.eclipse.chemclipse.logging.core.LogLevel;
import org.eclipse.chemclipse.logging.core.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;

/**
 * Forwards SLF4J log events to the ChemClipse logger, which decides
 * by -Declipse.log.level which levels are logged. The name of the
 * SLF4J logger, usually a class, is shown as the plug-in.
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

		LogLevel logLevel = switch(level) {
			case ERROR -> LogLevel.ERROR;
			case WARN -> LogLevel.WARN;
			case INFO -> LogLevel.INFO;
			case DEBUG -> LogLevel.DEBUG;
			case TRACE -> LogLevel.TRACE;
		};
		logger.log(logLevel, name, MessageFormatter.basicArrayFormat(messagePattern, arguments), throwable);
	}
}
