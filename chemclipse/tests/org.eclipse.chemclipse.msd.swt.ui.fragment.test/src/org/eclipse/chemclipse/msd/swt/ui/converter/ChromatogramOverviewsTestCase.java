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
package org.eclipse.chemclipse.msd.swt.ui.converter;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class ChromatogramOverviewsTestCase extends TestCase {

	private IChromatogramMSD chromatogram1;
	private IChromatogramMSD chromatogram2;
	private IVendorMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		// ---------------------------------chromatogram1
		chromatogram1 = new ChromatogramMSD();
		chromatogram1.setScanDelay(5000);
		chromatogram1.setScanInterval(1000);
		// Scan RT - Abundance
		// 5000 - 22000
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(IIon.TIC_ION, 22000));
		chromatogram1.addScan(massSpectrum);
		// 6000 - 66200
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(IIon.TIC_ION, 66200));
		chromatogram1.addScan(massSpectrum);
		// 7000 - 1712850
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(IIon.TIC_ION, 1712850));
		chromatogram1.addScan(massSpectrum);
		// 8000 - 812450
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(IIon.TIC_ION, 812450));
		chromatogram1.addScan(massSpectrum);
		chromatogram1.recalculateRetentionTimes();
		// ---------------------------------chromatogram1
		// ---------------------------------chromatogram2
		chromatogram2 = new ChromatogramMSD();
		chromatogram2.setScanDelay(3000);
		chromatogram2.setScanInterval(1500);
		// Scan RT - Abundance
		// 3000 - 2500
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(IIon.TIC_ION, 2500));
		chromatogram2.addScan(massSpectrum);
		// 4500 - 55200
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(IIon.TIC_ION, 55200));
		chromatogram2.addScan(massSpectrum);
		// 6000 - 869542
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(IIon.TIC_ION, 869542));
		chromatogram2.addScan(massSpectrum);
		// 7500 - 23654
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(IIon.TIC_ION, 23654));
		chromatogram2.addScan(massSpectrum);
		chromatogram2.recalculateRetentionTimes();
		// ---------------------------------chromatogram2
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram1 = null;
		chromatogram2 = null;
		super.tearDown();
	}

	public IChromatogramOverview getChromatogramOverview1() {

		return chromatogram1;
	}

	public IChromatogramOverview getChromatogramOverview2() {

		return chromatogram2;
	}
}
