/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.core.settings.FilterSettingsCSD;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.zeroset.core.settings.FilterSettingsWSD;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	private static IPreferenceSupplier preferenceSupplier = null;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

	}

	public static FilterSettingsCSD getFilterSettingsCSD() {

		FilterSettingsCSD filterSettings = new FilterSettingsCSD();
		return filterSettings;
	}

	public static FilterSettingsWSD getFilterSettingsWSD() {

		FilterSettingsWSD filterSettings = new FilterSettingsWSD();
		return filterSettings;
	}
}