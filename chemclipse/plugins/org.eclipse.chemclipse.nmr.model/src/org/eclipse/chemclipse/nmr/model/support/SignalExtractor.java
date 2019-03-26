/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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

import java.util.Iterator;

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.nmr.model.core.IMeasurementNMR;
import org.eclipse.chemclipse.nmr.model.core.ISignalFID;
import org.eclipse.chemclipse.nmr.model.core.ISignalNMR;
import org.eclipse.chemclipse.nmr.model.core.SignalFID;
import org.eclipse.chemclipse.nmr.model.core.SignalNMR;
import org.eclipse.chemclipse.nmr.model.selection.IDataNMRSelection;

@Deprecated
public class SignalExtractor implements ISignalExtractor {

	private IDataNMRSelection nmrSelection;
	private IMeasurementNMR measurementNMR;

	public SignalExtractor(IDataNMRSelection nmrSelection) {
		this.nmrSelection = nmrSelection;
	}

	public SignalExtractor(IMeasurementNMR measurementNMR) {
		this.measurementNMR = measurementNMR;
	}

	public double[] extractSignalIntesity() {

		return getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().mapToDouble(ISignalNMR::getIntensityOfSpectrum).toArray();
	}

	@Override
	public Complex[] extractFourierTransformedData() {

		return getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getFourierTransformedData).toArray(Complex[]::new);
	}

	@Override
	public Complex[] extractPhaseCorrectedData() {

		return getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getPhaseCorrectedData).toArray(Complex[]::new);
	}

	@Override
	public Complex[] extractBaselineCorrectedData() {

		return getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getBaselineCorrectedData).toArray(Complex[]::new);
	}

	@Override
	public double[] extractFourierTransformedDataRealPart() {

		return getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getFourierTransformedData).mapToDouble(Complex::getReal).toArray();
	}

	@Override
	public double[] extractPhaseCorrectedDataRealPart() {

		return getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getPhaseCorrectedData).mapToDouble(Complex::getReal).toArray();
	}

	@Override
	public double[] extractBaselineCorrectedDataRealPart() {

		return getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getBaselineCorrectedData).mapToDouble(Complex::getReal).toArray();
	}

	@Override
	public Complex[] extractIntensityPreprocessedFID() {

		return getMeasurmentNMR().getScanFID().getSignalsFID().stream().map(s -> s.getIntensityPreprocessedFID()).toArray(Complex[]::new);
	}

	@Override
	public double[] extractIntensityPreprocessedFIDReal() {

		return getMeasurmentNMR().getScanFID().getSignalsFID().stream().mapToDouble(s -> s.getIntensityPreprocessedFID().getReal()).toArray();
	}

	@Override
	public Complex[] extractIntensityUnprocessedFID() {

		return getMeasurmentNMR().getScanFID().getSignalsFID().stream().map(s -> s.getIntensityUnprocessedFID()).toArray(Complex[]::new);
	}

	@Override
	public double[] extractIntensityUnprocessedFIDReal() {

		return getMeasurmentNMR().getScanFID().getSignalsFID().stream().mapToDouble(s -> s.getIntensityUnprocessedFID().getReal()).toArray();
	}

	@Override
	public Complex[] extractIntensityProcessedFID() {

		return getMeasurmentNMR().getScanFID().getSignalsFID().stream().map(s -> s.getIntensityProcessedFID()).toArray(Complex[]::new);
	}

	@Override
	public double[] extractIntensityProcessedFIDReal() {

		return getMeasurmentNMR().getScanFID().getSignalsFID().stream().mapToDouble(s -> s.getIntensityProcessedFID().getReal()).toArray();
	}

	@Override
	public long[] extractAcquisitionTimeFID() {

		return getMeasurmentNMR().getScanFID().getSignalsFID().stream().mapToLong(ISignalFID::getAcquisitionTime).toArray();
	}

	@Override
	public double[] extractChemicalShift() {

		return getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().mapToDouble(ISignalNMR::getChemicalShift).toArray();
	}

	@Override
	public void storeFrequencyDomainSpectrum(Complex[] fourierTransformedData, double[] chemicalShift) {

		getMeasurmentNMR().getScanMNR().removeAllSignalsNMR();
		for(int i = 0; i < fourierTransformedData.length; i++) {
			getMeasurmentNMR().getScanMNR().addSignalNMR(new SignalNMR(chemicalShift[i], fourierTransformedData[i]));
		}
	}

	@Override
	public void storeTimeDomainSignal(Complex[] complexFID, long[] time) {

		getMeasurmentNMR().getScanFID().removeAllSignalsFID();
		for(int i = 0; i < complexFID.length; i++) {
			getMeasurmentNMR().getScanFID().addSignalFID(new SignalFID(time[i], complexFID[i]));
		}
	}

	@Override
	public void setIntensityDouble(double[] intensities) {

		Iterator<ISignalNMR> signals = getMeasurmentNMR().getScanMNR().getSignalsNMR().iterator();
		int i = 0;
		while(signals.hasNext()) {
			ISignalNMR signal = signals.next();
			double intensity = intensities[i];
			signal.setIntensityOfSpectrum(intensity);
			i++;
		}
	}

	@Override
	public void setIntensityComplex(Complex[] intensities) {

		Iterator<ISignalNMR> signals = getMeasurmentNMR().getScanMNR().getSignalsNMR().iterator();
		int i = 0;
		while(signals.hasNext()) {
			ISignalNMR signal = signals.next();
			Complex intensity = intensities[i];
			signal.setIntensityOfSpectrum(intensity.getReal());
			i++;
		}
	}

	@Override
	public void setPhaseCorrection(Complex[] phaseCorrection, boolean resetIntesityValues) {

		Iterator<ISignalNMR> signals = getMeasurmentNMR().getScanMNR().getSignalsNMR().iterator();
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

		Iterator<ISignalNMR> signals = getMeasurmentNMR().getScanMNR().getSignalsNMR().iterator();
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

		getMeasurmentNMR().getScanMNR().getSignalsNMR().forEach(signal -> signal.resetIntensityOfSpectrum());
	}

	private IMeasurementNMR getMeasurmentNMR() {

		if(measurementNMR != null) {
			return measurementNMR;
		}
		throw new IllegalArgumentException("no measurementNMR");
	}

	@Override
	public void setScansFIDCorrection(double[] correction, boolean reset) {

		if(reset) {
			getMeasurmentNMR().getScanFID().getSignalsFID().forEach(ISignalFID::resetIntensityProcessedFID);
		}
		Iterator<ISignalFID> it = getMeasurmentNMR().getScanFID().getSignalsFID().iterator();
		int i = 0;
		while(it.hasNext()) {
			ISignalFID iSignalFID = it.next();
			Complex intensity = iSignalFID.getIntensityProcessedFID().multiply(correction[i]);
			iSignalFID.setIntensityProcessedFID(intensity);
			i++;
		}
	}

	@Override
	public void setPreprocessFID(Complex[] preprocessedSignal) {

		Iterator<ISignalFID> it = getMeasurmentNMR().getScanFID().getSignalsFID().iterator();
		int i = 0;
		while(it.hasNext()) {
			ISignalFID iSignalFID = it.next();
			iSignalFID.setIntensityPreprocessedFID(preprocessedSignal[i]);
			iSignalFID.resetIntensityProcessedFID();
			i++;
		}
	}
}
