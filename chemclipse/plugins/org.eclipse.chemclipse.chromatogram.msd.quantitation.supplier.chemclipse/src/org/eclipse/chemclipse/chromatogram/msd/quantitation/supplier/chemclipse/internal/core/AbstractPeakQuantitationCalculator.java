/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;

public abstract class AbstractPeakQuantitationCalculator {

	public boolean doQuantify(IPeak peakToQuantify, String nameOfStandard) {

		boolean doQuantify = false;
		List<String> quantitationReferences = peakToQuantify.getQuantitationReferences();
		if(quantitationReferences.size() == 0 || quantitationReferences.contains(nameOfStandard)) {
			doQuantify = true;
		}
		return doQuantify;
	}

	public boolean isAreaValid(IPeak peakToQuantify, IPeak peakISTD) {

		double peakAreaQuantify = peakToQuantify.getIntegratedArea();
		double peakAreaISTD = peakISTD.getIntegratedArea();
		//
		return (peakAreaQuantify > 0 && peakAreaISTD > 0);
	}
}
