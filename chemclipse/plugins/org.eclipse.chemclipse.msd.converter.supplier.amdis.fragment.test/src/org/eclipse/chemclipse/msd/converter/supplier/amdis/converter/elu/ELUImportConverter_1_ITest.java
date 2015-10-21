/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.elu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.msd.converter.processing.peak.IPeakImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.AmdisELUReader;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class ELUImportConverter_1_ITest extends TestCase {

	private AmdisELUReader reader;
	private File file;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		reader = new AmdisELUReader();
		String pathname = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PEAKS_1);
		file = new File(pathname);
	}

	@Override
	protected void tearDown() throws Exception {

		reader = null;
		super.tearDown();
	}

	public void testRead_1() {

		try {
			IPeakImportConverterProcessingInfo processingInfo = reader.read(file, new NullProgressMonitor());
			List<IPeak> peaks = processingInfo.getPeaks().getPeaks();
			for(IPeak peak : peaks) {
				System.out.println(peak);
			}
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
