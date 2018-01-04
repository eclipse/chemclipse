/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.core;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;

public class ChromatogramReport_1_ITest extends ChromatogramImporterTestCase {

	private static final String reportGeneratorId = "org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.chromatogramReportASCII";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testReportChromatogram_1() {

		File file = new File("Chromatogram1-Report.txt");
		ChromatogramReports.generate(file, false, chromatogram, reportGeneratorId, new NullProgressMonitor());
		assertTrue(file.exists());
	}
}
