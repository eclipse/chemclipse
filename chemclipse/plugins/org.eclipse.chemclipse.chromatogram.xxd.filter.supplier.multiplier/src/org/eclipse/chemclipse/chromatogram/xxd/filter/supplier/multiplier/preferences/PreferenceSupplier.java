/*******************************************************************************
 * Copyright (c) 2010, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.settings.DivisorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.settings.MultiplierSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_MULTIPLIER = "multiplier";
	public static final float DEF_MULTIPLIER = 1.0f;
	public static final float MIN_MULTIPLIER = 1.0E-12f;
	public static final float MAX_MULTIPLIER = Float.MAX_VALUE;
	//
	public static final String P_DIVISOR = "divisor";
	public static final float DEF_DIVISOR = 1.0f;
	public static final float MIN_DIVISOR = 1.0E-12f;
	public static final float MAX_DIVISOR = Float.MAX_VALUE;
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

		putDefault(P_MULTIPLIER, Float.toString(DEF_MULTIPLIER));
		putDefault(P_DIVISOR, Float.toString(DEF_DIVISOR));
	}

	public static MultiplierSettings getFilterSettingsMultiplier() {

		MultiplierSettings filterSettings = new MultiplierSettings();
		filterSettings.setMultiplier(INSTANCE().getFloat(P_MULTIPLIER, DEF_MULTIPLIER));
		return filterSettings;
	}

	public static DivisorSettings getFilterSettingsDivisor() {

		DivisorSettings filterSettings = new DivisorSettings();
		filterSettings.setDivisor(INSTANCE().getFloat(P_DIVISOR, DEF_DIVISOR));
		return filterSettings;
	}
}