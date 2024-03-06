/*******************************************************************************
 * Copyright (c) 2015, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.filter.core.peak;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakFilter {

	/**
	 * Applies the filter to the selected peak using the settings.
	 * 
	 * @param peak
	 * @param peakFilterSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramFilterResult> applyFilter(IPeakCSD peak, IPeakFilterSettings peakFilterSettings, IProgressMonitor monitor);

	/**
	 * Applies the filter to the selected peak.
	 * 
	 * @param peak
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramFilterResult> applyFilter(IPeakCSD peak, IProgressMonitor monitor);

	/**
	 * Applies the filter to the selected peaks using the settings.
	 * 
	 * @param peaks
	 * @param peakFilterSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramFilterResult> applyFilter(List<IPeakCSD> peaks, IPeakFilterSettings peakFilterSettings, IProgressMonitor monitor);

	/**
	 * Applies the filter to the selected peaks.
	 * 
	 * @param peaks
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramFilterResult> applyFilter(List<IPeakCSD> peaks, IProgressMonitor monitor);

	/**
	 * Applies the filter to the selected peaks in the chromatogram selection using the settings.
	 * 
	 * @param chromatogramSelection
	 * @param peakFilterSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionCSD chromatogramSelection, IPeakFilterSettings peakFilterSettings, IProgressMonitor monitor);

	/**
	 * Applies the filter to the selected peaks in the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionCSD chromatogramSelection, IProgressMonitor monitor);

	/**
	 * Validates the peak and the settings.
	 * 
	 * @param peak
	 * @param peakFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramFilterResult> validate(IPeakCSD peak, IPeakFilterSettings peakFilterSettings);

	/**
	 * Validates the peak and the settings.
	 * 
	 * @param peaks
	 * @param peakFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramFilterResult> validate(List<IPeakCSD> peaks, IPeakFilterSettings peakFilterSettings);

	/**
	 * Validates the chromatogram selection and the settings.
	 * 
	 * @param chromatogramSelection
	 * @param peakFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<IChromatogramFilterResult> validate(IChromatogramSelectionCSD chromatogramSelection, IPeakFilterSettings peakFilterSettings);
}
