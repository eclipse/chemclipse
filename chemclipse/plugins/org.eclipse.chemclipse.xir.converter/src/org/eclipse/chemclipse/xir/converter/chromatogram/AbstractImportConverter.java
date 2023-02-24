/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.converter.chromatogram;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramImportConverter;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractImportConverter extends AbstractChromatogramImportConverter<IChromatogramISD> implements IImportConverterISD {

	private static final String DESCRIPTION = "Import Converter (ISD)";

	@Override
	public IProcessingInfo<IChromatogramISD> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramISD> processingInfo = new ProcessingInfo<>();
		try (FileInputStream inputStream = new FileInputStream(file)) {
			IChromatogramISD chromatogramISD = convert(inputStream, monitor);
			setFile(file, chromatogramISD);
			processingInfo.setProcessingResult(chromatogramISD);
		} catch(Exception e) {
			processingInfo.addErrorMessage(DESCRIPTION, "Failed to load the file: " + file);
		}
		//
		return processingInfo;
	}

	@Override
	public IProcessingInfo<IChromatogramOverview> convertOverview(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramOverview> processingInfo = new ProcessingInfo<>();
		try (FileInputStream inputStream = new FileInputStream(file)) {
			IChromatogramOverview chromatogramOverview = convertOverview(inputStream, monitor);
			processingInfo.setProcessingResult(chromatogramOverview);
		} catch(Exception e) {
			processingInfo.addErrorMessage(DESCRIPTION, "Failed to load the overview of the file: " + file);
		}
		//
		return processingInfo;
	}

	private void setFile(File file, IChromatogramISD chromatogramISD) {

		if(chromatogramISD != null) {
			chromatogramISD.setFile(file);
		}
	}
}