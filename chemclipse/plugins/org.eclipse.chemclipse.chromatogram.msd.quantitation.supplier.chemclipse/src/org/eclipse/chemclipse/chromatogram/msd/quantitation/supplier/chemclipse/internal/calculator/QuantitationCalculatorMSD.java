/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationSignalsMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IntegrationQuantitationSupport;
import org.eclipse.chemclipse.msd.model.exceptions.EvaluationException;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationEntryMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.numeric.equations.QuadraticEquation;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;

public class QuantitationCalculatorMSD implements IQuantitationCalculatorMSD {

	private static final Logger logger = Logger.getLogger(QuantitationCalculatorMSD.class);

	@Override
	public List<IQuantitationEntryMSD> calculateQuantitationResults(IPeak peak, List<IQuantitationCompoundMSD> quantitationCompounds, IProcessingInfo processingInfo) {

		List<IQuantitationEntryMSD> quantitationEntries = new ArrayList<IQuantitationEntryMSD>();
		for(IQuantitationCompoundMSD quantitationCompound : quantitationCompounds) {
			try {
				List<IQuantitationEntryMSD> quantitationEntriesPeak = calculateQuantitationResults(peak, quantitationCompound);
				quantitationEntries.addAll(quantitationEntriesPeak);
			} catch(Exception e) {
				processingInfo.addMessage(new ProcessingMessage(MessageType.WARN, "ChemClipse Quantitation", "Something has gone wrong to quantify the peak: " + peak + " using the quantitation compound: " + quantitationCompound));
				logger.warn(e);
			}
		}
		return quantitationEntries;
	}

	@Override
	public List<IQuantitationEntryMSD> calculateQuantitationResults(IPeak peak, IQuantitationCompoundMSD quantitationCompound) throws EvaluationException {

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
		IntegrationQuantitationSupport integrationQuantitationSupport = new IntegrationQuantitationSupport(peak);
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
			IQuantitationSignalsMSD quantitationSignals = quantitationCompound.getQuantitationSignalsMSD();
			List<Double> selectedQuantitationIons = quantitationSignals.getSelectedIons();
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
	private List<IQuantitationEntryMSD> getQuantitationEntriesTIC(IQuantitationCompoundMSD quantitationCompound, IPeak peak) {

		List<IQuantitationEntryMSD> quantitationEntries = new ArrayList<IQuantitationEntryMSD>();
		double integratedArea = peak.getIntegratedArea();
		/*
		 * TODO merge with AbstractQuantitationCompoundMSD
		 */
		double ion = AbstractIon.TIC_ION;
		IQuantitationEntryMSD quantitationEntry = getQuantitationEntry(ion, quantitationCompound, integratedArea);
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
	private List<IQuantitationEntryMSD> getQuantitationEntriesXIC(IQuantitationCompoundMSD quantitationCompound, IPeakMSD peak, List<Double> selectedQuantitationIons, IntegrationQuantitationSupport integrationQuantitationSupport) {

		List<IQuantitationEntryMSD> quantitationEntries = new ArrayList<IQuantitationEntryMSD>();
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
			IQuantitationEntryMSD quantitationEntry;
			/*
			 * It makes a difference if the TIC or the XIC signal is integrated.
			 */
			if(integrationQuantitationSupport.isTheTotalSignalIntegrated()) {
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
	 * @param ion
	 * @param quantitationCompound
	 * @param integratedArea
	 * @param isZeroCrossing
	 * @return IQuantitationEntryMSD
	 */
	private IQuantitationEntryMSD getQuantitationEntry(double ion, IQuantitationCompoundMSD quantitationCompound, double integratedArea) {

		String name = quantitationCompound.getName();
		String chemicalClass = quantitationCompound.getChemicalClass();
		String concentrationUnit = quantitationCompound.getConcentrationUnit();
		boolean isCrossZero = quantitationCompound.isCrossZero();
		double concentration = 0.0d;
		//
		CalibrationMethod calibrationMethod = quantitationCompound.getCalibrationMethod();
		switch(calibrationMethod) {
			case LINEAR:
				LinearEquation linearEquation = quantitationCompound.getConcentrationResponseEntriesMSD().getLinearEquation(ion, isCrossZero);
				concentration = linearEquation.calculateX(integratedArea);
				break;
			case QUADRATIC:
				/*
				 * The quadratic equation could lead to two results.
				 * Select the result that is closer to the average value.
				 */
				double factorAverage = quantitationCompound.getConcentrationResponseEntriesMSD().getAverageFactor(ion, isCrossZero);
				double concentrationAverage = factorAverage * integratedArea;
				//
				QuadraticEquation quadraticEquation = quantitationCompound.getConcentrationResponseEntriesMSD().getQuadraticEquation(ion, isCrossZero);
				double concentration1 = quadraticEquation.calculateX(integratedArea, true);
				double concentration2 = quadraticEquation.calculateX(integratedArea, false);
				double delta1 = Math.abs(concentration1 - concentrationAverage);
				double delta2 = Math.abs(concentration2 - concentrationAverage);
				//
				concentration = (delta1 < delta2) ? concentration1 : concentration2;
				break;
			case AVERAGE:
				double factor = quantitationCompound.getConcentrationResponseEntriesMSD().getAverageFactor(ion, isCrossZero);
				concentration = factor * integratedArea;
				break;
			case ISTD:
				/*
				 * Handles separately.
				 */
				break;
			default:
				break;
		}
		//
		IQuantitationEntryMSD quantitationEntryMSD = new QuantitationEntryMSD(name, concentration, concentrationUnit, integratedArea, ion);
		quantitationEntryMSD.setCalibrationMethod(calibrationMethod.toString());
		quantitationEntryMSD.setUsedCrossZero(isCrossZero);
		quantitationEntryMSD.setChemicalClass(chemicalClass);
		//
		return quantitationEntryMSD;
	}
}
