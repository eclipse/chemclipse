/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.internal.core.support;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.TestPathHelper;
import org.eclipse.chemclipse.model.settings.Delimiter;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.csv.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class ChromatogramTestCase extends TestCase {

	IChromatogramMSD chromatogram;
	IChromatogramSelectionMSD chromatogramSelection;
	File chromatogramFile;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		PreferenceSupplier.setImportDelimiter(Delimiter.SEMICOLON);
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1_ZIP))));
		zipInputStream.getNextEntry();
		String inputChromatogramFile = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1_FOLDER);
		inputChromatogramFile += File.separator + TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1_NAME;
		chromatogramFile = new File(inputChromatogramFile);
		if(chromatogramFile.exists()) {
			chromatogramFile.delete();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(chromatogramFile);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 2048);
		int count;
		int buffer = 2048;
		byte data[] = new byte[buffer];
		while((count = zipInputStream.read(data, 0, buffer)) != -1) {
			bufferedOutputStream.write(data, 0, count);
		}
		bufferedOutputStream.flush();
		bufferedOutputStream.close();
		zipInputStream.close();
		/*
		 * Read the chromatogram
		 */
		IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(chromatogramFile, new NullProgressMonitor());
		chromatogram = processingInfo.getProcessingResult();
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		PreferenceSupplier.setImportDelimiter(Delimiter.COMMA);
		//
		chromatogram = null;
		chromatogramSelection = null;
		chromatogramFile.delete();
		//
		System.gc();
		//
		super.tearDown();
	}

	public IChromatogramSelectionMSD getChromatogramSelection() {

		return chromatogramSelection;
	}
}