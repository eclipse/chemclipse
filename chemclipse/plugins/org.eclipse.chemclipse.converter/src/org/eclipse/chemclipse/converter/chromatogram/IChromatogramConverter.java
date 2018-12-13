/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.chromatogram;

import java.io.File;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public interface IChromatogramConverter<T extends IChromatogram> {

	/**
	 * This methods returns an {@link IChromatogramConverterSupport} instance.<br/>
	 * The {@link ChromatogramConverterSupport} instance stores descriptions
	 * about all valid and registered chromatogram converters.<br/>
	 * It can be used to get more information about the registered converters
	 * such like filter names, file extensions.
	 * 
	 * @return ChromatogramConverterSupport
	 */
	IChromatogramConverterSupport getChromatogramConverterSupport();

	IProcessingInfo convertOverview(File file, IProgressMonitor monitor);

	/**
	 * Returns an IChromatogramOverview instance if the given converter is
	 * available.<br/>
	 * If no converter was available, a
	 * NoChromatogramConverterAvailableException will be thrown.
	 * 
	 * @param chromatogram
	 * @param converterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo convertOverview(File file, String converterId, IProgressMonitor monitor);

	IProcessingInfo convert(File file, IProgressMonitor monitor);

	/**
	 * Returns an IChromatogram instance.<br/>
	 * The raw files will be converted depending on the selected converter id.<br/>
	 * If a failure in the file could be detected an exception will be thrown.<br/>
	 * For example that the file is not readable or the file is not existent.<br/>
	 * If no converter was able to read the file, a
	 * NoChromatogramConverterAvailableException will be thrown.
	 * 
	 * @param chromatogram
	 * @param converterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo convert(File file, String converterId, IProgressMonitor monitor);

	/**
	 * If no suitable parser was found, null will be returned.
	 * 
	 * @param file
	 * @param overview
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo getChromatogram(File file, boolean overview, IProgressMonitor monitor);

	/**
	 * Maybe override to add your own methods.
	 * 
	 * @param processingInfo
	 */
	void postProcessChromatogram(IProcessingInfo processingInfo, IProgressMonitor monitor);

	IProcessingInfo convert(File file, IChromatogram<? extends IPeak> chromatogram, String converterId, IProgressMonitor monitor);
}