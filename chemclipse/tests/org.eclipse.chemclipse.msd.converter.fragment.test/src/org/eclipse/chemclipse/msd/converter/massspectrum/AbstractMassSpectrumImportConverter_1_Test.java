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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.massspectrum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.msd.converter.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

/**
 * This class validates the exceptions thrown by
 * AbstractMassSpectrumImportConverter. Because
 * AbstractMassSpectrumImportConverter is an abstract class,
 * TestMassSpectrumImportConverter is instantiated which extends
 * AbstractMassSpectrumImportConverter.
 */
public class AbstractMassSpectrumImportConverter_1_Test extends TestCase {

	TestMassSpectrumImportConverter ic = new TestMassSpectrumImportConverter();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ic = new TestMassSpectrumImportConverter();
	}

	@Override
	protected void tearDown() throws Exception {

		ic = null;
		super.tearDown();
	}

	public void testFileNotFoundException_1() {

		File file = new File("");
		IProcessingInfo<IMassSpectra> processingInfo = ic.convert(file, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testFileIsNotReadableException_1() {

		File file = null;
		try {
			file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_MASSSPECTRUM_NOT_READABLE));
			file.setReadable(false);
			ic.convert(file, new NullProgressMonitor());
		} catch(FileNotFoundException e) {
			assertTrue("FileNotFoundException", false);
		} catch(IOException e) {
			assertTrue("IOException", false);
		} finally {
			if(file != null) {
				file.setReadable(true);
			}
		}
	}

	public void testFileIsEmptyException_1() {

		File file = null;
		try {
			file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_MASSSPECTRUM_EMPTY));
			ic.convert(file, new NullProgressMonitor());
		} catch(FileNotFoundException e) {
			assertTrue("FileNotFoundException", false);
		} catch(IOException e) {
			assertTrue("IOException", false);
		}
	}
}
