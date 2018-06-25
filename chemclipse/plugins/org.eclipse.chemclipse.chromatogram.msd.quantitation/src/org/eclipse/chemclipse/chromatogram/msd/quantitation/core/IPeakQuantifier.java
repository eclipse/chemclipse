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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.settings.IPeakQuantifierSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakQuantifier {

	/**
	 * Quantifies the peak.
	 * 
	 * @param peak
	 * @param peakQuantifierSettings
	 * @param monitor
	 * @return IProcessingInfo
	 */
	IProcessingInfo quantify(IPeakMSD peak, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor);

	/**
	 * Quantifies the peak.
	 * 
	 * @param peak
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo quantify(IPeakMSD peak, IProgressMonitor monitor);

	/**
	 * Quantifies the list of peaks.
	 * 
	 * @param peaks
	 * @param peakQuantifierSettings
	 * @param monitor
	 * @return IProcessingInfo
	 */
	IProcessingInfo quantify(List<IPeakMSD> peaks, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor);

	/**
	 * Quantifies the list of peaks.
	 * 
	 * @param peaks
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo quantify(List<IPeakMSD> peaks, IProgressMonitor monitor);
}
