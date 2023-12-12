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
package org.eclipse.chemclipse.msd.converter.chromatogram;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.converter.TestPathHelper;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * This class validates the exceptions thrown by
 * AbstractChromatogramImportConverter. Because
 * AbstractChromatogramImportConverter is an abstract class,
 * TestChromatogramImportConverter is instantiated which extends
 * AbstractChromatogramImportConverter.
 * 
 * @author eselmeister
 */
public class AbstractChromatogramImportConverter_1_Test extends TestCase {

	TestChromatogramImportConverter ic = new TestChromatogramImportConverter();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ic = new TestChromatogramImportConverter();
	}

	@Override
	protected void tearDown() throws Exception {

		ic = null;
		super.tearDown();
	}

	@Test
	public void testFileNotFoundException_1() {

		File file = new File("");
		IProcessingInfo<IChromatogram<?>> processingInfo = ic.convert(file, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	@Test
	public void testFileIsNotReadableException_1() {

		File file = null;
		try {
			file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_NOT_READABLE));
			file.setReadable(false);
			IProcessingInfo<IChromatogram<?>> processingInfo = ic.convert(file, new NullProgressMonitor());
			assertTrue(processingInfo.hasErrorMessages());
		} catch(IOException e) {
			assertTrue("IOException", false);
		} finally {
			if(file != null) {
				file.setReadable(true);
			}
		}
	}

	@Test
	public void testFileIsEmptyException_1() {

		File file = null;
		try {
			file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_EMPTY));
			IProcessingInfo<IChromatogram<?>> processingInfo = ic.convert(file, new NullProgressMonitor());
			assertTrue(processingInfo.hasErrorMessages());
		} catch(IOException e) {
			assertTrue("IOException", false);
		}
	}
}
