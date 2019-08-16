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
 * Tests the methods getMinIonAbundance() and
 * getMaxIonAbundance().
 * 
 * @author eselmeister
 */
public class Chromatogram_10_Test extends TestCase {

	private ChromatogramMSD chromatogram;
	private VendorMassSpectrum supplierMassSpectrum;
	private ScanIon ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		// Scan 1
		supplierMassSpectrum = new VendorMassSpectrum();
		ion = new ScanIon(45.3f, 45827.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new ScanIon(55.8f, 457.1f);
		supplierMassSpectrum.addIon(ion);
		ion = new ScanIon(75.2f, 827.8f);
		supplierMassSpectrum.addIon(ion);
		chromatogram.addScan(supplierMassSpectrum);
		// Scan 2
		supplierMassSpectrum = new VendorMassSpectrum();
		ion = new ScanIon(45.3f, 827.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new ScanIon(55.8f, 45827.4f);
		supplierMassSpectrum.addIon(ion);
		ion = new ScanIon(75.2f, 427.4f);
		supplierMassSpectrum.addIon(ion);
		chromatogram.addScan(supplierMassSpectrum);
		// Scan 3
		supplierMassSpectrum = new VendorMassSpectrum();
		ion = new ScanIon(45.3f, 927.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new ScanIon(55.8f, 74627.2f);
		supplierMassSpectrum.addIon(ion);
		ion = new ScanIon(75.2f, 12477.3f);
		supplierMassSpectrum.addIon(ion);
		chromatogram.addScan(supplierMassSpectrum);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		supplierMassSpectrum = null;
		ion = null;
		super.tearDown();
	}

	public void testMinIonAbundance_1() {

		assertEquals("minIonAbundance", 427.4f, chromatogram.getMinIonAbundance());
	}

	public void testMaxIonAbundance_1() {

		assertEquals("maxIonAbundance", 74627.2f, chromatogram.getMaxIonAbundance());
	}
}
