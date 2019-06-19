/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.converter;

// TODO JUnit
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.numeric.statistics.Calculations;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class ConverterMSD {

	/**
	 * Use only the static methods.
	 */
	private ConverterMSD() {
	}

	/**
	 * Returns a list of filtered ions if the setting has been activated and the
	 * number of stored ions is >= the limit of ions to show.
	 * 
	 * @param massSpectrum
	 * @return List
	 */
	public static List<IIon> getFilteredIons(IScanMSD massSpectrum) {

		List<IIon> ions;
		//
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		boolean filterMassSpectrum = preferences.getBoolean(PreferenceSupplier.P_FILTER_MASS_SPECTRUM, PreferenceSupplier.DEF_FILTER_MASS_SPECTRUM);
		int filterLimitIons = preferences.getInt(PreferenceSupplier.P_FILTER_LIMIT_IONS, PreferenceSupplier.DEF_FILTER_LIMIT_IONS);
		int numberOfIons = massSpectrum.getNumberOfIons();
		//
		if(filterMassSpectrum && numberOfIons > filterLimitIons) {
			//
			ions = new ArrayList<IIon>();
			//
			float[] intensities = new float[massSpectrum.getIons().size()];
			int i = 0;
			for(IIon ion : massSpectrum.getIons()) {
				intensities[i++] = ion.getAbundance();
			}
			//
			float maxIntensity = Calculations.getMax(intensities);
			float medianIntensity = Calculations.getMedian(intensities);
			float limitIntensity = medianIntensity + ((maxIntensity - medianIntensity) / 6.0f);
			int moduloValue = numberOfIons / 100;
			//
			int j = 0;
			for(IIon ion : massSpectrum.getIons()) {
				float intensity = ion.getAbundance();
				if(intensity >= limitIntensity) {
					ions.add(ion);
				} else {
					if(moduloValue > 0 && j % moduloValue == 0) {
						ions.add(ion);
					}
				}
				j++;
			}
		} else {
			ions = new ArrayList<>(massSpectrum.getIons());
		}
		return ions;
	}
}
