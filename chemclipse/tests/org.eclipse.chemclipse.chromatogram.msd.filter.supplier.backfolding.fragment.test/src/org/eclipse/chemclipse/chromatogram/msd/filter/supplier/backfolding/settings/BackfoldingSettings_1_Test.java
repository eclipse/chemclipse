/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.BackfoldingSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.IBackfoldingSettings;

import junit.framework.TestCase;

public class BackfoldingSettings_1_Test extends TestCase {

	private IBackfoldingSettings settings;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		settings = new BackfoldingSettings();
	}

	@Override
	protected void tearDown() throws Exception {

		settings = null;
		super.tearDown();
	}

	public void testGetMaximumRetentionTimeShift_1() {

		int shift = settings.getMaximumRetentionTimeShift();
		assertEquals("RetentionTimeShift", 5000, shift);
	}

	public void testGetNumberOfBackfoldingRuns_1() {

		int runs = settings.getNumberOfBackfoldingRuns();
		assertEquals("Backfolding Runs", 3, runs);
	}

	public void testSetMaximumRetentionTimeShift_1() {

		settings.setMaximumRetentionTimeShift(1000);
		int shift = settings.getMaximumRetentionTimeShift();
		assertEquals("RetentionTimeShift", 1000, shift);
	}

	public void testSetMaximumRetentionTimeShift_2() {

		settings.setMaximumRetentionTimeShift(500);
		int shift = settings.getMaximumRetentionTimeShift();
		assertEquals("RetentionTimeShift", 500, shift);
	}

	public void testSetMaximumRetentionTimeShift_3() {

		settings.setMaximumRetentionTimeShift(25000);
		int shift = settings.getMaximumRetentionTimeShift();
		assertEquals("RetentionTimeShift", 25000, shift);
	}

	public void testSetMaximumRetentionTimeShift_4() {

		settings.setMaximumRetentionTimeShift(499);
		int shift = settings.getMaximumRetentionTimeShift();
		assertEquals("RetentionTimeShift", 5000, shift);
	}

	public void testSetMaximumRetentionTimeShift_5() {

		settings.setMaximumRetentionTimeShift(25001);
		int shift = settings.getMaximumRetentionTimeShift();
		assertEquals("RetentionTimeShift", 5000, shift);
	}

	public void testSetNumberOfBackfoldingRuns_1() {

		settings.setNumberOfBackfoldingRuns(5);
		int runs = settings.getNumberOfBackfoldingRuns();
		assertEquals("Backfolding Runs", 5, runs);
	}

	public void testSetNumberOfBackfoldingRuns_2() {

		settings.setNumberOfBackfoldingRuns(1);
		int runs = settings.getNumberOfBackfoldingRuns();
		assertEquals("Backfolding Runs", 1, runs);
	}

	public void testSetNumberOfBackfoldingRuns_3() {

		settings.setNumberOfBackfoldingRuns(10);
		int runs = settings.getNumberOfBackfoldingRuns();
		assertEquals("Backfolding Runs", 10, runs);
	}

	public void testSetNumberOfBackfoldingRuns_4() {

		settings.setNumberOfBackfoldingRuns(0);
		int runs = settings.getNumberOfBackfoldingRuns();
		assertEquals("Backfolding Runs", 3, runs);
	}

	public void testSetNumberOfBackfoldingRuns_5() {

		settings.setNumberOfBackfoldingRuns(11);
		int runs = settings.getNumberOfBackfoldingRuns();
		assertEquals("Backfolding Runs", 3, runs);
	}
}
