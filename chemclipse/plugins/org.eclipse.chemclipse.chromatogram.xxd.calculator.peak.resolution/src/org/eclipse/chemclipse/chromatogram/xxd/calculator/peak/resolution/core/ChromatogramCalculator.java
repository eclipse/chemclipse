/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.peak.resolution.core;

import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.AbstractChromatogramCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.peak.resolution.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.peak.resolution.result.IPeakResolutionResult;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.peak.resolution.result.PeakResolutionResult;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.settings.IChromatogramCalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.implementation.MeasurementResult;
import org.eclipse.chemclipse.model.implementation.PeakResolution;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramCalculator extends AbstractChromatogramCalculator {

	@Override
	public IProcessingInfo<?> applyCalculator(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramCalculatorSettings chromatogramCalculatorSettings, IProgressMonitor monitor) {

		IProcessingInfo<?> processingInfo = validate(chromatogramSelection, chromatogramCalculatorSettings);
		if(!processingInfo.hasErrorMessages()) {
			return applyCalculator(chromatogramSelection);
		}
		return null;
	}

	@Override
	public IProcessingInfo<?> applyCalculator(IChromatogramSelection<?, ?> chromatogramSelection, IProgressMonitor monitor) {

		return applyCalculator(chromatogramSelection);
	}

	@SuppressWarnings("unchecked")
	IProcessingInfo<IPeakResolutionResult> applyCalculator(IChromatogramSelection<?, ?> chromatogramSelection) {

		IProcessingInfo<IPeakResolutionResult> processingInfo = new ProcessingInfo<>();
		PeakResolutionResult peakResolutionResult = new PeakResolutionResult(ResultStatus.OK, Messages.peakResolutionCalculated);
		applyCalculator((List<IPeak>)chromatogramSelection.getChromatogram().getPeaks(), peakResolutionResult);
		IMeasurementResult<?> measurementResult = new MeasurementResult(Messages.peakResolution, //
				"org.eclipse.chemclipse.chromatogram.xxd.calculator.peak.resolution", // //$NON-NLS-1$
				Messages.peakResolutionDescription, peakResolutionResult);
		chromatogramSelection.getChromatogram().addMeasurementResult(measurementResult);
		processingInfo.setProcessingResult(peakResolutionResult);
		return processingInfo;
	}

	void applyCalculator(List<IPeak> peaks, PeakResolutionResult peakResolutionResult) {

		Iterator<IPeak> iterator = peaks.iterator();
		IPeak current = iterator.next();
		while(iterator.hasNext()) {
			IPeak next = iterator.next();
			peakResolutionResult.getPeakResolutions().add(new PeakResolution(current, next));
			current = next;
		}
	}
}