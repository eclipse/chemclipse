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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
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
public class ChromatogramSelectionsTestCase extends TestCase {

	private IChromatogramMSD chromatogram;
	private List<IChromatogramSelection<?, ?>> chromatogramSelections;
	private IChromatogramSelectionMSD chromatogramSelection;
	private IVendorMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogramSelections = new ArrayList<>();
		// ------------------------------------chromatogram1
		chromatogram = new ChromatogramMSD();
		chromatogram.setScanDelay(5000);
		chromatogram.setScanInterval(1000);
		// Scan RT - Abundance
		// 5000 - 15000
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(45, 4000));
		massSpectrum.addIon(new Ion(48, 5000));
		massSpectrum.addIon(new Ion(53, 6000));
		chromatogram.addScan(massSpectrum);
		// 6000 - 66200
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(18, 5600));
		massSpectrum.addIon(new Ion(78, 50700));
		massSpectrum.addIon(new Ion(53, 6500));
		massSpectrum.addIon(new Ion(90, 3400));
		chromatogram.addScan(massSpectrum);
		// 7000 - 709850
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(28, 54000));
		massSpectrum.addIon(new Ion(53, 600450));
		massSpectrum.addIon(new Ion(104, 55400));
		chromatogram.addScan(massSpectrum);
		// 8000 - 812450
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(104, 5000));
		massSpectrum.addIon(new Ion(60, 23000));
		massSpectrum.addIon(new Ion(53, 230450));
		massSpectrum.addIon(new Ion(105, 554000));
		chromatogram.addScan(massSpectrum);
		chromatogram.recalculateRetentionTimes();
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
		chromatogramSelections.add(chromatogramSelection);
		// ------------------------------------chromatogram1
		// ------------------------------------chromatogram2
		chromatogram = new ChromatogramMSD();
		chromatogram.setScanDelay(4000);
		chromatogram.setScanInterval(1500);
		// Scan RT - Abundance
		// 4000 - 28000
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(45, 4000));
		massSpectrum.addIon(new Ion(48, 5000));
		massSpectrum.addIon(new Ion(94, 12000));
		massSpectrum.addIon(new Ion(70, 7000));
		chromatogram.addScan(massSpectrum);
		// 5500 - 16200
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(18, 5600));
		massSpectrum.addIon(new Ion(21, 700));
		massSpectrum.addIon(new Ion(53, 6500));
		massSpectrum.addIon(new Ion(90, 3400));
		chromatogram.addScan(massSpectrum);
		// 7000 - 1272450
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(28, 54000));
		massSpectrum.addIon(new Ion(48, 553000));
		massSpectrum.addIon(new Ion(46, 607450));
		massSpectrum.addIon(new Ion(99, 58000));
		chromatogram.addScan(massSpectrum);
		// 8500 - 630500
		massSpectrum = new VendorMassSpectrum();
		massSpectrum.addIon(new Ion(104, 5000));
		massSpectrum.addIon(new Ion(60, 230));
		massSpectrum.addIon(new Ion(53, 7850));
		massSpectrum.addIon(new Ion(89, 452100));
		massSpectrum.addIon(new Ion(46, 78900));
		massSpectrum.addIon(new Ion(80, 1250));
		massSpectrum.addIon(new Ion(47, 85170));
		chromatogram.addScan(massSpectrum);
		chromatogram.recalculateRetentionTimes();
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
		chromatogramSelections.add(chromatogramSelection);
		// ------------------------------------chromatogram2
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		chromatogramSelections = null;
		super.tearDown();
	}

	public IChromatogramMSD getChromatogram() {

		return chromatogram;
	}

	public List<IChromatogramSelection<?, ?>> getChromatogramSelections() {

		return chromatogramSelections;
	}
}
