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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.eclipse.chemclipse.logging.core.LogLevel;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingBridge_ITest {

	private static final String MESSAGE = LoggingBridge_ITest.class.getName() + ": Bridge test 42";
	private static final LogLevel LEVEL = LogLevel.of(System.getProperty(LogLevel.D_ECLIPSE_LOG_LEVEL));

	@Test
	public void testProvider() {

		assertEquals("org.eclipse.chemclipse.logging.slf4j.ChemClipseLoggerFactory", LoggerFactory.getILoggerFactory().getClass().getName());
	}

	@Test
	public void testWarn() {

		List<IStatus> statuses = log(logger -> logger.warn("Bridge test {}", 42));
		assertTrue(contains(statuses, IStatus.WARNING));
	}

	@Test
	public void testDebug() {

		List<IStatus> statuses = log(logger -> logger.debug("Bridge test {}", 42));
		assertEquals(LEVEL.includes(LogLevel.DEBUG), LoggerFactory.getLogger(LoggingBridge_ITest.class).isDebugEnabled());
		assertEquals(LEVEL.includes(LogLevel.DEBUG), contains(statuses, IStatus.OK));
	}

	@Test
	public void testTrace() {

		List<IStatus> statuses = log(logger -> logger.trace("Bridge test {}", 42));
		assertEquals(LEVEL.includes(LogLevel.TRACE), LoggerFactory.getLogger(LoggingBridge_ITest.class).isTraceEnabled());
		assertEquals(LEVEL.includes(LogLevel.TRACE), contains(statuses, IStatus.OK));
	}

	private static List<IStatus> log(Consumer<Logger> consumer) {

		List<IStatus> statuses = new CopyOnWriteArrayList<>();
		ILogListener listener = (status, _) -> statuses.add(status);
		Platform.addLogListener(listener);
		try {
			consumer.accept(LoggerFactory.getLogger(LoggingBridge_ITest.class));
		} finally {
			Platform.removeLogListener(listener);
		}
		return statuses;
	}

	private static boolean contains(List<IStatus> statuses, int severity) {

		return statuses.stream().anyMatch(status -> status.getSeverity() == severity && status.getMessage().equals(MESSAGE));
	}
}
