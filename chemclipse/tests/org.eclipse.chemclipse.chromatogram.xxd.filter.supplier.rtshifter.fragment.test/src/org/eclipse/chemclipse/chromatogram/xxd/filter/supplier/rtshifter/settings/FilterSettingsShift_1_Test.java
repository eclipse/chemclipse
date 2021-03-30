/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings;

import junit.framework.TestCase;

public class FilterSettingsShift_1_Test extends TestCase {

	private FilterSettingsShift settings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test_1() {

		int millisecondsToShift = 0;
		settings = new FilterSettingsShift(millisecondsToShift, true);
		assertEquals(millisecondsToShift, settings.getMillisecondsShift());
	}

	public void test_2() {

		int millisecondsToShift = 1;
		settings = new FilterSettingsShift(millisecondsToShift, true);
		assertEquals(millisecondsToShift, settings.getMillisecondsShift());
	}

	public void test_3() {

		int millisecondsToShift = -1;
		settings = new FilterSettingsShift(millisecondsToShift, true);
		assertEquals(millisecondsToShift, settings.getMillisecondsShift());
	}

	public void test_4() {

		int millisecondsToShift = 1500;
		settings = new FilterSettingsShift(millisecondsToShift, true);
		assertEquals(millisecondsToShift, settings.getMillisecondsShift());
	}

	public void test_5() {

		int millisecondsToShift = -1500;
		settings = new FilterSettingsShift(millisecondsToShift, true);
		assertEquals(millisecondsToShift, settings.getMillisecondsShift());
	}
}