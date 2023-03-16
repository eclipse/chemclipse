/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.Activator;
import org.eclipse.chemclipse.model.math.IonRoundMethod;
import org.eclipse.chemclipse.model.targets.LibraryField;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

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
	public static final String P_BEST_TARGET_LIBRARY_FIELD = "bestTargetLibraryField";
	public static final String DEF_BEST_TARGET_LIBRARY_FIELD = LibraryField.NAME.name();
	public static final String P_ION_ROUND_METHOD = "ionRoundMethod"; // When changing this value, call clearCacheActiveIonRoundMethod.
	public static final String DEF_ION_ROUND_METHOD = IonRoundMethod.DEFAULT.name();
	public static final String P_SKIP_PEAK_WIDTH_CHECK = "skipPeakWidthCheck";
	public static final boolean DEF_SKIP_PEAK_WIDTH_CHECK = false;
	//
	public static final String P_SEPARATION_COLUMN_MAPPINGS = "separationColumnMappings";
	public static final String DEF_SEPARATION_COLUMN_MAPPINGS = "";
	/*
	 * Don't show in the preference page
	 */
	public static final String P_LIST_PATH_IMPORT = "listPathImport";
	public static final String DEF_LIST_PATH_IMPORT = "";
	public static final String P_LIST_PATH_EXPORT = "listPathExport";
	public static final String DEF_LIST_PATH_EXPORT = "";
	/*
	 * Used to cache the round method.
	 * Call clearCacheActiveIonRoundMethod to force a reload.
	 */
	private static IonRoundMethod activeIonRoundMethod = null;
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
		//
		defaultValues.put(P_MISC_SEPARATOR, DEF_MISC_SEPARATOR);
		defaultValues.put(P_MISC_SEPARATED_DELIMITER, DEF_MISC_SEPARATED_DELIMITER);
		defaultValues.put(P_ALTERNATE_WINDOW_MOVE_DIRECTION, Boolean.toString(DEF_ALTERNATE_WINDOW_MOVE_DIRECTION));
		defaultValues.put(P_CONDENSE_CYCLE_NUMBER_SCANS, Boolean.toString(DEF_CONDENSE_CYCLE_NUMBER_SCANS));
		defaultValues.put(P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, Boolean.toString(DEF_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS));
		defaultValues.put(P_SHOW_AREA_WITHOUT_DECIMALS, Boolean.toString(DEF_SHOW_AREA_WITHOUT_DECIMALS));
		defaultValues.put(P_SORT_CASE_SENSITIVE, Boolean.toString(DEF_SORT_CASE_SENSITIVE));
		defaultValues.put(P_SEARCH_CASE_SENSITIVE, Boolean.toString(DEF_SEARCH_CASE_SENSITIVE));
		defaultValues.put(P_USE_RETENTION_INDEX_QC, Boolean.toString(DEF_USE_RETENTION_INDEX_QC));
		defaultValues.put(P_BEST_TARGET_LIBRARY_FIELD, DEF_BEST_TARGET_LIBRARY_FIELD);
		defaultValues.put(P_ION_ROUND_METHOD, DEF_ION_ROUND_METHOD);
		defaultValues.put(P_SKIP_PEAK_WIDTH_CHECK, Boolean.toString(DEF_SKIP_PEAK_WIDTH_CHECK));
		defaultValues.put(P_SEPARATION_COLUMN_MAPPINGS, DEF_SEPARATION_COLUMN_MAPPINGS);
		defaultValues.put(P_LIST_PATH_IMPORT, DEF_LIST_PATH_IMPORT);
		defaultValues.put(P_LIST_PATH_EXPORT, DEF_LIST_PATH_EXPORT);
		//
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

		return INSTANCE().getBoolean(P_CONDENSE_CYCLE_NUMBER_SCANS, DEF_CONDENSE_CYCLE_NUMBER_SCANS);
	}

	public static boolean showRetentionIndexWithoutDecimals() {

		return INSTANCE().getBoolean(P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, DEF_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS);
	}

	public static boolean showAreaWithoutDecimals() {

		return INSTANCE().getBoolean(P_SHOW_AREA_WITHOUT_DECIMALS, DEF_SHOW_AREA_WITHOUT_DECIMALS);
	}

	public static boolean isSortCaseSensitive() {

		return INSTANCE().getBoolean(P_SORT_CASE_SENSITIVE, DEF_SORT_CASE_SENSITIVE);
	}

	public static boolean useAlternateWindowMoveDirection() {

		return INSTANCE().getBoolean(P_ALTERNATE_WINDOW_MOVE_DIRECTION, DEF_ALTERNATE_WINDOW_MOVE_DIRECTION);
	}

	public static boolean isSearchCaseSensitive() {

		return INSTANCE().getBoolean(P_SEARCH_CASE_SENSITIVE, DEF_SEARCH_CASE_SENSITIVE);
	}

	public static void setSearchCaseSensitive(boolean searchCaseSensitive) {

		INSTANCE().putBoolean(P_SEARCH_CASE_SENSITIVE, searchCaseSensitive);
	}

	public static boolean isUseRetentionIndexQC() {

		return INSTANCE().getBoolean(P_USE_RETENTION_INDEX_QC, DEF_USE_RETENTION_INDEX_QC);
	}

	public static void setUseRetentionIndexQC(boolean value) {

		INSTANCE().putBoolean(P_USE_RETENTION_INDEX_QC, value);
	}

	public static LibraryField getBestTargetLibraryField() {

		try {
			IEclipsePreferences preferences = INSTANCE().getPreferences();
			return LibraryField.valueOf(preferences.get(P_BEST_TARGET_LIBRARY_FIELD, DEF_BEST_TARGET_LIBRARY_FIELD));
		} catch(Exception e) {
			/*
			 * Default if something went wrong.
			 */
			return LibraryField.NAME;
		}
	}

	/**
	 * For a better performance, the active round method is cached.
	 * Clear the cache if the settings value has been changed, e.g.
	 * by the preference page.
	 */
	public static void clearCacheActiveIonRoundMethod() {

		activeIonRoundMethod = null;
	}

	/**
	 * Get the active ion round method.
	 * 
	 * @return
	 */
	public static IonRoundMethod getIonRoundMethod() {

		if(activeIonRoundMethod == null) {
			/*
			 * Try to get the currently used ion round method.
			 */
			try {
				IEclipsePreferences preferences = INSTANCE().getPreferences();
				activeIonRoundMethod = IonRoundMethod.valueOf(preferences.get(P_ION_ROUND_METHOD, DEF_ION_ROUND_METHOD));
			} catch(Exception e) {
				activeIonRoundMethod = IonRoundMethod.DEFAULT;
			}
		}
		//
		return activeIonRoundMethod;
	}

	/**
	 * Set the active ion round method.
	 * 
	 * @param ionRoundMethod
	 */
	public static void setIonRoundMethod(IonRoundMethod ionRoundMethod) {

		ionRoundMethod = (ionRoundMethod == null) ? IonRoundMethod.DEFAULT : ionRoundMethod;
		INSTANCE().put(P_ION_ROUND_METHOD, ionRoundMethod.name());
		activeIonRoundMethod = ionRoundMethod;
	}

	public static boolean isSkipPeakWidthCheck() {

		return INSTANCE().getBoolean(P_SKIP_PEAK_WIDTH_CHECK, DEF_SKIP_PEAK_WIDTH_CHECK);
	}

	public static String getListPathImport() {

		return INSTANCE().get(P_LIST_PATH_IMPORT, DEF_LIST_PATH_IMPORT);
	}

	public static void setListPathImport(String filterPath) {

		INSTANCE().put(P_LIST_PATH_IMPORT, filterPath);
	}

	public static String getListPathExport() {

		return INSTANCE().get(P_LIST_PATH_EXPORT, DEF_LIST_PATH_EXPORT);
	}

	public static void setListPathExport(String filterPath) {

		INSTANCE().put(P_LIST_PATH_EXPORT, filterPath);
	}

	public static String getSeparationColumnMappings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_SEPARATION_COLUMN_MAPPINGS, DEF_SEPARATION_COLUMN_MAPPINGS);
	}
}