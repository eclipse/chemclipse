/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.ranges;

import junit.framework.TestCase;

public class TimeRange_9_Test extends TestCase {

	private TimeRange timeRange;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		timeRange = new TimeRange("Test", 500, 750, 1000);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test0() {

		assertEquals("Test", timeRange.getIdentifier());
	}

	public void test1() {

		assertEquals(500, timeRange.getStart());
	}

	public void test2() {

		assertEquals(750, timeRange.getCenter());
	}

	public void test3() {

		assertEquals(1000, timeRange.getStop());
	}

	public void test4() {

		timeRange.updateStart(800);
		assertEquals(800, timeRange.getStart());
		assertEquals(900, timeRange.getCenter());
		assertEquals(1000, timeRange.getStop());
	}

	public void test5() {

		timeRange.updateStop(700);
		assertEquals(500, timeRange.getStart());
		assertEquals(600, timeRange.getCenter());
		assertEquals(700, timeRange.getStop());
	}
}
