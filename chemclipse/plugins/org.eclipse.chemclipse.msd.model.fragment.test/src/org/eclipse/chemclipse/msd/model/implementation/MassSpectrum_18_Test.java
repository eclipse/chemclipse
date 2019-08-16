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

import junit.framework.TestCase;

/**
 * + ion = new DefaultIon(45.5f, 78500.2f); +
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
 * massSpectrum.addIon(ion); + ion = new
 * DefaultIon(85.4f, 3000.5f); +
 * massSpectrum.addIon(ion);
 * 
 * @author eselmeister
 */
public class MassSpectrum_18_Test extends TestCase {

	private ScanMSD massSpectrum;
	private Ion ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new ScanMSD();
		ion = new Ion(45.5f, 78500.2f);
		massSpectrum.addIon(ion);
		ion = new Ion(104.1f, 120000.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(32.6f, 890520.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(105.7f, 120000.4f);
		massSpectrum.addIon(ion);
		ion = new Ion(28.2f, 33000.5f);
		massSpectrum.addIon(ion);
		massSpectrum.adjustIons(0.3f);
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		ion = null;
		super.tearDown();
	}

	public void testGetIons_1() {

		assertEquals("getIons", 5, massSpectrum.getIons().size());
	}

	public void testGetTotalSignal_1() {

		assertEquals("getTotalSignal", 1614628.4f, massSpectrum.getTotalSignal());
	}

	public void testGetExtractedIonSignal_1() {

		assertEquals("getExtractedIonSignal", 0.0f, massSpectrum.getExtractedIonSignal().getAbundance(0));
	}

	public void testGetExtractedIonSignal_2() {

		assertEquals("getExtractedIonSignal", 156000.52f, massSpectrum.getExtractedIonSignal(25, 120).getAbundance(104));
	}

	public void testGetExtractedIonSignal_3() {

		assertEquals("getExtractedIonSignal", 1157676.5f, massSpectrum.getExtractedIonSignal(25, 120).getAbundance(33));
	}

	public void testGetExtractedIonSignal_4() {

		assertEquals("getExtractedIonSignal", 156000.52f, massSpectrum.getExtractedIonSignal(25, 120).getAbundance(106));
	}
}
