/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.io;

import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.TestPathHelper;
import org.eclipse.core.runtime.NullProgressMonitor;

public class ChromatogramReader_1_MSD_1500_ITest extends ChromatogramReaderMSDTestCase {

	@Override
	protected void setUp() throws Exception {

		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1500);
		super.setUp();
	}

	public void testReader_1() {

		assertEquals(110, chromatogram.getNumberOfScans());
	}

	public void testReader_2() {

		assertEquals("Chromatogram1-1500", chromatogram.getName());
	}

	public void testReader_3() {

		assertEquals(3927, chromatogram.getNumberOfScanIons());
	}

	public void testReader_4() {

		assertEquals(841111, chromatogram.getStartRetentionTime());
	}

	public void testReader_5() {

		assertEquals(918652, chromatogram.getStopRetentionTime());
	}

	public void testReader_6() {

		assertEquals(15.1d, chromatogram.getStartIon());
	}

	public void testReader_7() {

		assertEquals(281.3d, chromatogram.getStopIon());
	}

	public void testReader_8() {

		assertEquals(442733.0f, chromatogram.getMaxSignal());
	}

	public void testReader_8a() {

		chromatogram.enforceLoadScanProxies(new NullProgressMonitor());
		assertEquals(442733.0f, chromatogram.getMaxSignal());
	}

	public void testReader_9() {

		assertEquals(21543.0f, chromatogram.getMinSignal());
	}

	public void testReader_9a() {

		chromatogram.enforceLoadScanProxies(new NullProgressMonitor());
		assertEquals(21543.0f, chromatogram.getMinSignal());
	}

	public void testReader_10() {

		assertEquals(841111, chromatogram.getScanDelay());
	}

	public void testReader_11() {

		assertEquals(8351, chromatogram.getScanInterval());
	}

	public void testChromatogramReader_12() {

		IScanMSD massSpectrum = chromatogram.getSupplierScan(1);
		/*
		 * Proxy
		 */
		assertEquals(32, massSpectrum.getNumberOfIons());
		assertEquals(25538.0f, massSpectrum.getTotalSignal());
		/*
		 * Import complete
		 */
		massSpectrum.enforceLoadScanProxy();
		assertEquals(32, massSpectrum.getNumberOfIons());
		assertEquals(15.1d, massSpectrum.getLowestIon().getIon());
		assertEquals(141.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(206.9d, massSpectrum.getHighestIon().getIon());
		assertEquals(131.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(28.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(7851.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(841111, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(1, massSpectrum.getTimeSegmentId());
		assertEquals(1, massSpectrum.getCycleNumber());
		assertEquals(25538.0f, massSpectrum.getTotalSignal());
	}

	public void testChromatogramReader_13() {

		IScanMSD massSpectrum = chromatogram.getSupplierScan(22);
		/*
		 * Proxy
		 */
		assertEquals(35, massSpectrum.getNumberOfIons());
		assertEquals(32342.0f, massSpectrum.getTotalSignal());
		/*
		 * Import complete
		 */
		massSpectrum.enforceLoadScanProxy();
		assertEquals(35, massSpectrum.getNumberOfIons());
		assertEquals(15.1d, massSpectrum.getLowestIon().getIon());
		assertEquals(107.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(206.9d, massSpectrum.getHighestIon().getIon());
		assertEquals(148.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(28.1d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(7952.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(856050, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(1, massSpectrum.getTimeSegmentId());
		assertEquals(1, massSpectrum.getCycleNumber());
		assertEquals(32342.0f, massSpectrum.getTotalSignal());
	}

	public void testChromatogramReader_14() {

		IScanMSD massSpectrum = chromatogram.getSupplierScan(80);
		/*
		 * Proxy
		 */
		assertEquals(29, massSpectrum.getNumberOfIons());
		assertEquals(21543.0f, massSpectrum.getTotalSignal());
		/*
		 * Import complete
		 */
		massSpectrum.enforceLoadScanProxy();
		assertEquals(29, massSpectrum.getNumberOfIons());
		assertEquals(16.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(223.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(281.1d, massSpectrum.getHighestIon().getIon());
		assertEquals(100.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(28.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(7228.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(897310, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(1, massSpectrum.getTimeSegmentId());
		assertEquals(1, massSpectrum.getCycleNumber());
		assertEquals(21543.0f, massSpectrum.getTotalSignal());
	}

	public void testChromatogramReader_15() {

		IScanMSD massSpectrum = chromatogram.getSupplierScan(110);
		/*
		 * Proxy
		 */
		assertEquals(31, massSpectrum.getNumberOfIons());
		assertEquals(22801.0f, massSpectrum.getTotalSignal());
		/*
		 * Import complete
		 */
		massSpectrum.enforceLoadScanProxy();
		assertEquals(31, massSpectrum.getNumberOfIons());
		assertEquals(15.1d, massSpectrum.getLowestIon().getIon());
		assertEquals(83.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(206.7d, massSpectrum.getHighestIon().getIon());
		assertEquals(67.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(28.1d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(7759.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(918652, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(1, massSpectrum.getTimeSegmentId());
		assertEquals(1, massSpectrum.getCycleNumber());
		assertEquals(22801.0f, massSpectrum.getTotalSignal());
	}

	public void testReader_16() {

		assertEquals(4, chromatogram.getNumberOfPeaks());
	}

	public void testReader_17() {

		assertEquals(1.7471400074129336E7d, chromatogram.getPeakIntegratedArea());
	}

	public void testReader_18() {

		assertEquals(3.7163158155000016E7d, chromatogram.getChromatogramIntegratedArea());
	}

	public void testReader_19() {

		assertEquals(1.7316770049970705E7d, chromatogram.getBackgroundIntegratedArea());
	}

	public void testReader_20() {

		assertEquals(0.0d, chromatogram.getSampleWeight());
	}

	public void testReader_21() {

		IIonTransitionSettings ionTransitionSettings = chromatogram.getIonTransitionSettings();
		assertEquals(0, ionTransitionSettings.size());
	}

	public void testReader_22() {

		IVendorMassSpectrum massSpectrum = chromatogram.getSupplierScan(37);
		assertEquals(16, massSpectrum.getTargets().size());
	}

	public void testReader_23() {

		IVendorMassSpectrum massSpectrum = chromatogram.getSupplierScan(44);
		assertEquals(16, massSpectrum.getTargets().size());
	}

	public void testChromatogramReader_24() {

		IScanMSD massSpectrum = chromatogram.getSupplierScan(87);
		massSpectrum.enforceLoadScanProxy();
		assertNotNull(massSpectrum.getOptimizedMassSpectrum());
		assertEquals(5, massSpectrum.getTargets().size());
	}
}