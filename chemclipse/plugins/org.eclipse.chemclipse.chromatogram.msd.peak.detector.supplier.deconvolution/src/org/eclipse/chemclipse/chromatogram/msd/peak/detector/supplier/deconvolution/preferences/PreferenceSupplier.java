/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.Activator;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.settings.DeconvolutionPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.settings.IDeconvolutionPeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.settings.Sensitivity;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SENSITIVITY = "sensitivity";
	public static final String DEF_SENSITIVITY = Sensitivity.MEDIUM.toString();
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
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static IDeconvolutionPeakDetectorSettings getPeakDetectorSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		IDeconvolutionPeakDetectorSettings peakDetectorSettings = new DeconvolutionPeakDetectorSettings();
		peakDetectorSettings.setSensitivity(Sensitivity.valueOf(preferences.get(P_SENSITIVITY, DEF_SENSITIVITY)));
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
}
