/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.traces;

public class Trace_NominalMSD_07_Test extends TraceTestCase {

	private TraceNominalMSD trace;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		trace = TraceFactory.parseTrace("79 (x0.27)", TraceNominalMSD.class);
	}

	public void testNull() {

		assertNotNull(trace);
	}

	public void testMZ() {

		assertEquals(79, trace.getMZ());
	}

	public void testScaleFactor() {

		assertEquals(0.27d, trace.getScaleFactor());
	}

	public void testString() {

		assertEquals("79 (x0.27)", trace.toString());
	}
}