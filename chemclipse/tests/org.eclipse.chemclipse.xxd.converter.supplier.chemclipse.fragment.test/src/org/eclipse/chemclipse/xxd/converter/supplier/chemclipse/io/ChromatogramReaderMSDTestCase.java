/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
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
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.io;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class ChromatogramReaderMSDTestCase extends TestCase {

	protected IChromatogramMSD chromatogram;
	protected String pathImport;
	protected File fileImport;
	private final static String EXTENSION_POINT_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";

	@SuppressWarnings("deprecation")
	@Override
	protected void setUp() throws Exception {

		super.setUp();
		fileImport = new File(this.pathImport);
		IProcessingInfo<IChromatogramMSD> processingInfo = ChromatogramConverterMSD.getInstance().convert(fileImport, EXTENSION_POINT_ID, new NullProgressMonitor());
		try {
			chromatogram = processingInfo.getProcessingResult(IChromatogramMSD.class);
		} catch(TypeCastException e) {
			chromatogram = null;
		}
	}

	@Override
	protected void tearDown() throws Exception {

		pathImport = null;
		fileImport = null;
		chromatogram = null;
		//
		System.gc();
		//
		super.tearDown();
	}
}
