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

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class Chromatogram_20_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private VendorMassSpectrum supplierMassSpectrum;
	private IIon ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		// ------------------------------Scan 1
		supplierMassSpectrum = new VendorMassSpectrum();
		supplierMassSpectrum.setRetentionTime(7500);
		ion = new Ion(45.4f, 65883.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new Ion(104.3f, 102453.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new Ion(86.2f, 302410.3f);
		supplierMassSpectrum.addIon(ion);
		chromatogram.addScan(supplierMassSpectrum);
		// ------------------------------Scan 1
		// ------------------------------Scan 2
		supplierMassSpectrum = new VendorMassSpectrum();
		supplierMassSpectrum.setRetentionTime(10500);
		ion = new Ion(18.1f, 883.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new Ion(146.3f, 2453.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new Ion(48.2f, 3021.3f);
		supplierMassSpectrum.addIon(ion);
		chromatogram.addScan(supplierMassSpectrum);
		// ------------------------------Scan 2
		// ------------------------------Scan 3
		supplierMassSpectrum = new VendorMassSpectrum();
		supplierMassSpectrum.setRetentionTime(13500);
		ion = new Ion(22.1f, 6883.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new Ion(80.3f, 1023.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new Ion(190.2f, 2410.3f);
		supplierMassSpectrum.addIon(ion);
		chromatogram.addScan(supplierMassSpectrum);
		// ------------------------------Scan 3
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testGetStartIon_1() {

		double startIon = chromatogram.getStartIon();
		assertEquals("startIon", 18.100000381469727d, startIon);
	}

	public void testGetStopIon_1() {

		double stopIon = chromatogram.getStopIon();
		assertEquals("stopIon", 190.1999969482422d, stopIon);
	}
}
