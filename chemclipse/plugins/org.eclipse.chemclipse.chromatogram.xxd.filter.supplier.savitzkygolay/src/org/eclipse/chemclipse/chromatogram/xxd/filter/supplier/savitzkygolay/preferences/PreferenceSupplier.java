/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.FilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ISavitzkyGolayMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.SavitzkyGolayMassSpectrumFilterSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final int MIN_DERIVATIVE = 0;
	public static final int MAX_DERIVATIVE = 5;
	public static final String P_DERIVATIVE = "derivative";
	public static final int DEF_DERIVATIVE = MIN_DERIVATIVE;
	public static final int STEP_DERIVATIVE = 1;
	//
	public static final int MIN_ORDER = 2;
	public static final int MAX_ORDER = 5;
	public static final String P_ORDER = "order";
	public static final int DEF_ORDER = MIN_ORDER;
	public static final int STEP_ORDER = 1;
	//
	public static final int MIN_WIDTH = 5;
	public static final int MAX_WIDTH = 51;
	public static final String P_WIDTH = "width";
	public static final int DEF_WIDTH = 7;
	public static final int STEP_WIDTH = 1;
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
		defaultValues.put(P_DERIVATIVE, Integer.toString(DEF_DERIVATIVE));
		defaultValues.put(P_ORDER, Integer.toString(DEF_ORDER));
		defaultValues.put(P_WIDTH, Integer.toString(DEF_WIDTH));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static FilterSettings getFilterSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		FilterSettings filterSettings = new FilterSettings();
		filterSettings.setOrder(preferences.getInt(P_ORDER, DEF_ORDER));
		filterSettings.setWidth(preferences.getInt(P_WIDTH, DEF_WIDTH));
		return filterSettings;
	}

	public static ISavitzkyGolayMassSpectrumFilterSettings getMassSpectrumFilterSettings() {

		ISavitzkyGolayMassSpectrumFilterSettings settings = new SavitzkyGolayMassSpectrumFilterSettings();
		settings.setDerivative(getDerivative());
		settings.setOrder(getOrder());
		settings.setWidth(getWidth());
		return settings;
	}

	public static int getDerivative() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_DERIVATIVE, DEF_DERIVATIVE);
	}

	public static int getOrder() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_ORDER, DEF_ORDER);
	}

	public static int getWidth() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_WIDTH, DEF_WIDTH);
	}
}
