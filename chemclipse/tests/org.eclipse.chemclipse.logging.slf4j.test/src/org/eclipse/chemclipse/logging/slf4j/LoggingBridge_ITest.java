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

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class LoggingBridge_ITest {

	@Test
	public void testProvider() {

		assertEquals("org.eclipse.chemclipse.logging.slf4j.ChemClipseLoggerFactory", LoggerFactory.getILoggerFactory().getClass().getName());
	}

	@Test
	public void testLog() {

		List<IStatus> statuses = new CopyOnWriteArrayList<>();
		ILogListener listener = (status, _) -> statuses.add(status);
		Platform.addLogListener(listener);
		try {
			LoggerFactory.getLogger(LoggingBridge_ITest.class).warn("Bridge test {}", 42);
		} finally {
			Platform.removeLogListener(listener);
		}
		assertTrue(statuses.stream().anyMatch(status -> status.getSeverity() == IStatus.WARNING && status.getMessage().equals(LoggingBridge_ITest.class.getName() + ": Bridge test 42")));
	}
}
