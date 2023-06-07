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
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.database.IDatabaseImportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class MSLImportConverter_1_ITest extends TestCase {

	private IDatabaseImportConverter importConverter;
	private File importFile;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		importConverter = new MSLDatabaseImportConverter();
	}

	@Override
	protected void tearDown() throws Exception {

		importConverter = null;
		super.tearDown();
	}

	public void testExceptions_1() {

		importFile = null;
		IProcessingInfo<?> processingInfo = importConverter.convert(null, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testExceptions_2() {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_EMPTY));
		IProcessingInfo<?> processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testExceptions_3() {

		importFile = new File("nirvana");
		IProcessingInfo<?> processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
		assertTrue(processingInfo.hasErrorMessages());
	}

	public void testExceptions_4() {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_NOT_READABLE));
		importFile.setReadable(false);
		try {
			IProcessingInfo<?> processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
			assertTrue(processingInfo.hasErrorMessages());
		} finally {
			importFile.setReadable(true);
		}
	}
}
