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

public class Traces_03_Test extends TraceTestCase {

	private String content = "18 28 32 55 - 65 84 207";

	public void test1() {

		List<? extends ITrace> traces = TraceFactory.parseTraces(content, TraceType.GENERIC.clazz());
		assertTrue(traces.isEmpty());
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
		assertTrue(traces.isEmpty());
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