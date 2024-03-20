/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonBounds;

import junit.framework.TestCase;

/**
 * Tests the method massSpectrum.normalize(0); It is not allowed. + ion
 * = new DefaultIon(45.5f, 78500.2f); +
 * massSpectrum.addIon(ion); + ion = new
 * DefaultIon(85.4f, 3000.5f); +
 * massSpectrum.addIon(ion); + ion = new
 * DefaultIon(104.1f, 120000.4f); +
 * massSpectrum.addIon(ion); + ion = new
 * DefaultIon(32.6f, 890520.4f); +
 * massSpectrum.addIon(ion); + ion = new
 * DefaultIon(105.7f, 120000.4f); +
 * massSpectrum.addIon(ion); + ion = new
 * DefaultIon(28.2f, 33000.5f); +
 * massSpectrum.addIon(ion); - ion = new
 * DefaultIon(85.4f, 3000.5f); -
 * massSpectrum.addIon(ion);
 * 
 * @author eselmeister
 */
public class MassSpectrum_16_Test extends TestCase {

	private ScanMSD massSpectrum;
	private Ion ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new ScanMSD();
		ion = new Ion(45.5f, 78500.2f);
		massSpectrum.addIon(ion);
		ion = new Ion(85.4f, 3000.5f);
		massSpectrum.addIon(ion);
		ion = new Ion(104.1f, 120000.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(32.6f, 890520.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(105.7f, 120000.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(28.2f, 33000.5f);
		massSpectrum.addIon(ion);
		ion = new Ion(85.4f, 3000.5f);
		massSpectrum.removeIon(ion);
		massSpectrum.normalize(0.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		ion = null;
		super.tearDown();
	}

	public void testGetNormalizationBase_1() {

		assertEquals("normalizationBase", 0.0f, massSpectrum.getNormalizationBase());
	}

	public void testIsNormalized_1() {

		assertFalse("isNormalized", massSpectrum.isNormalized());
	}

	public void testGetIons_1() {

		assertEquals("getIons", 5, massSpectrum.getIons().size());
	}

	public void testGetTotalSignal_1() {

		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
	}

	public void testGetExtractedIonSignal_1() {

		assertEquals("getExtractedIonSignal", 0.0f, massSpectrum.getExtractedIonSignal().getAbundance(0));
	}

	public void testGetExtractedIonSignal_2() {

		assertEquals("getExtractedIonSignal", 120000.4f, massSpectrum.getExtractedIonSignal(25, 120).getAbundance(104));
	}

	public void testGetExtractedIonSignal_3() {

		assertEquals("getExtractedIonSignal", 890520.4f, massSpectrum.getExtractedIonSignal(25, 120).getAbundance(33));
	}

	public void testGetExtractedIonSignal_4() {

		assertEquals("getExtractedIonSignal", 120000.4f, massSpectrum.getExtractedIonSignal(25, 120).getAbundance(106));
	}

	public void testGetBasePeak_1() {

		assertEquals("getBasePeak", 32.599998474121094d, massSpectrum.getBasePeak());
	}

	public void testGetBasePeakAbundance_1() {

		assertEquals("getBasePeakAbundance", 890520.4f, massSpectrum.getBasePeakAbundance());
	}

	public void testGetHighestAbundance_1() {

		assertEquals("getHighestAbundance", 890520.4f, massSpectrum.getHighestAbundance().getAbundance());
	}

	public void testGetHighestAbundance_2() {

		assertEquals("getHighestAbundance", 32.599998474121094d, massSpectrum.getHighestAbundance().getIon());
	}

	public void testGetHighestIon_1() {

		assertEquals("getHighestIon", 120000.4f, massSpectrum.getHighestIon().getAbundance());
	}

	public void testGetHighestIon_2() {

		assertEquals("getHighestIon", 105.69999694824219d, massSpectrum.getHighestIon().getIon());
	}

	public void testGetLowestAbundance_1() {

		assertEquals("getLowestAbundance", 33000.5f, massSpectrum.getLowestAbundance().getAbundance());
	}

	public void testGetLowestAbundance_2() {

		assertEquals("getLowestAbundance", 28.200000762939453d, massSpectrum.getLowestAbundance().getIon());
	}

	public void testGetLowestIon_1() {

		assertEquals("getLowestIon", 33000.5f, massSpectrum.getLowestIon().getAbundance());
	}

	public void testGetLowestIon_2() {

		assertEquals("getLowestIon", 28.200000762939453d, massSpectrum.getLowestIon().getIon());
	}

	public void testGetIonBounds_1() {

		IIonBounds bounds = massSpectrum.getIonBounds();
		assertEquals("getLowestIon().getAbundance()", 33000.5f, bounds.getLowestIon().getAbundance());
		assertEquals("getLowestIon().getIon()", 28.200000762939453d, bounds.getLowestIon().getIon());
		assertEquals("getHighestIon().getAbundance()", 120000.4f, bounds.getHighestIon().getAbundance());
		assertEquals("getHighestIon().getIon()", 105.69999694824219d, bounds.getHighestIon().getIon());
	}

	public void testGetNumberOfIons_1() {

		assertEquals("getNumberOfIons", 5, massSpectrum.getNumberOfIons());
	}

	public void testGetIon_1() {

		IIon ion = massSpectrum.getIon(5);
		assertEquals("getIon", null, ion);
		ion = massSpectrum.getIon(46);
		assertTrue("getIon", ion != null);
		assertEquals("getIon(46) abundance", 78500.2f, ion.getAbundance());
		assertEquals("getIon(46) ion", 46.0d, ion.getIon());
	}

	public void testIsDirty_1() {

		assertEquals("isDirty", true, massSpectrum.isDirty());
	}
}
