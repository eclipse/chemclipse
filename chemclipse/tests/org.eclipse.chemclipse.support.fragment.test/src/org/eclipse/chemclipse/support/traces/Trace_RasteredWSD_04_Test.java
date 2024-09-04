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

public class Trace_RasteredWSD_04_Test extends TraceTestCase {

	private TraceRasteredWSD trace;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		trace = TraceFactory.parseTrace("201 (x1.6)", TraceRasteredWSD.class);
	}

	public void testNull() {

		assertNotNull(trace);
	}

	public void testWavelength() {

		assertEquals(201, trace.getWavelength());
	}

	public void testScaleFactor() {

		assertEquals(1.6d, trace.getScaleFactor());
	}

	public void testString() {

		assertEquals("201 (x1.6)", trace.toString());
	}
}