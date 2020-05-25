/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.calculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonValueComparator;
import org.eclipse.core.runtime.IProgressMonitor;

public class FilterSupplier {

	public void applySnipFilter(List<IScanMSD> massSpectra, int iterations, int transitions, double magnificationFactor, IProgressMonitor monitor) {

		for(IScanMSD massSpectrum : massSpectra) {
			/*
			 * Apply the filter to all peaks.
			 */
			for(int i = 1; i <= transitions; i++) {
				/*
				 * Do the subtraction n times.
				 */
				applySnipTransition(massSpectrum, iterations, magnificationFactor, monitor);
			}
		}
	}

	private void applySnipTransition(IScanMSD massSpectrum, int iterations, double magnificationFactor, IProgressMonitor monitor) {

		int extraValues = 6; // Leading and tailing extra values.
		List<IIon> ions = new ArrayList<>(massSpectrum.getIons());
		Collections.sort(ions, new IonValueComparator());
		float[] intensityValues = getIntensityValues(ions, extraValues);
		/*
		 * Run the SNIP baseline calculation.
		 */
		SnipCalculator.calculateBaselineIntensityValues(intensityValues, iterations, monitor);
		/*
		 * Subtract the values.
		 */
		List<IIon> ionsToRemove = applySnipBaselineToSubtractIons(ions, intensityValues, extraValues, magnificationFactor);
		for(IIon ion : ionsToRemove) {
			massSpectrum.removeIon(ion);
		}
	}

	private float[] getIntensityValues(List<IIon> ions, int extraValues) {

		float[] intensityValues = new float[ions.size() + extraValues * 2];
		int counter = 0;
		/*
		 * Extra values before.
		 */
		for(int i = 0; i < extraValues; i++) {
			intensityValues[counter++] = 1;
		}
		/*
		 * Data
		 */
		for(IIon ion : ions) {
			intensityValues[counter++] = ion.getAbundance();
		}
		/*
		 * Extra values after.
		 */
		for(int i = 0; i < extraValues; i++) {
			intensityValues[counter++] = 1;
		}
		//
		return intensityValues;
	}

	private List<IIon> applySnipBaselineToSubtractIons(List<IIon> ions, float[] intensityValues, int extraValues, double magnificationFactor) {

		List<IIon> ionsToRemove = new ArrayList<IIon>();
		int counter = 0;
		/*
		 * Skip leading extra values.
		 */
		for(int i = 0; i < extraValues; i++) {
			counter++;
		}
		/*
		 * Subtract intensity values.
		 */
		for(IIon ion : ions) {
			/*
			 * Get the baseline intensity and magnify the signal.
			 * 1 = no changes
			 * 1.3 = 30% extra
			 */
			float baselineIntensity = intensityValues[counter++];
			baselineIntensity *= magnificationFactor;
			/*
			 * Remove the calculated intensity.
			 */
			float abundance = ion.getAbundance() - baselineIntensity;
			if(abundance <= 0) {
				ionsToRemove.add(ion);
			} else {
				try {
					ion.setAbundance(abundance);
				} catch(AbundanceLimitExceededException e) {
					// logger.warn(e);
				}
			}
		}
		/*
		 * Delete the ions that have been marked to be removed.
		 */
		return ionsToRemove;
	}
}
