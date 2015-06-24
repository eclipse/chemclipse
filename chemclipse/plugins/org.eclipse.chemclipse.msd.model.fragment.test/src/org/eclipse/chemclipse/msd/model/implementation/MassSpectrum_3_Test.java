/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

import junit.framework.TestCase;

/**
 * massSpectrum = new DefaultMassSpectrum();
 * 
 * @author eselmeister
 */
public class MassSpectrum_3_Test extends TestCase {

	private ScanMSD massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new ScanMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		super.tearDown();
	}

	public void testGetIons_1() {

		assertEquals("getIons", 0, massSpectrum.getIons().size());
	}

	public void testGetParentChromatogram_1() {

		assertEquals("getParentChromatogram", null, massSpectrum.getParentChromatogram());
	}

	public void getTotalSignal_1() {

		assertEquals("getTotalSignal", 0, massSpectrum.getTotalSignal());
	}

	public void getExtractedIonSignal_1() {

		assertEquals("getExtractedIonSignal", 0, massSpectrum.getExtractedIonSignal().getAbundance(0));
	}

	public void getExtractedIonSignal_2() {

		assertEquals("getExtractedIonSignal", 0, massSpectrum.getExtractedIonSignal(1, 50).getAbundance(1));
	}

	public void testGetBasePeak_1() {

		assertEquals("getBasePeak", 0.0d, massSpectrum.getBasePeak());
	}

	public void testGetBasePeakAbundance_1() {

		assertEquals("getBasePeakAbundance", 0.0f, massSpectrum.getBasePeakAbundance());
	}

	public void testGetHighestAbundance_1() {

		assertEquals("getHighestAbundance", null, massSpectrum.getHighestAbundance());
	}

	public void testGetHighestIon_1() {

		assertEquals("getHighestIon", null, massSpectrum.getHighestIon());
	}

	public void testGetLowestAbundance_1() {

		assertEquals("getLowestAbundance", null, massSpectrum.getLowestAbundance());
	}

	public void testGetLowestIon_1() {

		assertEquals("getLowestIon", null, massSpectrum.getLowestIon());
	}

	public void testGetIonBounds_1() {

		assertEquals("getIonBounds", null, massSpectrum.getIonBounds());
	}

	public void testGetNumberOfIons_1() {

		assertEquals("getNumberOfIons", 0, massSpectrum.getNumberOfIons());
	}

	public void testGetIon_1() {

		IIon ion;
		try {
			ion = massSpectrum.getIon(5);
			assertEquals("getIon", null, ion);
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException", false);
		}
	}

	public void testIsDirty_1() {

		assertEquals("isDirty", false, massSpectrum.isDirty());
	}
}
