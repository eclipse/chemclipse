/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.preferences;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.settings.FilterSettings;
import org.eclipse.chemclipse.support.model.SegmentWidth;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_CODA_THRESHOLD = "codaThreshold";
	public static final float DEF_CODA_THRESHOLD = 0.75f;
	public static final float CODA_THRESHOLD_MIN_VALUE = 0.0f;
	public static final float CODA_THRESHOLD_MAX_VALUE = 1.0f;
	//
	private static IPreferenceSupplier preferenceSupplier = null;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_CODA_THRESHOLD, Float.toString(DEF_CODA_THRESHOLD));
	}

	public static FilterSettings getChromatogramFilterSettings() {

		FilterSettings filterSettings = new FilterSettings();
		filterSettings.setCodaThreshold(INSTANCE().getFloat(P_CODA_THRESHOLD, DEF_CODA_THRESHOLD));
		return filterSettings;
	}

	/**
	 * Returns the segment width enum.
	 * 
	 * @return {@link SegmentWidth}
	 */
	public static float getCodaThreshold() {

		return INSTANCE().getFloat(P_CODA_THRESHOLD, DEF_CODA_THRESHOLD);
	}
}