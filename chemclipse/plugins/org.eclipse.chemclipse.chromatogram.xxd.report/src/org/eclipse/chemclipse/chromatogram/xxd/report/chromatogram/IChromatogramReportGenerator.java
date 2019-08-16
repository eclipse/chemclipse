/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.chromatogram;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.settings.IChromatogramReportSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IChromatogramReportGenerator {

	IProcessingInfo generate(File file, boolean append, IChromatogram<? extends IPeak> chromatogram, IChromatogramReportSettings chromatogramReportSettings, IProgressMonitor monitor);

	IProcessingInfo generate(File file, boolean append, IChromatogram<? extends IPeak> chromatogram, IProgressMonitor monitor);

	IProcessingInfo generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IChromatogramReportSettings chromatogramReportSettings, IProgressMonitor monitor);

	IProcessingInfo generate(File file, boolean append, List<IChromatogram<? extends IPeak>> chromatograms, IProgressMonitor monitor);

	/**
	 * This method validates whether the file is writable or not.<br/>
	 * 
	 * @param file
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo validate(File file);
}
