/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.classifier;

import java.util.Map;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.identifier.BasePeakIdentifier;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model.ILigninRatios;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model.LigninRatios;

public class BasePeakClassifier {

	public static ILigninRatios calculateLigninRatios(IChromatogramSelection<?, ?> chromatogramSelection) {

		ILigninRatios ligninRatios = new LigninRatios();
		//
		double counterS = 0;
		double counterG = 0;
		double counterL = 0;
		double counterC = 0;
		//
		IChromatogram<?> chromatogramMSD = chromatogramSelection.getChromatogram();
		for(IPeak peak : chromatogramMSD.getPeaks()) {
			for(IIdentificationTarget peakTarget : peak.getTargets()) {
				String name = peakTarget.getLibraryInformation().getName();
				if(name.equals(BasePeakIdentifier.SYRINGYL)) {
					counterS += peak.getIntegratedArea();
				} else if(name.equals(BasePeakIdentifier.GUAIACYL)) {
					counterG += peak.getIntegratedArea();
				} else if(name.equals(BasePeakIdentifier.PHYDROXYPHENYL)) {
					counterL += peak.getIntegratedArea();
				} else if(name.equals(BasePeakIdentifier.CARBOHYDRATE)) {
					counterC += peak.getIntegratedArea();
				}
			}
		}
		//
		double size = counterS + counterG + counterL + counterC;
		if(size > 0) {
			Map<String, Double> resultMap = ligninRatios.getResults();
			resultMap.put(BasePeakIdentifier.SYRINGYL, counterS / size * 100);
			resultMap.put(BasePeakIdentifier.GUAIACYL, counterG / size * 100);
			resultMap.put(BasePeakIdentifier.PHYDROXYPHENYL, counterL / size * 100);
			resultMap.put(BasePeakIdentifier.CARBOHYDRATE, counterC / size * 100);
		}
		//
		return ligninRatios;
	}
}
