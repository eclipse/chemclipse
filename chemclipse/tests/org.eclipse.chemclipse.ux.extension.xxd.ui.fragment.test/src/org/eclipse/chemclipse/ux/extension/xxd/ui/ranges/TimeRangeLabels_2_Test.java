/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import junit.framework.TestCase;

public class TimeRangeLabels_2_Test extends TestCase {

	private TimeRangeLabels timeRangeLabels;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		timeRangeLabels = new TimeRangeLabels("Alkane", "C20");
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("Alkane", timeRangeLabels.getTitle());
	}

	public void test2() {

		assertEquals("C20", timeRangeLabels.getInitialValue());
	}

	public void test3() {

		assertEquals("Add a new Alkane.", timeRangeLabels.getAddMessage());
	}

	public void test4() {

		assertEquals("Please define a new Alkane.", timeRangeLabels.getAddError());
	}

	public void test5() {

		assertEquals("The Alkane exists already.", timeRangeLabels.getAddExists());
	}

	public void test6() {

		assertEquals("Create a new Alkane.", timeRangeLabels.getCreateMessage());
	}

	public void test7() {

		assertEquals("C20 | 10.2 | 10.4 | 10.6", timeRangeLabels.getCreateInitialValue());
	}

	public void test8() {

		assertEquals("Edit the selected Alkane.", timeRangeLabels.getEditMessage());
	}

	public void test9() {

		assertEquals("Would you like to delete the selected Alkane?", timeRangeLabels.getDeleteMessage());
	}

	public void test10() {

		assertEquals("Would you like to delete the all Alkanes?", timeRangeLabels.getClearMessage());
	}
}