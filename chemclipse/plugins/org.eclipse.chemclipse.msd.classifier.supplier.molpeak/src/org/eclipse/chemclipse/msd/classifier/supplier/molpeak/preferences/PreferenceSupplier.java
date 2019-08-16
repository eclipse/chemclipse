/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.Activator;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.ClassifierSettings;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.IBasePeakSettings;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_MATCH_SENSITIVITY = "matchSensitivity";
	public static final float DEF_MATCH_SENSITIVITY = 80.0f;
	public static final float MIN_MATCH_SENSITIVITY = 0.0f;
	public static final float MAX_MATCH_SENSITIVITY = 100.0f;
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
		//
		defaultValues.put(P_MATCH_SENSITIVITY, Float.toString(DEF_MATCH_SENSITIVITY));
		//
		return defaultValues;
	}

	public static MassSpectrumIdentifierSettings getMassSpectrumIdentifierSettings() {

		MassSpectrumIdentifierSettings settings = new MassSpectrumIdentifierSettings();
		setBasePeakSettings(settings);
		return settings;
	}

	public static PeakIdentifierSettings getPeakIdentifierSettings() {

		PeakIdentifierSettings settings = new PeakIdentifierSettings();
		setBasePeakSettings(settings);
		return settings;
	}

	public static ClassifierSettings getChromatogramClassifierSettings() {

		return new ClassifierSettings();
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	private static void setBasePeakSettings(IBasePeakSettings settings) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		settings.setMatchSensitivity(preferences.getFloat(P_MATCH_SENSITIVITY, DEF_MATCH_SENSITIVITY));
	}
}
