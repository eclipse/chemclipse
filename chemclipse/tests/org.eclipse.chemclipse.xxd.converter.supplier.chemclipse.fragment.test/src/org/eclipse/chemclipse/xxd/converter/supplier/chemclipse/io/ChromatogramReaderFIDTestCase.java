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
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.io;

import java.io.File;

import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class ChromatogramReaderFIDTestCase extends TestCase {

	protected IChromatogramCSD chromatogram;
	protected String pathImport;
	protected File fileImport;
	private static final String EXTENSION_POINT_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";

	@SuppressWarnings("deprecation")
	@Override
	protected void setUp() throws Exception {

		super.setUp();
		fileImport = new File(this.pathImport);
		IProcessingInfo<IChromatogramCSD> processingInfo = ChromatogramConverterCSD.getInstance().convert(fileImport, EXTENSION_POINT_ID, new NullProgressMonitor());
		try {
			chromatogram = processingInfo.getProcessingResult(IChromatogramCSD.class);
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
