/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.xy.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.csd.converter.supplier.xy.Activator;
import org.eclipse.chemclipse.csd.converter.supplier.xy.io.DelimiterFormat;
import org.eclipse.chemclipse.csd.converter.supplier.xy.io.RetentionTimeFormat;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_AUTO_DETECT_FORMAT = "autoDetectFormat";
	public static final boolean DEF_AUTO_DETECT_FORMAT = true;
	public static final String P_DELIMITER_FORMAT = "delimiterFormat";
	public static final String DEF_DELIMITER_FORMAT = DelimiterFormat.TAB.name();
	public static final String P_RETENTION_TIME_FORMAT = "timeFormat";
	public static final String DEF_RETENTION_TIME_FORMAT = RetentionTimeFormat.MINUTES.name();
	//
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	public static boolean isAvailable() {

		return Activator.getContext() != null;
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
		defaultValues.put(P_AUTO_DETECT_FORMAT, Boolean.toString(DEF_AUTO_DETECT_FORMAT));
		defaultValues.put(P_DELIMITER_FORMAT, DEF_DELIMITER_FORMAT);
		defaultValues.put(P_RETENTION_TIME_FORMAT, DEF_RETENTION_TIME_FORMAT);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static boolean isAutoDetectFormat() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_AUTO_DETECT_FORMAT, DEF_AUTO_DETECT_FORMAT);
	}

	public static DelimiterFormat getDelimiterFormat() {

		try {
			IEclipsePreferences preferences = INSTANCE().getPreferences();
			return DelimiterFormat.valueOf(preferences.get(P_DELIMITER_FORMAT, DEF_DELIMITER_FORMAT));
		} catch(Exception e) {
			return DelimiterFormat.TAB;
		}
	}

	public static RetentionTimeFormat getRetentionTimeFormat() {

		try {
			IEclipsePreferences preferences = INSTANCE().getPreferences();
			return RetentionTimeFormat.valueOf(preferences.get(P_RETENTION_TIME_FORMAT, DEF_RETENTION_TIME_FORMAT));
		} catch(Exception e) {
			return RetentionTimeFormat.MINUTES;
		}
	}
}