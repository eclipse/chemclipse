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
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.chemclipse.msd.converter.massspectrum.IMassSpectrumExportConverter;
import org.eclipse.chemclipse.msd.converter.massspectrum.IMassSpectrumImportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl.MSLMassSpectrumExportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl.MSLMassSpectrumImportConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;

/**
 * Imports a msl file.
 * 
 * @author eselmeister
 */
public class MassSpectrumExportConverterTestCase extends TestCase {

	protected File exportFile;
	protected IMassSpectrumExportConverter exportConverter;
	protected File importFile;
	protected IMassSpectra massSpectra;
	protected IMassSpectrumImportConverter importConverter;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		exportConverter = new MSLMassSpectrumExportConverter();
		importConverter = new MSLMassSpectrumImportConverter();
	}

	@Override
	protected void tearDown() throws Exception {

		if(exportFile != null) {
			exportFile.delete();
		}
		exportFile = null;
		exportConverter = null;
		importConverter = null;
		massSpectra = null;
		//
		System.gc();
		//
		super.tearDown();
	}
}
