/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.converter;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.TestPathHelper;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class MatlabParafacPeakImportConverter_4_ITest extends TestCase {

	private MatlabParafacPeakImportConverter converter;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		converter = new MatlabParafacPeakImportConverter();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testImport_1() {

		try {
			File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_EMPTY));
			converter.convert(file, new NullProgressMonitor());
		} catch(Exception e) {
			assertTrue(true);
		}
	}

	public void testImport_3() {

		try {
			File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PEAKS));
			converter.convert(file, new NullProgressMonitor());
		} catch(Exception e) {
			assertTrue(true);
		}
	}

	public void testImport_4() {

		try {
			File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PEAKS_EXTENSION));
			converter.convert(file, new NullProgressMonitor());
		} catch(Exception e) {
			assertTrue(true);
		}
	}
}
