/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
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

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.msd.converter.massspectrum.IMassSpectrumImportConverter;
import org.eclipse.chemclipse.msd.converter.processing.massspectrum.IMassSpectrumImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl.MSLMassSpectrumImportConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;

import junit.framework.TestCase;

/**
 * Imports a msl file.
 * 
 * @author eselmeister
 */
public class MassSpectrumImportConverterTestCase extends TestCase {

	protected File importFile;
	protected IMassSpectra massSpectra;
	protected IMassSpectrumImportConverter importConverter;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		importConverter = new MSLMassSpectrumImportConverter();
		IMassSpectrumImportConverterProcessingInfo processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
		massSpectra = processingInfo.getMassSpectra();
	}

	@Override
	protected void tearDown() throws Exception {

		importFile = null;
		massSpectra = null;
		importConverter = null;
		super.tearDown();
	}
}
