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
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.calculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonValueComparator;
import org.eclipse.core.runtime.IProgressMonitor;

public class FilterSupplier {

	public void applySavitzkyGolayFilter(List<IScanMSD> massSpectra, int derivative, int order, int width, IProgressMonitor monitor) {

		for(IScanMSD massSpectrum : massSpectra) {
			applySavitzkyGolay(massSpectrum, derivative, order, width, monitor);
		}
	}

	private void applySavitzkyGolay(IScanMSD massSpectrum, int derivative, int order, int width, IProgressMonitor monitor) {

		List<IIon> ions = new ArrayList<>(massSpectrum.getIons());
		Collections.sort(ions, new IonValueComparator());
		double[] intensityValues = getIntensityValues(ions);
		SavitzkyGolayProcessor processor = new SavitzkyGolayProcessor();
		double[] smoothed = processor.smooth(intensityValues, derivative, order, width, monitor);
		int i = 0;
		for(IIon ion : ions) {
			try {
				ion.setAbundance((float)smoothed[i]);
			} catch(AbundanceLimitExceededException e) {
				System.out.println(e);
			}
			i++;
		}
	}

	private double[] getIntensityValues(List<IIon> ions) {

		double[] intensityValues = new double[ions.size()];
		int counter = 0;
		/*
		 * Data
		 */
		for(IIon ion : ions) {
			intensityValues[counter++] = ion.getAbundance();
		}
		//
		return intensityValues;
	}
}
