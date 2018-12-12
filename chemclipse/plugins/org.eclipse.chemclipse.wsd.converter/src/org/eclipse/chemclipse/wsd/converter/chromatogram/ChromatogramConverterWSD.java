/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.chromatogram;

import java.io.File;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverter;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.Converter;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramConverterWSD {

	private static ChromatogramConverter<IChromatogramWSD> CHROMATOGRAM_CONVERTER = new ChromatogramConverter<>("org.eclipse.chemclipse.wsd.converter.chromatogramSupplier", IChromatogramWSD.class);

	/**
	 * This class has only static methods.
	 */
	private ChromatogramConverterWSD() {
	}

	public static IProcessingInfo convert(final File file, final String converterId, final IProgressMonitor monitor) {

		return CHROMATOGRAM_CONVERTER.convert(file, converterId, monitor);
	}

	public static IProcessingInfo convert(File file, IProgressMonitor monitor) {

		return CHROMATOGRAM_CONVERTER.getChromatogram(file, false, monitor);
	}

	public static IProcessingInfo convertOverview(File file, String converterId, IProgressMonitor monitor) {

		return CHROMATOGRAM_CONVERTER.convertOverview(file, converterId, monitor);
	}

	public static IProcessingInfo convertOverview(File file, IProgressMonitor monitor) {

		return CHROMATOGRAM_CONVERTER.getChromatogram(file, true, monitor);
	}

	public static IProcessingInfo convert(final File file, final IChromatogramWSD chromatogram, final String converterId, final IProgressMonitor monitor) {

		IProcessingInfo processingInfo;
		Object converter = CHROMATOGRAM_CONVERTER.getChromatogramConverter(converterId, Converter.EXPORT_CONVERTER);
		if(converter instanceof IChromatogramWSDExportConverter) {
			processingInfo = ((IChromatogramWSDExportConverter)converter).convert(file, chromatogram, monitor);
		} else {
			processingInfo = CHROMATOGRAM_CONVERTER.getNoExportConverterAvailableProcessingInfo(file);
		}
		return processingInfo;
	}

	public static IChromatogramConverterSupport getChromatogramConverterSupport() {

		return CHROMATOGRAM_CONVERTER.getChromatogramConverterSupport();
	}
}
