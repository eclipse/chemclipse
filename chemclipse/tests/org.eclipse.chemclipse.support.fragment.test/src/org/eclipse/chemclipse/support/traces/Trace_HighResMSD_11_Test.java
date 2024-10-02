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

public class Trace_HighResMSD_11_Test extends TraceTestCase {

	public void test1() {

		TraceHighResMSD trace = TraceFactory.parseTrace("400.01627+-10ppm", TraceHighResMSD.class);
		assertEquals(400.01627d, trace.getMZ());
		assertEquals(10, trace.getPPM());
		assertEquals(0.0040001627000000005d, trace.getDelta());
		assertEquals(400.01226983730004d, trace.getStartMZ());
		assertEquals(400.0202701627d, trace.getStopMZ());
		assertFalse(trace.matches(400.0122698373d));
		assertTrue(trace.matches(400.0122698374d));
		assertTrue(trace.matches(400.0202701627d));
		assertFalse(trace.matches(400.0202701628d));
	}

	public void test2() {

		TraceHighResMSD trace = TraceFactory.parseTrace("400.01627", TraceHighResMSD.class);
		assertEquals(400.01627d, trace.getMZ());
		assertEquals(0, trace.getPPM());
		assertEquals(0.0d, trace.getDelta());
		assertEquals(400.01627d, trace.getStartMZ());
		assertEquals(400.01627d, trace.getStopMZ());
		assertFalse(trace.matches(400.01626d));
		assertTrue(trace.matches(400.01627d));
		assertFalse(trace.matches(400.01628d));
	}

	public void test3() {

		TraceHighResMSD trace = TraceFactory.parseTrace("400.01627", TraceHighResMSD.class);
		trace.setPPM(10);
		assertEquals(400.01627d, trace.getMZ());
		assertEquals(10, trace.getPPM());
		assertEquals(0.0040001627000000005d, trace.getDelta());
		assertEquals(400.01226983730004d, trace.getStartMZ());
		assertEquals(400.0202701627d, trace.getStopMZ());
		assertFalse(trace.matches(400.0122698373d));
		assertTrue(trace.matches(400.0122698374d));
		assertTrue(trace.matches(400.0202701627d));
		assertFalse(trace.matches(400.0202701628d));
	}
}