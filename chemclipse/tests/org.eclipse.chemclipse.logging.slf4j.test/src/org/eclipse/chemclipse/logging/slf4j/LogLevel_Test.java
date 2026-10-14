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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.chemclipse.logging.core.LogLevel;
import org.junit.jupiter.api.Test;

public class LogLevel_Test {

	@Test
	public void testDefault() {

		assertEquals(LogLevel.INFO, LogLevel.of(null));
		assertEquals(LogLevel.INFO, LogLevel.of("unknown"));
	}

	@Test
	public void testEclipseLevels() {

		assertEquals(LogLevel.ERROR, LogLevel.of("ERROR"));
		assertEquals(LogLevel.WARN, LogLevel.of("WARNING"));
		assertEquals(LogLevel.INFO, LogLevel.of("INFO"));
		assertEquals(LogLevel.TRACE, LogLevel.of("ALL"));
	}

	@Test
	public void testDebugLevels() {

		assertEquals(LogLevel.DEBUG, LogLevel.of("DEBUG"));
		assertEquals(LogLevel.TRACE, LogLevel.of("TRACE"));
	}

	@Test
	public void testIncludes() {

		assertTrue(LogLevel.DEBUG.includes(LogLevel.INFO));
		assertTrue(LogLevel.DEBUG.includes(LogLevel.DEBUG));
		assertFalse(LogLevel.DEBUG.includes(LogLevel.TRACE));
		assertFalse(LogLevel.WARN.includes(LogLevel.INFO));
	}
}
