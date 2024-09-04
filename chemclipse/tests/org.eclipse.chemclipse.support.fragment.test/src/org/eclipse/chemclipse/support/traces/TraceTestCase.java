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

import junit.framework.TestCase;

public class TraceTestCase extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		TraceGeneric traceGeneric = new TraceGeneric();
		traceGeneric.setValue(78d);
		traceGeneric.setScaleFactor(1.0d);
		assertEquals("78", traceGeneric.toString());
	}

	public void test2() {

		TraceGeneric traceGeneric = new TraceGeneric();
		traceGeneric.setValue(78.4d);
		traceGeneric.setScaleFactor(1.0d);
		assertEquals("78", traceGeneric.toString());
	}

	public void test3() {

		TraceGeneric traceGeneric = new TraceGeneric();
		traceGeneric.setValue(78.5d);
		traceGeneric.setScaleFactor(1.0d);
		assertEquals("79", traceGeneric.toString());
	}

	public void test4() {

		TraceGeneric traceGeneric = new TraceGeneric();
		traceGeneric.setValue(78d);
		traceGeneric.setScaleFactor(1.1d);
		assertEquals("78 (x1.1)", traceGeneric.toString());
	}

	public void test5() {

		TraceGeneric traceGeneric = new TraceGeneric();
		traceGeneric.setValue(78d);
		traceGeneric.setScaleFactor(1.12d);
		assertEquals("78 (x1.12)", traceGeneric.toString());
	}

	public void test6() {

		TraceGeneric traceGeneric = new TraceGeneric();
		traceGeneric.setValue(78d);
		traceGeneric.setScaleFactor(1.123d);
		assertEquals("78 (x1.123)", traceGeneric.toString());
	}

	public void test7() {

		TraceGeneric traceGeneric = new TraceGeneric();
		traceGeneric.setValue(78d);
		traceGeneric.setScaleFactor(1.1234d);
		assertEquals("78 (x1.1234)", traceGeneric.toString());
	}

	public void test8() {

		TraceGeneric traceGeneric = new TraceGeneric();
		traceGeneric.setValue(78d);
		traceGeneric.setScaleFactor(1.12345d);
		assertEquals("78 (x1.12345)", traceGeneric.toString());
	}

	public void test9() {

		TraceGeneric traceGeneric = new TraceGeneric();
		traceGeneric.setValue(78d);
		traceGeneric.setScaleFactor(1.123456d);
		assertEquals("78 (x1.12346)", traceGeneric.toString());
	}
}