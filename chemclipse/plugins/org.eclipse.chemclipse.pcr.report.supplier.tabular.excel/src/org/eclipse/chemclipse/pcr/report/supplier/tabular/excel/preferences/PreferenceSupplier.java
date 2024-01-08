/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - preference initializer
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.excel.preferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.pcr.report.supplier.tabular.Activator;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMappings;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.preferences.StringUtils;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

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
	public static final String P_OPEN_REPORT = "xlsx-pcr-open-report";
	public static final boolean DEF_OPEN_REPORT = true;
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
		defaultValues.put(P_OPEN_REPORT, Boolean.toString(DEF_OPEN_REPORT));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static ChannelMappings getChannelMappings() {

		ChannelMappings channelMappings = new ChannelMappings();
		channelMappings.load(INSTANCE().get(P_CHANNEL_MAPPING, DEF_CHANNEL_MAPPING));
		return channelMappings;
	}

	public static Set<String> getIgnoredSubsets() {

		Set<String> subsets = new HashSet<>();
		String preferenceEntry = INSTANCE().get(P_IGNORE_SUBSETS, DEF_IGNORE_SUBSETS);
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

		return INSTANCE().get(P_ANALYSIS_SEPARATOR, DEF_ANALYSIS_SEPARATOR);
	}

	public static String getListPathImport() {

		return INSTANCE().get(P_LIST_PATH_IMPORT, DEF_LIST_PATH_IMPORT);
	}

	public static String getListPathExport() {

		return INSTANCE().get(P_LIST_PATH_EXPORT, DEF_LIST_PATH_EXPORT);
	}

	public static Boolean isOpenReport() {

		return INSTANCE().getBoolean(P_OPEN_REPORT, DEF_OPEN_REPORT);
	}
}