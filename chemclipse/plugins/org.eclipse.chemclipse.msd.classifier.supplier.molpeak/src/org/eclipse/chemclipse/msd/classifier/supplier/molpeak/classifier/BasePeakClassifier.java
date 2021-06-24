/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - Adjust to new API
 * Matthias Mailänder - work together with other targets
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.classifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.identifier.BasePeakIdentifier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model.ILigninRatios;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model.LigninRatios;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

public class BasePeakClassifier {

	public static boolean areClassificationsAvailable(List<IPeakMSD> peaks) {

		for(IPeak peak : peaks) {
			for(IIdentificationTarget identificationTarget : peak.getTargets()) {
				if(BasePeakClassifier.markerIsAvailable(identificationTarget)) {
					return true;
				}
			}
		}
		//
		return false;
	}

	public static boolean arePeaksIntegrated(List<IPeakMSD> peaks) {

		for(IPeak peak : peaks) {
			if(peak.getIntegratedArea() == 0) {
				return false;
			}
		}
		//
		return true;
	}

	public static List<IPeakMSD> getPeaks(IChromatogramSelection<?, ?> chromatogramSelection) {

		List<IPeakMSD> peaks = new ArrayList<>();
		//
		IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
		for(IPeak peak : chromatogram.getPeaks()) {
			if(peak instanceof IPeakMSD) {
				peaks.add((IPeakMSD)peak);
			}
		}
		//
		return peaks;
	}

	public static boolean markerIsAvailable(IIdentificationTarget identificationTarget) {

		if(identificationTarget != null) {
			String name = identificationTarget.getLibraryInformation().getName();
			if(name.equals(BasePeakIdentifier.SYRINGYL)) {
				return true;
			} else if(name.equals(BasePeakIdentifier.GUAIACYL)) {
				return true;
			} else if(name.equals(BasePeakIdentifier.PHYDROXYPHENYL)) {
				return true;
			} else if(name.equals(BasePeakIdentifier.CARBOHYDRATE)) {
				return true;
			}
		}
		//
		return false;
	}

	public static ILigninRatios calculateLigninRatios(IChromatogramSelection<?, ?> chromatogramSelection) {

		ILigninRatios ligninRatios = new LigninRatios();
		//
		double counterS = 0;
		double counterG = 0;
		double counterH = 0;
		double counterC = 0;
		double counterU = 0;
		//
		List<IPeakMSD> peaks = getPeaks(chromatogramSelection);
		boolean countArea = arePeaksIntegrated(peaks);
		/*
		 * Count (either area or just the occurrence)
		 */
		for(IPeak peak : peaks) {
			double count = countArea ? peak.getIntegratedArea() : 1;
			exitloop:
			for(IIdentificationTarget peakTarget : peak.getTargets()) {
				if(!peakTarget.getIdentifier().equals(BasePeakIdentifier.IDENTIFIER)) {
					continue;
				}
				String name = peakTarget.getLibraryInformation().getName();
				if(name.equals(BasePeakIdentifier.SYRINGYL)) {
					counterS += count;
					break exitloop;
				} else if(name.equals(BasePeakIdentifier.GUAIACYL)) {
					counterG += count;
					break exitloop;
				} else if(name.equals(BasePeakIdentifier.PHYDROXYPHENYL)) {
					counterH += count;
					break exitloop;
				} else if(name.equals(BasePeakIdentifier.CARBOHYDRATE)) {
					counterC += count;
					break exitloop;
				} else {
					counterU += count;
					break exitloop;
				}
			}
		}
		/*
		 * Statistics.
		 */
		double size = counterS + counterG + counterH + counterC + counterU;
		if(size > 0) {
			Map<String, Double> resultMap = ligninRatios.getResults();
			resultMap.put(BasePeakIdentifier.SYRINGYL, counterS / size * 100);
			resultMap.put(BasePeakIdentifier.GUAIACYL, counterG / size * 100);
			resultMap.put(BasePeakIdentifier.PHYDROXYPHENYL, counterH / size * 100);
			resultMap.put(BasePeakIdentifier.CARBOHYDRATE, counterC / size * 100);
			resultMap.put("No Match", counterU / size * 100);
		}
		//
		return ligninRatios;
	}
}
