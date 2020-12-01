/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.calculator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.implementation.QuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignals;
import org.eclipse.chemclipse.model.quantitation.IResponseSignals;
import org.eclipse.chemclipse.model.quantitation.QuantitationSupport;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.EvaluationException;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.equations.QuadraticEquation;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class QuantitationCalculatorMSD implements IQuantitationCalculatorMSD {

	private static final Logger logger = Logger.getLogger(QuantitationCalculatorMSD.class);

	@Override
	public List<IQuantitationEntry> calculateQuantitationResults(IPeak peak, Set<IQuantitationCompound> quantitationCompounds, IProcessingInfo<?> processingInfo) {

		List<IQuantitationEntry> quantitationEntries = new ArrayList<IQuantitationEntry>();
		for(IQuantitationCompound quantitationCompound : quantitationCompounds) {
			try {
				List<IQuantitationEntry> quantitationEntriesPeak = calculateQuantitationResults(peak, quantitationCompound);
				quantitationEntries.addAll(quantitationEntriesPeak);
			} catch(Exception e) {
				processingInfo.addMessage(new ProcessingMessage(MessageType.WARN, "ChemClipse Quantitation", "Something has gone wrong to quantify the peak: " + peak + " using the quantitation compound: " + quantitationCompound));
				logger.warn(e);
			}
		}
		return quantitationEntries;
	}

	@Override
	public List<IQuantitationEntry> calculateQuantitationResults(IPeak peak, IQuantitationCompound quantitationCompound) throws EvaluationException {

		if(peak == null || quantitationCompound == null) {
			throw new EvaluationException("Peak and QuantitationCompound must be not null.");
		}
		/*
		 * Get the peak area.
		 * And check if the correct m/z values or TIC have been integrated.
		 */
		if(peak.getIntegratedArea() <= 0) {
			logger.warn("Quantitation - peak area is 0: " + peak);
			throw new EvaluationException("The peak area must be greater than 0.");
		}
		QuantitationSupport integrationQuantitationSupport = new QuantitationSupport(peak);
		/*
		 * Check whether to use TIC or XIC values.
		 */
		if(quantitationCompound.isUseTIC()) {
			/*
			 * TIC
			 */
			if(integrationQuantitationSupport.validateTIC()) {
				return getQuantitationEntriesTIC(quantitationCompound, peak);
			} else {
				throw new EvaluationException("The peak integration entries (m/z - abundance) do not match with the quantitation TIC ion. See log file.");
			}
		} else {
			/*
			 * XIC
			 */
			IQuantitationSignals quantitationSignals = quantitationCompound.getQuantitationSignals();
			List<Double> selectedQuantitationIons = quantitationSignals.getSelectedSignals();
			if(integrationQuantitationSupport.validateXIC(selectedQuantitationIons)) {
				if(peak instanceof IPeakMSD) {
					IPeakMSD peakMSD = (IPeakMSD)peak;
					return getQuantitationEntriesXIC(quantitationCompound, peakMSD, selectedQuantitationIons, integrationQuantitationSupport);
				} else {
					throw new EvaluationException("The peak is not of type peakMSD.");
				}
			} else {
				throw new EvaluationException("The peak integration entries (m/z - abundance) do not match with the quantitation XIC ions. See log file.");
			}
		}
	}

	/**
	 * TIC
	 * 
	 * @param quantitationCompound
	 * @param integratedArea
	 * @param isZeroCrossing
	 * @return List<IQuantitationEntryMSD>
	 */
	private List<IQuantitationEntry> getQuantitationEntriesTIC(IQuantitationCompound quantitationCompound, IPeak peak) {

		List<IQuantitationEntry> quantitationEntries = new ArrayList<IQuantitationEntry>();
		double integratedArea = peak.getIntegratedArea();
		double signal = ISignal.TOTAL_INTENSITY;
		IQuantitationEntry quantitationEntry = getQuantitationEntry(signal, quantitationCompound, integratedArea);
		quantitationEntries.add(quantitationEntry);
		//
		return quantitationEntries;
	}

	/**
	 * XIC
	 * 
	 * @param quantitationCompound
	 * @param integratedArea
	 * @param isZeroCrossing
	 * @return List<IQuantitationEntryMSD>
	 */
	private List<IQuantitationEntry> getQuantitationEntriesXIC(IQuantitationCompound quantitationCompound, IPeakMSD peak, List<Double> selectedQuantitationIons, QuantitationSupport integrationQuantitationSupport) {

		List<IQuantitationEntry> quantitationEntries = new ArrayList<IQuantitationEntry>();
		//
		IScanMSD massSpectrum = peak.getExtractedMassSpectrum();
		IExtractedIonSignal extractedIonSignal = massSpectrum.getExtractedIonSignal();
		float totalSignalMassSpectrum = extractedIonSignal.getTotalSignal();
		/*
		 * Parse all ions that shall be used for quantitation.
		 */
		for(double ion : selectedQuantitationIons) {
			/*
			 * TODO merge algorithm with AbstractQuantitationCompoundMSD
			 */
			double integratedArea = integrationQuantitationSupport.getIntegrationArea(ion);
			float abundance = extractedIonSignal.getAbundance((int)ion); // TODO cast ion to int (only for nominal mass spectra)
			IQuantitationEntry quantitationEntry;
			/*
			 * It makes a difference if the TIC or the XIC signal is integrated.
			 */
			if(integrationQuantitationSupport.isTotalSignalIntegrated()) {
				/*
				 * If the TIC was used, the m/z value has only a percentage area of the integrated TIC area.
				 */
				float percentageIonAbundance = (float)((1.0d / totalSignalMassSpectrum) * abundance);
				quantitationEntry = getQuantitationEntry(ion, quantitationCompound, integratedArea * percentageIonAbundance);
			} else {
				/*
				 * If the selected ion was used, take the integrated area of the ion.
				 */
				quantitationEntry = getQuantitationEntry(ion, quantitationCompound, integratedArea);
			}
			quantitationEntries.add(quantitationEntry);
		}
		//
		return quantitationEntries;
	}

	/**
	 * Calculates the concentration and creates a quantitation entry.
	 * 
	 * @param signal
	 * @param quantitationCompound
	 * @param integratedArea
	 * @param isZeroCrossing
	 * @return IQuantitationEntryMSD
	 */
	private IQuantitationEntry getQuantitationEntry(double signal, IQuantitationCompound quantitationCompound, double integratedArea) {

		String name = quantitationCompound.getName();
		String chemicalClass = quantitationCompound.getChemicalClass();
		String concentrationUnit = quantitationCompound.getConcentrationUnit();
		boolean isCrossZero = quantitationCompound.isCrossZero();
		double concentration = 0.0d;
		String description = "";
		//
		CalibrationMethod calibrationMethod = quantitationCompound.getCalibrationMethod();
		IResponseSignals responseSignals = quantitationCompound.getResponseSignals();
		double minResponse = responseSignals.getMinResponseValue(signal);
		double maxResponse = responseSignals.getMaxResponseValue(signal);
		//
		switch(calibrationMethod) {
			case LINEAR:
				/*
				 * Only warn if it's outside min/max response.
				 */
				LinearEquation linearEquation = responseSignals.getLinearEquation(signal, isCrossZero);
				concentration = linearEquation.calculateX(integratedArea);
				if(integratedArea < minResponse) {
					description = getDescriptionResponse(integratedArea, minResponse, "< min");
				} else if(integratedArea > maxResponse) {
					description = getDescriptionResponse(integratedArea, maxResponse, "> max");
				}
				break;
			case QUADRATIC:
				/*
				 * The quadratic equation could lead to two results.
				 * Select the result that is closer to the average value.
				 */
				double factorAverage = quantitationCompound.getResponseSignals().getAverageFactor(signal, isCrossZero);
				double concentrationAverage = factorAverage * integratedArea;
				/*
				 * Don't quantify if it's outside min/max response.
				 */
				QuadraticEquation quadraticEquation = responseSignals.getQuadraticEquation(signal, isCrossZero);
				if(integratedArea < minResponse) {
					description = getDescriptionResponse(integratedArea, minResponse, "< min");
				} else if(integratedArea > maxResponse) {
					description = getDescriptionResponse(integratedArea, maxResponse, "> max");
				} else {
					double concentration1 = quadraticEquation.calculateX(integratedArea, true);
					double concentration2 = quadraticEquation.calculateX(integratedArea, false);
					double delta1 = Math.abs(concentration1 - concentrationAverage);
					double delta2 = Math.abs(concentration2 - concentrationAverage);
					concentration = (delta1 < delta2) ? concentration1 : concentration2;
				}
				break;
			case AVERAGE:
				double factor = responseSignals.getAverageFactor(signal, isCrossZero);
				concentration = factor * integratedArea;
				break;
			case ISTD:
				/*
				 * Handled separately.
				 */
				break;
			default:
				break;
		}
		//
		IQuantitationEntry quantitationEntry = new QuantitationEntry(name, concentration, concentrationUnit, integratedArea);
		quantitationEntry.setSignal(signal);
		quantitationEntry.setCalibrationMethod(calibrationMethod.toString());
		quantitationEntry.setUsedCrossZero(isCrossZero);
		quantitationEntry.setChemicalClass(chemicalClass);
		quantitationEntry.setDescription(description);
		//
		return quantitationEntry;
	}

	private String getDescriptionResponse(double integratedArea, double response, String definition) {

		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0E0");
		StringBuilder builder = new StringBuilder();
		builder.append("The integrated area '");
		builder.append(decimalFormat.format(integratedArea));
		builder.append("' is ");
		builder.append(definition); // < min or > max
		builder.append(" response '");
		builder.append(decimalFormat.format(response));
		builder.append("'.");
		return builder.toString();
	}
}
