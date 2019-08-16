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
package org.eclipse.chemclipse.msd.model.core.selection;

import org.easymock.EasyMock;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanIon;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import junit.framework.TestCase;

public class ChromatogramSelection_8_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IChromatogramSelectionMSD selection;
	private IVendorMassSpectrum scan;
	private IChromatogramPeakMSD peak;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Use createNiceMock if you use void methods that are not important to
		 * test.
		 */
		chromatogram = EasyMock.createNiceMock(IChromatogramMSD.class);
		EasyMock.expect(chromatogram.getStartRetentionTime()).andStubReturn(1);
		EasyMock.expect(chromatogram.getStopRetentionTime()).andStubReturn(100);
		EasyMock.expect(chromatogram.getMaxSignal()).andStubReturn(127500.0f);
		scan = new VendorMassSpectrum();
		scan.setRetentionTime(4500);
		scan.addIon(new ScanIon(45.0f, 2883.9f));
		peak = EasyMock.createNiceMock(IChromatogramPeakMSD.class);
		EasyMock.expect(peak.getIntegratedArea()).andStubReturn(893002.3d);
		EasyMock.replay(peak);
		EasyMock.replay(chromatogram);
		/*
		 * Default values from IChromatogram will be chosen.
		 */
		selection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		selection = null;
		peak = null;
		scan = null;
		super.tearDown();
	}

	public void testSetSelectedScan_1() {

		selection.setSelectedScan(null);
		assertNull(selection.getSelectedScan());
	}

	public void testSetSelectedScan_2() {

		selection.setSelectedScan(scan);
		scan = selection.getSelectedScan();
		assertNotNull(selection.getSelectedScan());
		assertEquals("RetentionTime", 4500, scan.getRetentionTime());
	}

	public void testSetSelectedPeak_1() {

		selection.setSelectedPeak(null);
		assertNull(selection.getSelectedPeak());
	}

	public void testSetSelectedPeak_2() {

		selection.setSelectedPeak(peak);
		peak = selection.getSelectedPeak();
		assertNotNull(peak);
		assertEquals("IntegratedArea", 893002.3d, peak.getIntegratedArea());
	}
}
