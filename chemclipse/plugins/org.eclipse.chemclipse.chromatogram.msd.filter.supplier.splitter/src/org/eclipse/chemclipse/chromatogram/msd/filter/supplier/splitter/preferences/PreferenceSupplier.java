/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.preferences;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.settings.FilterSettingsHighResMS;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.settings.FilterSettingsMSx;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.settings.FilterSettingsSIM;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.splitter.settings.FilterSettingsTandemMS;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final int MIN_LIMIT_IONS_SIM = 1;
	public static final int MAX_LIMIT_IONS_SIM = Integer.MAX_VALUE;
	//
	public static final String P_LIMIT_IONS_SIM = "limitIonsSIM";
	public static final int DEF_LIMIT_IONS_SIM = 5;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_LIMIT_IONS_SIM, Integer.toString(DEF_LIMIT_IONS_SIM));
	}

	public static FilterSettingsMSx getFilterSettingsMSx() {

		return new FilterSettingsMSx();
	}

	public static FilterSettingsSIM getFilterSettingsSIM() {

		FilterSettingsSIM settings = new FilterSettingsSIM();
		settings.setLimitIons(INSTANCE().getInteger(P_LIMIT_IONS_SIM, DEF_LIMIT_IONS_SIM));
		//
		return settings;
	}

	public static FilterSettingsTandemMS getFilterSettingsTandemMS() {

		return new FilterSettingsTandemMS();
	}

	public static FilterSettingsHighResMS getFilterSettingsHighResMS() {

		return new FilterSettingsHighResMS();
	}
}