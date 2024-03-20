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

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class XICChromatogramSelectionTestCase extends TestCase {

	private IChromatogramMSD chromatogram;
	private IChromatogramSelectionMSD chromatogramSelection;
	private IVendorMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		chromatogram.setScanDelay(5000);
		chromatogram.setScanInterval(1000);
		// Scan RT - Abundance
		// 5000 - 22050
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(45, 4000));
		massSpectrum.addIon(new Ion(48, 5000));
		massSpectrum.addIon(new Ion(53, 6000));
		massSpectrum.addIon(new Ion(70, 7000));
		massSpectrum.addIon(new Ion(104, 50));
		chromatogram.addScan(massSpectrum);
		// 6000 - 66239
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(18, 5600));
		massSpectrum.addIon(new Ion(78, 50700));
		massSpectrum.addIon(new Ion(53, 6500));
		massSpectrum.addIon(new Ion(104, 39));
		massSpectrum.addIon(new Ion(90, 3400));
		chromatogram.addScan(massSpectrum);
		// 7000 - 1761450
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(28, 54000));
		massSpectrum.addIon(new Ion(48, 553000));
		massSpectrum.addIon(new Ion(53, 600450));
		massSpectrum.addIon(new Ion(104, 554000));
		chromatogram.addScan(massSpectrum);
		// 8000 - 812450
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(104, 5000));
		massSpectrum.addIon(new Ion(60, 23000));
		massSpectrum.addIon(new Ion(53, 230450));
		massSpectrum.addIon(new Ion(105, 554000));
		chromatogram.addScan(massSpectrum);
		chromatogram.recalculateRetentionTimes();
		// Baseline RT - Abundance
		// 5000 - 0
		// 6000 - 260
		// 7000 - 390
		// 8000 - 500
		chromatogram.getBaselineModel().addBaseline(6000, 7000, 260, 390, true);
		chromatogram.getBaselineModel().addBaseline(7001, 8000, 390, 500, true);
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		chromatogramSelection = null;
		super.tearDown();
	}

	public IChromatogramMSD getChromatogram() {

		return chromatogram;
	}

	public IChromatogramSelectionMSD getChromatogramSelection() {

		return chromatogramSelection;
	}
}
