/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.classifier;

import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model.ILigninRatios;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model.LigninRatios;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public class BasePeakClassifier {

	private static final String SYRINGYL = "Syringyl";
	private static final String GUAIACYL = "Guaiacyl";
	private static final String PHYDROXYPHENYL = "p-Hydroxyphenyl";
	private static final String CARBOHYDRATE = "Carbohydrate";

	public ILigninRatios calculateLigninRatios(IChromatogramSelectionMSD chromatogramSelection) {

		ILigninRatios ligninRatios = new LigninRatios();
		//
		int counterS = 0;
		int counterG = 0;
		int counterL = 0;
		int counterC = 0;
		//
		IChromatogramMSD chromatogramMSD = chromatogramSelection.getChromatogramMSD();
		List<IChromatogramPeakMSD> peaks = chromatogramMSD.getPeaks();
		for(IChromatogramPeakMSD peak : peaks) {
			for(IPeakTarget peakTarget : peak.getTargets()) {
				String name = peakTarget.getLibraryInformation().getName();
				if(name.equals(SYRINGYL)) {
					counterS += (int)peak.getIntegratedArea();
				} else if(name.equals(GUAIACYL)) {
					counterG += (int)peak.getIntegratedArea();
				} else if(name.equals(PHYDROXYPHENYL)) {
					counterL += (int)peak.getIntegratedArea();
				} else if(name.equals(CARBOHYDRATE)) {
					counterC += (int)peak.getIntegratedArea();
				}
			}
		}
		//
		double size = counterS + counterG + counterL + counterC;
		if(size > 0) {
			Map<String, Double> resultMap = ligninRatios.getResults();
			resultMap.put(SYRINGYL, counterS / size * 100);
			resultMap.put(GUAIACYL, counterG / size * 100);
			resultMap.put(PHYDROXYPHENYL, counterL / size * 100);
			resultMap.put(CARBOHYDRATE, counterC / size * 100);
		}
		//
		return ligninRatios;
	}
}
