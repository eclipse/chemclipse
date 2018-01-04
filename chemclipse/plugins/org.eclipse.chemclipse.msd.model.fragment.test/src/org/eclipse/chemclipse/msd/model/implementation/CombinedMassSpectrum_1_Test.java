/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import junit.framework.TestCase;

public class CombinedMassSpectrum_1_Test extends TestCase {

	private ICombinedMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new CombinedMassSpectrum();
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		super.tearDown();
	}

	public void testStartRetentionTime_1() {

		assertEquals("Start RT", 0, massSpectrum.getStartRetentionTime());
	}

	public void testStartRetentionTime_2() {

		massSpectrum.setStartRetentionTime(-1);
		assertEquals("Start RT", 0, massSpectrum.getStartRetentionTime());
	}

	public void testStartRetentionTime_3() {

		massSpectrum.setStartRetentionTime(4500);
		assertEquals("Start RT", 4500, massSpectrum.getStartRetentionTime());
	}

	public void testStartRetentionIndex_1() {

		assertEquals("Start RI", 0.0f, massSpectrum.getStartRetentionIndex());
	}

	public void testStartRetentionIndex_2() {

		massSpectrum.setStartRetentionIndex(-1.0f);
		assertEquals("Start RI", 0.0f, massSpectrum.getStartRetentionIndex());
	}

	public void testStartRetentionIndex_3() {

		massSpectrum.setStartRetentionIndex(745.2f);
		assertEquals("Start RI", 745.2f, massSpectrum.getStartRetentionIndex());
	}

	public void testStartScan_1() {

		assertEquals("Start Scan", 0, massSpectrum.getStartScan());
	}

	public void testStartScan_2() {

		massSpectrum.setStartScan(-1);
		assertEquals("Start Scan", 0, massSpectrum.getStartScan());
	}

	public void testStartScan_3() {

		massSpectrum.setStartScan(425);
		assertEquals("Start Scan", 425, massSpectrum.getStartScan());
	}

	public void testStopRetentionTime_1() {

		assertEquals("Stop RT", 0, massSpectrum.getStopRetentionTime());
	}

	public void testStopRetentionTime_2() {

		massSpectrum.setStopRetentionTime(-1);
		assertEquals("Stop RT", 0, massSpectrum.getStopRetentionTime());
	}

	public void testStopRetentionTime_3() {

		massSpectrum.setStopRetentionTime(7892);
		assertEquals("Stop RT", 7892, massSpectrum.getStopRetentionTime());
	}

	public void testStopRetentionIndex_1() {

		assertEquals("Stop RI", 0.0f, massSpectrum.getStopRetentionIndex());
	}

	public void testStopRetentionIndex_2() {

		massSpectrum.setStopRetentionIndex(-1.0f);
		assertEquals("Stop RI", 0.0f, massSpectrum.getStopRetentionIndex());
	}

	public void testStopRetentionIndex_3() {

		massSpectrum.setStopRetentionIndex(4050.3f);
		assertEquals("Stop RI", 4050.3f, massSpectrum.getStopRetentionIndex());
	}

	public void testStopScan_1() {

		assertEquals("Stop Scan", 0, massSpectrum.getStopScan());
	}

	public void testStopScan_2() {

		massSpectrum.setStopScan(-1);
		assertEquals("Stop Scan", 0, massSpectrum.getStopScan());
	}

	public void testStopScan_3() {

		massSpectrum.setStopScan(7855);
		assertEquals("Stop Scan", 7855, massSpectrum.getStopScan());
	}
}
