/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.msd.converter.massspectrum.IMassSpectrumImportConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl.MSLMassSpectrumImportConverter;

import junit.framework.TestCase;

public class MSLImportConverter_1_ITest extends TestCase {

	private IMassSpectrumImportConverter importConverter;
	private File importFile;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		importConverter = new MSLMassSpectrumImportConverter();
	}

	@Override
	protected void tearDown() throws Exception {

		importConverter = null;
		super.tearDown();
	}

	public void testExceptions_1() {

		importFile = null;
		IMassSpectrumImportConverterProcessingInfo processingInfo = importConverter.convert(null, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testExceptions_2() {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_EMPTY));
		IMassSpectrumImportConverterProcessingInfo processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testExceptions_3() {

		importFile = new File("nirvana");
		IMassSpectrumImportConverterProcessingInfo processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testExceptions_4() {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_NOT_READABLE));
		importFile.setReadable(false);
		try {
			IMassSpectrumImportConverterProcessingInfo processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
			assertTrue(processingInfo.hasErrorMessages());
		} finally {
			importFile.setReadable(true);
		}
	}
}
