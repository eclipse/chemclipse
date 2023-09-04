/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.excel.preferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.pcr.report.supplier.tabular.Activator;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMappings;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.preferences.StringUtils;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_IGNORE_SUBSETS = "ignore-subsets";
	public static final String DEF_IGNORE_SUBSETS = "New Subset";
	public static final String P_CHANNEL_MAPPING = "channel-mapping-xlsx";
	public static final String DEF_CHANNEL_MAPPING = "";
	public static final String P_LIST_PATH_IMPORT = "listPathImport";
	public static final String DEF_LIST_PATH_IMPORT = "";
	public static final String P_LIST_PATH_EXPORT = "listPathExport";
	public static final String DEF_LIST_PATH_EXPORT = "";
	public static final String P_ANALYSIS_SEPARATOR = "xlsx-pcr-analysis-separator";
	public static final String DEF_ANALYSIS_SEPARATOR = "_";
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

		Map<String, String> defaultValues = new HashMap<>();
		defaultValues.put(P_CHANNEL_MAPPING, DEF_CHANNEL_MAPPING);
		defaultValues.put(P_IGNORE_SUBSETS, DEF_IGNORE_SUBSETS);
		defaultValues.put(P_ANALYSIS_SEPARATOR, DEF_ANALYSIS_SEPARATOR);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static ChannelMappings getChannelMappings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		ChannelMappings channelMappings = new ChannelMappings();
		channelMappings.load(preferences.get(P_CHANNEL_MAPPING, DEF_CHANNEL_MAPPING));
		return channelMappings;
	}

	public static Set<String> getIgnoredSubsets() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		Set<String> subsets = new HashSet<>();
		String preferenceEntry = preferences.get(P_IGNORE_SUBSETS, DEF_IGNORE_SUBSETS);
		if(!"".equals(preferenceEntry)) {
			String[] items = StringUtils.parseString(preferenceEntry);
			if(items.length > 0) {
				for(String item : items) {
					subsets.add(item);
				}
			}
		}
		return subsets;
	}

	public static String getAnalysisSeparator() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_ANALYSIS_SEPARATOR, DEF_ANALYSIS_SEPARATOR);
	}

	public static String getListPathImport() {

		return getFilterPath(P_LIST_PATH_IMPORT, DEF_LIST_PATH_IMPORT);
	}

	public static String getListPathExport() {

		return getFilterPath(P_LIST_PATH_EXPORT, DEF_LIST_PATH_EXPORT);
	}

	private static String getFilterPath(String key, String def) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(key, def);
	}
}
