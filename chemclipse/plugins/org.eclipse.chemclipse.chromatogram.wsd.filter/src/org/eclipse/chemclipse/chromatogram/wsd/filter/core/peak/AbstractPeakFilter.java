/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.filter.core.peak;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

public abstract class AbstractPeakFilter implements IPeakFilter {

	private static final String DESCRIPTION = "Peak Filter";

	@Override
	public IProcessingInfo<?> validate(IPeakWSD peak, IPeakFilterSettings peakFilterSettings) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(validatePeak(peak));
		processingInfo.addMessages(validateFilterSettings(peakFilterSettings));
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> validate(List<IPeakWSD> peaks, IPeakFilterSettings peakFilterSettings) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(validatePeaks(peaks));
		processingInfo.addMessages(validateFilterSettings(peakFilterSettings));
		return processingInfo;
	}

	@Override
	public IProcessingInfo<?> validate(IChromatogramSelectionWSD chromatogramSelection, IPeakFilterSettings peakFilterSettings) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		processingInfo.addMessages(validateChromatogramSelection(chromatogramSelection));
		processingInfo.addMessages(validateFilterSettings(peakFilterSettings));
		return processingInfo;
	}

	/**
	 * Validates the peak.
	 * 
	 * @param peak
	 * @return {@link IProcessingInfo}
	 */
	private IProcessingInfo<?> validatePeak(IPeakWSD peak) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		if(peak == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The peak is not valid.");
		}
		return processingInfo;
	}

	/**
	 * Validates the peak.
	 * 
	 * @param peak
	 * @return {@link IProcessingInfo}
	 */
	private IProcessingInfo<?> validatePeaks(List<IPeakWSD> peaks) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		if(peaks == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The peak list is not valid.");
		}
		return processingInfo;
	}

	/**
	 * Validates that the filter settings are not null.
	 * 
	 * @param peakFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	private IProcessingInfo<?> validateFilterSettings(IPeakFilterSettings peakFilterSettings) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		if(peakFilterSettings == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The filter settings are not valid.");
		}
		return processingInfo;
	}

	/**
	 * Validates the chromatogram selection and the referenced chromatogram.
	 * 
	 * @param chromatogramSelection
	 * @return {@link IProcessingInfo}
	 */
	private IProcessingInfo<?> validateChromatogramSelection(IChromatogramSelectionWSD chromatogramSelection) {

		IProcessingInfo<?> processingInfo = new ProcessingInfo<>();
		if(chromatogramSelection == null) {
			processingInfo.addErrorMessage(DESCRIPTION, "The chromatogram selection is not valid.");
		} else {
			if(chromatogramSelection.getChromatogram() == null) {
				processingInfo.addErrorMessage(DESCRIPTION, "The chromatogram is not valid.");
			}
		}
		return processingInfo;
	}
}
