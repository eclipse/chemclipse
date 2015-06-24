/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;

public abstract class AbstractQuantitationPeaksMSD implements IQuantitationPeaksMSD {

	private static final Logger logger = Logger.getLogger(AbstractQuantitationPeaksMSD.class);
	private String concentrationUnit;
	private List<IQuantitationPeakMSD> quantitationPeaks;

	public AbstractQuantitationPeaksMSD(String concentrationUnit) {

		this.concentrationUnit = concentrationUnit;
		quantitationPeaks = new ArrayList<IQuantitationPeakMSD>();
	}

	@Override
	public List<IQuantitationPeakMSD> getQuantitationPeaks() {

		return quantitationPeaks;
	}

	@Override
	public void addQuantitationPeak(IQuantitationPeakMSD quantitationPeakMSD) {

		if(quantitationPeakMSD != null && concentrationUnit.equals(quantitationPeakMSD.getConcentrationUnit())) {
			quantitationPeaks.add(quantitationPeakMSD);
		} else {
			logger.warn("The IQuantitationPeakMSD cause it has a different concentration unit.");
		}
	}

	@Override
	public void removeQuantitationPeak(IQuantitationPeakMSD quantitationPeakMSD) {

		quantitationPeaks.remove(quantitationPeakMSD);
	}
}
