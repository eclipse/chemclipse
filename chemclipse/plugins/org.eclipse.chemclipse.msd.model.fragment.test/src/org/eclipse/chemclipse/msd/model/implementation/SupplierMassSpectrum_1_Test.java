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

import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import junit.framework.TestCase;

public class SupplierMassSpectrum_1_Test extends TestCase {

	private IVendorMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new VendorMassSpectrum();
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		super.tearDown();
	}

	public void testRetentionTime_1() {

		assertEquals("Retention Time", 0, massSpectrum.getRetentionTime());
	}

	public void testRetentionTime_2() {

		massSpectrum.setRetentionTime(5000);
		assertEquals("Retention Time", 5000, massSpectrum.getRetentionTime());
	}

	public void testRetentionIndex_1() {

		assertEquals("Retention Index", 0.0f, massSpectrum.getRetentionIndex());
	}

	public void testRetentionIndex_2() {

		massSpectrum.setRetentionIndex(56.3f);
		assertEquals("Retention Index", 56.3f, massSpectrum.getRetentionIndex());
	}

	public void testRetentionIndex_3() {

		massSpectrum.setRetentionIndex(-1.0f);
		assertEquals("Retention Index", 0.0f, massSpectrum.getRetentionIndex());
	}

	public void testScanNumber_1() {

		assertEquals("Scan Number", 0, massSpectrum.getScanNumber());
	}

	public void testScanNumber_2() {

		massSpectrum.setScanNumber(78);
		assertEquals("Scan Number", 78, massSpectrum.getScanNumber());
	}
}
