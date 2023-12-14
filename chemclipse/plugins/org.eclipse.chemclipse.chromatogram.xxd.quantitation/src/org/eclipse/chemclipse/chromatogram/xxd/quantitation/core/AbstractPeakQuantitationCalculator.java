/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.quantitation.core;

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

public abstract class AbstractPeakQuantitationCalculator {

	public boolean doQuantify(IPeak peakToQuantify, String nameOfStandard) {

		boolean doQuantify = false;
		List<String> quantitationReferences = peakToQuantify.getQuantitationReferences();
		if(quantitationReferences.isEmpty() || quantitationReferences.contains(nameOfStandard)) {
			doQuantify = true;
		}
		return doQuantify;
	}

	public boolean isIdentifierMatch(IPeak peak, String nameOfQuantitationCompound) {

		Set<IIdentificationTarget> targets = peak.getTargets();
		for(IIdentificationTarget target : targets) {
			if(target.getLibraryInformation().getName().equals(nameOfQuantitationCompound)) {
				return true;
			}
		}
		//
		return false;
	}

	public boolean isAreaValid(IPeak peakToQuantify, IPeak peakISTD) {

		double peakAreaQuantify = peakToQuantify.getIntegratedArea();
		double peakAreaISTD = peakISTD.getIntegratedArea();
		//
		return (peakAreaQuantify > 0 && peakAreaISTD > 0);
	}
}
