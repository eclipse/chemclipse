/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.settings.IPeakQuantifierSettings;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.implementation.QuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationEntryMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakQuantitationCalculatorISTD extends AbstractPeakQuantitationCalculator {

	@SuppressWarnings("rawtypes")
	public IProcessingInfo quantifySelectedPeak(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		List<? extends IPeak> internalStandardPeaks = getInternalStandardPeaks(chromatogram);
		IPeak peakToQuantify = chromatogramSelection.getSelectedPeak();
		//
		quantifyPeak(internalStandardPeaks, peakToQuantify);
		//
		return processingInfo;
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo quantifyAllPeaks(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		List<? extends IPeak> internalStandardPeaks = getInternalStandardPeaks(chromatogram);
		List<? extends IPeak> peaksToQuantify = getPeaksToQuantify(chromatogramSelection);
		//
		for(IPeak peakToQuantify : peaksToQuantify) {
			quantifyPeak(internalStandardPeaks, peakToQuantify);
		}
		//
		return processingInfo;
	}

	public IProcessingInfo quantify(List<IPeak> peaks, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		/*
		 * Collect the internal standards.
		 */
		List<IPeak> internalStandardPeaks = new ArrayList<>();
		for(IPeak peak : peaks) {
			if(peak.getInternalStandards().size() > 0) {
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
						double responseFactor = internalStandard.getResponseFactor();
						String chemicalClass = internalStandard.getChemicalClass();
						double integratedArea = peakToQuantify.getIntegratedArea();
						double concentrationCalculated = ((concentration / peakAreaISTD) * integratedArea) * responseFactor;
						//
						if(peakToQuantify instanceof IChromatogramPeakMSD) {
							/*
							 * MSD
							 */
							IQuantitationEntryMSD quantitationEntryMSD = new QuantitationEntryMSD(nameOfStandard, concentrationCalculated, concentrationUnit, integratedArea, IIon.TIC_ION);
							quantitationEntryMSD.setCalibrationMethod(CalibrationMethod.ISTD.toString());
							quantitationEntryMSD.setUsedCrossZero(false);
							quantitationEntryMSD.setChemicalClass(chemicalClass);
							peakToQuantify.addQuantitationEntry(quantitationEntryMSD);
						} else if(peakToQuantify instanceof IChromatogramPeakCSD || peakToQuantify instanceof IChromatogramPeakWSD) {
							/*
							 * CSD/WSD
							 */
							IQuantitationEntry quantitationEntry = new QuantitationEntry(nameOfStandard, concentrationCalculated, concentrationUnit, integratedArea);
							quantitationEntry.setCalibrationMethod(CalibrationMethod.ISTD.toString());
							quantitationEntry.setUsedCrossZero(false);
							quantitationEntry.setChemicalClass(chemicalClass);
							peakToQuantify.addQuantitationEntry(quantitationEntry);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private List<? extends IPeak> getInternalStandardPeaks(IChromatogram chromatogram) {

		if(chromatogram != null) {
			if(chromatogram instanceof IChromatogramMSD) {
				/*
				 * MSD
				 */
				List<IChromatogramPeakMSD> internalStandardPeaks = new ArrayList<IChromatogramPeakMSD>();
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				for(IChromatogramPeakMSD peak : chromatogramMSD.getPeaks()) {
					if(peak.getInternalStandards().size() > 0) {
						internalStandardPeaks.add(peak);
					}
				}
				return internalStandardPeaks;
			} else if(chromatogram instanceof IChromatogramCSD) {
				/*
				 * CSD
				 */
				List<IChromatogramPeakCSD> internalStandardPeaks = new ArrayList<IChromatogramPeakCSD>();
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				for(IChromatogramPeakCSD peak : chromatogramCSD.getPeaks()) {
					if(peak.getInternalStandards().size() > 0) {
						internalStandardPeaks.add(peak);
					}
				}
				return internalStandardPeaks;
			}
		}
		/*
		 * No peak was found.
		 */
		return new ArrayList<IPeak>();
	}

	@SuppressWarnings("rawtypes")
	private List<IPeak> getPeaksToQuantify(IChromatogramSelection chromatogramSelection) {

		List<IPeak> peaksToQuantify = new ArrayList<IPeak>();
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			/*
			 * MSD
			 */
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			for(IChromatogramPeakMSD peak : chromatogramSelectionMSD.getChromatogramMSD().getPeaks(chromatogramSelectionMSD)) {
				peaksToQuantify.add(peak);
			}
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			/*
			 * CSD
			 */
			IChromatogramSelectionCSD chromatogramSelectionCSD = (IChromatogramSelectionCSD)chromatogramSelection;
			for(IChromatogramPeakCSD peak : chromatogramSelectionCSD.getChromatogramCSD().getPeaks(chromatogramSelectionCSD)) {
				peaksToQuantify.add(peak);
			}
		}
		//
		return peaksToQuantify;
	}
}
