/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.chromatogram;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.processing.chromatogram.ChromatogramOverviewImportConverterProcessingInfo;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramOverviewImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.chromatogram.AbstractChromatogramMSDImportConverter;
import org.eclipse.chemclipse.msd.converter.processing.chromatogram.ChromatogramMSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.converter.processing.chromatogram.IChromatogramMSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

/**
 * THIS CLASS IS NOT SUITED FOR PRODUCTIVE USE!<br/>
 * IT IS AN TESTCLASS!
 * 
 * @author eselmeister
 */
public class TestChromatogramImportConverter extends AbstractChromatogramMSDImportConverter {

	@Override
	public IChromatogramMSDImportConverterProcessingInfo convert(File chromatogram, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(chromatogram);
		IChromatogramMSDImportConverterProcessingInfo processingInfoImport = new ChromatogramMSDImportConverterProcessingInfo();
		processingInfoImport.addMessages(processingInfo);
		return processingInfoImport;
	}

	@Override
	public IChromatogramOverviewImportConverterProcessingInfo convertOverview(File chromatogram, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(chromatogram);
		IChromatogramOverviewImportConverterProcessingInfo processingInfoImport = new ChromatogramOverviewImportConverterProcessingInfo();
		processingInfoImport.addMessages(processingInfo);
		return processingInfoImport;
	}
}
