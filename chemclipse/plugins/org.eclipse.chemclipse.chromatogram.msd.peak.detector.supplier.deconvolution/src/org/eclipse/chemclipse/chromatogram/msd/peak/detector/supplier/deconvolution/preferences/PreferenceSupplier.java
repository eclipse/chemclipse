/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Activator;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.settings.PeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.settings.Sensitivity;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SENSITIVITY = "sensitivity";
	public static final String DEF_SENSITIVITY = Sensitivity.MEDIUM.toString();
	//
	public static final String P_MIN_SNR = "minSNRatio";
	public static final double DEF_MIN_SNR = 10.0d;
	public static final String P_MIN_PEAKWIDTH = "minPeakWidth";
	public static final int DEF_MIN_PEAKWIDTH = 4;
	public static final String P_MIN_PEAKRISING = "minPeakRising";
	public static final int DEF_MIN_PEAKRISING = 1; // should be between 1,4
	public static final String P_SNIP_ITERATIONS = "snipIterations";
	public static final int DEF_SNIP_ITERATIONS = 70;
	public static final String P_NOISE_SEGMENTS = "howManySegmentsNoiseSplit";
	public static final int DEF_NOISE_SEGMENTS = 15;
	public static final String P_SENS_DECONVOLUTIONS = "Sensitivity Deconvolution";
	public static final int DEF_SENS_DECONVOLUTIONS = 1;
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
		defaultValues.put(P_SENSITIVITY, DEF_SENSITIVITY);
		defaultValues.put(P_MIN_SNR, Double.toString(DEF_MIN_SNR));
		defaultValues.put(P_MIN_PEAKWIDTH, Integer.toString(DEF_MIN_PEAKWIDTH));
		defaultValues.put(P_MIN_PEAKRISING, Integer.toString(DEF_MIN_PEAKRISING));
		defaultValues.put(P_SNIP_ITERATIONS, Integer.toString(DEF_SNIP_ITERATIONS));
		defaultValues.put(P_NOISE_SEGMENTS, Integer.toString(DEF_NOISE_SEGMENTS));
		defaultValues.put(P_SENS_DECONVOLUTIONS, Integer.toString(DEF_SENS_DECONVOLUTIONS));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static PeakDetectorSettings getPeakDetectorSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		PeakDetectorSettings peakDetectorSettings = new PeakDetectorSettings();
		peakDetectorSettings.setSensitivity(Sensitivity.valueOf(preferences.get(P_SENSITIVITY, DEF_SENSITIVITY)));
		peakDetectorSettings.setMinimumSignalToNoiseRatio((float)preferences.getDouble(P_MIN_SNR, DEF_MIN_SNR));
		peakDetectorSettings.setMinimumPeakWidth(preferences.getInt(P_MIN_PEAKWIDTH, DEF_MIN_PEAKWIDTH));
		peakDetectorSettings.setMinimumPeakRising(preferences.getInt(P_MIN_PEAKRISING, DEF_MIN_PEAKRISING));
		peakDetectorSettings.setBaselineIterations(preferences.getInt(P_SNIP_ITERATIONS, DEF_SNIP_ITERATIONS));
		peakDetectorSettings.setQuantityNoiseSegments(preferences.getInt(P_NOISE_SEGMENTS, DEF_NOISE_SEGMENTS));
		peakDetectorSettings.setSensitivityOfDeconvolution(preferences.getInt(P_SENS_DECONVOLUTIONS, DEF_SENS_DECONVOLUTIONS));
		return peakDetectorSettings;
	}

	public static Sensitivity getSensitivity() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String sensitivity = preferences.get(P_SENSITIVITY, DEF_SENSITIVITY);
		if(sensitivity.equals("")) {
			sensitivity = Sensitivity.OFF.toString();
		}
		return Sensitivity.valueOf(sensitivity);
	}

	public static float getMinimumSignalToNoiseRatio() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return (float)preferences.getDouble(P_MIN_SNR, DEF_MIN_SNR);
	}

	public static int getMinimumPeakWidth() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_MIN_PEAKWIDTH, DEF_MIN_PEAKWIDTH);
	}

	public static int getMinimumPeakRising() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_MIN_PEAKRISING, DEF_MIN_PEAKRISING);
	}

	public static int getBaselineIterations() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_SNIP_ITERATIONS, DEF_SNIP_ITERATIONS);
	}

	public static int getQuantityNoiseSegments() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_NOISE_SEGMENTS, DEF_NOISE_SEGMENTS);
	}

	public static int getSensitivityOfDeconvolution() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_SENS_DECONVOLUTIONS, DEF_SENS_DECONVOLUTIONS);
	}
}
