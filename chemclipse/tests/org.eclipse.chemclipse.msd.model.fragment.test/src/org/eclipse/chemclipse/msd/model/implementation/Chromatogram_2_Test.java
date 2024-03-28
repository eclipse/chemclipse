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

import junit.framework.TestCase;

/**
 * Tests the methods equals and hashCode.
 * 
 * @author eselmeister
 */
public class Chromatogram_2_Test extends TestCase {

	private ChromatogramMSD chromatogram1;
	private ChromatogramMSD chromatogram2;
	private VendorMassSpectrum supplierMassSpectrum;
	private IIon ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram1 = new ChromatogramMSD();
		chromatogram2 = new ChromatogramMSD();
		supplierMassSpectrum = new VendorMassSpectrum();
		ion = new Ion(25.5f, 45862.3f);
		supplierMassSpectrum.addIon(ion);
		chromatogram2.addScan(supplierMassSpectrum);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram1 = null;
		chromatogram2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertFalse(chromatogram1.equals(chromatogram2));
	}

	public void testEquals_2() {

		assertFalse(chromatogram2.equals(chromatogram1));
	}

	public void testHashCode_1() {

		assertTrue(chromatogram1.hashCode() != chromatogram2.hashCode());
	}

	public void testHashCode_2() {

		assertTrue(chromatogram2.hashCode() != chromatogram1.hashCode());
	}
}
