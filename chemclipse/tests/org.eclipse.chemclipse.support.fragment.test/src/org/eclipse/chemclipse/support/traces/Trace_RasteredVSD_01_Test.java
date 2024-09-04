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

public class Trace_RasteredVSD_01_Test extends TraceTestCase {

	private TraceRasteredVSD trace;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		trace = TraceFactory.parseTrace("1800", TraceRasteredVSD.class);
	}

	public void testNull() {

		assertNotNull(trace);
	}

	public void testWavenumber() {

		assertEquals(1800, trace.getWavenumber());
	}

	public void testScaleFactor() {

		assertEquals(1.0d, trace.getScaleFactor());
	}

	public void testString() {

		assertEquals("1800", trace.toString());
	}
}