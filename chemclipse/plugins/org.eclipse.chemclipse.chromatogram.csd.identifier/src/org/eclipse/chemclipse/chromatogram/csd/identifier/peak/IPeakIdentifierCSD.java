/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.identifier.peak;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.identifier.settings.IPeakIdentifierSettingsCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakIdentifierCSD {

	/**
	 * Identifies a peak.
	 * 
	 * @param peak
	 * @param peakIdentifierSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo identify(IPeakCSD peak, IPeakIdentifierSettingsCSD peakIdentifierSettings, IProgressMonitor monitor);

	/**
	 * Identifies the peak.
	 * 
	 * @param peak
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo identify(IPeakCSD peak, IProgressMonitor monitor);

	/**
	 * Identifies a list of peaks.
	 * 
	 * @param peaks
	 * @param peakIdentifierSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo identify(List<IPeakCSD> peaks, IPeakIdentifierSettingsCSD peakIdentifierSettings, IProgressMonitor monitor);

	/**
	 * The same as the other method but without settings.
	 * 
	 * @param peaks
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo identify(List<IPeakCSD> peaks, IProgressMonitor monitor);

	IProcessingInfo identify(IChromatogramSelectionCSD chromatogramSelectionCSD, IProgressMonitor monitor);

	IProcessingInfo identify(IChromatogramSelectionCSD chromatogramSelectionCSD, IPeakIdentifierSettingsCSD peakIdentifierSettings, IProgressMonitor monitor);
}
