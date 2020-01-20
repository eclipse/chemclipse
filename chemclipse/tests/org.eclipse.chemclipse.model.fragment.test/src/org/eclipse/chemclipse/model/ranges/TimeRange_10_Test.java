/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

public class TimeRange_10_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test0a() {

		try {
			new TimeRange("Test", -1, -1);
		} catch(IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void test0b() {

		try {
			new TimeRange("Test", -1, -1, -1);
		} catch(IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void test1a() {

		TimeRange timeRange = new TimeRange("Test", 0, 0);
		assertEquals(0, timeRange.getStart());
		assertEquals(0, timeRange.getCenter());
		assertEquals(0, timeRange.getStop());
	}

	public void test1b() {

		TimeRange timeRange = new TimeRange("Test", 0, 0, 0);
		assertEquals(0, timeRange.getStart());
		assertEquals(0, timeRange.getCenter());
		assertEquals(0, timeRange.getStop());
	}

	public void test2a() {

		TimeRange timeRange = new TimeRange("Test", 1, 1);
		assertEquals(1, timeRange.getStart());
		assertEquals(1, timeRange.getCenter());
		assertEquals(1, timeRange.getStop());
	}

	public void test2b() {

		TimeRange timeRange = new TimeRange("Test", 1, 1, 1);
		assertEquals(1, timeRange.getStart());
		assertEquals(1, timeRange.getCenter());
		assertEquals(1, timeRange.getStop());
	}
}
