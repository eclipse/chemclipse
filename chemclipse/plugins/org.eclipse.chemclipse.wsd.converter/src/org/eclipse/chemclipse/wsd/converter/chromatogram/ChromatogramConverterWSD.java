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
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramConverterWSD {

	private static ChromatogramConverter<IChromatogramWSD> chromatogramConverter = new ChromatogramConverter<>("org.eclipse.chemclipse.wsd.converter.chromatogramSupplier", IChromatogramWSD.class);

	/**
	 * This class has only static methods.
	 */
	private ChromatogramConverterWSD() {
	}

	public static IProcessingInfo convert(final File file, final String converterId, final IProgressMonitor monitor) {

		return chromatogramConverter.convert(file, converterId, monitor);
	}

	public static IProcessingInfo convert(File file, IProgressMonitor monitor) {

		return chromatogramConverter.getChromatogram(file, false, monitor);
	}

	public static IProcessingInfo convertOverview(File file, String converterId, IProgressMonitor monitor) {

		return chromatogramConverter.convertOverview(file, converterId, monitor);
	}

	public static IProcessingInfo convertOverview(File file, IProgressMonitor monitor) {

		return chromatogramConverter.getChromatogram(file, true, monitor);
	}

	public static IProcessingInfo convert(File file, IChromatogramWSD chromatogram, String converterId, IProgressMonitor monitor) {

		return chromatogramConverter.convert(file, chromatogram, converterId, monitor);
	}

	public static IChromatogramConverterSupport getChromatogramConverterSupport() {

		return chromatogramConverter.getChromatogramConverterSupport();
	}
}
