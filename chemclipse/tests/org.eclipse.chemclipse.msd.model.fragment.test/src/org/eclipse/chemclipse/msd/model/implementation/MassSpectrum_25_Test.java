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
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

import junit.framework.TestCase;

/**
 * Tests adjustTotalSignal(float totalSignal).
 * 
 * @author eselmeister
 */
public class MassSpectrum_25_Test extends TestCase {

	private ScanMSD massSpectrum1;
	private ScanMSD massSpectrum2;
	private IScanMSD mergedMassSpectrum;
	private IIon ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum1 = new ScanMSD();
		ion = new Ion(45.5f, 78500.2f);
		massSpectrum1.addIon(ion);
		ion = new Ion(104.1f, 120000.4f);
		massSpectrum1.addIon(ion);
		ion = new Ion(32.6f, 890520.4f);
		massSpectrum1.addIon(ion);
		ion = new Ion(105.7f, 120000.4f);
		massSpectrum1.addIon(ion);
		ion = new Ion(28.2f, 33000.5f);
		massSpectrum1.addIon(ion);
		massSpectrum2 = new ScanMSD();
		ion = new Ion(45.5f, 95678.2f);
		massSpectrum2.addIon(ion);
		ion = new Ion(110.1f, 120000.4f);
		massSpectrum2.addIon(ion);
		ion = new Ion(24.0f, 890520.4f);
		massSpectrum2.addIon(ion);
		ion = new Ion(105.7f, 90025.4f);
		massSpectrum2.addIon(ion);
		ion = new Ion(190.0f, 33000.5f);
		massSpectrum2.addIon(ion);
		mergedMassSpectrum = massSpectrum1.makeDeepCopy();
		mergedMassSpectrum.addIons(massSpectrum2.getIons(), false);
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum1 = null;
		massSpectrum2 = null;
		mergedMassSpectrum = null;
		ion = null;
		super.tearDown();
	}

	public void testGetTotalSignal_1() {

		assertEquals("getTotalSignal", 1242021.9f, massSpectrum1.getTotalSignal());
		assertEquals("getTotalSignal", 1229224.9f, massSpectrum2.getTotalSignal());
	}

	public void testGetTotalSignal_2() {

		assertEquals("getTotalSignal", 2302721.0f, mergedMassSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_3() {

		ion = mergedMassSpectrum.getIon(46);
		assertEquals("getTotalSignal ion 46", 95678.2f, ion.getAbundance());
	}

	public void testGetTotalSignal_4() {

		ion = mergedMassSpectrum.getIon(110);
		assertEquals("getTotalSignal ion 110", 120000.4f, ion.getAbundance());
	}

	public void testGetTotalSignal_5() {

		ion = mergedMassSpectrum.getIon(33);
		assertEquals("getTotalSignal ion 33", 890520.4f, ion.getAbundance());
	}
}
