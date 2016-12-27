/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.openchromx.converter;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.processing.chromatogram.IChromatogramMSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.converter.ChromatogramExportConverter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.history.EditInformation;

import junit.framework.TestCase;

public class ChromatogramExportConverter_1_ITest extends TestCase {

	private IChromatogramMSD chromatogram;
	private File importFile;
	private File exportFile;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1_AGILENT));
		IChromatogramMSDImportConverterProcessingInfo processingInfo = ChromatogramConverterMSD.convert(importFile, new NullProgressMonitor());
		chromatogram = processingInfo.getChromatogram();
		chromatogram.setMiscInfo("Hello Test Misc Info");
		chromatogram.getEditHistory().add(new EditInformation("I have done </Chromatogram> something"));
		chromatogram.getEditHistory().add(new EditInformation("me too"));
		exportFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_EXPORT_TEST));
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testExport_1() {

		ChromatogramExportConverter converter = new ChromatogramExportConverter();
		IChromatogramExportConverterProcessingInfo processingInfo = converter.convert(exportFile, chromatogram, new NullProgressMonitor());
		try {
			File file = processingInfo.getFile();
			assertNotNull(file);
		} catch(TypeCastException e) {
			assertTrue("TypeCastException", false);
		}
	}
}
