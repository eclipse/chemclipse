/*******************************************************************************
 * Copyright (c) 2014, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.thirdderivative.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.thirdderivative.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.thirdderivative.settings.PeakDetectorSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_THRESHOLD = "threshold";
	public static final String P_INCLUDE_BACKGROUND = "includeBackground";
	public static final boolean DEF_INCLUDE_BACKGROUND = false; // false will use BV oder VB, if true VV will be used.
	public static final String P_MIN_SN_RATIO = "minSNRatio";
	public static final float DEF_MIN_SN_RATIO = 0.0f; // 0 = all peaks will be added
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
		defaultValues.put(P_MIN_SN_RATIO, Float.toString(DEF_MIN_SN_RATIO));
		defaultValues.put(P_INCLUDE_BACKGROUND, Boolean.toString(DEF_INCLUDE_BACKGROUND));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static PeakDetectorSettings getPeakDetectorSettings() {

		PeakDetectorSettings peakDetectorSettings = new PeakDetectorSettings();
		peakDetectorSettings.setIncludeBackground(INSTANCE().getBoolean(P_INCLUDE_BACKGROUND, DEF_INCLUDE_BACKGROUND));
		peakDetectorSettings.setMinimumSignalToNoiseRatio(INSTANCE().getFloat(P_MIN_SN_RATIO, DEF_MIN_SN_RATIO));
		return peakDetectorSettings;
	}
}