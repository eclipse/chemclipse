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
package org.eclipse.chemclipse.model.quantitation;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.chemclipse.logging.core.Logger;

public class QuantitationPeaks extends ArrayList<IQuantitationPeak> implements IQuantitationPeaks {

	private static final long serialVersionUID = -4303659297540195715L;
	private static final Logger logger = Logger.getLogger(QuantitationPeaks.class);
	//
	private String concentrationUnit = "";

	public QuantitationPeaks(String concentrationUnit) {
		this.concentrationUnit = concentrationUnit;
	}

	@Override
	public void add(int index, IQuantitationPeak quantitationPeak) {

		if(isValid(quantitationPeak)) {
			super.add(index, quantitationPeak);
		}
	}

	@Override
	public boolean add(IQuantitationPeak quantitationPeak) {

		if(isValid(quantitationPeak)) {
			return super.add(quantitationPeak);
		} else {
			return false;
		}
	}

	@Override
	public boolean addAll(Collection<? extends IQuantitationPeak> quantitationPeaks) {

		logger.warn("Please use the add() method.");
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends IQuantitationPeak> quantitationPeaks) {

		logger.warn("Please use the add() method.");
		return false;
	}

	public boolean isValid(IQuantitationPeak quantitationPeak) {

		if(quantitationPeak != null && concentrationUnit.equals(quantitationPeak.getConcentrationUnit())) {
			return true;
		} else {
			logger.warn("Can't add the IQuantitationPeak, cause it has a different concentration unit.");
			return false;
		}
	}
}
