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
 * Forwards SLF4J log events to the ChemClipse logger. The Eclipse log has no
 * debug or trace level, so these are dropped to keep the error log readable.
 */
public class ChemClipseLoggerAdapter extends LegacyAbstractLogger {

	private static final long serialVersionUID = 3215894760917643028L;
	private static final Logger logger = Logger.getLogger(ChemClipseLoggerAdapter.class);

	public ChemClipseLoggerAdapter(String name) {

		this.name = name;
	}

	@Override
	public boolean isTraceEnabled() {

		return false;
	}

	@Override
	public boolean isDebugEnabled() {

		return false;
	}

	@Override
	public boolean isInfoEnabled() {

		return true;
	}

	@Override
	public boolean isWarnEnabled() {

		return true;
	}

	@Override
	public boolean isErrorEnabled() {

		return true;
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
			case INFO, DEBUG, TRACE -> logger.info(message, throwable);
		}
	}
}
