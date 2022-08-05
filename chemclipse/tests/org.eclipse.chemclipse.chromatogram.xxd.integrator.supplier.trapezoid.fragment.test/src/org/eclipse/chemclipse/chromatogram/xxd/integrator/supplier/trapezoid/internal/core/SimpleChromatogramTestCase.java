/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.internal.core;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanIon;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class SimpleChromatogramTestCase extends TestCase {

	protected IChromatogramMSD chromatogram;
	protected IVendorMassSpectrum supplierMassSpectrum;
	protected IScanIon ion;
	protected IChromatogramSelectionMSD chromatogramSelection;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		// ------------------------------Chromatogram
		chromatogram = new ChromatogramMSD();
		chromatogram.setScanDelay(4500);
		chromatogram.setScanInterval(1000);
		supplierMassSpectrum = new VendorMassSpectrum();
		ion = new ScanIon(43, 1000.0f);
		supplierMassSpectrum.addIon(ion);
		chromatogram.addScan(supplierMassSpectrum);
		supplierMassSpectrum = new VendorMassSpectrum();
		ion = new ScanIon(43, 2000.0f);
		supplierMassSpectrum.addIon(ion);
		chromatogram.addScan(supplierMassSpectrum);
		supplierMassSpectrum = new VendorMassSpectrum();
		ion = new ScanIon(43, 1000.0f);
		supplierMassSpectrum.addIon(ion);
		chromatogram.addScan(supplierMassSpectrum);
		chromatogram.recalculateRetentionTimes();
		// ------------------------------Chromatogram
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		ion = null;
		supplierMassSpectrum = null;
		chromatogramSelection = null;
		super.tearDown();
	}
}
