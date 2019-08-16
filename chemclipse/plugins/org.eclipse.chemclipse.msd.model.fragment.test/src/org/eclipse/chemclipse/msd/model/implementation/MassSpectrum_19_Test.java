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
public class MassSpectrum_19_Test extends TestCase {

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
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		ion = null;
		super.tearDown();
	}

	public void testGetTotalSignal_1() {

		massSpectrum.adjustIons(-1.1f);
		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_2() {

		massSpectrum.adjustIons(1.1f);
		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_3() {

		massSpectrum.adjustIons(0.0f);
		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_4() {

		// adjust +20%
		massSpectrum.adjustIons(0.2f);
		assertEquals("getTotalSignal", 1490426.4f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_5() {

		// adjust -20%
		massSpectrum.adjustIons(-0.2f);
		assertEquals("getTotalSignal", 993617.5f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_6() {

		// adjust -100%
		massSpectrum.adjustIons(-1.0f);
		assertEquals("getTotalSignal", 0.0f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_7() {

		// adjust +100%
		massSpectrum.adjustIons(1.0f);
		assertEquals("getTotalSignal", 2484043.8f, massSpectrum.getTotalSignal());
	}
}
