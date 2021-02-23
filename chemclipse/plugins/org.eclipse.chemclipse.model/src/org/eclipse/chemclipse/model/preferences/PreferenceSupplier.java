/*******************************************************************************
 * Copyright (c) 2016, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.Activator;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	private static final Logger logger = Logger.getLogger(PreferenceSupplier.class);
	//
	public static final String P_MISC_SEPARATOR = "miscSeparator";
	public static final String DEF_MISC_SEPARATOR = "!";
	public static final String P_MISC_SEPARATED_DELIMITER = "miscSeparatedDelimiter";
	public static final String DEF_MISC_SEPARATED_DELIMITER = " ";
	public static final String P_USE_RETENTION_INDEX_QC = "useRetentionIndexQC";
	public static final boolean DEF_USE_RETENTION_INDEX_QC = false; // Must be activated manually.
	public static final String P_ALTERNATE_WINDOW_MOVE_DIRECTION = "useAlternateWindowMoveDirection";
	public static final boolean DEF_ALTERNATE_WINDOW_MOVE_DIRECTION = false;
	public static final String P_CONDENSE_CYCLE_NUMBER_SCANS = "condenseCycleNumberScans";
	public static final boolean DEF_CONDENSE_CYCLE_NUMBER_SCANS = true;
	public static final String P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS = "showRetentionIndexWithoutDecimals";
	public static final boolean DEF_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS = true;
	public static final String P_SHOW_AREA_WITHOUT_DECIMALS = "showAreaWithoutDecimals";
	public static final boolean DEF_SHOW_AREA_WITHOUT_DECIMALS = true;
	public static final String P_SORT_CASE_SENSITIVE = "sortCaseSensitive";
	public static final boolean DEF_SORT_CASE_SENSITIVE = true;
	public static final String P_SEARCH_CASE_SENSITIVE = "searchCaseSensitive";
	public static final boolean DEF_SEARCH_CASE_SENSITIVE = true;
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

		return Activator.getDefault().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_MISC_SEPARATOR, DEF_MISC_SEPARATOR);
		defaultValues.put(P_MISC_SEPARATED_DELIMITER, DEF_MISC_SEPARATED_DELIMITER);
		defaultValues.put(P_ALTERNATE_WINDOW_MOVE_DIRECTION, Boolean.toString(DEF_ALTERNATE_WINDOW_MOVE_DIRECTION));
		defaultValues.put(P_CONDENSE_CYCLE_NUMBER_SCANS, Boolean.toString(DEF_CONDENSE_CYCLE_NUMBER_SCANS));
		defaultValues.put(P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, Boolean.toString(DEF_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS));
		defaultValues.put(P_SHOW_AREA_WITHOUT_DECIMALS, Boolean.toString(DEF_SHOW_AREA_WITHOUT_DECIMALS));
		defaultValues.put(P_SORT_CASE_SENSITIVE, Boolean.toString(DEF_SORT_CASE_SENSITIVE));
		defaultValues.put(P_SEARCH_CASE_SENSITIVE, Boolean.toString(DEF_SEARCH_CASE_SENSITIVE));
		defaultValues.put(P_USE_RETENTION_INDEX_QC, Boolean.toString(DEF_USE_RETENTION_INDEX_QC));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static String getMiscSeparator() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_MISC_SEPARATOR, DEF_MISC_SEPARATOR);
	}

	public static String getMiscSeparatedDelimiter() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_MISC_SEPARATED_DELIMITER, DEF_MISC_SEPARATED_DELIMITER);
	}

	public static boolean condenseCycleNumberScans() {

		return getBoolean(P_CONDENSE_CYCLE_NUMBER_SCANS, DEF_CONDENSE_CYCLE_NUMBER_SCANS);
	}

	public static boolean showRetentionIndexWithoutDecimals() {

		return getBoolean(P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, DEF_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS);
	}

	public static boolean showAreaWithoutDecimals() {

		return getBoolean(P_SHOW_AREA_WITHOUT_DECIMALS, DEF_SHOW_AREA_WITHOUT_DECIMALS);
	}

	public static boolean isSortCaseSensitive() {

		return getBoolean(P_SORT_CASE_SENSITIVE, DEF_SORT_CASE_SENSITIVE);
	}

	public static boolean useAlternateWindowMoveDirection() {

		return getBoolean(P_ALTERNATE_WINDOW_MOVE_DIRECTION, DEF_ALTERNATE_WINDOW_MOVE_DIRECTION);
	}

	public static boolean isSearchCaseSensitive() {

		return getBoolean(P_SEARCH_CASE_SENSITIVE, DEF_SEARCH_CASE_SENSITIVE);
	}

	public static void setSearchCaseSensitive(boolean searchCaseSensitive) {

		putBoolean(P_SEARCH_CASE_SENSITIVE, searchCaseSensitive);
	}

	public static boolean isUseRetentionIndexQC() {

		return getBoolean(P_USE_RETENTION_INDEX_QC, DEF_USE_RETENTION_INDEX_QC);
	}

	public static void setUseRetentionIndexQC(boolean value) {

		putBoolean(P_USE_RETENTION_INDEX_QC, value);
	}

	private static boolean getBoolean(String key, boolean def) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(key, def);
	}

	private static void putBoolean(String key, boolean value) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		try {
			preferences.putBoolean(key, value);
			preferences.flush();
		} catch(Exception e) {
			logger.warn(e);
		}
	}
}
