/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.HighPassFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.xpass.settings.LowPassFilterSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_NUMBER_HIGHEST = "numberHighest";
	public static final int DEF_NUMBER_HIGHEST = 5;
	public static final int MIN_NUMBER_HIGHEST = 2;
	public static final int MAX_NUMBER_HIGHEST = 100;
	//
	public static final String P_NUMBER_LOWEST = "numberLowest";
	public static final int DEF_NUMBER_LOWEST = 5;
	public static final int MIN_NUMBER_LOWEST = 2;
	public static final int MAX_NUMBER_LOWEST = 100;
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
		defaultValues.put(P_NUMBER_HIGHEST, Integer.toString(DEF_NUMBER_HIGHEST));
		defaultValues.put(P_NUMBER_LOWEST, Integer.toString(DEF_NUMBER_LOWEST));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static HighPassFilterSettings getHighPassFilterSettings() {

		HighPassFilterSettings settings = new HighPassFilterSettings();
		settings.setNumberHighest(getNumberHighest());
		return settings;
	}

	public static LowPassFilterSettings getLowPassFilterSettings() {

		LowPassFilterSettings settings = new LowPassFilterSettings();
		settings.setNumberLowest(getNumberLowest());
		return settings;
	}

	public static int getNumberHighest() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_NUMBER_HIGHEST, DEF_NUMBER_HIGHEST);
	}

	public static int getNumberLowest() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_NUMBER_LOWEST, DEF_NUMBER_LOWEST);
	}
}
