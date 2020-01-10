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

import org.eclipse.chemclipse.msd.model.core.PeakIonType;
import junit.framework.TestCase;

public class PeakIon_2_Test extends TestCase {

	private PeakIon peakIon;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		peakIon = new PeakIon(45.5f, 2500.4f);
	}

	@Override
	protected void tearDown() throws Exception {

		peakIon = null;
		super.tearDown();
	}

	public void testGetUncertaintyFactor_1() {

		assertEquals("GetUncertaintyFactor", 1.0f, peakIon.getUncertaintyFactor());
		peakIon.setUncertaintyFactor(0.3f);
		assertEquals("GetUncertaintyFactor", 0.3f, peakIon.getUncertaintyFactor());
	}

	public void testGetUncertaintyFactor_2() {

		assertEquals("GetUncertaintyFactor", 1.0f, peakIon.getUncertaintyFactor());
		peakIon.setUncertaintyFactor(0.0f);
		assertEquals("GetUncertaintyFactor", 0.0f, peakIon.getUncertaintyFactor());
	}

	public void testGetUncertaintyFactor_3() {

		assertEquals("GetUncertaintyFactor", 1.0f, peakIon.getUncertaintyFactor());
		peakIon.setUncertaintyFactor(1.0f);
		assertEquals("GetUncertaintyFactor", 1.0f, peakIon.getUncertaintyFactor());
	}

	public void testGetUncertaintyFactor_4() {

		assertEquals("GetUncertaintyFactor", 1.0f, peakIon.getUncertaintyFactor());
		peakIon.setUncertaintyFactor(-0.1f);
		assertEquals("GetUncertaintyFactor", 1.0f, peakIon.getUncertaintyFactor());
	}

	public void testGetUncertaintyFactor_5() {

		assertEquals("GetUncertaintyFactor", 1.0f, peakIon.getUncertaintyFactor());
		peakIon.setUncertaintyFactor(1.1f);
		assertEquals("GetUncertaintyFactor", 1.0f, peakIon.getUncertaintyFactor());
	}

	public void testGetPeakIonType_1() {

		assertEquals("GetPeakIonType", PeakIonType.NO_TYPE, peakIon.getPeakIonType());
		peakIon.setPeakIonType(PeakIonType.B);
		assertEquals("GetPeakIonType", PeakIonType.B, peakIon.getPeakIonType());
	}

	public void testGetPeakIonType_2() {

		assertEquals("GetPeakIonType", PeakIonType.NO_TYPE, peakIon.getPeakIonType());
		peakIon.setPeakIonType(null);
		assertEquals("GetPeakIonType", PeakIonType.NO_TYPE, peakIon.getPeakIonType());
	}
}
