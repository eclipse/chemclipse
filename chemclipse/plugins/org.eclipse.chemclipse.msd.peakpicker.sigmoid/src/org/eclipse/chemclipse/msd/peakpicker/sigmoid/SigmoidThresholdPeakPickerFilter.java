/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.peakpicker.sigmoid;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.AbstractMassSpectrumFilter;
import org.eclipse.chemclipse.chromatogram.msd.filter.result.IMassSpectrumFilterResult;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.model.core.IMassSpectrumPeak;
import org.eclipse.chemclipse.model.core.MassSpectrumPeak;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IStandaloneMassSpectrum;
import org.eclipse.chemclipse.msd.model.notifier.MassSpectrumUpdateNotifier;
import org.eclipse.chemclipse.msd.peakpicker.sigmoid.SigmoidThreshold.PickedPeak;
import org.eclipse.chemclipse.msd.peakpicker.sigmoid.SigmoidThreshold.Result;
import org.eclipse.chemclipse.msd.peakpicker.sigmoid.settings.SigmoidThresholdPeakPickerFilterSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.core.runtime.IProgressMonitor;

public class SigmoidThresholdPeakPickerFilter extends AbstractMassSpectrumFilter {

	private static final String DESCRIPTION = "Sigmoid Threshold Peak Picker";

	@Override
	public IProcessingInfo<IMassSpectrumFilterResult> applyFilter(List<IScanMSD> massSpectra, IMassSpectrumFilterSettings settings, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectrumFilterResult> processingInfo = validate(massSpectra, settings);
		if(processingInfo.hasErrorMessages()) {
			return processingInfo;
		}

		if(settings instanceof SigmoidThresholdPeakPickerFilterSettings sigmoidSettings) {
			for(IScanMSD massSpectrum : massSpectra) {
				if(monitor.isCanceled()) {
					break;
				}
				if(massSpectrum instanceof IStandaloneMassSpectrum standaloneMassSpectrum) {
					int numberOfIons = standaloneMassSpectrum.getNumberOfIons();
					double[] baseline = valuesOrZero(standaloneMassSpectrum.getBaseline(), numberOfIons);
					double[] noise = valuesOrZero(standaloneMassSpectrum.getNoise(), numberOfIons);
					if(baseline == null || noise == null) {
						String message = "Baseline or noise do not match the number of data points.";
						String solution = "Estimate baseline and noise again.";
						processingInfo.addMessage(new ProcessingMessage(MessageType.ERROR, DESCRIPTION, message, solution));
						continue;
					}
					if(standaloneMassSpectrum.getNoise().isEmpty() && sigmoidSettings.getNoiseFactor() > 0) {
						String message = "Noise is missing.";
						String solution = "Estimate noise first or set the noise factor to 0.";
						processingInfo.addMessage(new ProcessingMessage(MessageType.ERROR, DESCRIPTION, message, solution));
						continue;
					}
					Result result = pickPeaks(standaloneMassSpectrum, baseline, noise, sigmoidSettings);
					String message = "The sigmoid threshold peak picker has detected " + result.peaks().size() + " peaks";
					if(sigmoidSettings.getNumberOfPeaks() > 0) {
						message += " using a scaling factor of " + result.scalingFactor();
					}
					processingInfo.addMessage(new ProcessingMessage(MessageType.INFO, DESCRIPTION, message + "."));
					standaloneMassSpectrum.setDirty(true);
					MassSpectrumUpdateNotifier.update(standaloneMassSpectrum);
				}
			}
		}

		return processingInfo;
	}

	private static Result pickPeaks(IStandaloneMassSpectrum massSpectrum, double[] baseline, double[] noise, SigmoidThresholdPeakPickerFilterSettings settings) {

		List<IIon> ions = massSpectrum.getIons();
		double[] mz = ions.stream().mapToDouble(IIon::getIon).toArray();
		double[] intensity = ions.stream().mapToDouble(IIon::getAbundance).toArray();
		Result result = SigmoidThreshold.pick(mz, intensity, baseline, noise, settings);

		massSpectrum.getPeaks().clear();
		for(PickedPeak pickedPeak : result.peaks()) {
			massSpectrum.getPeaks().add(createPeak(ions.get(pickedPeak.index()), noise[pickedPeak.index()]));
		}
		return result;
	}

	/**
	 * @return null if the values do not match the data points
	 */
	private static double[] valuesOrZero(List<Double> values, int size) {

		if(values.isEmpty()) {
			return new double[size];
		}
		if(values.size() != size) {
			return null;
		}
		return values.stream().mapToDouble(Double::doubleValue).toArray();
	}

	private static IMassSpectrumPeak createPeak(IIon ion, double noise) {

		IMassSpectrumPeak peak = new MassSpectrumPeak();
		peak.setIon(ion.getIon());
		float intensity = ion.getAbundance();
		peak.setAbundance(intensity);
		if(noise > 0) {
			peak.setSignalToNoise(intensity / noise);
		}
		return peak;
	}
}
