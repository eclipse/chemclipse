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
 * Philip Wenig - refactoring INSTANCE
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.csv.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.settings.Delimiter;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.Activator;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMappings;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.DecimalSeparator;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.VirtualChannels;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.WellMappings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_DELIMITER = "separator";
	public static final String DEF_DELIMITER = Delimiter.SEMICOLON.name();
	public static final String P_DECIMAL_SEPARATOR = "decimal-separator";
	public static final String DEF_DECIMAL_SEPARATOR = DecimalSeparator.DOT.name();
	//
	public static final String P_CHANNEL_MAPPING = "channel-mapping";
	public static final String DEF_CHANNEL_MAPPING = "";
	public static final String P_WELL_MAPPING = "well-mapping";
	public static final String DEF_WELL_MAPPING = "";
	public static final String P_VIRTUAL_CHANNELS = "virtual-channels";
	public static final String DEF_VIRTUAL_CHANNELS = "";
	public static final String P_LIST_PATH_IMPORT = "listPathImport";
	public static final String DEF_LIST_PATH_IMPORT = "";
	public static final String P_LIST_PATH_EXPORT = "listPathExport";
	public static final String DEF_LIST_PATH_EXPORT = "";
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
		defaultValues.put(P_DELIMITER, DEF_DELIMITER);
		defaultValues.put(P_DECIMAL_SEPARATOR, DEF_DECIMAL_SEPARATOR);
		defaultValues.put(P_CHANNEL_MAPPING, DEF_CHANNEL_MAPPING);
		defaultValues.put(P_WELL_MAPPING, DEF_WELL_MAPPING);
		defaultValues.put(P_VIRTUAL_CHANNELS, DEF_VIRTUAL_CHANNELS);
		defaultValues.put(P_LIST_PATH_IMPORT, DEF_LIST_PATH_IMPORT);
		defaultValues.put(P_LIST_PATH_EXPORT, DEF_LIST_PATH_EXPORT);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static ChannelMappings getChannelMappings() {

		ChannelMappings channelMappings = new ChannelMappings();
		channelMappings.load(INSTANCE().get(P_CHANNEL_MAPPING, DEF_CHANNEL_MAPPING));
		//
		return channelMappings;
	}

	public static WellMappings getWellMappings() {

		WellMappings wellMappings = new WellMappings();
		wellMappings.load(INSTANCE().get(P_WELL_MAPPING, DEF_WELL_MAPPING));
		//
		return wellMappings;
	}

	public static VirtualChannels getVirtualChannels() {

		VirtualChannels virtualChannels = new VirtualChannels();
		virtualChannels.load(INSTANCE().get(P_VIRTUAL_CHANNELS, DEF_VIRTUAL_CHANNELS));
		//
		return virtualChannels;
	}

	public static Delimiter getDelimiter() {

		try {
			return Delimiter.valueOf(INSTANCE().get(P_DELIMITER, DEF_DELIMITER));
		} catch(Exception e) {
			return Delimiter.SEMICOLON;
		}
	}

	public static DecimalSeparator getDecimalSeparator() {

		try {
			return DecimalSeparator.valueOf(INSTANCE().get(P_DECIMAL_SEPARATOR, DEF_DECIMAL_SEPARATOR));
		} catch(Exception e) {
			return DecimalSeparator.COMMA;
		}
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
}