/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.jcampdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.converter.ChromatogramImportConverter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class Chromatogram_02_ITest {

	private IChromatogramMSD chromatogram;

	@BeforeAll
	public void setUp() {

		File file = new File("testData/Chromatogram_02.jdx");
		ChromatogramImportConverter chromatogramImportConverter = new ChromatogramImportConverter();
		IProcessingInfo<IChromatogramMSD> processingInfo = chromatogramImportConverter.convert(file, new NullProgressMonitor());
		chromatogram = processingInfo.getProcessingResult();
	}

	@Test
	public void testReader_0() {

		assertNotNull(chromatogram);
	}

	@Test
	public void testReader_1() {

		assertEquals(6, chromatogram.getNumberOfScans());
	}

	@Test
	public void testReader_2() {

		assertEquals("Chromatogram_02", chromatogram.getName());
	}

	@Test
	public void testReader_3() {

		assertEquals(135, chromatogram.getNumberOfScanIons());
	}

	@Test
	public void testReader_4() {

		assertEquals(245580, chromatogram.getStartRetentionTime());
	}

	@Test
	public void testReader_5() {

		assertEquals(247344, chromatogram.getStopRetentionTime());
	}

	@Test
	public void testReader_6() {

		assertEquals(38.0d, chromatogram.getStartIon());
	}

	@Test
	public void testReader_7() {

		assertEquals(355.0d, chromatogram.getStopIon());
	}

	@Test
	public void testReader_8() {

		assertEquals(12627.0f, chromatogram.getMaxSignal());
	}

	@Test
	public void testReader_9() {

		assertEquals(11186.0f, chromatogram.getMinSignal());
	}

	@Test
	public void testReader_10() {

		assertEquals(245580, chromatogram.getScanDelay());
	}

	@Test
	public void testReader_11() {

		assertEquals(350, chromatogram.getScanInterval());
	}

	@Test
	public void testChromatogramReader_12() {

		IScanMSD massSpectrum = chromatogram.getScan(1);
		assertEquals(22, massSpectrum.getNumberOfIons());
		assertEquals(39.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(332.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(355.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(205.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(44.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(1653.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(245580, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
	}

	@Test
	public void testChromatogramReader_13() {

		IScanMSD massSpectrum = chromatogram.getScan(3);
		assertEquals(19, massSpectrum.getNumberOfIons());
		assertEquals(39.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(454.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(221.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(343.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(44.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(2049.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(246288, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
	}

	@Test
	public void testChromatogramReader_14() {

		IScanMSD massSpectrum = chromatogram.getScan(4);
		assertEquals(22, massSpectrum.getNumberOfIons());
		assertEquals(38.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(203.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(281.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(288.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(44.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(1749.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(246636, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
	}

	@Test
	public void testChromatogramReader_15() {

		IScanMSD massSpectrum = chromatogram.getScan(6);
		assertEquals(23, massSpectrum.getNumberOfIons());
		assertEquals(39.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(394.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(281.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(223.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(44.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(1727.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(247344, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
	}

	@Test
	public void testReader_16() {

		assertEquals(0, chromatogram.getPeaks().size());
	}

	@Test
	public void testReader_17() {

		assertEquals(0.0d, chromatogram.getPeakIntegratedArea());
	}

	@Test
	public void testReader_18() {

		assertEquals(0.0d, chromatogram.getChromatogramIntegratedArea());
	}

	@Test
	public void testReader_19() {

		assertEquals(0.0d, chromatogram.getBackgroundIntegratedArea());
	}

	@Test
	public void testReader_20() {

		assertEquals(0.0d, chromatogram.getSampleWeight());
	}

	@Test
	public void testReader_21() {

		IIonTransitionSettings ionTransitionSettings = chromatogram.getIonTransitionSettings();
		assertEquals(0, ionTransitionSettings.size());
	}
}