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
package org.eclipse.chemclipse.msd.swt.ui.converter;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanIon;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class ChromatogramOverviewTestCase extends TestCase {

	private IChromatogramMSD chromatogram;
	private IVendorMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		chromatogram.setScanDelay(5000);
		chromatogram.setScanInterval(1000);
		// Scan RT - Abundance
		// 5000 - 22000
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new ScanIon(IIon.TIC_ION, 22000));
		chromatogram.addScan(massSpectrum);
		// 6000 - 66200
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new ScanIon(IIon.TIC_ION, 66200));
		chromatogram.addScan(massSpectrum);
		// 7000 - 1712850
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new ScanIon(IIon.TIC_ION, 1712850));
		chromatogram.addScan(massSpectrum);
		// 8000 - 812450
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new ScanIon(IIon.TIC_ION, 812450));
		chromatogram.addScan(massSpectrum);
		chromatogram.recalculateRetentionTimes();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public IChromatogramOverview getChromatogramOverview() {

		return chromatogram;
	}
}
