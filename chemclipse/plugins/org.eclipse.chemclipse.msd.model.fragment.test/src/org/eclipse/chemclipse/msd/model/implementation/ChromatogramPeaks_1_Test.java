/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.implementation.Peaks;

public class ChromatogramPeaks_1_Test extends ChromatogramPeaksTestCase {

	private IPeaks peaks;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		peaks = new Peaks();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		assertEquals(0, peaks.size());
	}

	public void testSize_2() {

		peaks.addPeak(getPeak1());
		assertEquals(1, peaks.size());
	}

	public void testSize_3() {

		peaks.addPeak(getPeak1());
		peaks.addPeak(getPeak1());
		assertEquals(2, peaks.size());
	}

	public void testSize_4() {

		peaks.addPeak(getPeak2());
		assertEquals(1, peaks.size());
	}

	public void testSize_5() {

		peaks.addPeak(getPeak2());
		peaks.addPeak(getPeak2());
		assertEquals(2, peaks.size());
	}

	public void testSize_6() {

		peaks.addPeak(getPeak3());
		assertEquals(1, peaks.size());
	}

	public void testSize_7() {

		peaks.addPeak(getPeak3());
		peaks.addPeak(getPeak3());
		assertEquals(2, peaks.size());
	}
}
