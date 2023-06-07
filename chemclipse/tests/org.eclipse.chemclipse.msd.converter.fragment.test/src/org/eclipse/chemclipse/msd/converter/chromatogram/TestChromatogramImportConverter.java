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
package org.eclipse.chemclipse.msd.converter.chromatogram;

import java.io.File;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramImportConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramImportConverter;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * THIS CLASS IS NOT SUITED FOR PRODUCTIVE USE!<br/>
 * IT IS AN TESTCLASS!
 */
public class TestChromatogramImportConverter extends AbstractChromatogramImportConverter<IChromatogram<?>> implements IChromatogramImportConverter<IChromatogram<?>> {

	@Override
	public IProcessingInfo<IChromatogram<?>> convert(File chromatogram, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogram<?>> processingInfo = super.validate(chromatogram);
		IProcessingInfo<IChromatogram<?>> processingInfoImport = new ProcessingInfo<>();
		processingInfoImport.addMessages(processingInfo);
		return processingInfoImport;
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File chromatogram, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramOverview> processingInfo = super.validate(chromatogram);
		IProcessingInfo<IChromatogramOverview> processingInfoImport = new ProcessingInfo<>();
		processingInfoImport.addMessages(processingInfo);
		return processingInfoImport;
	}
}
