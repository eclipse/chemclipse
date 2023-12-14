/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.quantitation.core.AbstractPeakQuantitationCalculator;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.implementation.QuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;

public class PeakQuantitationCalculatorISTD extends AbstractPeakQuantitationCalculator {

	public IProcessingInfo<Void> quantifySelectedPeak(IChromatogramSelection<?, ?> chromatogramSelection) {

		IProcessingInfo<Void> processingInfo = new ProcessingInfo<>();
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		List<? extends IPeak> internalStandardPeaks = getInternalStandardPeaks(chromatogram);
		IPeak peakToQuantify = chromatogramSelection.getSelectedPeak();
		//
		quantifyPeak(internalStandardPeaks, peakToQuantify);
		//
		return processingInfo;
	}

	public IProcessingInfo<Void> quantifyAllPeaks(IChromatogramSelection<?, ?> chromatogramSelection) {

		IProcessingInfo<Void> processingInfo = new ProcessingInfo<>();
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		List<? extends IPeak> internalStandardPeaks = getInternalStandardPeaks(chromatogram);
		List<? extends IPeak> peaksToQuantify = getPeaksToQuantify(chromatogramSelection);
		//
		for(IPeak peakToQuantify : peaksToQuantify) {
			quantifyPeak(internalStandardPeaks, peakToQuantify);
		}
		//
		return processingInfo;
	}

	public IProcessingInfo<Void> quantify(List<IPeak> peaks) {

		IProcessingInfo<Void> processingInfo = new ProcessingInfo<>();
		/*
		 * Collect the internal standards.
		 */
		List<IPeak> internalStandardPeaks = new ArrayList<>();
		for(IPeak peak : peaks) {
			if(!peak.getInternalStandards().isEmpty()) {
				internalStandardPeaks.add(peak);
			}
		}
		/*
		 * Quantify the peaks.
		 */
		for(IPeak peakToQuantify : peaks) {
			quantifyPeak(internalStandardPeaks, peakToQuantify);
		}
		//
		return processingInfo;
	}

	private void quantifyPeak(List<? extends IPeak> internalStandardPeaks, IPeak peakToQuantify) {

		for(IPeak peakISTD : internalStandardPeaks) {
			if(isAreaValid(peakToQuantify, peakISTD)) {
				for(IInternalStandard internalStandard : peakISTD.getInternalStandards()) {
					String nameOfStandard = internalStandard.getName();
					if(doQuantify(peakToQuantify, nameOfStandard)) {
						/*
						 * ISTD - PEAK
						 */
						double peakAreaISTD = peakISTD.getIntegratedArea();
						double concentration = internalStandard.getConcentration();
						String concentrationUnit = internalStandard.getConcentrationUnit();
						double compensationFactor = internalStandard.getCompensationFactor();
						String chemicalClass = internalStandard.getChemicalClass();
						double integratedArea = peakToQuantify.getIntegratedArea();
						double concentrationCalculated = ((concentration / peakAreaISTD) * integratedArea) * compensationFactor;
						//
						IQuantitationEntry quantitationEntryMSD = new QuantitationEntry(nameOfStandard, concentrationCalculated, concentrationUnit, integratedArea);
						quantitationEntryMSD.setSignal(ISignal.TOTAL_INTENSITY);
						quantitationEntryMSD.setCalibrationMethod(CalibrationMethod.ISTD.toString());
						quantitationEntryMSD.setUsedCrossZero(false);
						quantitationEntryMSD.setChemicalClass(chemicalClass);
						peakToQuantify.addQuantitationEntry(quantitationEntryMSD);
					}
				}
			}
		}
	}

	private List<? extends IPeak> getInternalStandardPeaks(IChromatogram<?> chromatogram) {

		if(chromatogram != null) {
			if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
				/*
				 * MSD
				 */
				List<IChromatogramPeakMSD> internalStandardPeaks = new ArrayList<>();
				for(IChromatogramPeakMSD peak : chromatogramMSD.getPeaks()) {
					if(!peak.getInternalStandards().isEmpty()) {
						internalStandardPeaks.add(peak);
					}
				}
				return internalStandardPeaks;
			} else if(chromatogram instanceof IChromatogramCSD chromatogramCSD) {
				/*
				 * CSD
				 */
				List<IChromatogramPeakCSD> internalStandardPeaks = new ArrayList<>();
				for(IChromatogramPeakCSD peak : chromatogramCSD.getPeaks()) {
					if(!peak.getInternalStandards().isEmpty()) {
						internalStandardPeaks.add(peak);
					}
				}
				return internalStandardPeaks;
			}
		}
		/*
		 * No peak was found.
		 */
		return new ArrayList<>();
	}

	private List<IPeak> getPeaksToQuantify(IChromatogramSelection<?, ?> chromatogramSelection) {

		List<IPeak> peaksToQuantify = new ArrayList<>();
		if(chromatogramSelection instanceof IChromatogramSelectionMSD chromatogramSelectionMSD) {
			/*
			 * MSD
			 */
			for(IChromatogramPeakMSD peak : chromatogramSelectionMSD.getChromatogram().getPeaks(chromatogramSelectionMSD)) {
				peaksToQuantify.add(peak);
			}
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD chromatogramSelectionCSD) {
			/*
			 * CSD
			 */
			for(IChromatogramPeakCSD peak : chromatogramSelectionCSD.getChromatogram().getPeaks(chromatogramSelectionCSD)) {
				peaksToQuantify.add(peak);
			}
		}
		//
		return peaksToQuantify;
	}
}
