/*******************************************************************************
 * Copyright (c) 2010, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.settings.DivisorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.settings.MultiplierSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_MULTIPLIER = "multiplier";
	public static final float DEF_MULTIPLIER = 1.0f;
	public static final float MIN_MULTIPLIER = 1.0E-12f;
	public static final float MAX_MULTIPLIER = Float.MAX_VALUE;
	//
	public static final String P_DIVISOR = "divisor";
	public static final float DEF_DIVISOR = 1.0f;
	public static final float MIN_DIVISOR = 1.0E-12f;
	public static final float MAX_DIVISOR = Float.MAX_VALUE;
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
		defaultValues.put(P_MULTIPLIER, Float.toString(DEF_MULTIPLIER));
		defaultValues.put(P_DIVISOR, Float.toString(DEF_DIVISOR));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static MultiplierSettings getFilterSettingsMultiplier() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		MultiplierSettings filterSettings = new MultiplierSettings();
		filterSettings.setMultiplier(preferences.getFloat(P_MULTIPLIER, DEF_MULTIPLIER));
		return filterSettings;
	}

	public static DivisorSettings getFilterSettingsDivisor() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		DivisorSettings filterSettings = new DivisorSettings();
		filterSettings.setDivisor(preferences.getFloat(P_DIVISOR, DEF_DIVISOR));
		return filterSettings;
	}
}
