/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add default implementations
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.peak;

import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
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
	default IProcessingInfo applyFilter(IPeakMSD peak, IPeakFilterSettings peakFilterSettings, IProgressMonitor monitor) {

		return applyFilter(Collections.singletonList(peak), peakFilterSettings, monitor);
	}

	/**
	 * Applies the filter to the selected peak.
	 * 
	 * @param peak
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	default IProcessingInfo applyFilter(IPeakMSD peak, IProgressMonitor monitor) {

		return applyFilter(peak, null, monitor);
	}

	/**
	 * Applies the filter to the selected peaks using the settings.
	 * 
	 * @param peaks
	 * @param peakFilterSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo applyFilter(List<IPeakMSD> peaks, IPeakFilterSettings peakFilterSettings, IProgressMonitor monitor);

	/**
	 * Applies the filter to the selected peaks.
	 * 
	 * @param peaks
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	default IProcessingInfo applyFilter(List<IPeakMSD> peaks, IProgressMonitor monitor) {

		return applyFilter(peaks, null, monitor);
	}

	/**
	 * Applies the filter to the selected peaks in the chromatogram selection using the settings.
	 * 
	 * @param chromatogramSelection
	 * @param peakFilterSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	default IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IPeakFilterSettings peakFilterSettings, IProgressMonitor monitor) {

		return applyFilter(Collections.unmodifiableList(chromatogramSelection.getChromatogram().getPeaks(chromatogramSelection)), peakFilterSettings, monitor);
	}

	/**
	 * Applies the filter to the selected peaks in the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	default IProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		return applyFilter(chromatogramSelection, null, monitor);
	}

	/**
	 * Validates the peak and the settings.
	 * 
	 * @param peak
	 * @param peakFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo validate(IPeakMSD peak, IPeakFilterSettings peakFilterSettings);

	/**
	 * Validates the peak and the settings.
	 * 
	 * @param peaks
	 * @param peakFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo validate(List<IPeakMSD> peaks, IPeakFilterSettings peakFilterSettings);

	/**
	 * Validates the chromatogram selection and the settings.
	 * 
	 * @param chromatogramSelection
	 * @param peakFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo validate(IChromatogramSelectionMSD chromatogramSelection, IPeakFilterSettings peakFilterSettings);
}
