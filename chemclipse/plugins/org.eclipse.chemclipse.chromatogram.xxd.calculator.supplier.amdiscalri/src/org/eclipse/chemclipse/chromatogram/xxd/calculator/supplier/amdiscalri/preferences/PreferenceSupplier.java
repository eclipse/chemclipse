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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.IRetentionIndexFilterSettingsPeak;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.ISupplierCalculatorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.RetentionIndexFilterSettingsPeak;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.SupplierCalculatorSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

public class PreferenceSupplier implements IPreferenceSupplier {

	private static final Logger logger = Logger.getLogger(PreferenceSupplier.class);
	//
	public static final String P_RETENTION_INDEX_FILES = "retentionIndexFiles";
	public static final String DEF_RETENTION_INDEX_FILES = "";
	//
	public static final String P_FILTER_PATH_INDEX_FILES = "filterPathIndexFiles";
	public static final String DEF_FILTER_PATH_INDEX_FILES = "";
	//
	public static final String P_FILTER_PATH_MODELS_MSD = "filterPathModelsMSD";
	public static final String DEF_FILTER_PATH_MODELS_MSD = "";
	//
	public static final String P_FILTER_PATH_MODELS_CSD = "filterPathModelsCSD";
	public static final String DEF_FILTER_PATH_MODELS_CSD = "";
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
		defaultValues.put(P_RETENTION_INDEX_FILES, DEF_RETENTION_INDEX_FILES);
		defaultValues.put(P_FILTER_PATH_INDEX_FILES, DEF_FILTER_PATH_INDEX_FILES);
		defaultValues.put(P_FILTER_PATH_MODELS_MSD, DEF_FILTER_PATH_MODELS_MSD);
		defaultValues.put(P_FILTER_PATH_MODELS_CSD, DEF_FILTER_PATH_MODELS_CSD);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static ISupplierCalculatorSettings getChromatogramCalculatorSettings() {

		ISupplierCalculatorSettings chromatogramCalculatorSettings = new SupplierCalculatorSettings();
		chromatogramCalculatorSettings.setRetentionIndexFiles(getRetentionIndexFiles());
		return chromatogramCalculatorSettings;
	}

	public static IRetentionIndexFilterSettingsPeak getPeakFilterSettings() {

		IRetentionIndexFilterSettingsPeak peakFilterSettings = new RetentionIndexFilterSettingsPeak();
		peakFilterSettings.setRetentionIndexFiles(getRetentionIndexFiles());
		return peakFilterSettings;
	}

	public static List<String> getRetentionIndexFiles() {

		FileListUtil fileListUtil = new FileListUtil();
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		return fileListUtil.getFiles(preferences.get(P_RETENTION_INDEX_FILES, DEF_RETENTION_INDEX_FILES));
	}

	public static void setRetentionIndexFiles(List<String> retentionIndexFiles) {

		try {
			FileListUtil fileListUtil = new FileListUtil();
			IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
			String items[] = retentionIndexFiles.toArray(new String[retentionIndexFiles.size()]);
			preferences.put(P_RETENTION_INDEX_FILES, fileListUtil.createList(items));
			preferences.flush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}

	public static String getFilterPathIndexFiles() {

		return getFilterPath(P_FILTER_PATH_INDEX_FILES, DEF_FILTER_PATH_INDEX_FILES);
	}

	public static void setFilterPathIndexFiles(String filterPath) {

		setFilterPath(P_FILTER_PATH_INDEX_FILES, filterPath);
	}

	public static String getFilterPathModelsMSD() {

		return getFilterPath(P_FILTER_PATH_MODELS_MSD, DEF_FILTER_PATH_MODELS_MSD);
	}

	public static void setFilterPathModelsMSD(String filterPath) {

		setFilterPath(P_FILTER_PATH_MODELS_MSD, filterPath);
	}

	public static String getFilterPathModelsCSD() {

		return getFilterPath(P_FILTER_PATH_MODELS_CSD, DEF_FILTER_PATH_MODELS_CSD);
	}

	public static void setFilterPathModelsCSD(String filterPath) {

		setFilterPath(P_FILTER_PATH_MODELS_CSD, filterPath);
	}

	private static String getFilterPath(String key, String def) {

		IEclipsePreferences eclipsePreferences = INSTANCE().getPreferences();
		return eclipsePreferences.get(key, def);
	}

	private static void setFilterPath(String key, String filterPath) {

		try {
			IEclipsePreferences eclipsePreferences = INSTANCE().getPreferences();
			eclipsePreferences.put(key, filterPath);
			eclipsePreferences.flush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}
}
