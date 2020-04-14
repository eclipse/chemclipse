/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.numeric.statistics.Calculations;

public class CombinedMassSpectrumCalculator {

	private Map<Integer, List<Double>> combinedMassSpectrum = new HashMap<Integer, List<Double>>();

	public void addIon(double ion, double abundance) {

		/*
		 * If the abundance is zero, do nothing and return.
		 */
		if(abundance == 0.0d) {
			return;
		}
		int key = AbstractIon.getIon(ion);
		/*
		 * Add the abundance if still a ion exists, otherwise still add the ion.
		 */
		List<Double> intensities = combinedMassSpectrum.get(key);
		if(intensities == null) {
			intensities = new ArrayList<Double>();
			combinedMassSpectrum.put(key, intensities);
		}
		intensities.add(abundance);
	}

	public void addIons(List<IIon> ions, IMarkedIons excludedIons) {

		if(ions == null || excludedIons == null) {
			return;
		}
		//
		Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
		for(IIon ion : ions) {
			if(!excludedIonsNominal.contains(ion.getIon())) {
				addIon(ion.getIon(), ion.getAbundance());
			}
		}
	}

	public void removeIon(double ion) {

		int key = AbstractIon.getIon(ion);
		combinedMassSpectrum.remove(key);
	}

	public void removeIons(IMarkedIons excludedIons) {

		for(Integer ion : excludedIons.getIonsNominal()) {
			combinedMassSpectrum.remove(ion);
		}
	}

	public double getAbundance(double ion) {

		int key = AbstractIon.getIon(ion);
		return calculateSumIntensity(combinedMassSpectrum.get(key));
	}

	public Map<Integer, List<Double>> getValues() {

		return combinedMassSpectrum;
	}

	public Map<Integer, Double> getValuesIntensities() {

		Map<Integer, Double> map = new HashMap<>();
		for(Integer key : combinedMassSpectrum.keySet()) {
			map.put(key, calculateSumIntensity(combinedMassSpectrum.get(key)));
		}
		return map;
	}

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
		double max = Collections.max(getValuesIntensities().values());
		if(max == 0.0d) {
			return;
		}
		//
		double correlationFactor = normalizationFactor / max;
		for(Integer key : combinedMassSpectrum.keySet()) {
			List<Double> adjustedIntensities = new ArrayList<>();
			for(double intensity : combinedMassSpectrum.get(key)) {
				adjustedIntensities.add(correlationFactor * intensity);
			}
			combinedMassSpectrum.put(key, adjustedIntensities);
		}
	}

	public double calculateSumIntensity(List<Double> intensities) {

		double sum = 0.0d;
		if(intensities != null) {
			int size = intensities.size();
			double[] values = new double[size];
			for(int i = 0; i < size; i++) {
				values[i] = intensities.get(i);
			}
			/*
			 * Add an option here to calculate the sum,
			 * mean or median signal.
			 */
			sum = Calculations.getSum(values);
			// sum = Calculations.getMean(values);
			// sum = Calculations.getMedian(values);
		}
		return sum;
	}
}
