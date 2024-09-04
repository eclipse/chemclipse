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

import java.util.List;

public class Traces_05_Test extends TraceTestCase {

	private String content = "400.01627±10ppm, 417.028±5ppm";

	public void test1() {

		List<? extends ITrace> traces = TraceFactory.parseTraces(content, TraceType.GENERIC.clazz());
		assertEquals(2, traces.size());
		assertEquals(400.01627d, traces.get(0).getValue());
		assertEquals(417.028d, traces.get(1).getValue());
	}

	public void test2() {

		List<? extends ITrace> traces = TraceFactory.parseTraces(content, TraceType.MSD_NOMINAL.clazz());
		assertTrue(traces.isEmpty());
	}

	public void test3() {

		List<? extends ITrace> traces = TraceFactory.parseTraces(content, TraceType.MSD_TANDEM.clazz());
		assertTrue(traces.isEmpty());
	}

	public void test4() {

		List<? extends ITrace> traces = TraceFactory.parseTraces(content, TraceType.MSD_HIGHRES.clazz());
		assertEquals(2, traces.size());
		assertEquals(400.01627d, ((TraceHighResMSD)traces.get(0)).getMZ());
		assertEquals(417.028d, ((TraceHighResMSD)traces.get(1)).getMZ());
	}

	public void test5() {

		List<? extends ITrace> traces = TraceFactory.parseTraces(content, TraceType.VSD_RASTERED.clazz());
		assertTrue(traces.isEmpty());
	}

	public void test6() {

		List<? extends ITrace> traces = TraceFactory.parseTraces(content, TraceType.WSD_RASTERED.clazz());
		assertTrue(traces.isEmpty());
	}

	public void test7() {

		List<? extends ITrace> traces = TraceFactory.parseTraces(content, TraceType.WSD_HIGHRES.clazz());
		assertTrue(traces.isEmpty());
	}
}