/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.comparator.IonAbundanceComparator;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public abstract class XPassFilter {

	public static void applyHighPass(IScanMSD massSpectrum, int number) {

		filter(massSpectrum, SortOrder.DESC, number);
	}

	public static void applyLowPass(IScanMSD massSpectrum, int number) {

		filter(massSpectrum, SortOrder.ASC, number);
	}

	private static void filter(IScanMSD massSpectrum, SortOrder sortOrder, int number) {

		List<IIon> ions = new ArrayList<>(massSpectrum.getIons());
		Collections.sort(ions, new IonAbundanceComparator(sortOrder));
		List<IIon> ionsToRemove = new ArrayList<IIon>();
		int counter = 0;
		for(IIon ion : ions) {
			if(counter >= number) {
				ionsToRemove.add(ion);
			}
			counter++;
		}
		/*
		 * Remove the ions.
		 */
		for(IIon ion : ionsToRemove) {
			massSpectrum.removeIon(ion);
		}
	}
}
