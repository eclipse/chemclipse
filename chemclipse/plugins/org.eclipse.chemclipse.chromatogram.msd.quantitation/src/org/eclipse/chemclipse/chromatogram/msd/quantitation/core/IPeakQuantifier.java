/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.processing.IPeakQuantifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.settings.IPeakQuantifierSettings;

public interface IPeakQuantifier {

	/**
	 * Quantifies the peak.
	 * 
	 * @param peak
	 * @param peakQuantifierSettings
	 * @param monitor
	 * @return IPeakQuantifierProcessingInfo
	 */
	IPeakQuantifierProcessingInfo quantify(IPeakMSD peak, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor);

	/**
	 * Quantifies the peak.
	 * 
	 * @param peak
	 * @param monitor
	 * @return {@link IPeakQuantifierProcessingInfo}
	 */
	IPeakQuantifierProcessingInfo quantify(IPeakMSD peak, IProgressMonitor monitor);

	/**
	 * Quantifies the list of peaks.
	 * 
	 * @param peaks
	 * @param peakQuantifierSettings
	 * @param monitor
	 * @return IPeakQuantifierProcessingInfo
	 */
	IPeakQuantifierProcessingInfo quantify(List<IPeakMSD> peaks, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor);

	/**
	 * Quantifies the list of peaks.
	 * 
	 * @param peaks
	 * @param monitor
	 * @return {@link IPeakQuantifierProcessingInfo}
	 */
	IPeakQuantifierProcessingInfo quantify(List<IPeakMSD> peaks, IProgressMonitor monitor);
}
