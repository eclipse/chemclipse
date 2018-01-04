/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.preferences;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_CODA_THRESHOLD = "codaThreshold";
	public static final float DEF_CODA_THRESHOLD = 0.75f;
	public static final float CODA_THRESHOLD_MIN_VALUE = 0.0f;
	public static final float CODA_THRESHOLD_MAX_VALUE = 1.0f;
	//
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_CODA_THRESHOLD, Float.toString(DEF_CODA_THRESHOLD));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	/**
	 * Returns the chromatogram filter settings.
	 * 
	 * @return IChromatogramFilterSettings
	 */
	public static IChromatogramFilterSettings getChromatogramFilterSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		ISupplierFilterSettings chromatogramFilterSettings = new SupplierFilterSettings();
		/*
		 * Get the actual preference.
		 * If it's not available, a default value will be returned.
		 */
		chromatogramFilterSettings.getCodaSettings().setCodaThreshold(preferences.getFloat(P_CODA_THRESHOLD, DEF_CODA_THRESHOLD));
		return chromatogramFilterSettings;
	}

	/**
	 * Returns the segment width enum.
	 * 
	 * @return {@link SegmentWidth}
	 */
	public static float getCodaThreshold() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getFloat(P_CODA_THRESHOLD, DEF_CODA_THRESHOLD);
	}
}
