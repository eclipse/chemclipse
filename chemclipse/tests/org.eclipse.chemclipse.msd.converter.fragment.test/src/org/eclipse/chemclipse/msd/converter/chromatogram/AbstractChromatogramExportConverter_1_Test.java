/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.converter.TestPathHelper;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

/**
 * This class validates the exceptions thrown by
 * AbstractChromatogramExportConverter.<br/>
 * Because AbstractChromatogramExportConverter is an abstract class,
 * TestChromatogramExportConverter is instantiated which extends
 * AbstractChromatogramExportConverter.
 * 
 * @author eselmeister
 */
public class AbstractChromatogramExportConverter_1_Test extends TestCase {

	private TestChromatogramExportConverter ec;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ec = new TestChromatogramExportConverter();
	}

	@Override
	protected void tearDown() throws Exception {

		ec = null;
		super.tearDown();
	}

	public void testFileNotFoundException_1() {

		File file = null;
		IProcessingInfo<?> processingInfo = ec.convert(file, null, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testFileNotWritableException_1() {

		File file = null;
		try {
			file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_EXPORT_CHROMATOGRAM_NOT_WRITEABLE));
			file.setWritable(false);
			IProcessingInfo<?> processingInfo = ec.convert(file, null, new NullProgressMonitor());
			assertTrue(processingInfo.hasErrorMessages());
		} catch(IOException e) {
			assertTrue("IOException", false);
		} finally {
			if(file != null) {
				file.setWritable(true);
			}
		}
	}
}
