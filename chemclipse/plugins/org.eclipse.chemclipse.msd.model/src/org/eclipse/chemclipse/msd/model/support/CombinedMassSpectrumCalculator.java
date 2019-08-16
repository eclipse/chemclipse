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
package org.eclipse.chemclipse.msd.model.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;

public class CombinedMassSpectrumCalculator implements ICombinedMassSpectrumCalculator {

	private Map<Integer, Double> combinedMassSpectrum;

	public CombinedMassSpectrumCalculator() {
		combinedMassSpectrum = new HashMap<Integer, Double>();
	}

	@Override
	public void addIon(double ion, float abundance) {

		/*
		 * If the abundance is zero, do nothing and return.
		 */
		if(abundance == 0.0f) {
			return;
		}
		int key = AbstractIon.getIon(ion);
		/*
		 * Add the abundance if still a ion exists, otherwise still add the ion.
		 */
		if(combinedMassSpectrum.containsKey(key)) {
			combinedMassSpectrum.put(key, combinedMassSpectrum.get(key) + abundance);
		} else {
			combinedMassSpectrum.put(key, (double)abundance);
		}
	}

	@Override
	public void addIons(List<IIon> ions, IMarkedIons excludedIons) {

		if(ions == null || excludedIons == null) {
			return;
		}
		Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
		for(IIon ion : ions) {
			if(!excludedIonsNominal.contains(ion.getIon())) {
				addIon(ion.getIon(), ion.getAbundance());
			}
		}
	}

	@Override
	public void removeIon(double ion) {

		int key = AbstractIon.getIon(ion);
		combinedMassSpectrum.remove(key);
	}

	@Override
	public void removeIons(IMarkedIons excludedIons) {

		for(Integer ion : excludedIons.getIonsNominal()) {
			combinedMassSpectrum.remove(ion);
		}
	}

	@Override
	public double getAbundance(double ion) {

		double result = 0.0d;
		int key = AbstractIon.getIon(ion);
		if(combinedMassSpectrum.containsKey(key)) {
			result = combinedMassSpectrum.get(key);
		}
		return result;
	}

	@Override
	public Map<Integer, Double> getValues() {

		return combinedMassSpectrum;
	}

	@Override
	public void normalize(float normalizationFactor) {

		/*
		 * Return if the normalization factor is smaller or equal zero.
		 */
		if(normalizationFactor <= 0) {
			return;
		}
		/*
		 * If max is zero it doesn't even make sense to go further on.
		 */
		double max = Collections.max(combinedMassSpectrum.values());
		if(max == 0.0d) {
			return;
		}
		double correlationFactor = normalizationFactor / max;
		double value;
		for(Integer key : combinedMassSpectrum.keySet()) {
			value = correlationFactor * combinedMassSpectrum.get(key);
			combinedMassSpectrum.put(key, value);
		}
	}
}
