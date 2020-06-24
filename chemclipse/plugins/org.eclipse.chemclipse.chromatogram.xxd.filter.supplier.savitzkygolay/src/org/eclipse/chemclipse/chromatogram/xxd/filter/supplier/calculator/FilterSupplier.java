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

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayFilter;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonProvider;
import org.eclipse.chemclipse.msd.model.core.comparator.IonValueComparator;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class FilterSupplier {

	public IProcessingResult<Integer> applySavitzkyGolayFilter(List<? extends IIonProvider> massSpectra, int derivative, int order, int width, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, massSpectra.size());
		DefaultProcessingResult<Integer> result = new DefaultProcessingResult<>();
		result.setProcessingResult(0);
		for(IIonProvider massSpectrum : massSpectra) {
			applySavitzkyGolay(massSpectrum, derivative, order, width, subMonitor.split(1), result);
		}
		return result;
	}

	private void applySavitzkyGolay(IIonProvider massSpectrum, int derivative, int order, int width, IProgressMonitor monitor, IProcessingResult<Integer> result) {

		List<IIon> ions = new ArrayList<>(massSpectrum.getIons());
		Collections.sort(ions, new IonValueComparator());
		double[] intensityValues = getIntensityValues(ions);
		SavitzkyGolayFilter filter = new SavitzkyGolayFilter(order, width, derivative);
		double[] smoothed = SavitzkyGolayProcessor.smooth(intensityValues, filter, monitor);
		int i = 0;
		int smoothedIons = result.getProcessingResult();
		for(IIon ion : ions) {
			try {
				ion.setAbundance((float)smoothed[i]);
				smoothedIons++;
			} catch(AbundanceLimitExceededException e) {
				result.addWarnMessage("Savitzky-Golay", "Ion " + i + " of massspectrum can not be smoothed because the abundance limit is exceeded");
			}
			i++;
		}
		result.setProcessingResult(smoothedIons);
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
