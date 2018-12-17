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

import java.util.Iterator;

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.nmr.model.core.IMeasurementNMR;
import org.eclipse.chemclipse.nmr.model.core.ISignalFID;
import org.eclipse.chemclipse.nmr.model.core.ISignalNMR;
import org.eclipse.chemclipse.nmr.model.core.SignalFID;
import org.eclipse.chemclipse.nmr.model.core.SignalNMR;
import org.eclipse.chemclipse.nmr.model.selection.DataNMRSelection;
import org.eclipse.chemclipse.nmr.model.selection.IDataNMRSelection;

public class SignalExtractor implements ISignalExtractor {

	private IDataNMRSelection dataNMRSelection;

	public SignalExtractor(IDataNMRSelection scanNMR) {

		this.dataNMRSelection = scanNMR;
	}

	public SignalExtractor(IMeasurementNMR scanNMR) {

		this.dataNMRSelection = new DataNMRSelection(scanNMR);
	}

	public double[] extractSignalIntesity() {

		return dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().mapToDouble(ISignalNMR::getIntensityOfSpectrum).toArray();
	}

	@Override
	public Complex[] extractFourierTransformedData() {

		return dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getFourierTransformedData).toArray(Complex[]::new);
	}

	@Override
	public Complex[] extractPhaseCorrectedData() {

		return dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getPhaseCorrectedData).toArray(Complex[]::new);
	}

	@Override
	public Complex[] extractBaselineCorrectedData() {

		return dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getBaselineCorrectedData).toArray(Complex[]::new);
	}

	@Override
	public double[] extractFourierTransformedDataRealPart() {

		return dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getFourierTransformedData).mapToDouble(Complex::getReal).toArray();
	}

	@Override
	public double[] extractPhaseCorrectedDataRealPart() {

		return dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getPhaseCorrectedData).mapToDouble(Complex::getReal).toArray();
	}

	@Override
	public double[] extractBaselineCorrectedDataRealPart() {

		return dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().map(ISignalNMR::getBaselineCorrectedData).mapToDouble(Complex::getReal).toArray();
	}

	@Override
	public Complex[] extractIntensityPreprocessedFID() {

		return dataNMRSelection.getMeasurmentNMR().getScanFID().getSignalsFID().stream().map(s -> s.getIntensityPreprocessedFID()).toArray(Complex[]::new);
	}

	@Override
	public double[] extractIntensityPreprocessedFIDReal() {

		return dataNMRSelection.getMeasurmentNMR().getScanFID().getSignalsFID().stream().mapToDouble(s -> s.getIntensityPreprocessedFID().getReal()).toArray();
	}

	@Override
	public Complex[] extractIntensityUnprocessedFID() {

		return dataNMRSelection.getMeasurmentNMR().getScanFID().getSignalsFID().stream().map(s -> s.getIntensityUnprocessedFID()).toArray(Complex[]::new);
	}

	@Override
	public double[] extractIntensityUnprocessedFIDReal() {

		return dataNMRSelection.getMeasurmentNMR().getScanFID().getSignalsFID().stream().mapToDouble(s -> s.getIntensityUnprocessedFID().getReal()).toArray();
	}

	@Override
	public Complex[] extractIntensityProcessedFID() {

		return dataNMRSelection.getMeasurmentNMR().getScanFID().getSignalsFID().stream().map(s -> s.getIntensityProcessedFID()).toArray(Complex[]::new);
	}

	@Override
	public double[] extractIntensityProcessedFIDReal() {

		return dataNMRSelection.getMeasurmentNMR().getScanFID().getSignalsFID().stream().mapToDouble(s -> s.getIntensityProcessedFID().getReal()).toArray();
	}

	@Override
	public long[] extractAcquisitionTimeFID() {

		return dataNMRSelection.getMeasurmentNMR().getScanFID().getSignalsFID().stream().mapToLong(ISignalFID::getAcquisitionTime).toArray();
	}

	@Override
	public double[] extractChemicalShift() {

		return dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().stream().mapToDouble(ISignalNMR::getChemicalShift).toArray();
	}

	@Override
	public void storeFrequencyDomainSpectrum(Complex[] fourierTransformedData, double[] chemicalShift) {

		dataNMRSelection.getMeasurmentNMR().getScanMNR().removeAllSignalsNMR();
		for(int i = 0; i < fourierTransformedData.length; i++) {
			dataNMRSelection.getMeasurmentNMR().getScanMNR().addSignalNMR(new SignalNMR(chemicalShift[i], fourierTransformedData[i]));
		}
	}

	@Override
	public void storeTimeDomainSignal(Complex[] complexFID, long[] time) {

		dataNMRSelection.getMeasurmentNMR().getScanFID().removeAllSignalsFID();
		for(int i = 0; i < complexFID.length; i++) {
			dataNMRSelection.getMeasurmentNMR().getScanFID().addSignalFID(new SignalFID(time[i], complexFID[i]));
		}
	}

	@Override
	public void setIntensityDouble(double[] intensities) {

		Iterator<ISignalNMR> signals = dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().iterator();
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

		Iterator<ISignalNMR> signals = dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().iterator();
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

		Iterator<ISignalNMR> signals = dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().iterator();
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

		Iterator<ISignalNMR> signals = dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().iterator();
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

		dataNMRSelection.getMeasurmentNMR().getScanMNR().getSignalsNMR().forEach(signal -> signal.resetIntensityOfSpectrum());
	}

	@Override
	public void setScansFIDCorrection(double[] correction, boolean reset) {

		if(reset) {
			dataNMRSelection.getMeasurmentNMR().getScanFID().getSignalsFID().forEach(ISignalFID::resetIntensityProcessedFID);
		}
		Iterator<ISignalFID> it = dataNMRSelection.getMeasurmentNMR().getScanFID().getSignalsFID().iterator();
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

		Iterator<ISignalFID> it = dataNMRSelection.getMeasurmentNMR().getScanFID().getSignalsFID().iterator();
		int i = 0;
		while(it.hasNext()) {
			ISignalFID iSignalFID = it.next();
			iSignalFID.setIntensityPreprocessedFID(preprocessedSignal[i]);
			iSignalFID.resetIntensityProcessedFID();
			i++;
		}
	}
}
