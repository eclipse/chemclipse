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
package org.eclipse.chemclipse.chromatogram.msd.identifier.peak;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IPeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

public interface IPeakIdentifier {

	/**
	 * Identifies a peak.
	 * 
	 * @param peak
	 * @param peakIdentifierSettings
	 * @param monitor
	 * @return {@link IPeakIdentifierProcessingInfo}
	 */
	IPeakIdentifierProcessingInfo identify(IPeakMSD peak, IPeakIdentifierSettings peakIdentifierSettings, IProgressMonitor monitor);

	/**
	 * Identifies the peak.
	 * 
	 * @param peak
	 * @param monitor
	 * @return {@link IPeakIdentifierProcessingInfo}
	 */
	IPeakIdentifierProcessingInfo identify(IPeakMSD peak, IProgressMonitor monitor);

	/**
	 * Identifies a list of peaks.
	 * 
	 * @param peaks
	 * @param peakIdentifierSettings
	 * @param monitor
	 * @return {@link IPeakIdentifierProcessingInfo}
	 */
	IPeakIdentifierProcessingInfo identify(List<IPeakMSD> peaks, IPeakIdentifierSettings peakIdentifierSettings, IProgressMonitor monitor);

	/**
	 * The same as the other method but without settings.
	 * 
	 * @param peaks
	 * @param monitor
	 * @return {@link IPeakIdentifierProcessingInfo}
	 */
	IPeakIdentifierProcessingInfo identify(List<IPeakMSD> peaks, IProgressMonitor monitor);
}
