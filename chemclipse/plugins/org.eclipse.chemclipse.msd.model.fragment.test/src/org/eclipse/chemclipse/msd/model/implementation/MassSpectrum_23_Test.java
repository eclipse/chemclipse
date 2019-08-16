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
 * Tests adjustTotalSignal(float totalSignal).
 * 
 * @author eselmeister
 */
public class MassSpectrum_23_Test extends TestCase {

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

		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_2() {

		/*
		 * There could be small calculation differences in the given and the
		 * result total signal.
		 */
		massSpectrum.adjustTotalSignal(1000000.0f);
		assertEquals("getTotalSignal", 1000000.0f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_3() {

		/*
		 * There could be small calculation differences in the given and the
		 * result total signal.
		 */
		massSpectrum.adjustTotalSignal(0.0f);
		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_4() {

		/*
		 * There could be small calculation differences in the given and the
		 * result total signal.
		 */
		massSpectrum.adjustTotalSignal(Float.NaN);
		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_5() {

		/*
		 * There could be small calculation differences in the given and the
		 * result total signal.
		 */
		massSpectrum.adjustTotalSignal(Float.POSITIVE_INFINITY);
		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_6() {

		/*
		 * There could be small calculation differences in the given and the
		 * result total signal.
		 */
		massSpectrum.adjustTotalSignal(10.0f);
		assertEquals("getTotalSignal", 9.999999f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_7() {

		/*
		 * There could be small calculation differences in the given and the
		 * result total signal.
		 */
		massSpectrum.adjustTotalSignal(5000000.0f);
		assertEquals("getTotalSignal", 5000000.0f, massSpectrum.getTotalSignal());
	}

	public void testGetTotalSignal_8() {

		/*
		 * There could be small calculation differences in the given and the
		 * result total signal.
		 */
		massSpectrum.adjustTotalSignal(-1.0f);
		assertEquals("getTotalSignal", 1242021.9f, massSpectrum.getTotalSignal());
	}
}
