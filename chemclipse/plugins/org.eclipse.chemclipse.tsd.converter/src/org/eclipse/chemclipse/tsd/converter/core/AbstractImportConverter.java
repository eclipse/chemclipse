/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.tsd.converter.core;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.chemclipse.converter.chromatogram.AbstractChromatogramImportConverter;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.tsd.model.core.IChromatogramTSD;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractImportConverter extends AbstractChromatogramImportConverter<IChromatogramTSD> implements IImportConverterTSD {

	private static final String DESCRIPTION = "Import Converter (TSD)";

	@Override
	public IProcessingInfo<IChromatogramTSD> convert(File file, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramTSD> processingInfo = new ProcessingInfo<>();
		try (FileInputStream inputStream = new FileInputStream(file)) {
			IChromatogramTSD chromatogramTSD = convert(inputStream, monitor);
			processingInfo.setProcessingResult(chromatogramTSD);
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
}