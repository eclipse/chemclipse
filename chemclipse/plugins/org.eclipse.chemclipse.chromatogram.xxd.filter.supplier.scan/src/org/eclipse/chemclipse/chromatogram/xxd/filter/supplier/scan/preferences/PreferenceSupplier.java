/*******************************************************************************
 * Copyright (c) 2011, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.model.ScanSelectorOption;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsCleaner;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsDeleteIdentifier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsRemover;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scan.settings.FilterSettingsScanSelector;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final Character REMOVE_SIGN = 'X';
	public static final Character PRESERVE_SIGN = 'O';
	//
	public static final double MIN_SCAN_SELECTOR_VALUE = 0.0d;
	public static final double MAX_SCAN_SELECTOR_VALUE = Double.MAX_VALUE;
	//
	public static final String P_REMOVER_PATTERN = "removerPattern";
	public static final String DEF_REMOVER_PATTERN = "XO";
	public static final String CHECK_REMOVER_PATTERN = "^[OX]+";
	//
	public static final String P_SCAN_SELECTOR_OPTION = "scanSelectorOption";
	public static final String DEF_SCAN_SELECTOR_OPTION = ScanSelectorOption.RETENTION_TIME_MS.name();
	public static final String P_SCAN_SELECTOR_VALUE = "scanSelectorValue";
	public static final double DEF_SCAN_SELECTOR_VALUE = 1000.0d;
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
		defaultValues.put(P_REMOVER_PATTERN, DEF_REMOVER_PATTERN);
		defaultValues.put(P_SCAN_SELECTOR_OPTION, DEF_SCAN_SELECTOR_OPTION);
		defaultValues.put(P_SCAN_SELECTOR_VALUE, Double.toString(DEF_SCAN_SELECTOR_VALUE));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static FilterSettingsCleaner getCleanerFilterSettings() {

		return new FilterSettingsCleaner();
	}

	public static FilterSettingsRemover getRemoverFilterSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		FilterSettingsRemover filterSettings = new FilterSettingsRemover();
		filterSettings.setScanRemoverPattern(preferences.get(P_REMOVER_PATTERN, DEF_REMOVER_PATTERN));
		return filterSettings;
	}

	public static FilterSettingsDeleteIdentifier getDeleteIdentifierFilterSettings() {

		return new FilterSettingsDeleteIdentifier();
	}

	public static FilterSettingsScanSelector getFilterSettingsScanSelector() {

		FilterSettingsScanSelector settings = new FilterSettingsScanSelector();
		settings.setScanSelectorOption(getScanSelectorOption());
		settings.setScanSelectorValue(getScanSelectorValue());
		//
		return settings;
	}

	/**
	 * Returns the scan remover pattern.
	 * 
	 * @return String
	 */
	public static String getScanRemoverPattern() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_REMOVER_PATTERN, DEF_REMOVER_PATTERN);
	}

	private static ScanSelectorOption getScanSelectorOption() {

		try {
			IEclipsePreferences preferences = INSTANCE().getPreferences();
			return ScanSelectorOption.valueOf(preferences.get(P_SCAN_SELECTOR_OPTION, DEF_SCAN_SELECTOR_OPTION));
		} catch(Exception e) {
			return ScanSelectorOption.RETENTION_TIME_MS;
		}
	}

	private static double getScanSelectorValue() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getDouble(P_SCAN_SELECTOR_VALUE, DEF_SCAN_SELECTOR_VALUE);
	}
}