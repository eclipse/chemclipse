/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.identifier.support;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.targets.TargetUnknownSettings;
import org.eclipse.chemclipse.model.targets.UnknownTargetBuilder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public class TargetBuilderWSD {

	private static final Logger logger = Logger.getLogger(TargetBuilderWSD.class);

	public void setPeakTargetUnknown(IPeakWSD peakWSD, String identifier, TargetUnknownSettings targetUnknownSettings) {

		try {
			IScan scan = peakWSD.getPeakModel().getPeakMaximum();
			if(scan instanceof IScanWSD unknown) {
				String traces = extractTraces(unknown, targetUnknownSettings);
				ILibraryInformation libraryInformation = UnknownTargetBuilder.getLibraryInformationUnknown(unknown, targetUnknownSettings, traces);
				IComparisonResult comparisonResult = UnknownTargetBuilder.getComparisonResultUnknown(targetUnknownSettings.getMatchQuality());
				IIdentificationTarget peakTarget = new IdentificationTarget(libraryInformation, comparisonResult);
				peakTarget.setIdentifier(identifier);
				peakWSD.getTargets().add(peakTarget);
			}
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
	}

	public void setWaveSpectrumTargetUnknown(IScanWSD unknown, String identifier, TargetUnknownSettings targetUnknownSettings) {

		try {
			String traces = extractTraces(unknown, targetUnknownSettings);
			ILibraryInformation libraryInformation = UnknownTargetBuilder.getLibraryInformationUnknown(unknown, targetUnknownSettings, traces);
			IComparisonResult comparisonResult = UnknownTargetBuilder.getComparisonResultUnknown(targetUnknownSettings.getMatchQuality());
			IIdentificationTarget waveSpectrumTarget = new IdentificationTarget(libraryInformation, comparisonResult);
			waveSpectrumTarget.setIdentifier(identifier);
			unknown.getTargets().add(waveSpectrumTarget);
		} catch(ReferenceMustNotBeNullException e) {
			logger.warn(e);
		}
	}

	private String extractTraces(IScanWSD unknown, TargetUnknownSettings targetUnknownSettings) {

		String traces = "";
		int numberWavelengths = targetUnknownSettings.getNumberTraces();
		if(numberWavelengths > 0) {
			/*
			 * wavelengths
			 */
			StringBuilder builder = new StringBuilder();
			boolean includeIntensityPercent = targetUnknownSettings.isIncludeIntensityPercent();
			//
			long sizePositive = unknown.getScanSignals().stream().filter(s -> s.getAbundance() >= 0).count();
			long sizeNegative = unknown.getScanSignals().stream().filter(s -> s.getAbundance() < 0).count();
			/*
			 * Negative, Positive, Both
			 */
			if(sizePositive == 0) {
				builder.append(extractTraces(unknown, false, includeIntensityPercent, numberWavelengths));
			} else if(sizeNegative == 0) {
				builder.append(extractTraces(unknown, true, includeIntensityPercent, numberWavelengths));
			} else {
				int numberNegative = numberWavelengths / 2;
				if(numberNegative > 0) {
					builder.append(extractTraces(unknown, false, includeIntensityPercent, numberNegative));
					builder.append(UnknownTargetBuilder.DELIMITER_TRACES);
				}
				//
				int numberPositive = numberWavelengths - numberNegative;
				builder.append(extractTraces(unknown, true, includeIntensityPercent, numberPositive));
			}
			//
			traces = builder.toString();
		}
		//
		return traces;
	}

	private String extractTraces(IScanWSD unknown, boolean positive, boolean includeIntensityPercent, int numberWavelengths) {

		StringBuilder builder = new StringBuilder();
		final List<IScanSignalWSD> signalsSorted;
		//
		if(positive) {
			signalsSorted = new ArrayList<>(unknown.getScanSignals().stream().filter(s -> s.getAbundance() >= 0).collect(Collectors.toList()));
			Collections.sort(signalsSorted, (s1, s2) -> Float.compare(s2.getAbundance(), s1.getAbundance()));
		} else {
			signalsSorted = new ArrayList<>(unknown.getScanSignals().stream().filter(s -> s.getAbundance() < 0).collect(Collectors.toList()));
			Collections.sort(signalsSorted, (s1, s2) -> Float.compare(s1.getAbundance(), s2.getAbundance()));
		}
		//
		int size = (signalsSorted.size() >= numberWavelengths) ? numberWavelengths : signalsSorted.size();
		double maxIntensity = !signalsSorted.isEmpty() ? signalsSorted.get(0).getAbundance() : 0;
		final double factorMax;
		if(positive) {
			factorMax = maxIntensity > 0 ? (100 / maxIntensity) : 0;
		} else {
			factorMax = maxIntensity < 0 ? (-100 / maxIntensity) : 0;
		}
		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0");
		/*
		 * Negative
		 */
		for(int i = 0; i < size; i++) {
			IScanSignalWSD signal = signalsSorted.get(i);
			builder.append((int)signal.getWavelength());
			if(includeIntensityPercent) {
				builder.append(UnknownTargetBuilder.DELIMITER_INTENSITY);
				double percent = factorMax * signal.getAbundance();
				builder.append(decimalFormat.format(percent));
			}
			/*
			 * Next entry available?
			 */
			if(i < size - 1) {
				builder.append(UnknownTargetBuilder.DELIMITER_TRACES);
			}
		}
		//
		return builder.toString();
	}
}
