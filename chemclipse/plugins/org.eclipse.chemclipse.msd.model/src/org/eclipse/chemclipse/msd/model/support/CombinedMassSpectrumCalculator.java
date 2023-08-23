/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.CombinedMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.numeric.statistics.Calculations;

public class CombinedMassSpectrumCalculator {

	private static final Logger logger = Logger.getLogger(CombinedMassSpectrumCalculator.class);
	private Map<Integer, List<Double>> combinedMassSpectrum = new HashMap<Integer, List<Double>>();

	public int size() {

		return combinedMassSpectrum.size();
	}

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
			int mz = (int)ion.getIon();
			if(!excludedIonsNominal.contains(mz)) {
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

	public ICombinedMassSpectrum createMassSpectrum(CalculationType calculationType) {

		ICombinedMassSpectrum massSpectrum = new CombinedMassSpectrum();
		for(Integer ion : combinedMassSpectrum.keySet()) {
			float intensity = (float)getAbundance(ion, calculationType);
			if(intensity > IIon.ZERO_INTENSITY) {
				try {
					massSpectrum.addIon(new Ion(ion, intensity));
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
			}
		}
		return massSpectrum;
	}

	public Map<Integer, List<Double>> getValues() {

		return Collections.unmodifiableMap(combinedMassSpectrum);
	}

	private double getAbundance(double ion, CalculationType calculationType) {

		int key = AbstractIon.getIon(ion);
		return calculateSumIntensity(combinedMassSpectrum.get(key), calculationType);
	}

	private double calculateSumIntensity(List<Double> intensities, CalculationType calculationType) {

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
			switch(calculationType) {
				case SUM:
					sum = Calculations.getSum(values);
					break;
				case MEAN:
					sum = Calculations.getMean(values);
					break;
				case MEDIAN:
					sum = Calculations.getMedian(values);
					break;
			}
		}
		return sum;
	}
}