/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

/**
 * @author eselmeister
 */
public class IonPercentages implements IIonPercentages {

	private IScanMSD massSpectrum;
	private SortedMap<Integer, Float> ionDistribution; // ion -
														// percentage

	/**
	 * Given by the mass spectrum, a new ion distribution will be
	 * calculated.
	 * 
	 * @param massSpectrum
	 */
	public IonPercentages(IScanMSD massSpectrum) {
		this.massSpectrum = massSpectrum;
		ionDistribution = new TreeMap<Integer, Float>();
		setIonDistribution();
	}

	// --------------------------------------IIonPercentageDistribution
	@Override
	public IScanMSD getMassSpectrum() {

		return massSpectrum;
	}

	@Override
	public float getPercentage(int ion) {

		float result = 0.0f;
		if(massSpectrumIsNull()) {
			result = 0.0f;
		} else {
			if(ionDistribution.containsKey(ion)) {
				result = ionDistribution.get(ion);
			}
		}
		return result;
	}

	@Override
	public float getPercentage(List<Integer> ions) {

		float result = 0.0f;
		if(ions != null) {
			for(Integer ion : ions) {
				result += getPercentage(ion);
			}
		}
		return result;
	}

	// --------------------------------------IIonPercentageDistribution
	// --------------------------------------private methods
	/**
	 * Calculates a ion percentage distribution.
	 */
	private void setIonDistribution() {

		if(!massSpectrumIsNull()) {
			float percentage;
			int ion;
			float totalIonSignal = massSpectrum.getTotalSignal();
			if(totalIonSignal == 0) {
				return;
			}
			float factor = MAX_PERCENTAGE / totalIonSignal;
			/*
			 * Calculate each percentage distribution and set it to the list.
			 */
			for(IIon actualIon : massSpectrum.getIons()) {
				if(actualIon != null) {
					percentage = factor * actualIon.getAbundance();
					ion = AbstractIon.getIon(actualIon.getIon());
					ionDistribution.put(ion, percentage);
				}
			}
		}
	}

	/**
	 * If the stored mass spectrum is null, than return true, otherwise false.
	 * 
	 * @return boolean
	 */
	private boolean massSpectrumIsNull() {

		boolean result = false;
		if(massSpectrum == null) {
			result = true;
		}
		return result;
	}
	// --------------------------------------private methods
}
