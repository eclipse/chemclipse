/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.massspectrum.IMassSpectrumExportConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class MSLExportConverter_1_ITest extends TestCase {

	private IMassSpectrumExportConverter exportConverter;
	private File exportFile;
	private IScanMSD massSpectrum;
	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		exportConverter = new MSLMassSpectrumExportConverter();
		exportFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTDIR_EXPORT) + File.separator + TestPathHelper.TESTFILE_EXPORT_DB_MSL);
		massSpectrum = new ScanMSD();
		massSpectrum.addIon(new Ion(56.3f, 7382.3f));
		massSpectrum.addIon(new Ion(26.3f, 73382.3f));
		massSpectrum.addIon(new Ion(89.3f, 382.3f));
		massSpectra = new MassSpectra();
		massSpectra.addMassSpectrum(massSpectrum);
	}

	@Override
	protected void tearDown() throws Exception {

		exportConverter = null;
		massSpectra = null;
		massSpectrum = null;
		exportFile = null;
		super.tearDown();
	}

	public void testExport_1() {

		IMassSpectrumExportConverterProcessingInfo processingInfo = exportConverter.convert(null, massSpectrum, false, new NullProgressMonitor());
		try {
			assertTrue(processingInfo.hasErrorMessages());
		} catch(TypeCastException e) {
			assertTrue("TypeCastException", true);
		}
	}

	public void testExport_2() {

		massSpectrum = null;
		IMassSpectrumExportConverterProcessingInfo processingInfo = exportConverter.convert(exportFile, massSpectrum, false, new NullProgressMonitor());
		try {
			assertTrue(processingInfo.hasErrorMessages());
		} catch(TypeCastException e) {
			assertTrue("TypeCastException", true);
		}
	}

	public void testExport_3() {

		massSpectra = null;
		IMassSpectrumExportConverterProcessingInfo processingInfo = exportConverter.convert(exportFile, massSpectra, false, new NullProgressMonitor());
		try {
			assertTrue(processingInfo.hasErrorMessages());
		} catch(TypeCastException e) {
			assertTrue("TypeCastException", true);
		}
	}

	public void testExport_4() {

		try {
			exportFile.setWritable(false);
			IMassSpectrumExportConverterProcessingInfo processingInfo = exportConverter.convert(exportFile, massSpectra, false, new NullProgressMonitor());
			try {
				assertTrue(processingInfo.hasErrorMessages());
			} catch(TypeCastException e) {
				assertTrue("TypeCastException", true);
			}
		} finally {
			exportFile.setWritable(true);
		}
	}
}
