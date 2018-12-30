/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Janos Binder - new features
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.Activator;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.settings.PeakQuantifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

public class PreferenceSupplier implements IPreferenceSupplier {

	private static Logger logger = Logger.getLogger(PreferenceSupplier.class);
	//
	public static final String P_SELECTED_QUANTITATION_TABLE = "selectedQuantitationTable";
	public static final String DEF_SELECTED_QUANTITATION_TABLE = "";
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
		//
		defaultValues.put(P_SELECTED_QUANTITATION_TABLE, DEF_SELECTED_QUANTITATION_TABLE);
		//
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static String getSelectedQuantitationTable() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_SELECTED_QUANTITATION_TABLE, DEF_SELECTED_QUANTITATION_TABLE);
	}

	public static void setSelectedQuantitationTable(String selectedQuantitationTable) {

		try {
			IEclipsePreferences preferences = INSTANCE().getPreferences();
			preferences.put(P_SELECTED_QUANTITATION_TABLE, selectedQuantitationTable);
			preferences.flush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}

	public static PeakQuantifierSettings getPeakQuantifierSetting() {

		PeakQuantifierSettings peakQuantifierSettings = new PeakQuantifierSettings();
		return peakQuantifierSettings;
	}
}
