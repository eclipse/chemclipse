/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.scanremover.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final Character REMOVE_SIGN = 'X';
	public static final Character PRESERVE_SIGN = 'O';
	public static final String P_REMOVER_PATTERN = "removerPattern";
	public static final String DEF_REMOVER_PATTERN = PRESERVE_SIGN.toString() + REMOVE_SIGN.toString();
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
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	/**
	 * Returns the chromatogram filter settings.
	 * 
	 * @return IChromatogramFilterSettings
	 */
	public static IChromatogramFilterSettings getChromatogramFilterSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		ISupplierFilterSettings chromatogramFilterSettings = new SupplierFilterSettings();
		chromatogramFilterSettings.setScanRemoverPattern(preferences.get(P_REMOVER_PATTERN, DEF_REMOVER_PATTERN));
		return chromatogramFilterSettings;
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
}
