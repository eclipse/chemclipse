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
 * + ion = new DefaultIon(45.5f, 78500.2f); + ion =
 * new DefaultIon(85.4f, 3000.5f); + ion = new
 * DefaultIon(104.1f, 120000.4f);
 * 
 * @author eselmeister
 */
public class MassSpectrum_5_Test extends TestCase {

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
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		ion = null;
		super.tearDown();
	}

	public void testGetIons_1() {

		assertEquals("getIons", 3, massSpectrum.getIons().size());
	}

	public void getTotalSignal_1() {

		assertEquals("getTotalSignal", 201601.1f, massSpectrum.getTotalSignal());
	}

	public void getExtractedIonSignal_1() {

		assertEquals("getExtractedIonSignal", 0, massSpectrum.getExtractedIonSignal().getAbundance(0));
	}

	public void getExtractedIonSignal_2() {

		assertEquals("getExtractedIonSignal", 120000.4f, massSpectrum.getExtractedIonSignal(40, 120).getAbundance(104));
	}

	public void getExtractedIonSignal_3() {

		assertEquals("getExtractedIonSignal", 3000.5f, massSpectrum.getExtractedIonSignal(40, 120).getAbundance(85));
	}

	public void getExtractedIonSignal_4() {

		assertEquals("getExtractedIonSignal", 78500.2f, massSpectrum.getExtractedIonSignal(40, 120).getAbundance(46));
	}

	public void testGetBasePeak_1() {

		assertEquals("getBasePeak", 104.0999984741211d, massSpectrum.getBasePeak());
	}

	public void testGetBasePeakAbundance_1() {

		assertEquals("getBasePeakAbundance", 120000.4f, massSpectrum.getBasePeakAbundance());
	}

	public void testGetHighestAbundance_1() {

		assertEquals("getHighestAbundance", 120000.4f, massSpectrum.getHighestAbundance().getAbundance());
	}

	public void testGetHighestAbundance_2() {

		assertEquals("getHighestAbundance", 104.0999984741211d, massSpectrum.getHighestAbundance().getIon());
	}

	public void testGetHighestIon_1() {

		assertEquals("getHighestIon", 120000.4f, massSpectrum.getHighestIon().getAbundance());
	}

	public void testGetHighestIon_2() {

		assertEquals("getHighestIon", 104.0999984741211d, massSpectrum.getHighestIon().getIon());
	}

	public void testGetLowestAbundance_1() {

		assertEquals("getLowestAbundance", 3000.5f, massSpectrum.getLowestAbundance().getAbundance());
	}

	public void testGetLowestAbundance_2() {

		assertEquals("getLowestAbundance", 85.4000015258789d, massSpectrum.getLowestAbundance().getIon());
	}

	public void testGetLowestIon_1() {

		assertEquals("getLowestIon", 78500.2f, massSpectrum.getLowestIon().getAbundance());
	}

	public void testGetLowestIon_2() {

		assertEquals("getLowestIon", 45.5d, massSpectrum.getLowestIon().getIon());
	}

	public void testGetIonBounds_1() {

		IIonBounds bounds = massSpectrum.getIonBounds();
		assertEquals("getLowestIon().getAbundance()", 78500.2f, bounds.getLowestIon().getAbundance());
		assertEquals("getLowestIon().getIon()", 45.5d, bounds.getLowestIon().getIon());
		assertEquals("getHighestIon().getAbundance()", 120000.4f, bounds.getHighestIon().getAbundance());
		assertEquals("getHighestIon().getIon()", 104.0999984741211d, bounds.getHighestIon().getIon());
	}

	public void testGetNumberOfIons_1() {

		assertEquals("getNumberOfIons", 3, massSpectrum.getNumberOfIons());
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
