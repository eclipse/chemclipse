/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - Settings
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.ReportSettings1;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.ReportSettings2;
import org.eclipse.chemclipse.chromatogram.xxd.report.supplier.openchrom.settings.ReportSettings3;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final int MIN_DELTA_RETENTION_TIME = 0;
	public static final int MAX_DELTA_RETENTION_TIME = Integer.MAX_VALUE;
	//
	public static final String P_APPEND_FILES = "appendFiles";
	public static final boolean DEF_APPEND_FILES = false;
	public static final String P_DELTA_RETENTION_TIME = "deltaRetentionTime";
	public static final int DEF_DELTA_RETENTION_TIME = 1000; // 1 Second
	public static final String P_USE_BEST_MATCH = "useBestMatch";
	public static final boolean DEF_USE_BEST_MATCH = true;
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
		defaultValues.put(P_APPEND_FILES, Boolean.toString(DEF_APPEND_FILES));
		defaultValues.put(P_DELTA_RETENTION_TIME, Integer.toString(DEF_DELTA_RETENTION_TIME));
		defaultValues.put(P_USE_BEST_MATCH, Boolean.toString(DEF_USE_BEST_MATCH));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static ReportSettings1 getReportSettings1() {

		return new ReportSettings1();
	}

	public static ReportSettings2 getReportSettings2() {

		ReportSettings2 settings = new ReportSettings2();
		settings.setDeltaRetentionTime(getDeltaRetentionTime());
		settings.setUseBestMatch(isUseBestMatch());
		return settings;
	}

	public static ReportSettings3 getReportSettings3() {

		return new ReportSettings3();
	}

	public static boolean isAppendFiles() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_APPEND_FILES, DEF_APPEND_FILES);
	}

	public static int getDeltaRetentionTime() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_DELTA_RETENTION_TIME, DEF_DELTA_RETENTION_TIME);
	}

	public static boolean isUseBestMatch() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_USE_BEST_MATCH, DEF_USE_BEST_MATCH);
	}
}
