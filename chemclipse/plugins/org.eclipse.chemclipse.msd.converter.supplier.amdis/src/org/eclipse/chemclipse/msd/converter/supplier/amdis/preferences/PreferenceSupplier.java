/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.Activator;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SPLIT_LIBRARY = "splitLibrary";
	public static final boolean DEF_SPLIT_LIBRARY = false;
	public static final String P_EXCLUDE_UNCERTAIN_IONS = "excludeUncertainIons";
	public static final boolean DEF_EXCLUDE_UNCERTAIN_IONS = false;
	public static final String P_USE_UNIT_MASS_RESOLUTION = "useUnitMassResolution";
	public static final boolean DEF_USE_UNIT_MASS_RESOLUTION = true;
	public static final String P_REMOVE_INTENSITIES_LOWER_THAN_ONE = "removeIntensitiesLowerThanOne";
	public static final boolean DEF_REMOVE_INTENSITIES_LOWER_THAN_ONE = true;
	public static final String P_NORMALIZE_INTENSITIES = "normalizeIntensities";
	public static final boolean DEF_NORMALIZE_INTENSITIES = true;
	public static final String P_EXPORT_INTENSITIES_AS_INTEGER = "exportIntensitiesAsInteger";
	public static final boolean DEF_EXPORT_INTENSITIES_AS_INTEGER = true;
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
		defaultValues.put(P_SPLIT_LIBRARY, Boolean.toString(DEF_SPLIT_LIBRARY));
		defaultValues.put(P_EXCLUDE_UNCERTAIN_IONS, Boolean.toString(DEF_EXCLUDE_UNCERTAIN_IONS));
		defaultValues.put(P_USE_UNIT_MASS_RESOLUTION, Boolean.toString(DEF_USE_UNIT_MASS_RESOLUTION));
		defaultValues.put(P_REMOVE_INTENSITIES_LOWER_THAN_ONE, Boolean.toString(DEF_REMOVE_INTENSITIES_LOWER_THAN_ONE));
		defaultValues.put(P_NORMALIZE_INTENSITIES, Boolean.toString(DEF_NORMALIZE_INTENSITIES));
		defaultValues.put(P_EXPORT_INTENSITIES_AS_INTEGER, Boolean.toString(DEF_EXPORT_INTENSITIES_AS_INTEGER));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static boolean isSplitLibrary() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_SPLIT_LIBRARY, DEF_SPLIT_LIBRARY);
	}

	public static boolean isExcludeUncertainIons() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_EXCLUDE_UNCERTAIN_IONS, DEF_EXCLUDE_UNCERTAIN_IONS);
	}

	public static boolean isUseUnitMassResolution() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_USE_UNIT_MASS_RESOLUTION, DEF_USE_UNIT_MASS_RESOLUTION);
	}

	public static boolean isRemoveIntensitiesLowerThanOne() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_REMOVE_INTENSITIES_LOWER_THAN_ONE, DEF_REMOVE_INTENSITIES_LOWER_THAN_ONE);
	}

	public static boolean isNormalizeIntensities() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_NORMALIZE_INTENSITIES, DEF_NORMALIZE_INTENSITIES);
	}

	public static boolean isExportIntensitiesAsInteger() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_EXPORT_INTENSITIES_AS_INTEGER, DEF_EXPORT_INTENSITIES_AS_INTEGER);
	}
}
