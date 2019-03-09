/*******************************************************************************
 * Copyright (c) 2015, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.elu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.io.IPeakReader;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.PathResolver;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.ELUReader;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import junit.framework.TestCase;

public class ELUImportConverter_1_ITest extends TestCase {

	private ELUReader reader;
	private File file;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		reader = new ELUReader();
		String pathname = PathResolver.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PEAKS_1_ELU);
		file = new File(pathname);
	}

	@Override
	protected void tearDown() throws Exception {

		reader = null;
		super.tearDown();
	}

	public void testRead_1() {

		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		preferences.putBoolean(PreferenceSupplier.P_EXCLUDE_UNCERTAIN_IONS, false);
		try {
			IProcessingInfo<IPeaks> processingInfo = reader.read(file, new NullProgressMonitor());
			List<IPeak> peaks = processingInfo.getProcessingResult(IPeaks.class).getPeaks();
			IPeakMSD peak1 = (IPeakMSD)peaks.get(0);
			//
			assertEquals(14, peak1.getPeakModel().getTemporarilyInfo(IPeakReader.TEMP_INFO_START_SCAN));
			assertEquals(23, peak1.getPeakModel().getTemporarilyInfo(IPeakReader.TEMP_INFO_STOP_SCAN));
			assertEquals(17, peak1.getPeakModel().getTemporarilyInfo(IPeakReader.TEMP_INFO_MAX_SCAN));
			//
			IPeakMassSpectrum peakMassSpectrum1 = peak1.getPeakModel().getPeakMassSpectrum();
			List<IIon> ions1 = peakMassSpectrum1.getIons();
			IIon ion10 = ions1.get(0);
			assertEquals("Ion 0-0", 28.0, ion10.getIon());
			assertEquals("Abundance 0-0", 4308.653, ion10.getAbundance(), 1E-3);
			IIon ion11 = ions1.get(1);
			assertEquals("Ion 0-1", 29.0, ion11.getIon());
			assertEquals("Abundance 0-1", 73.32042, ion11.getAbundance(), 1E-5);
			IIon ion12 = ions1.get(2);
			assertEquals("Ion 0-2", 32.0, ion12.getIon());
			assertEquals("Abundance 0-2", 1168.8137, ion12.getAbundance(), 1E-4);
			IIon ion13 = ions1.get(3);
			assertEquals("Ion 0-3", 16.0, ion13.getIon());
			assertEquals("Abundance 0-3", 64.69449, ion13.getAbundance(), 1E-5);
			IIon ion14 = ions1.get(4);
			assertEquals("Ion 0-4", 18.0, ion14.getIon());
			assertEquals("Abundance 0-4", 120.76305, ion14.getAbundance(), 1E-5);
			IIon ion15 = ions1.get(5);
			assertEquals("Ion 0-5", 40.0, ion15.getIon());
			assertEquals("Abundance 0-5", 51.755592, ion15.getAbundance(), 1E-6);
			IPeakMSD peak2 = (IPeakMSD)peaks.get(1);
			IPeakMassSpectrum peakMassSpectrum2 = peak2.getPeakModel().getPeakMassSpectrum();
			List<IIon> ions2 = peakMassSpectrum2.getIons();
			IIon ion20 = ions2.get(0);
			assertEquals("Ion 1-0", 16.0, ion20.getIon());
			assertEquals("Abundance 1-0", 92493.41, ion20.getAbundance(), 1E-2);
			IIon ion21 = ions2.get(1);
			assertEquals("Ion 1-5", 28.0, ion21.getIon());
			assertEquals("Abundance 1-5", 7107762.5, ion21.getAbundance(), 1E-6);
			IIon ion22 = ions2.get(2);
			assertEquals("Ion 1-2", 29.0, ion22.getIon());
			assertEquals("Abundance 1-2", 56919.02, ion22.getAbundance(), 1E-2);
			IIon ion23 = ions2.get(3);
			assertEquals("Ion 1-4", 32.0, ion23.getIon());
			assertEquals("Abundance 1-4", 2106003.8, ion23.getAbundance(), 1E-1);
			IIon ion24 = ions2.get(4);
			assertEquals("Ion 1-4", 34.0, ion24.getIon());
			assertEquals("Abundance 1-4", 7114.8774, ion24.getAbundance(), 1E-4);
			IIon ion25 = ions2.get(5);
			assertEquals("Ion 1-5", 40.0, ion25.getIon());
			assertEquals("Abundance 1-5", 177871.94, ion25.getAbundance(), 1E-2);
			IPeakMSD peak3 = (IPeakMSD)peaks.get(2);
			IPeakMassSpectrum peakMassSpectrum3 = peak3.getPeakModel().getPeakMassSpectrum();
			List<IIon> ions3 = peakMassSpectrum3.getIons();
			IIon ion30 = ions3.get(0);
			assertEquals("Ion 2-0", 20.0, ion30.getIon());
			assertEquals("Abundance 2-0", 4579.351, ion30.getAbundance(), 1E-4);
			IIon ion31 = ions3.get(1);
			assertEquals("Ion 2-1", 28.0, ion31.getIon());
			assertEquals("Abundance 2-1", 4574771.5, ion31.getAbundance(), 1E-2);
			IIon ion32 = ions3.get(2);
			assertEquals("Ion 2-2", 29.0, ion32.getIon());
			assertEquals("Abundance 2-2", 45793.51, ion32.getAbundance(), 1E-2);
			IIon ion33 = ions3.get(3);
			assertEquals("Ion 2-3", 18.0, ion33.getIon());
			assertEquals("Abundance 2-3", 4579.351, ion33.getAbundance(), 1E-2);
		} catch(FileNotFoundException e) {
			assertTrue(false);
		} catch(FileIsNotReadableException e) {
			assertTrue(false);
		} catch(FileIsEmptyException e) {
			assertTrue(false);
		} catch(IOException e) {
			assertTrue(false);
		} catch(TypeCastException e) {
			assertTrue(false);
		}
	}
}
