/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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
import java.io.IOException;

import org.eclipse.chemclipse.msd.converter.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

/**
 * This class validates the exceptions thrown by
 * AbstractMassSpectrumExportConverter.<br/>
 * Because AbstractMassSpectrumExportConverter is an abstract class,
 * TestMassSpectrumExportConverter is instantiated which extends
 * AbstractMassSpectrumExportConverter.
 */
public class AbstractMassSpectrumExportConverter_1_Test extends TestCase {

	private TestMassSpectrumExportConverter ec;
	private IScanMSD massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ec = new TestMassSpectrumExportConverter();
		massSpectrum = new ScanMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		ec = null;
		massSpectrum = null;
		super.tearDown();
	}

	public void testFileNotFoundException_1() {

		File file = null;
		massSpectrum = null;
		IProcessingInfo processingInfo = ec.convert(file, massSpectrum, false, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testFileNotWritableException_1() {

		File file = null;
		try {
			file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_EXPORT_MASSSPECTRUM_NOT_WRITEABLE));
			file.setWritable(false);
			IProcessingInfo processingInfo = ec.convert(file, massSpectrum, false, new NullProgressMonitor());
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
