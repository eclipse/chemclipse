/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.support;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.nmr.model.core.IScanNMR;
import org.eclipse.chemclipse.nmr.model.core.ISignalNMR;
import org.eclipse.chemclipse.nmr.model.core.SignalNMR;

public class SignalExtractor implements ISignalExtractor {

	private IScanNMR scanNMR;

	public SignalExtractor(IScanNMR scanNMR) {

		this.scanNMR = scanNMR;
	}

	public double[] extractSignalIntesity() {

		return scanNMR.getProcessedSignals().stream().mapToDouble(ISignalNMR::getIntensity).toArray();
	}

	@Override
	public Complex[] extractFourierTransformedData() {

		return scanNMR.getProcessedSignals().stream().map(ISignalNMR::getFourierTransformedData).toArray(Complex[]::new);
	}

	@Override
	public Complex[] extractPhaseCorrectedData() {

		return scanNMR.getProcessedSignals().stream().map(ISignalNMR::getPhaseCorrectedData).toArray(Complex[]::new);
	}

	@Override
	public Complex[] extractBaselineCorrectedData() {

		return scanNMR.getProcessedSignals().stream().map(ISignalNMR::getBaselineCorrectedData).toArray(Complex[]::new);
	}

	@Override
	public double[] extractFourierTransformedDataRealPart() {

		return scanNMR.getProcessedSignals().stream().map(ISignalNMR::getFourierTransformedData).mapToDouble(Complex::getReal).toArray();
	}

	@Override
	public double[] extractPhaseCorrectedDataRealPart() {

		return scanNMR.getProcessedSignals().stream().map(ISignalNMR::getPhaseCorrectedData).mapToDouble(Complex::getReal).toArray();
	}

	@Override
	public double[] extractBaselineCorrectedDataRealPart() {

		return scanNMR.getProcessedSignals().stream().map(ISignalNMR::getBaselineCorrectedData).mapToDouble(Complex::getReal).toArray();
	}

	@Override
	public double[] extractChemicalShift() {

		return scanNMR.getProcessedSignals().stream().mapToDouble(ISignalNMR::getChemicalShift).toArray();
	}

	@Override
	public void createScans(Complex[] fourierTransformedData, double[] chemicalShift) {

		Collection<ISignalNMR> signals = scanNMR.getProcessedSignals();
		signals.clear();
		for(int i = 0; i < fourierTransformedData.length; i++) {
			signals.add(new SignalNMR(chemicalShift[i], fourierTransformedData[i]));
		}
	}

	@Override
	public void setIntesity(double[] intensities) {

		Iterator<ISignalNMR> signals = scanNMR.getProcessedSignals().iterator();
		int i = 0;
		while(signals.hasNext()) {
			ISignalNMR signal = signals.next();
			double intensity = intensities[i];
			signal.setIntensity(intensity);
			i++;
		}
	}

	@Override
	public void setIntesity(Complex[] intensities) {

		Iterator<ISignalNMR> signals = scanNMR.getProcessedSignals().iterator();
		int i = 0;
		while(signals.hasNext()) {
			ISignalNMR signal = signals.next();
			Complex intensity = intensities[i];
			signal.setIntensity(intensity.getReal());
			i++;
		}
	}

	@Override
	public void setPhaseCorrection(Complex[] phaseCorrection, boolean resetIntesityValues) {

		Iterator<ISignalNMR> signals = scanNMR.getProcessedSignals().iterator();
		int i = 0;
		while(signals.hasNext()) {
			ISignalNMR signal = signals.next();
			Complex correction = phaseCorrection[i];
			signal.setPhaseCorrection(correction);
			i++;
		}
		if(resetIntesityValues) {
			resertValues();
		}
	}

	@Override
	public void setBaselineCorrection(Complex[] baseleniCorrection, boolean resetIntensityValue) {

		Iterator<ISignalNMR> signals = scanNMR.getProcessedSignals().iterator();
		int i = 0;
		while(signals.hasNext()) {
			ISignalNMR signal = signals.next();
			Complex correction = baseleniCorrection[i];
			signal.setBaselineCorrection(correction);
			i++;
		}
		if(resetIntensityValue) {
			resertValues();
		}
	}

	private void resertValues() {

		scanNMR.getProcessedSignals().forEach(signal -> signal.resetIntesity());
	}
}
