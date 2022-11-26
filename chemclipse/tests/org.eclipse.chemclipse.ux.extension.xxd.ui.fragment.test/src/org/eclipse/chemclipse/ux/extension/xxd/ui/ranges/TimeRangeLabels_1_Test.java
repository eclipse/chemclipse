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

public class TimeRangeLabels_1_Test extends TestCase {

	private TimeRangeLabels timeRangeLabels;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		timeRangeLabels = new TimeRangeLabels();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("Time Range", timeRangeLabels.getTitle());
	}

	public void test2() {

		assertEquals("C10", timeRangeLabels.getInitialValue());
	}

	public void test3() {

		assertEquals("Add a new Time Range.", timeRangeLabels.getAddMessage());
	}

	public void test4() {

		assertEquals("Please define a new Time Range.", timeRangeLabels.getAddError());
	}

	public void test5() {

		assertEquals("The Time Range exists already.", timeRangeLabels.getAddExists());
	}

	public void test6() {

		assertEquals("Create a new Time Range.", timeRangeLabels.getCreateMessage());
	}

	public void test7() {

		assertEquals("C10 | 10.2 | 10.4 | 10.6", timeRangeLabels.getCreateInitialValue());
	}

	public void test8() {

		assertEquals("Edit the selected Time Range.", timeRangeLabels.getEditMessage());
	}

	public void test9() {

		assertEquals("Would you like to delete the selected Time Range?", timeRangeLabels.getDeleteMessage());
	}

	public void test10() {

		assertEquals("Would you like to delete the all Time Ranges?", timeRangeLabels.getClearMessage());
	}

	public void test11() {

		assertEquals("The Time Range must not contain the following delimiter '|'.", timeRangeLabels.getErrorDelimiter());
	}

	public void test12() {

		assertEquals(3, timeRangeLabels.getProposals().length);
	}

	public void test13() {

		assertEquals("C10", timeRangeLabels.getProposals()[0]);
	}

	public void test14() {

		assertEquals("C11", timeRangeLabels.getProposals()[1]);
	}

	public void test15() {

		assertEquals("C12", timeRangeLabels.getProposals()[2]);
	}
}