/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.PathResolver;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class MassSpectrumExportConverter_DB_2_ITest extends MassSpectrumExportConverterTestCase {

	@Override
	protected void setUp() throws Exception {

		exportFile = new File(PathResolver.getAbsolutePath(TestPathHelper.TESTDIR_EXPORT) + File.separator + TestPathHelper.TESTFILE_EXPORT_DB_MSL);
		importFile = new File(PathResolver.getAbsolutePath(TestPathHelper.TESTDIR_EXPORT) + File.separator + TestPathHelper.TESTFILE_EXPORT_DB_MSL);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testExport_1() throws AbundanceLimitExceededException, IonLimitExceededException, FileNotFoundException, FileIsNotWriteableException, IOException, NoConverterAvailableException, FileIsNotReadableException, FileIsEmptyException {

		IIon ion;
		IScanMSD ms;
		massSpectra = new MassSpectra();
		for(int i = 1; i <= 3; i++) {
			ms = new ScanMSD();
			for(int j = 1; j <= 6; j++) {
				ion = new Ion(j * i, j * i * 10);
				ms.addIon(ion);
			}
			massSpectra.addMassSpectrum(ms);
		}
		assertEquals("Size before", 3, massSpectra.size());
		exportConverter.convert(exportFile, massSpectra, false, new NullProgressMonitor());
		IProcessingInfo<?> processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
		try {
			massSpectra = (IMassSpectra)processingInfo.getProcessingResult();
		} catch(TypeCastException e) {
			assertTrue("TypeCastException", false);
		}
		assertEquals("Size after", 3, massSpectra.size());
	}
}
