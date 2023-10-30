/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.io;

import java.io.File;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.peak.PeakConverterMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class PeakReaderMSDTestCase extends TestCase {

	protected IPeaks<?> peaks;
	protected String pathImport;
	protected File fileImport;
	private static final String EXTENSION_POINT_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.peaks";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		fileImport = new File(this.pathImport);
		IProcessingInfo<IPeaks<?>> processingInfo = PeakConverterMSD.convert(fileImport, EXTENSION_POINT_ID, new NullProgressMonitor());
		try {
			peaks = processingInfo.getProcessingResult();
		} catch(TypeCastException e) {
			peaks = null;
		}
	}

	@Override
	protected void tearDown() throws Exception {

		pathImport = null;
		fileImport = null;
		peaks = null;
		//
		System.gc();
		//
		super.tearDown();
	}
}
