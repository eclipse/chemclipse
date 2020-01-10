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

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonBounds;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;

import junit.framework.TestCase;

/**
 * chromatogram = new DefaultChromatogram();
 * massSpectrum.setParentChromatogram(chromatogram); ion = new
 * DefaultIon(45.5f, 3000.5f);
 * massSpectrum.addIon(ion);
 * 
 * @author eselmeister
 */
public class MassSpectrum_4_Test extends TestCase {

	private ChromatogramMSD chromatogram;
	private ScanMSD massSpectrum;
	private Ion ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		massSpectrum = new ScanMSD();
		ion = new Ion(45.5f, 3000.5f);
		massSpectrum.addIon(ion);
		chromatogram.addScan(massSpectrum);
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		ion = null;
		super.tearDown();
	}

	public void testGetIons_1() {

		assertEquals("getIons", 1, massSpectrum.getIons().size());
	}

	public void testGetParentChromatogram_1() {

		assertEquals("getParentChromatogram", chromatogram, massSpectrum.getParentChromatogram());
	}

	public void getTotalSignal_1() {

		assertEquals("getTotalSignal", 3000.5f, massSpectrum.getTotalSignal());
	}

	public void getExtractedIonSignal_1() {

		assertEquals("getExtractedIonSignal", 0, massSpectrum.getExtractedIonSignal().getAbundance(0));
	}

	public void getExtractedIonSignal_2() {

		assertEquals("getExtractedIonSignal", 3000.5f, massSpectrum.getExtractedIonSignal(1, 50).getAbundance(46));
	}

	public void testGetBasePeak_1() {

		assertEquals("getBasePeak", 45.5d, massSpectrum.getBasePeak());
	}

	public void testGetBasePeakAbundance_1() {

		assertEquals("getBasePeakAbundance", 3000.5f, massSpectrum.getBasePeakAbundance());
	}

	public void testGetHighestAbundance_1() {

		assertEquals("getHighestAbundance", 3000.5f, massSpectrum.getHighestAbundance().getAbundance());
	}

	public void testGetHighestAbundance_2() {

		assertEquals("getHighestAbundance", 45.5d, massSpectrum.getHighestAbundance().getIon());
	}

	public void testGetHighestIon_1() {

		assertEquals("getHighestIon", 3000.5f, massSpectrum.getHighestIon().getAbundance());
	}

	public void testGetHighestIon_2() {

		assertEquals("getHighestIon", 45.5d, massSpectrum.getHighestIon().getIon());
	}

	public void testGetLowestAbundance_1() {

		assertEquals("getLowestAbundance", 3000.5f, massSpectrum.getLowestAbundance().getAbundance());
	}

	public void testGetLowestAbundance_2() {

		assertEquals("getLowestAbundance", 45.5d, massSpectrum.getLowestAbundance().getIon());
	}

	public void testGetLowestIon_1() {

		assertEquals("getLowestIon", 3000.5f, massSpectrum.getLowestIon().getAbundance());
	}

	public void testGetLowestIon_2() {

		assertEquals("getLowestIon", 45.5d, massSpectrum.getLowestIon().getIon());
	}

	public void testGetIonBounds_1() {

		IIonBounds bounds = massSpectrum.getIonBounds();
		assertEquals("getIonBounds", 3000.5f, bounds.getLowestIon().getAbundance());
		assertEquals("getIonBounds", 45.5d, bounds.getLowestIon().getIon());
		assertEquals("getIonBounds", 3000.5f, bounds.getHighestIon().getAbundance());
		assertEquals("getIonBounds", 45.5d, bounds.getHighestIon().getIon());
	}

	public void testGetNumberOfIons_1() {

		assertEquals("getNumberOfIons", 1, massSpectrum.getNumberOfIons());
	}

	public void testGetIon_1() {

		IIon ion;
		try {
			ion = massSpectrum.getIon(5);
			assertEquals("getIon", null, ion);
			ion = massSpectrum.getIon(46);
			assertTrue("getIon", ion != null);
			assertEquals("getIon(46) abundance", 3000.5f, ion.getAbundance());
			assertEquals("getIon(46) ion", 46.0d, ion.getIon());
		} catch(AbundanceLimitExceededException e) {
			assertTrue("AbundanceLimitExceededException", false);
		} catch(IonLimitExceededException e) {
			assertTrue("IonLimitExceededException", false);
		}
	}

	public void testIsDirty_1() {

		assertEquals("isDirty", true, massSpectrum.isDirty());
	}
}
